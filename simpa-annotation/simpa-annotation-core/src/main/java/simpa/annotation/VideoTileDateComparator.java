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
public class VideoTileDateComparator implements Comparator<VideoTile> {

    public int compare(VideoTile o1, VideoTile o2) {
        return o1.getDate().compareTo(o2.getDate());
    }

}
