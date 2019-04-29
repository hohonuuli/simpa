/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.util.Comparator;

/**
 *
 * @author brian
 */
public class VideoTileTimecodeComparator implements Comparator<VideoTile> {

    public int compare(VideoTile o1, VideoTile o2) {
        return o1.getVideoTime().getTimecode().compareTo(o2.getVideoTime().getTimecode());
    }

}
