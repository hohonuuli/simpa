/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.util.Set;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public interface VideoTile<T extends TileAnnotation> extends Tile {
    
    void setVideoTime(VideoTime tapeTime);
    
    VideoTime getVideoTime();
    
    Set<T> getAnnotations();
    
    /**
     * @return The width in the referenced tile image in pixels. <b>null</b> if 
     * unable to read the image from the URL
     */
    Integer getWidthInPixels();
    
    /**
     * @return The height in the referenced tile image in pixels. <b>null</b> if 
     * unable to read the image from the URL
     */
    Integer getHeightInPixels();
    
}
