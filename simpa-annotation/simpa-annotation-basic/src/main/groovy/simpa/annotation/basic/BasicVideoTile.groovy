package simpa.annotation.basic

import simpa.annotation.VideoTile
import simpa.annotation.VideoTime

/**
 *
 *
 * @author brian
 * @since Nov 8, 2008 5:23:56 PM
 */

public class BasicVideoTile extends BasicTile implements VideoTile<BasicTileAnnotation> {

    VideoTime videoTime
    final Set<BasicTileAnnotation> annotations = new HashSet<BasicTileAnnotation>()


    public Integer getWidthInPixels() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Integer getHeightInPixels() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}