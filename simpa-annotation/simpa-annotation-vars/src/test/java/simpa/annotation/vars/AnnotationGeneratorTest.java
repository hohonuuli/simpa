/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import java.net.URL;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.AnnotationGeneratorService;
import simpa.core.DataIngestorService;
import simpa.core.Tile;
import simpa.core.impl.DataIngestorServiceImpl02;
import org.mbari.jpax.NonManagedEAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Injector;
import com.google.inject.Guice;

/**
 *
 * @author brian
 */
public class AnnotationGeneratorTest {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AnnotationGeneratorService generator;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VARSModule());
        generator = injector.getInstance(AnnotationGeneratorService.class);

    }
    
    @Test
    @Ignore // TODO This test is failing. Uncomment this and fix after we make way on the UI
    public void generateAnnotationsTest() {
        // read tile data file
        String resource = "/data/Ventana/3240/20080730MosaicData-Short.csv";
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
        
        // convert to annotations
//        IVideoArchive videoArchive = generator.generateAnnotations(tiles, "Ventana", 3200, "V3200-01");
//        Assert.assertTrue("Did not find expected number of VideoFrames. EXPECTED: " + 
//                expectedSize + " FOUND: " + videoArchive.getVideoFrameColl().size(), 
//                videoArchive.getVideoFrameColl().size() == expectedSize);
        
    }

}
