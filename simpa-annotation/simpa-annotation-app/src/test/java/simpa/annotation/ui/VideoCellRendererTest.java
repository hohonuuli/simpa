/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class VideoCellRendererTest {
    
    private static final Logger log = LoggerFactory.getLogger(VideoCellRendererTest.class);
    
    @Test
    //@Ignore
    public void testGenerateThumbnail() {
        // TODO read image
        String imageName = "/images/BrianSchlining.jpg";
        URL imageUrl = getClass().getResource(imageName);
        try {
            BufferedImage image = ImageIO.read(imageUrl);


            // TODO generate thumbnail
            BufferedImage thumbnail = VideoTileCellRenderer.generateThumbnail(image);
            // TODO write thumbnail to temp file
            File tempFile = File.createTempFile(getClass().getSimpleName(), ".png");
            log.debug("Writing thumbnail test image to " + tempFile.getAbsolutePath());
            ImageIO.write(thumbnail, "png", File.createTempFile("VideoCellRendererTest", "png"));
            
            
            // TODO verify size of thumnail
            log.info("Thumbnail is [" + thumbnail.getWidth() + ", " + thumbnail.getHeight() + "]" );
        } catch (IOException ex) {
            log.error("Failed to generate thumbnail", ex);
            Assert.fail("An Exception ocurred: " + ex.getMessage());
        }
        
        
        // TODO generate thumbnail
        
        // TODO write thumbnail to temp file
        
        // TODO verify size of thumnail
        
    }

}
