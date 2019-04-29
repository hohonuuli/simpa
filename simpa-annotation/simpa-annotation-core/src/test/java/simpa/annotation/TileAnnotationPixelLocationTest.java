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
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;
import simpa.annotation.impl.TileAnnotationImpl;
import simpa.annotation.impl.VideoTileImpl;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.core.CameraPosition;
import simpa.core.CoreException;
import simpa.core.impl.CameraPositionImpl;

/**
 *
 * @author brian
 */
public class TileAnnotationPixelLocationTest {

    private static BufferedImage image;
    private static URL url;
    private static VideoTime sourceVideoTime;
    private static VideoTime targetVideoTime;
    
    @BeforeClass
    public static void setup() {
        url = "".getClass().getResource("/images/01_10_54_12.jpg");
        try {
            image = ImageIO.read(url);
        }
        catch (IOException ex) {
            throw new CoreException("Failed to load image for testing", ex);
        }
        sourceVideoTime = new VideoTimeImpl("00:00:00:00", new Date());
        targetVideoTime = new VideoTimeImpl("00:00:01:00", new Date(sourceVideoTime.getDate().getTime() + 1000));
    }
    
    /**
     * Check that the top left coordinate resolves correctly
     */
    @Test
    public void test01() {

        // Source Tile is a 2m x 2m tile with center at (0m, 0m)
        final CameraPosition cameraPosition1 = new CameraPositionImpl(0, 0, 2, 0, 0, 0);
        VideoTile sourceTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, sourceVideoTime.getDate(), sourceVideoTime);
        
        // Annotation is at image origin
        final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(0, 0, "A01");
        sourceTile.getAnnotations().add(tileAnnotation1);
        
        // Target Tile is a 2m X 2m tile with center at (0m, 0m)
        final CameraPosition cameraPosition2 = new CameraPositionImpl(0, 0, 2, 0, 0, 0);
        VideoTile targetTile = new VideoTileImpl(0, 2, 2, url, cameraPosition2, targetVideoTime.getDate(), targetVideoTime);
        
