/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import java.util.Comparator;
import vars.annotation.VideoFrame;

/**
 * Compare VideoFrames by timecode
 * @author brian
 */
public class VideoFrameTimecodeComparator implements Comparator<VideoFrame> {

    public int compare(VideoFrame o1, VideoFrame o2) {
        return o1.getTimecode().compareTo(o2.getTimecode());
    }

}
