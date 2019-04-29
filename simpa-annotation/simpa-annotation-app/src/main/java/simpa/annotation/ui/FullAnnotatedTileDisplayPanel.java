/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpa.annotation.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.AnnotationLookupService;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.VideoTile;
import simpa.core.CameraPosition;

/**
 * A decorator over the {@link AnnotatedTileDisplayPanel}. This class can
 * draw the position of the center of the tile as well as bars for width, height
 * and direction.
 * @author brian
 */
public class FullAnnotatedTileDisplayPanel extends AnnotatedTileDisplayPanel {

    private final Logger log = LoggerFactory.getLogger(FullAnnotatedTileDisplayPanel.class);

    private final StringBuilder locationString = new StringBuilder();


    public FullAnnotatedTileDisplayPanel(AnnotationGeneratorService annotationGeneratorService,
            AnnotationLookupService annotationLookupService,
            AnnotationPersistenceService annotationUpdateService) {
        super(annotationGeneratorService, annotationLookupService, annotationUpdateService);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (isShowing() && getVideoTile() != null) {
            Graphics2D g2 = (Graphics2D) g;


            try {
                // Draw center marker
                drawCenter(g2);

                // Draw bars and labels showing width and height
                drawCrossBars(g2);
            }
            catch (Exception e) {
                log.warn("An exception occurred while drawing center and crossbars", e);
            }

            // TODO draw arrow showing relative heading.
        }
    }

    protected void drawCenter(final Graphics2D g2) {
        // Configure our paint brush
        g2.setPaintMode(); // Make sure XOR is turned off
        g2.setStroke(new BasicStroke(3));
        g2.setPaint(Color.GRAY);
        g2.setFont(selectionFont);

        // Extract the goodies we need
        final VideoTile videoTile = getVideoTile();
        final CameraPosition cameraPosition = videoTile.getCameraPosition();
        final double x = cameraPosition.getX();
        final double y = cameraPosition.getY();
        final double width = videoTile.getWidthInPixels();
        final double height = videoTile.getHeightInPixels();

        // Position to draw the center
        int u = (int) Math.round(width / 2);
        int v = (int) Math.round(height / 2);
        final int diameter = 10;
        g2.drawOval(u - diameter / 2, v - diameter / 2, diameter, diameter);

        // Draw the position in meters near the center
        float uf = u + 5;
        float vf = v;
        locationString.delete(0, locationString.length()); // Clear the stringbuilder
        locationString.append("(");
        locationString.append(numberFormat.format(x));
        locationString.append(", ");
        locationString.append(numberFormat.format(y));
        locationString.append(")");
        g2.drawString(locationString.toString(), uf, vf);

    }

    protected void drawCrossBars(final Graphics2D g2) {
        // Configure our paint brush
        g2.setPaintMode(); // Make sure XOR is turned off
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{2, 2}, 4));
        g2.setPaint(Color.GRAY);
        g2.setFont(selectionFont);
        
        final VideoTile videoTile = getVideoTile();
        final float width = videoTile.getWidthInPixels();
        final float height = videoTile.getHeightInPixels();
        // Position to draw the center
        float u = (float) Math.round(width / 2);
        float v = (float) Math.round(height / 2);

        /* 
         * Create new crossHair. Only draw portions that are over the image!
         */
        GeneralPath crossbars = new GeneralPath();
        crossbars.moveTo(0, v);
        crossbars.lineTo(width, v);
        crossbars.moveTo(u, 0);
        crossbars.lineTo(u, height);
        g2.draw(crossbars);

        // Label crossbar
        int offset = 5;
        g2.drawString(numberFormat.format(videoTile.getWidthInMeters()) + " meters", offset, v);
        //AffineTransform oldTransform = g2.getTransform();
        //g2.setTransform(AffineTransform.getRotateInstance(Math.PI / 2)); // rotate by 90 degrees
        g2.drawString(numberFormat.format(videoTile.getHeightInMeters()) + " meters", u, height - offset);
        //g2.setTransform(oldTransform); // Restore original transform
    }
}