        // Top Left corner should be (0px, 0px)
        TileAnnotationPixelLocation pixelLocation = new TileAnnotationPixelLocation(tileAnnotation1, sourceTile, targetTile);
        assertEquals(0D, pixelLocation.getXInPixels().doubleValue(), 0.5);
        assertEquals(0D, pixelLocation.getYInPixels().doubleValue(), 0.5);
    }
    
    
    /**
     * Check that the bottom right coordiinate resolves correctly
     */
    @Test
    public void test02() {
 
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Source tile is a 2m x 2m tile with center at (0m, 0m)
        final CameraPosition cameraPosition1 = new CameraPositionImpl(0, 0, 2, 0, 0, 0);
        VideoTile sourceTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, sourceVideoTime.getDate(), sourceVideoTime);
        
        // Annotation is at bottom right of image
        final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(width - 1, height - 1, "A01");
        sourceTile.getAnnotations().add(tileAnnotation1);
        
        // Target tile is a 2m x 2m tile with center at (0m, 0m) 
        final CameraPosition cameraPosition2 = new CameraPositionImpl(0, 0, 2, 0, 0, 0);
        VideoTile targetTile = new VideoTileImpl(0, 2, 2, url, cameraPosition2, targetVideoTime.getDate(), targetVideoTime);
        
        // Bottom right corner should be (width - 1px, height - 1px)
        TileAnnotationPixelLocation pixelLocation = new TileAnnotationPixelLocation(tileAnnotation1, sourceTile, targetTile);
        assertEquals(width - 1, pixelLocation.getXInPixels().doubleValue(), 0.5);
        assertEquals(height - 1, pixelLocation.getYInPixels().doubleValue(), 0.5);
    }
    
    /**
     * Test that tileAnnotation is correctly located between 2 offset tiles.
     */
    @Test
    public void test03() {
   
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Source tile is a 2m x 2m tile with center at (1m, 1m)
        final CameraPosition cameraPosition1 = new CameraPositionImpl(1, 1, 2, 0, 0, 0);
        VideoTile sourceTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, sourceVideoTime.getDate(), sourceVideoTime);
        
        // Annotation is at bottom left of image
        final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(0, height - 1, "A01");
        sourceTile.getAnnotations().add(tileAnnotation1);
        
        // Target tile is a 2m x 2m tile with center at (0m, 0m)
        final CameraPosition cameraPosition2 = new CameraPositionImpl(0, 0, 2, 0, 0, 0);
        VideoTile targetTile = new VideoTileImpl(0, 2, 2, url, cameraPosition2, targetVideoTime.getDate(), targetVideoTime);
        
        // annotation should be at center of targetTile (+/- 1 pixel)
        TileAnnotationPixelLocation pixelLocation = new TileAnnotationPixelLocation(tileAnnotation1, sourceTile, targetTile);
        assertEquals(width / 2, pixelLocation.getXInPixels().doubleValue(), 1);
        assertEquals(height / 2, pixelLocation.getYInPixels().doubleValue(), 1);
    }
    
    /**
     * Test that tileAnnotation is correctly located between 2 offset and 
     * rotated tiles.
     */
    @Test
    public void test04() {
   
        int height = image.getHeight();
        int width = image.getWidth();
        
        // Source tile is a 2m x 2m tile with center at (1m, 1m) rotated counter-clockwise 90 degrees
        final CameraPosition cameraPosition1 = new CameraPositionImpl(1, 1, 2, 0, 0, -Math.PI / 2);
        VideoTile sourceTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, sourceVideoTime.getDate(), sourceVideoTime);
        
        // Annotation is at top left of source image
        final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(0, 0, "A01");
        sourceTile.getAnnotations().add(tileAnnotation1);
        
        // Target tile is a 2m x 2m tile with center at (0m, 0m) rotated clockwise 90 degrees
        final CameraPosition cameraPosition2 = new CameraPositionImpl(0, 0, 2, 0, 0, Math.PI / 2);
        VideoTile targetTile = new VideoTileImpl(0, 2, 2, url, cameraPosition2, targetVideoTime.getDate(), targetVideoTime);
        
        // Annotation should be at center of target image
        TileAnnotationPixelLocation pixelLocation = new TileAnnotationPixelLocation(tileAnnotation1, sourceTile, targetTile);
        assertEquals(width / 2, pixelLocation.getXInPixels().doubleValue(), 1.0);
        assertEquals(height / 2, pixelLocation.getYInPixels().doubleValue(), 1.0);
        
        // Target tile is a 2m x 2m tile with center at (-1m, -1m) rotated clockwise 90 degrees
        final CameraPosition cameraPosition3 = new CameraPositionImpl(-1, -1, 2, 0, 0, Math.PI / 2);
        VideoTile targetTile2 = new VideoTileImpl(0, 2, 2, url, cameraPosition3, targetVideoTime.getDate(), targetVideoTime);
        
        // Annotation should be at origin of target image
        TileAnnotationPixelLocation pixelLocation2 = new TileAnnotationPixelLocation(tileAnnotation1, sourceTile, targetTile2);
        assertEquals(0, pixelLocation2.getXInPixels().doubleValue(), 1.0);
        assertEquals(0, pixelLocation2.getYInPixels().doubleValue(), 1.0);
    }
 
    /**
     * Verify that null's are returned from getXInPixels and getYInPixels if
     * the annotation is outside the target image
     */
    @Test
    public void test05() {
        
        // Source Tile is a 2m x 2m tile with center at (0m, 0m)
        final CameraPosition cameraPosition1 = new CameraPositionImpl(0, 0, 2, 0, 0, 0);
        VideoTile sourceTile = new VideoTileImpl(0, 2, 2, url, cameraPosition1, sourceVideoTime.getDate(), sourceVideoTime);
        
        // Annotation is at image origin
        final TileAnnotation tileAnnotation1 = new TileAnnotationImpl(0, 0, "A01");
        sourceTile.getAnnotations().add(tileAnnotation1);
        
        // Target Tile is a 2m X 2m tile with center at (4m, 4m)
        final CameraPosition cameraPosition2 = new CameraPositionImpl(4, 4, 2, 0, 0, 0);
        VideoTile targetTile = new VideoTileImpl(0, 2, 2, url, cameraPosition2, targetVideoTime.getDate(), targetVideoTime);
        
        // Top Left corner should be (0px, 0px)
        TileAnnotationPixelLocation pixelLocation = new TileAnnotationPixelLocation(tileAnnotation1, sourceTile, targetTile);
        assertNull(pixelLocation.getXInPixels());
        assertNull(pixelLocation.getYInPixels());
    }
    
}
