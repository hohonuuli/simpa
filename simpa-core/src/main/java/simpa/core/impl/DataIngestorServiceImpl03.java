/*
 * @(#)DataIngestorServiceImpl03.java   2009.08.06 at 09:03:35 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package simpa.core.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.core.CameraPosition;
import simpa.core.CoreException;
import simpa.core.DataIngestorService;
import simpa.core.Tile;

/**
 * Parser for CSV files with data like:
 *
 * <pre>1,30-Jul-08,17:07:15,0.0077498,0.0069826,-1.6712,0.032755,-0.087711,0,1.8920468,3.3621744</pre>
 * @author brian
 */
public class DataIngestorServiceImpl03 implements DataIngestorService {

    private static final Logger log = LoggerFactory.getLogger(DataIngestorServiceImpl03.class);

    /**
     * @return
     */
    public List<Tile> loadTiles(URL url) {
        List<Tile> tiles = null;

        try {
            tiles = read(url);
        }
        catch (IOException ex) {
            throw new CoreException("Failed to read '" + url.toExternalForm() + "'" + ex);
        }

        return tiles;
    }

    public static List<Tile> read(URL url) throws IOException {

        final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<Tile> tiles = new ArrayList<Tile>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        int lineCount = 0;
        String line = null;

        while ((line = reader.readLine()) != null) {

            // The 1st 7 lines are header
            if (lineCount > 11) {

                /*
                 * Split the line by white space. Drop the empty spaces in
                 * the resulting array.
                 */
                String[] parts = line.split(",");
                List<String> values = Arrays.asList(parts);

                if (values.size() == 11) {

                    /*
                     * Build a TileImpl object from the data in a line
                     */
                    int tileIndex = (int) Double.parseDouble(values.get(0));

                    // Parse time
                    String timeString = values.get(1) + " " + values.get(2);
                    Date date;

                    try {
                        date = dateFormat.parse(timeString);
                    }
                    catch (ParseException ex) {
                        throw new CoreException("Failed to parse " + timeString, ex);
                    }

                    double x = Double.parseDouble(values.get(4));    // Cirle swaps his axes (he uses +x = forward, +y = right)
                    double y = Double.parseDouble(values.get(3));
                    double z = Double.parseDouble(values.get(5));
                    double roll = Double.parseDouble(values.get(6));
                    double pitch = Double.parseDouble(values.get(7));
                    double heading = Double.parseDouble(values.get(8));
                    double width = Double.parseDouble(values.get(10));    // Swap width and height from file too.
                    double height = Double.parseDouble(values.get(9));

                    CameraPosition cameraPosition = new CameraPositionImpl(x, y, z, pitch, roll, heading);
                    TileImpl tile = new TileImpl(tileIndex, width, height, null, cameraPosition, date);

                    tiles.add(tile);
                }

            }

            lineCount++;
        }

        if (log.isDebugEnabled()) {
            log.debug("Found " + tiles.size() + " tiles in '" + url.toExternalForm() + "'");
        }

        return tiles;

    }
}
