/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.impl.MosaicAssemblyImp;
import simpa.annotation.impl.TileAnnotationImpl;
import simpa.annotation.impl.VideoTileImpl;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.core.CameraPosition;
import simpa.core.CoreException;
import simpa.core.GlobalParameters;
import simpa.core.MosaicAssembly;
import simpa.core.impl.CameraPositionImpl;

/**
 *
 * IO Utilities for supporting the unit tests
 * @author brian
 */
public class IOUtil {

    private final Logger log = LoggerFactory.getLogger(IOUtil.class);

    /**
     * IO used for positional tests. Reads in a data file and associaties the
     * known target-location data with the videoTiles
     * @param url
     * @return
     */
    public MosaicAssembly<VideoTileImpl> readDataFile(URL url) {
        MosaicAssembly<VideoTileImpl> mosaicAssembly = new MosaicAssemblyImp();
        List<VideoTileImpl> videoTiles = mosaicAssembly.getTiles();

        // Read in text file and convert to MosaicAssembly
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {

                if (lineNumber == 2) {
                    String[] parts = line.split(":");
                    mosaicAssembly.setCameraIdentifier(parts[parts.length - 1].trim());
                }
                else if (lineNumber == 3) {
                    String[] parts = line.split(":");
                    mosaicAssembly.setSessionIdentifier(parts[parts.length - 1].trim());
                }

                if (!line.startsWith("#")) {
                    // image,date,x-pixel,y-pixel,x,y,z,width,height,pitch,roll,heading
                    String[] parts = line.split(",");

                    int tileIndex = Integer.valueOf(parts[0]);
                    Date date = GlobalParameters.DATE_FORMAT_UTC.parse(parts[1]);
                    Date videoDate = GlobalParameters.DATE_FORMAT_UTC.parse(parts[2]);
                    String timecode = parts[3];
                    double x = Double.valueOf(parts[4]);
                    double y = Double.valueOf(parts[5]);
                    double z = Double.valueOf(parts[6]);
                    double width = Double.valueOf(parts[7]);
                    double height = Double.valueOf(parts[8]);
                    double pitch = Double.valueOf(parts[9]);
                    double roll = Double.valueOf(parts[10]);
                    double heading = Double.valueOf(parts[11]);
                    String image = parts[12];
                    URL imageUrl = getClass().getResource(resolveDirectory(mosaicAssembly.getCameraIdentifier(), 
                            mosaicAssembly.getSessionIdentifier()) + image);

                    final CameraPosition cameraPosition = new CameraPositionImpl(x, y, z, pitch, roll, heading);
                    final VideoTime videoTime = new VideoTimeImpl(timecode, date);
                    final VideoTileImpl videoTile = new VideoTileImpl(tileIndex, width, height, imageUrl, cameraPosition, date, videoTime);
                    //final TileAnnotation tileAnnotation = new TileAnnotationImpl(xPixel, yPixel, "TEST");
                    //videoTile.getAnnotations().add(tileAnnotation);
                    videoTiles.add(videoTile);

                }

                lineNumber++;
            }

            URL targetLocationUrl = getClass().getResource(resolveDirectory(mosaicAssembly.getCameraIdentifier(),
                            mosaicAssembly.getSessionIdentifier()) + "target-location.txt");
            log.info("Reading " + targetLocationUrl);
            Map<Integer, Point2D> map = readTargetLocationData(targetLocationUrl);
            for (VideoTileImpl videoTile : videoTiles) {
                Point2D point = map.get(videoTile.getTileIndex());
                if (point != null) {
                    final TileAnnotation tileAnnotation = new TileAnnotationImpl((int) point.getX(), (int) point.getY(), "TEST");
                    videoTile.getAnnotations().add(tileAnnotation);
                }
            }


        }
        catch (Exception e) {
            throw new CoreException("Failed to read " + url.toExternalForm(), e);
        }

        return mosaicAssembly;
    }

    public Map<Integer, Point2D> readTargetLocationData(URL url) {

        Map<Integer, Point2D> map = new HashMap<Integer, Point2D>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        int tileIndex = Integer.valueOf(parts[0]);
                        int x = Integer.valueOf(parts[1]);
                        int y = Integer.valueOf(parts[2]);
                        map.put(tileIndex, new Point2D.Float(x, y));
                    }
                }
            }
        }
        catch (Exception e) {
            throw new CoreException("Failed to read " + url.toExternalForm(), e);
        }

        return map;
    }

    public String resolveDirectory(String cameraId, String sessionId) {
        return "/data/" + cameraId + "/" + sessionId + "/";
    }

}
