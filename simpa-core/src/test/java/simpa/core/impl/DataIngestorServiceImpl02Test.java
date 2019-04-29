/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.core.impl;

import java.net.URL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.core.CameraPosition;
import simpa.core.DataIngestorService;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public class DataIngestorServiceImpl02Test {
    
    private static final Logger log = LoggerFactory.getLogger(DataIngestorServiceImpl02Test.class);
    
    @Test
    public void readTest01() {
        
        int expectedSize = 874;
        //String resource = "/20080505mosaicData.csv";
        String resource = "/data/Ventana/3200/20080505mosaicDataCorrected.csv";
        
        URL url = getClass().getResource(resource);
        if (url == null) {
            Assert.fail("Unable to find " + resource);
        }
        
        DataIngestorService ingest = new DataIngestorServiceImpl02();
        List<Tile> tiles = null;
        try {
            tiles = ingest.loadTiles(url);
        }
        catch (Exception ex) {
            log.error("Failed to read '" + url + "'", ex);
            Assert.fail("Unable to read data file");
        }
        
        Assert.assertTrue("Did not read all the records. EXPECTED: " + 
                expectedSize + " FOUND: " + tiles.size(), tiles.size() == expectedSize);
        
    }

    @Test
    public void readTest02() {

        int expectedSize = 874;
        //String resource = "/20080505mosaicData.csv";
        String resource = "/data/Ventana/3200/20080505mosaicDataCorrected.csv";

        URL url = getClass().getResource(resource);
        if (url == null) {
            Assert.fail("Unable to find " + resource);
        }

        DataIngestorService ingest = new DataIngestorServiceImpl02();
        List<Tile> tiles = null;
        try {
            tiles = ingest.loadTiles(url);
        }
        catch (Exception ex) {
            log.error("Failed to read '" + url + "'", ex);
            Assert.fail("Unable to read data file");
        }

        Assert.assertTrue("Did not read all the records. EXPECTED: " +
                expectedSize + " FOUND: " + tiles.size(), tiles.size() == expectedSize);

        /*
         
         Tile Index,
         Local Time ,
         "x position (positive right) (meters)",
         "y position (positive forward) (meters)",
         "z position (positive down) (meters)",
         "Roll angle (radians)",
         "Pitch angle (radians)",
         "Relative heading (radians)",
         "Image size x (meters)",
         "Image size y (meters)"

          110,10:14:54.76,1.1360,-0.2898,-1.5855,0.0304,-0.0873,-0.0038,3.4298,1.9331

         */
        for (Tile tile : tiles) {
            if (tile.getTileIndex() == 110) {
                CameraPosition cp = tile.getCameraPosition();
                Assert.assertEquals("X is bad", 1.1360, cp.getX(), 0.01);
                Assert.assertEquals("Y is bad", -0.2898, cp.getY(), 0.01);
                Assert.assertEquals("Z is bad", -1.5855, cp.getZ(), 0.01);
                Assert.assertEquals("Roll is bad", 0.0304, cp.getRoll(), 0.01);
                Assert.assertEquals("Pitch is bad", -0.0873, cp.getPitch(), 0.01);
                Assert.assertEquals("Heading is bad", -0.0038, cp.getHeading(), 0.01);
                Assert.assertEquals("Width is bad", 3.4298, cp.getHeading(), 0.01);
                Assert.assertEquals("Height is bad", 1.9331, cp.getHeading(), 0.01);
            }
            break;
        }

    }

}
