/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simpa.annotation.VideoTile;
import simpa.annotation.VideoTime;
import simpa.core.CameraPosition;
import simpa.core.impl.CameraPositionImpl;

/**
 *
 * @author brian
 */
public class VideoTileImplTest {
    
    VideoTile videoTile;
    
    @Before
    public void setup() {
        URL url = "".getClass().getResource("/images/01_10_54_12.jpg");
        VideoTime videoTime = new VideoTimeImpl("00:00:00:00", new Date());
        CameraPosition cameraPosition = new CameraPositionImpl(0, 0, 0, 0, 0, 0);
        videoTile = new VideoTileImpl(0, 2, 2, url, cameraPosition, videoTime.getDate(), videoTime);
    }
    
    /**
     * Test to verify that the image size is being read correctly by the
     * VideoTileImpl class
     */
    @Test
    public void testPixelSize() {
        BufferedImage image = null;
        Integer h0 = 0;
        Integer w0 = 0;
        try {
            image = ImageIO.read(videoTile.getUrl());
            h0 = image.getHeight();
            w0 = image.getWidth();
        }
        catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
        Integer h1 = videoTile.getHeightInPixels();
        Integer w1 = videoTile.getWidthInPixels();
        
        Assert.assertEquals(w0, w1);
        Assert.assertEquals(h0, h1);
        
    }

}
