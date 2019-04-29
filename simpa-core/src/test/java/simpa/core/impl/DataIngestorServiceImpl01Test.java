/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.core.impl;

import java.net.URL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import simpa.core.DataIngestorService;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public class DataIngestorServiceImpl01Test {
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DataIngestorServiceImpl01Test.class);
    
    @Test
    public void readTest() {
        
        String resource = "/data/Ventana/3200/V3200-simpaData.txt";
        URL url = getClass().getResource(resource);
        if (url == null) {
            Assert.fail("Unable to find " + resource);
        }
        
        DataIngestorService ingest = new DataIngestorServiceImpl01();
        List<Tile> tiles = null;
        try {
            tiles = ingest.loadTiles(url);
        }
        catch (Exception ex) {
            log.error("Failed to read '" + url + "'", ex);
            Assert.fail("Unable to read data file");
        }
        
        Assert.assertTrue("Did not read all the records", tiles.size() == 874);

    }

}
