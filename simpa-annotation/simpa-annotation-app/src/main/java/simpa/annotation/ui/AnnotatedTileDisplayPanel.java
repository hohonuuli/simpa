/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpa.annotation.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.AnnotationLookupService;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.TileAnnotation;
import simpa.annotation.TileAnnotationPixelLocation;
import simpa.annotation.VideoTile;
import simpa.core.MosaicAssembly;

/**
 * Like it's parent this panel draws annotations for a tile. In addition, this
 * panel also draws all annotations from the {@link MosaicAssembly} that occur
 * within the tile, even if the annotation doesn't belong to the {@link VideoTile}.
 * 
 * @author brian
 */
public class AnnotatedTileDisplayPanel extends VideoTileDisplayPanel {

    /**
     * Formatter for the position (in meters)
     */
    //protected final NumberFormat numberFormat;
    private boolean showing = true;
    private MosaicAssembly<VideoTile> mosaicAssembly;
    /** 
     * A List of annotations within the {@link MosaicAssembly} that are NOT
     * in the current tile but that are located within the bounds of the 
     * current tile 
     **/
    private List<TileAnnotationPixelLocation> pixelLocations = Collections.synchronizedList(new ArrayList<TileAnnotationPixelLocation>());

    public AnnotatedTileDisplayPanel(AnnotationGeneratorService annotationGeneratorService, AnnotationLookupService annotationLookupService, AnnotationPersistenceService annotationUpdateService) {
        super(annotationGeneratorService, annotationLookupService, annotationUpdateService);
        //numberFormat = NumberFormat.getNumberInstance();
        //numberFormat.setMaximumFractionDigits(1);
        //numberFormat.setMinimumFractionDigits(0);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        
        // Draw the selected annotations
        if (showing) {


            final Graphics2D g2 = (Graphics2D) g;

            // Draw the tiles
            g2.setPaintMode(); // Make sure XOR is turned off

            g2.setStroke(new BasicStroke(3));
            g2.setPaint(Color.GRAY);
            g2.setFont(selectionFont);
            final int armLength = crossHairArmLength; // Cross hair arm length

            final StringBuilder locationString = new StringBuilder();
            for (TileAnnotationPixelLocation pixelLocation : pixelLocations) {

                final TileAnnotation tileAnnotation = pixelLocation.getTileAnnotation();
                final int x = pixelLocation.getXInPixels();
                final int y = pixelLocation.getYInPixels();

                // Draw name of concept for selected annotations
                final String conceptName = tileAnnotation.getConceptName();
                if (conceptName != null) {

                    // Position to craw the concept name
                    float xf = x + 5;
                    float yf = y;

                    // Draw the concept name
                    g2.drawString(conceptName, xf, yf);

                    // Draw the position in meters below the concept name
                    FontRenderContext fontRenderContext = g2.getFontRenderContext();
                    LineMetrics lineMetrics = g2.getFont().getLineMetrics(conceptName, fontRenderContext);
                    yf = yf + lineMetrics.getHeight() + 2;             // Position below the concept name
                    locationString.delete(0, locationString.length()); // Clear the stringbuilder
                    locationString.append("(");
                    locationString.append(numberFormat.format(pixelLocation.getSpatialLocation().getXInMeters()));
                    locationString.append(", ");
                    locationString.append(numberFormat.format(pixelLocation.getSpatialLocation().getYInMeters()));
                    locationString.append(")");
                    g2.drawString(locationString.toString(), xf, yf);
                }

                // Draw the crosshair
                GeneralPath gp = new GeneralPath();
                gp.moveTo(x - armLength, y - armLength);
                gp.lineTo(x + armLength, y + armLength);
                gp.moveTo(x + armLength, y - armLength);
                gp.lineTo(x - armLength, y + armLength);
                g2.draw(gp);
            }

        }

    }

    @Override
    public void setVideoTile(final VideoTile targetTile) {
        super.setVideoTile(targetTile);

        /*
         * setVideoTile might be invoked by non-EDT thread. Make sure we redraw 
         * on the EDT.
         */
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (mosaicAssembly != null) {

                    pixelLocations.clear();

                    if (targetTile != null) {

                        List<VideoTile> tiles = new ArrayList<VideoTile>(mosaicAssembly.getTiles()); // Create a copy of the list
                        tiles.remove(targetTile); // Remove our currently selected tile

                        /*
                         * Iterate over ALL annotations in the mosiac and see if they fall 
                         * within the bounds of the targetTile.
                         */
                        for (VideoTile sourceTile : tiles) {
                            Integer height = sourceTile.getHeightInPixels(); // Null is returned if the image can't be read

                            if (height != null) {
                                Set<TileAnnotation> tileAnnotations = sourceTile.getAnnotations();
                                for (TileAnnotation tileAnnotation : tileAnnotations) {
                                    TileAnnotationPixelLocation pixelLocation = new TileAnnotationPixelLocation(tileAnnotation, sourceTile, targetTile);
                                    if (pixelLocation.getXInPixels() != null) {  // null is returned if NOT within target
                                        pixelLocations.add(pixelLocation);
                                    }
                                }
                            }
                        }
                    }

                }

                AnnotatedTileDisplayPanel.this.repaint();
            }
        });

    }

    public MosaicAssembly<VideoTile> getMosaicAssembly() {
        return mosaicAssembly;
    }

    public void setMosaicAssembly(MosaicAssembly<VideoTile> mosaicAssembly) {
        this.mosaicAssembly = mosaicAssembly;
    }

    /**
     * Tells this UI widget whether or not to draw any annotations that fall
     * within the image
     * @param showing true says show anot
     */
    public void setShowing(boolean showing) {
        if (showing!= this.showing) {
            this.showing = showing;
            repaint();
        }
    }

    @Override
    public boolean isShowing() {
        return showing;
    }
}
