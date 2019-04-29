package simpa.annotation;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import java.util.Collection;
import org.junit.Assert;
import org.junit.Ignore;
import simpa.core.MosaicAssembly;
import simpa.core.CameraPosition;
import simpa.annotation.impl.VideoTileImpl;

/**
 * @author brian
 * @since Sep 4, 2008 4:29:30 PM
 */
public class TileAnnotationLocationTests {

    private final Logger log = LoggerFactory.getLogger(TileAnnotationLocationTests.class);
    private final IOUtil testIO = new IOUtil();



    private List<TileAnnotationSpatialLocation> generateSpatialLocations(MosaicAssembly<VideoTileImpl> mosaicAssembly) {
        List<TileAnnotationSpatialLocation> locations = new ArrayList<TileAnnotationSpatialLocation>();
        log.debug("Processing tiles");
        for (VideoTileImpl videoTile : mosaicAssembly.getTiles()) {
            CameraPosition cameraPosition = videoTile.getCameraPosition();
            for (TileAnnotation tileAnnotation : videoTile.getAnnotations()) {
                final TileAnnotationSpatialLocation location = new TileAnnotationSpatialLocation(tileAnnotation, videoTile);
                locations.add(location);
            }
        }
        return locations;
    }

    private List<TileAnnotationPixelLocation> generatePixelLocations(MosaicAssembly<VideoTileImpl> mosaicAssembly) {
        List<TileAnnotationPixelLocation> locations = new ArrayList<TileAnnotationPixelLocation>();
        log.debug("Processing tiles");
        VideoTile targetTile = mosaicAssembly.getTiles().get(0);
        for (VideoTileImpl videoTile : mosaicAssembly.getTiles()) {
            CameraPosition cameraPosition = videoTile.getCameraPosition();
            for (TileAnnotation tileAnnotation : videoTile.getAnnotations()) {
                final TileAnnotationPixelLocation location = new TileAnnotationPixelLocation(tileAnnotation, videoTile, targetTile);
                locations.add(location);
            }
        }
        return locations;
    }

    @Test
    public void spatialLocationTest01() {
        URL url = getClass().getResource("/data/Ventana/3240/V3240.txt");
        Assert.assertNotNull("Failed to lookup data file", url);
        MosaicAssembly<VideoTileImpl> mosaicAssembly = testIO.readDataFile(url);
        List<TileAnnotationSpatialLocation> locations = generateSpatialLocations(mosaicAssembly);
        printSpatialLocations(locations);
    }

    @Test
    @Ignore
    public void spatialLocationTest02() {
        URL url = getClass().getResource("/data/antenna-base-refimages.txt");
        MosaicAssembly<VideoTileImpl> mosaicAssembly = testIO.readDataFile(url);
        List<TileAnnotationSpatialLocation> locations = generateSpatialLocations(mosaicAssembly);
        float x = -1.09F;
        float y = -30.6F;
        float tolerance = 0.1F;
        for (TileAnnotationSpatialLocation tasl : locations) {
            Assert.assertEquals("X value is not correct", x, tasl.getXInMeters(), tolerance);
            Assert.assertEquals("Y value is not correct", y, tasl.getYInMeters(), tolerance);
        }

    }

    @Test
    @Ignore
    public void pixelLocationTest02() {
        URL url = getClass().getResource("/data/Ventana/3240/V3240.txt");
        MosaicAssembly<VideoTileImpl> mosaicAssembly = testIO.readDataFile(url);
        List<TileAnnotationPixelLocation> locations = generatePixelLocations(mosaicAssembly);
        // First tile in the set is the target for all subsequent tiles.
        TileAnnotationPixelLocation targetLoc = locations.get(0);
        // TODO loop should test for a given pixel location not [x,y]
        int expectedX = 227;
        int expectedY = 281;
        int tol = 2;
        for (int i = 0; i < locations.size(); i++) {
            TileAnnotationPixelLocation loc = locations.get(i);

            int x = 0;
            int y = 0;
            if (i == 0) {
                TileAnnotation tileAnotation = loc.getTileAnnotation();
                x = tileAnotation.getX();
                y = tileAnotation.getY();
            }
            else {
                x = expectedX;
                y = expectedY;
            }
            boolean ok = Math.abs(loc.getXInPixels() - x) <= tol && Math.abs(loc.getYInPixels() - y) <= tol;
            if (!ok) {
                String msg = "Expected [" + x + ", " + y + "]. Found [" +
                        loc.getXInPixels() + ", " + loc.getYInPixels() + "]";
                Assert.fail(msg);
            }

        }
    }

    public void printSpatialLocations(Collection<TileAnnotationSpatialLocation> spatialLocations) {
        System.out.println("#Index\tCenterX[m]\tCenterY[m]\tdX[pix]\tdY[pix]\tdirection[deg]\tradius[m]\tDistanceX[m]\tDistanceY[m]");
        for (TileAnnotationSpatialLocation location : spatialLocations) {
            final VideoTile vt = location.getVideoTile();
            final CameraPosition cp = vt.getCameraPosition();

            System.out.printf("%3d\t%5.2f\t%5.2f\t%4d\t%4d\t%5.2f\t%5.2f\t%5.2f\t%5.2f\n",
                    vt.getTileIndex(), cp.getX(), cp.getY(), 0, 0, 0F, 0F, location.getXInMeters(), location.getYInMeters() );
        }
    }
}
