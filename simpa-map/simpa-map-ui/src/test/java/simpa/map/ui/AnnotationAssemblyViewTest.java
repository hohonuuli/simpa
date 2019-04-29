package simpa.map.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import org.mbari.awt.image.ImageUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 15, 2009
 * Time: 2:48:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationAssemblyViewTest {

    public final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    @Ignore
    public void test01() {


        log.debug("Creating Swing components for AnnotationAssemblyView");
        Dimension size = new Dimension(800, 800);
        AnnotationAssemblyView view = new AnnotationAssemblyView();
        view.setSize(size);
        view.setAnnotatedAssembly(TestUtilities.loadAssembly());


        log.debug("Painting view to an image");
        BufferedImage image = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_INT_RGB);
        view.paint(image.getGraphics());

        File target = new File("target", getClass().getSimpleName() + "-test01.png");
        log.debug("Writing image to " + target.getAbsolutePath());
        try {

            ImageUtilities.saveImage(image, target);
        }
        catch (IOException e) {
            log.error("Failed to create " + target.getAbsolutePath());
            Assert.fail("Failed to write image");
        }

    }
}
