package simpa.map.ui;

import simpa.map.core.AnnotatedAssembly;
import simpa.core.Tile;
import simpa.core.CameraPosition;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import org.mbari.geometry.Point2D;
import org.mbari.gis.util.GISUtilities;

public class AnnotationAssemblyView extends JPanel {

    private static final double ANGLE_OFFSET_GEO2VIEW = Math.toRadians(90);
	
    private AnnotatedAssembly annotatedAssembly;
    
	/**
	 * Create the panel
	 */
	public AnnotationAssemblyView() {
		super();
		//
	}

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);    //To change body of overridden methods use File | Settings | File Templates.

        if (annotatedAssembly == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        RenderingHints newHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(newHints);

        // Get bounds of view
        Point2D<Double> min = annotatedAssembly.getMin();
        Point2D<Double> max = annotatedAssembly.getMax();
        double widthInMeters = max.getX() - min.getX();
        double heightInMeters = max.getY() - min.getY();

        Dimension d = getSize();
        double widthInPixels = d.getWidth();
        double heightInPixels = d.getHeight();

        // Draw a rectangle for each tile in the assembly
        for (Tile tile : annotatedAssembly.getTiles()) {

            // Calculate scaling. offset and rotations
            CameraPosition cameraPosition = tile.getCameraPosition();
            double rotation = GISUtilities.geoToMath(cameraPosition.getHeading()) - ANGLE_OFFSET_GEO2VIEW;
            int width = (int) Math.round(tile.getWidthInMeters() / widthInMeters * widthInPixels);
            int height = (int) Math.round(tile.getHeightInMeters() / heightInMeters * heightInPixels);


            double tx = (cameraPosition.getX() - min.getX() + (tile.getWidthInMeters() / 2)) / widthInMeters * widthInPixels;
            double ty = (max.getY() - cameraPosition.getY() - (tile.getHeightInMeters() / 2)) / heightInMeters * heightInPixels;

            Shape shape = new Rectangle(width, height);
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(tx, ty);
            affineTransform.rotate(rotation);
            g2d.setTransform(affineTransform);
            g2d.draw(shape);

        }

    }
    

    public AnnotatedAssembly getAnnotatedAssembly() {
        return annotatedAssembly;
    }

    public void setAnnotatedAssembly(AnnotatedAssembly annotatedAssembly) {
        this.annotatedAssembly = annotatedAssembly;
        repaint();
    }
}
