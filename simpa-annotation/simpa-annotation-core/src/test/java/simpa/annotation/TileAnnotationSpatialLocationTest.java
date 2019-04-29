/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import javax.imageio.ImageIO;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import simpa.annotation.impl.TileAnnotationImpl;
import simpa.annotation.impl.VideoTileImpl;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.core.CameraPosition;
import simpa.core.impl.CameraPositionImpl;

/**
 *
 * @author brian
 */
public class TileAnnotationSpatialLocationTest {
    
    private final org.slf4j.Logger log = LoggerFactory.getLogger(TileAnnotationSpatialLocationTest.class);
    
    
    /**
     * Test position of pixels without any rotation in origin tile
     */
    @Test
    public void test01() {
        
        /*
         * Load a test image
         */
        final URL url = getClass().getResource("/images/01_10_54_12.jpg");
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        }
        catch (IOException ex) {
            fail(ex.getMessage());
        }
        int width = image.getWidth();
        int height = image.getHeight();
        
        /*
         * Setup A 2m x 2m tile with center at (0m, 0m)
         */
        final VideoTime videoTime1 = new VideoTimeImpl("00:00:00:00", new Date());
        final CameraPosition cameraPosition1 = new CameraPositionImpl(0, 0, 2, 0, 0, 0);
        VideoTile videoTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, videoTime1.getDate(), videoTime1);
        
        /*
         * Top Left corner should be (-1m, 1m)
         */
        final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(0, 0, "A01");
        videoTile.getAnnotations().add(tileAnnotation1);
        TileAnnotationSpatialLocation spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation1, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Top Center should be (0m ,1m)
         */
        final TileAnnotation tileAnnotation2 = new TileAnnotationImpl(width / 2, 0, "A02");
        videoTile.getAnnotations().add(tileAnnotation2);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation2, videoTile);
        assertEquals(0D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Top Right should be (1m ,1m)
         */
        final TileAnnotation tileAnnotation3 = new TileAnnotationImpl(width - 1, 0, "A03");
        videoTile.getAnnotations().add(tileAnnotation3);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation3, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Center Right should be (1m ,0m)
         */
        final TileAnnotation tileAnnotation4 = new TileAnnotationImpl(width - 1, height / 2, "A04");
        videoTile.getAnnotations().add(tileAnnotation4);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation4, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(0D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Right should be (1m ,-1m)
         */
        final TileAnnotation tileAnnotation5 = new TileAnnotationImpl(width - 1, height - 1, "A05");
        videoTile.getAnnotations().add(tileAnnotation5);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation5, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Center should be (0m ,-1m)
         */
        final TileAnnotation tileAnnotation6 = new TileAnnotationImpl(width / 2, height - 1, "A06");
        videoTile.getAnnotations().add(tileAnnotation6);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation6, videoTile);
        assertEquals(0D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Left should be (-1m ,-1m)
         */
        final TileAnnotation tileAnnotation7 = new TileAnnotationImpl(0, height - 1, "A07");
        videoTile.getAnnotations().add(tileAnnotation7);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation7, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Center Left should be (-1m , 0m)
         */
        final TileAnnotation tileAnnotation8 = new TileAnnotationImpl(0, height / 2, "A07");
        videoTile.getAnnotations().add(tileAnnotation8);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation8, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(0D, spatialLocation.getYInMeters(), 0.01);
        
    }
    
     /**
      * Test position of pixels with relative heading of -pi/2 (-90 degress or 
      * 90 degrees counterclockwise) in origin tile
      */
    @Test
    public void test02() {
        
        /*
         * Load a test image
         */
        final URL url = getClass().getResource("/images/01_10_54_12.jpg");
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        }
        catch (IOException ex) {
            fail(ex.getMessage());
        }
        int width = image.getWidth();
        int height = image.getHeight();
        
        /*
         * Setup A 2m x 2m tile with center at (0m, 0m)
         */
        final VideoTime videoTime1 = new VideoTimeImpl("00:00:00:00", new Date());
        final CameraPosition cameraPosition1 = new CameraPositionImpl(0, 0, 2, 0, 0, -Math.PI / 2);
        VideoTile videoTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, videoTime1.getDate(), videoTime1);
        
        /*
         * Top Left corner should be (-1m, -1m)
         */
        final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(0, 0, "A01");
        videoTile.getAnnotations().add(tileAnnotation1);
        TileAnnotationSpatialLocation spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation1, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Top Center should be (-1m ,0m)
         */
        final TileAnnotation tileAnnotation2 = new TileAnnotationImpl(width / 2, 0, "A02");
        videoTile.getAnnotations().add(tileAnnotation2);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation2, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(0D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Top Right should be (-1m ,1m)
         */
        final TileAnnotation tileAnnotation3 = new TileAnnotationImpl(width - 1, 0, "A03");
        videoTile.getAnnotations().add(tileAnnotation3);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation3, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Center Right should be (0m ,1m)
         */
        final TileAnnotation tileAnnotation4 = new TileAnnotationImpl(width - 1, height / 2, "A04");
        videoTile.getAnnotations().add(tileAnnotation4);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation4, videoTile);
        assertEquals(0D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Right should be (1m ,1m)
         */
        final TileAnnotation tileAnnotation5 = new TileAnnotationImpl(width - 1, height - 1, "A05");
        videoTile.getAnnotations().add(tileAnnotation5);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation5, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Center should be (1m ,0m)
         */
        final TileAnnotation tileAnnotation6 = new TileAnnotationImpl(width / 2, height - 1, "A06");
        videoTile.getAnnotations().add(tileAnnotation6);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation6, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(0D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Left should be (1m ,-1m)
         */
        final TileAnnotation tileAnnotation7 = new TileAnnotationImpl(0, height - 1, "A07");
        videoTile.getAnnotations().add(tileAnnotation7);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation7, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Center Left should be (0m , -1m)
         */
        final TileAnnotation tileAnnotation8 = new TileAnnotationImpl(0, height / 2, "A07");
        videoTile.getAnnotations().add(tileAnnotation8);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation8, videoTile);
        assertEquals(0D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
    }
    
    /**
     * Test position of pixels with off set and no rotation
     */
    @Test
    public void test03() {
        
        /*
         * Load a test image
         */
        final URL url = getClass().getResource("/images/01_10_54_12.jpg");
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        }
        catch (IOException ex) {
            fail(ex.getMessage());
        }
        int width = image.getWidth();
        int height = image.getHeight();
        
        for (int i = 0; i < 100; i++) {
            
            double x = (Math.random() * 1000) - 500;
            double y = (Math.random() * 1000) - 500;
        
            /*
             * Setup A 2m x 2m tile with center at (xm, ym)
             */
            final VideoTime videoTime1 = new VideoTimeImpl("00:00:00:00", new Date());
            final CameraPosition cameraPosition1 = new CameraPositionImpl(x, y, 2, 0, 0, 0);
            VideoTile videoTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, videoTime1.getDate(), videoTime1);

            /*
             * Top Left corner should be (-1m, 1m)
             */
            final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(0, 0, "A01");
            videoTile.getAnnotations().add(tileAnnotation1);
            TileAnnotationSpatialLocation spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation1, videoTile);
            assertEquals(-1D + x, spatialLocation.getXInMeters(), 0.01);
            assertEquals(1D + y, spatialLocation.getYInMeters(), 0.01);

            /*
             * Top Center should be (0m ,1m)
             */
            final TileAnnotation tileAnnotation2 = new TileAnnotationImpl(width / 2, 0, "A02");
            videoTile.getAnnotations().add(tileAnnotation2);
            spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation2, videoTile);
            assertEquals(0D + x, spatialLocation.getXInMeters(), 0.01);
            assertEquals(1D + y, spatialLocation.getYInMeters(), 0.01);

            /*
             * Top Right should be (1m ,1m)
             */
            final TileAnnotation tileAnnotation3 = new TileAnnotationImpl(width - 1, 0, "A03");
            videoTile.getAnnotations().add(tileAnnotation3);
            spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation3, videoTile);
            assertEquals(1D + x, spatialLocation.getXInMeters(), 0.01);
            assertEquals(1D + y, spatialLocation.getYInMeters(), 0.01);

            /*
             * Center Right should be (1m ,0m)
             */
            final TileAnnotation tileAnnotation4 = new TileAnnotationImpl(width - 1, height / 2, "A04");
            videoTile.getAnnotations().add(tileAnnotation4);
            spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation4, videoTile);
            assertEquals(1D + x, spatialLocation.getXInMeters(), 0.01);
            assertEquals(0D + y, spatialLocation.getYInMeters(), 0.01);

            /*
             * Bottom Right should be (1m ,-1m)
             */
            final TileAnnotation tileAnnotation5 = new TileAnnotationImpl(width - 1, height - 1, "A05");
            videoTile.getAnnotations().add(tileAnnotation5);
            spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation5, videoTile);
            assertEquals(1D + x, spatialLocation.getXInMeters(), 0.01);
            assertEquals(-1D + y, spatialLocation.getYInMeters(), 0.01);

            /*
             * Bottom Center should be (0m ,-1m)
             */
            final TileAnnotation tileAnnotation6 = new TileAnnotationImpl(width / 2, height - 1, "A06");
            videoTile.getAnnotations().add(tileAnnotation6);
            spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation6, videoTile);
            assertEquals(0D + x, spatialLocation.getXInMeters(), 0.01);
            assertEquals(-1D + y, spatialLocation.getYInMeters(), 0.01);

            /*
             * Bottom Left should be (-1m ,-1m)
             */
            final TileAnnotation tileAnnotation7 = new TileAnnotationImpl(0, height - 1, "A07");
            videoTile.getAnnotations().add(tileAnnotation7);
            spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation7, videoTile);
            assertEquals(-1D + x, spatialLocation.getXInMeters(), 0.01);
            assertEquals(-1D + y, spatialLocation.getYInMeters(), 0.01);

            /*
             * Center Left should be (-1m , 0m)
             */
            final TileAnnotation tileAnnotation8 = new TileAnnotationImpl(0, height / 2, "A07");
            videoTile.getAnnotations().add(tileAnnotation8);
            spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation8, videoTile);
            assertEquals(-1D + x, spatialLocation.getXInMeters(), 0.01);
            assertEquals(0D + y, spatialLocation.getYInMeters(), 0.01);
        }
        
    }
    
     /**
      * Test position of pixels with relative heading of pi/2 (90 degress or 
      * 90 degrees clockwise) in origin tile
      */
    @Test
    public void test05() {
        
        /*
         * Load a test image
         */
        final URL url = getClass().getResource("/images/01_10_54_12.jpg");
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        }
        catch (IOException ex) {
            fail(ex.getMessage());
        }
        int width = image.getWidth();
        int height = image.getHeight();
        
        /*
         * Setup A 2m x 2m tile with center at (0m, 0m)
         */
        final VideoTime videoTime1 = new VideoTimeImpl("00:00:00:00", new Date());
        final CameraPosition cameraPosition1 = new CameraPositionImpl(0, 0, 2, 0, 0, Math.PI / 2);
        VideoTile videoTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, videoTime1.getDate(), videoTime1);
        
        /*
         * Top Left corner should be (-1m, -1m)
         */
        final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(0, 0, "A01");
        videoTile.getAnnotations().add(tileAnnotation1);
        TileAnnotationSpatialLocation spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation1, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Top Center should be (-1m ,0m)
         */
        final TileAnnotation tileAnnotation2 = new TileAnnotationImpl(width / 2, 0, "A02");
        videoTile.getAnnotations().add(tileAnnotation2);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation2, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(0D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Top Right should be (-1m ,1m)
         */
        final TileAnnotation tileAnnotation3 = new TileAnnotationImpl(width - 1, 0, "A03");
        videoTile.getAnnotations().add(tileAnnotation3);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation3, videoTile);
        assertEquals(1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Center Right should be (0m ,1m)
         */
        final TileAnnotation tileAnnotation4 = new TileAnnotationImpl(width - 1, height / 2, "A04");
        videoTile.getAnnotations().add(tileAnnotation4);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation4, videoTile);
        assertEquals(0D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Right should be (1m ,1m)
         */
        final TileAnnotation tileAnnotation5 = new TileAnnotationImpl(width - 1, height - 1, "A05");
        videoTile.getAnnotations().add(tileAnnotation5);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation5, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(-1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Center should be (1m ,0m)
         */
        final TileAnnotation tileAnnotation6 = new TileAnnotationImpl(width / 2, height - 1, "A06");
        videoTile.getAnnotations().add(tileAnnotation6);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation6, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(0D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Bottom Left should be (1m ,-1m)
         */
        final TileAnnotation tileAnnotation7 = new TileAnnotationImpl(0, height - 1, "A07");
        videoTile.getAnnotations().add(tileAnnotation7);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation7, videoTile);
        assertEquals(-1D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
        /*
         * Center Left should be (0m , -1m)
         */
        final TileAnnotation tileAnnotation8 = new TileAnnotationImpl(0, height / 2, "A07");
        videoTile.getAnnotations().add(tileAnnotation8);
        spatialLocation = new TileAnnotationSpatialLocation(tileAnnotation8, videoTile);
        assertEquals(0D, spatialLocation.getXInMeters(), 0.01);
        assertEquals(1D, spatialLocation.getYInMeters(), 0.01);
        
    }


}
