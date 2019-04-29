/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import org.mbari.gis.util.GISUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.core.CameraPosition;

/**
 * The position of an {@link TileAnnotation} from a source {@link VideoTile}
 * in a target {@link VideoTile}. The position is calculated base on the size
 * of the image in the target tile and is immutable after creation. If you 
 * change the image URL, or other information in either the source or target 
 * tile you should toss the pixelLocation and create a new one since the 
 * position will no longer be correct.
 * 
 * @author brian
 */
public class TileAnnotationPixelLocation {
    
    private final TileAnnotationSpatialLocation spatialLocation;
    private final VideoTile videoTile;
    private Integer xInPixels;
    private Integer yInPixels;
    private static final double TOLERANCE_METERS = 0.001; 
    private static final Logger log = LoggerFactory.getLogger(TileAnnotationPixelLocation.class);

    /**
     * Constructor
     * 
     * @param targetTile the {@link VideoTile} that we want to find the 
     *      annotation's position relative to
     * @param spatialLocation A pre-existing spatialLocation 
     */
    public TileAnnotationPixelLocation(VideoTile targetTile, TileAnnotationSpatialLocation spatialLocation) {
        this.spatialLocation = spatialLocation;
        this.videoTile = targetTile;
        process();
    }

    /**
     * Constructor 
     * 
     * @param tileAnnotation The tileAnnotation within the <i>sourceTile</i>. This
     *      class will attempt to calculate it's pixel position within the
     *      <i>targetTile</i>
     * @param sourceTile The tile that contains the <i>tileAnnotation</i>
     * @param targetTile The tile that we want to find the position of the
     *      <i>tileAnnotation</i> within.
     */
    public TileAnnotationPixelLocation(TileAnnotation tileAnnotation, VideoTile sourceTile, VideoTile targetTile ) {
        this(targetTile, new TileAnnotationSpatialLocation(tileAnnotation, sourceTile));
    }

    /**
     * @return The spatial location of the tile relative to the mosaic origin.
     */
    public TileAnnotationSpatialLocation getSpatialLocation() {
        return spatialLocation;
    }

    /**
     * @return The VideoTile that we want to calculate the position of the 
     *      annotation relative to.
     */
    public VideoTile getTargetTile() {
        return videoTile;
    }
    
    /**
     * @return The VideoTile that contains the annotation of interest
     */
    public VideoTile getSourceTile() {
        return spatialLocation.getVideoTile();
    }
    
    /**
     * @return The {@link TileAnnotation} of interest
     */
    public TileAnnotation getTileAnnotation() {
        return spatialLocation.getTileAnnotation();
    }

    /**
     * @return X position of the annotation within the <i>targetTile</i> in pixels.
     *      <b>null</b> is returned if the <i>tileAnnotation</i> is not within
     *      the <i>targetTile</i>
     */
    public Integer getXInPixels() {
        return xInPixels;
    }

    /**
     * @return Y position of the annotation within the <i>targetTile</i> in pixels.
     *      <b>null</b> is returned if the <i>tileAnnotation</i> is not within
     *      the <i>targetTile</i>
     */
    public Integer getYInPixels() {
        return yInPixels;
    }
    
    private strictfp void process() {
  
        
        /* *********************************************************************
         * See if annotation is within the targe tile. If so convert the 
         * meters absolute position into the target image's pixel coordinates.
         ******************************************************************** */
        final CameraPosition cameraPosition = videoTile.getCameraPosition();
        final Integer m = videoTile.getWidthInPixels();
        final Integer n = videoTile.getHeightInPixels();
        if (m == null || n == null) {
            /*
             * This would occur if we were unable to read the image. For example
             * if it was hosted on a remote server and the server was down
             */
            log.debug("Missing the size of a dimension (in pixels) for " + videoTile);
        }
        else {
            final double w = videoTile.getWidthInMeters();
            final double h = videoTile.getHeightInMeters();
            final double x = cameraPosition.getX();
            final double y = cameraPosition.getY();
            //final double theta = GisUtil.geoToMath(cameraPosition.getHeading());

            final double dx1 = spatialLocation.getXInMeters() - x;  // dx of annotation from target tile center in meters
            final double dy1 = spatialLocation.getYInMeters() - y;  // dy of annotation from target tile center in meters
            final double r = Math.sqrt((dx1 * dx1) + (dy1 * dy1));  // Meters from annotation to center of target image
            final double angle = Math.atan2(dy1, dx1);              // Math Angle to annotation relative to intial heading
            final double phi = angle + cameraPosition.getHeading(); // Math Angle to annotation relative to image axis
            final double dx2 = r * Math.cos(phi);           // dx of annotation from target tile center in meters oriented along image axis
            final double dy2 = r * Math.sin(phi);           // dy of annotation from target tile center in meters oriented along image axis

            final double wHalf = w / 2D;
            final double hHalf = h / 2D;
            if (dx2 < (-wHalf - TOLERANCE_METERS) || 
                    dx2 > (wHalf + TOLERANCE_METERS) ||
                    dy2 < (-hHalf - TOLERANCE_METERS) ||
                    dy2 > (hHalf + TOLERANCE_METERS)) {
                // Do nothing. Annotation is outside the bounds of the targetTile
            }
            else {
                final double xPrime = wHalf + dx2;
                final double yPrime = hHalf - dy2;                  // Flip y direction since +y pixel is downwards
                xInPixels = new Integer((int) Math.round(xPrime * (m / w)));
                yInPixels = new Integer((int) Math.round(yPrime * (n / h)));
            }

            if (log.isDebugEnabled()) {
                log.debug("PixelLocation: " + this + "\n\t" +
                        "Annotation: \n\t" +
                        "\tLocation in target tile:                               [" + xInPixels + ", " + yInPixels + "]\n\t" + 
                        "\tDistance from target tile center to annotation:        [" + dx1 + "m, " + dy1 + "m]\n\t" +
                        "\tMath angle from target tile center to annotation:       " + Math.toDegrees(angle) +  "\n\t" +
                        "\tgeographic angle from target tile center to annotation: " + Math.toDegrees(GISUtilities.mathToGeo(angle)) + "\n\t" +
                        "\tgeo-frame angle from target tile center to annotation:  " + Math.toDegrees(GISUtilities.mathToGeo(phi)) + "\n\t");
            }
        }

    }

}
