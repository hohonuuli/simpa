/*
 * @(#)DataIngestorServiceImpl01.java   2009.08.06 at 09:03:35 PDT
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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.core.CameraPosition;
import simpa.core.CoreException;
import simpa.core.DataIngestorService;
import simpa.core.Tile;

/**
 * Ingest for files like V3200-simpaData.txt
 *
 * @author brian
 */
public class DataIngestorServiceImpl01 implements DataIngestorService {

    private static final Logger log = LoggerFactory.getLogger(DataIngestorServiceImpl01.class);

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

        List<Tile> tiles = new ArrayList<Tile>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        int lineCount = 0;
        String line = null;

        while ((line = reader.readLine()) != null) {

            // The 1st 7 lines are header
            if (lineCount > 7) {

                /*
                 * Split the line by white space. Drop the empty spaces in
                 * the resulting array.
                 */
                String[] parts = line.split("\\s");
                List<String> values = new ArrayList<String>(parts.length);

                for (String string : parts) {
                    if (string.length() > 0) {
                        values.add(string);
                    }
                }

                if (values.size() == 10) {

                    /*
                     * Build a TileImpl object from the data in a line
                     */
                    int tileIndex = (int) Double.parseDouble(values.get(0));
                    double time = Double.parseDouble(values.get(1));

                    // We should convert the time to a date object. But we won't for this demo class
                    double x = Double.parseDouble(values.get(2));
                    double y = Double.parseDouble(values.get(3));
                    double z = Double.parseDouble(values.get(4));
                    double roll = Double.parseDouble(values.get(5));
                    double pitch = Double.parseDouble(values.get(6));
                    double heading = Double.parseDouble(values.get(7));
                    double width = Double.parseDouble(values.get(8));
                    double height = Double.parseDouble(values.get(9));

                    CameraPosition cameraPosition = new CameraPositionImpl(x, y, z, pitch, roll, heading);
                    TileImpl tile = new TileImpl(tileIndex, width, height, null, cameraPosition, null);

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
