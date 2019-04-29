/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

//import java.awt.image.BufferedImage;

import org.junit.Ignore;
import org.junit.Test;

//import java.net.URL;
//import java.util.List;
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import simpa.annotation.AnnotationLookupService;
//import simpa.annotation.ImageCaptureService;
//import simpa.annotation.VideoControlService;
//
//import simpa.core.DataIngestorService;
//import simpa.core.MosaicAssembly;
//import simpa.core.Tile;
//import com.google.inject.Injector;
//import com.google.inject.Guice;
//import org.junit.Before;

/**
 *
 * @author brian
 */
public class SeekTimecodeAndCaptureImageTest {

    @Test
    @Ignore
    public void fakeTest() {

    }
    
//    private static final Logger log = LoggerFactory.getLogger(SeekTimecodeAndCaptureImageTest.class);
//    private AnnotationLookupService annotationLookupService;
//    private DataIngestorService dataIngestorService;
//    private VideoControlService videoControlService;
//    private ImageCaptureService imageCaptureService;
//
//
//    @Before
//    public void setup() {
//        Injector injector = Guice.createInjector(new VARSModule());
//        annotationLookupService = injector.getInstance(AnnotationLookupService.class);
//        dataIngestorService = injector.getInstance(DataIngestorService.class);
//        videoControlService = injector.getInstance(VideoControlService.class);
//        imageCaptureService = injector.getInstance(ImageCaptureService.class);
//
//    }
//
//    @Test
//    @Ignore
//    public void test01() {
//
//
//        // Read file
//        String resource = "/data/Ventana/3240/20080730MosaicData-Short.csv";
//        URL url = getClass().getResource(resource);
//        if (url == null) {
//            Assert.fail("Unable to find " + resource);
//        }
//
//        List<Tile> tiles = null;
//        try {
//            tiles = dataIngestorService.loadTiles(url);
//        }
//        catch (Exception ex) {
//            log.error("Failed to read '" + url + "'", ex);
//            Assert.fail("Unable to read data file");
//        }
//
//        // TODO convert to mosaic
//        MosaicAssembly<VARSVideoTile> mosaicAssembly = annotationLookupService.convertToMosaicAssembly("Ventana", "3240", tiles);
//
//        // Connect to videoControlService
//        videoControlService.connect("COM1", 29.97);
//
//        for (VARSVideoTile videoTile : mosaicAssembly.getTiles()) {
//            log.debug("Capturing image at " + videoTile.getVideoTime());
//            try {
//            BufferedImage image = imageCaptureService.capture(videoTile.getVideoTime());
//                Assert.assertNotNull(image);
//            }
//            catch (Exception e) {
//                log.debug("Failed to grab image", e);
//            }
//        }
//
//    }

}
