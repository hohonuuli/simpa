/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;


import java.util.Comparator;

/**
 * Compares TapeTimes based on Date
 * 
 * @author brian
 */
public class VideoTimeDateComparator implements Comparator<VideoTime> {

    public int compare(VideoTime t1, VideoTime t2) {
        return t1.getDate().compareTo(t2.getDate());
    }

}
