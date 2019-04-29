package simpa.map.core.impl;

import simpa.core.DataIngestorService;
import simpa.core.Tile;
import simpa.core.CoreException;
import simpa.core.CameraPosition;
import simpa.core.impl.CameraPositionImpl;
import simpa.core.impl.TileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Arrays;
import java.net.URL;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Parser for output form InformationResolver
 */
public class DataIngestorServiceImpl04 implements DataIngestorService {

    private static final Logger log = LoggerFactory.getLogger(DataIngestorServiceImpl04.class);
    private final File imageDirectory;

    public DataIngestorServiceImpl04(File imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    public List<Tile> loadTiles(URL url) {
        List<Tile> tiles;
        try {
            tiles = read(url);
        }
        catch (IOException ex) {
            throw new CoreException("Failed to read '" + url.toExternalForm() + "'" + ex);
        }
        return tiles;
    }

    private List<Tile> read(URL url) throws IOException {

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<Tile> tiles = new ArrayList<Tile>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        int lineCount = 0;
        String line = null;

        while ((line = reader.readLine()) != null) {

            // The 1st 5 lines are header
            if (lineCount > 4) {

                /*
                * Split the line by white space. Drop the empty spaces in
                * the resulting array.
                */
                String[] parts = line.split(",");
                List<String> values = Arrays.asList(parts);

                if (values.size() == 12) {

                    /*
                    * Build a TileImpl object from the data in a line
                    */
                    int tileIndex = (int) Double.parseDouble(values.get(0));

                    // Parse time
                    Date date;
                    try {
                        date = dateFormat.parse(values.get(2));
                    }
                    catch (ParseException ex) {
                        throw new CoreException("Failed to parse " + values.get(2), ex);
                    }

                    String timecode = values.get(3).replace(":", "_");
                    File image = new File(imageDirectory, timecode + ".png");
                    if (!image.exists()) {
                        log.info(image.getAbsolutePath() + " does not exist");
                        image = null;
                    }
                    URL imageUrl = (image == null) ? null : image.toURI().toURL();

                    double x = Double.parseDouble(values.get(4)); // Cirle swaps his axes (he uses +x = forward, +y = right)
                    double y = Double.parseDouble(values.get(5));
                    double z = Double.parseDouble(values.get(6));
                    double width = Double.parseDouble(values.get(8)); // Swap width and height from file too.
                    double height = Double.parseDouble(values.get(7));
                    double pitch = Double.parseDouble(values.get(9));
                    double roll = Double.parseDouble(values.get(10));
                    double heading = Double.parseDouble(values.get(11));

                    CameraPosition cameraPosition = new CameraPositionImpl(x, y, z, pitch, roll, heading);
                    TileImpl tile = new TileImpl(tileIndex, width, height, imageUrl, cameraPosition, date);
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