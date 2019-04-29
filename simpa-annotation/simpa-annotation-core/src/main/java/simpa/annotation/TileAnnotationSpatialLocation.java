/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import org.mbari.gis.util.GISUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.core.CameraPosition;
import simpa.core.CoreException;

/**
 * The position of an {@link TileAnnotation} in a {@link VideoTile}
 * relative to the origin and orientation of the origin tile.
 * 
 * @author brian
 */
public class TileAnnotationSpatialLocation {

    final TileAnnotation tileAnnotation;
    final VideoTile videoTile;
    private Double xInMeters;
    private Double yInMeters;
    private static final Logger log = LoggerFactory.getLogger(TileAnnotationSpatialLocation.class);

    public TileAnnotationSpatialLocation(TileAnnotation tileAnnotation, VideoTile videoTile) {
        this.tileAnnotation = tileAnnotation;
        this.videoTile = videoTile;
        if (!videoTile.getAnnotations().contains(tileAnnotation)) {
            throw new IllegalArgumentException(videoTile + " does not actually contain " + 
                    tileAnnotation + ". Check your arguments. The 'tileAnnotation' should be " +
                    "added to the 'sourceTile'");
        }
        process();
    }
    
    /**
     * @return The VideoTile that contains the annotation of interest
     */
    public VideoTile getVideoTile() {
        return videoTile;
    }


    /**
     * @return The TileAnnotation whose position we are calculating
     */
    public TileAnnotation getTileAnnotation() {
        return tileAnnotation;
    }

    /**
     * @return X position of the annotation relative to the origin (i.e. 0, 0)
     *      in meters
     */
    public Double getXInMeters() {
        return xInMeters;
    }



    /**
     * @return Y position of the annotation relative to the origin (i.e. 0, 0)
     *      in meters
     */
    public Double getYInMeters() {
        return yInMeters;
    }

    
    private strictfp void process() {
        // Calculate position of tileAnnotation in sourceTile relative to targetTile
        final CameraPosition cameraPosition = videoTile.getCameraPosition();
        
        /* *********************************************************************
         * Calculate the position (in meters) of the annotation relative
         * to the center of the origin tile.
         * 
         * ** Pixel Coordinates. Origin is top left. 
         * 
         * m = width in pixels
         * n = height in pixels
         * u = pixel position along u axis (+Right/-Left) origin top left
         * v = pixel position along v axis (-Up/+Down) origin top left
         * 
         * ** Spatial coordinates. Origin is image center
         * 
         * w = width in meters 
         * h = height in meters
         * x = east-west position in meters (+E/-W) relative to center of origin tile
         * y = north-south position in meters (+N/-S) relative to center of origin tile
         * r = radius in meters from center to any corner
         * theta = relative heading in radians
         * 
         ******************************************************************** */
        
        // Pixel coordinates
        final Integer m = videoTile.getWidthInPixels();     // Number of pixel columns in image
        final Integer n = videoTile.getHeightInPixels();    // Number of pixel rows in image
        if (m == null || n == null) {
            /*
             * This would occur if we were unable to read the image. For example
             * if it was hosted on a remote server and the server was down
             */
            throw new CoreException("Missing the size of a dimension (in pixels) for " + videoTile);
        }
        final int u = tileAnnotation.getX();                // X position of annotation within image in pixels
        final int v = tileAnnotation.getY();                // Y position of annotation within image in pixels

        // Spatial coordinates
        final double w = videoTile.getWidthInMeters();      // Width of image in meters
        final double h = videoTile.getHeightInMeters();     // Height of image in meters
        final double x = cameraPosition.getX();             // X Center of image in meters relative to origin tile
        final double y = cameraPosition.getY();             // Y Center of image in meters relative to origin tile
        
        final double m0 = m / 2D;                           // X pixel at center of image
        final double n0 = n / 2D;                           // Y pixel at center of image
        
        final double du0 = u - m0;                          // du of annotation relative to center in pixels
        final double dv0 = n0 - v;                          // dv of annotation relative to center in pixels (flip sign too)
        
        final double dx1 = du0 * w / m;                     // X meters from annotation to center of source image
        final double dy1 = dv0 * h / n;                     // Y meters from annotation to center of source image.
        
        final double phi = Math.atan2(dy1, dx1);            // Angle from center to annotation relative to image 'top' 
        final double theta = phi - cameraPosition.getHeading(); // Math angle, in radians, from center of tile, relative to initial heading of origin tile to annotation
        final double r = Math.sqrt((dx1 * dx1) + (dy1 * dy1));  // Meters from annotation to center of source image
        
        xInMeters = x + r * Math.cos(theta);                // X meters from annotation to origin tile center
        yInMeters = y + r * Math.sin(theta);                // Y meters from annotation to origin tile center
        
        if (log.isDebugEnabled()) {
            log.debug("SpatialLocation: " + this + "\n\t" +
                    "Image:\n\t" +
                    "\tDimensions:                                [" + m + "px x " + n + "px]\n\t" +
                    "\tSize:                                      [" + w +  "m x " + h + "m]\n\t" + 
                    "\tDistance from center to origin:            [" + x + "m, " + y + "m]\n\t" +
                    "\tHeading:                                    " + Math.toDegrees(cameraPosition.getHeading()) + " degrees\n\t" +
                    "Annotation:\n\t" +
                    "\tPixel:                                     [" + u + ", " + v + "]\n\t" + 
                    "\tDistance from center to annotation:        [" + dx1 + "m, " + dy1 + "m]\n\t" + 
                    "\tDistance from origin to annotation:        [" + xInMeters + "m, " + yInMeters + "m]\n\t" + 
                    "\tDistance from center to annotation:         " + r + "m\n\t" +
                    "\tMath angle from center to annotation:       " + Math.toDegrees(theta) +  "\n\t" +
                    "\tgeographic angle from center to annotation: " + Math.toDegrees(GISUtilities.mathToGeo(theta)) + "\n\t");
        }
    }
}
