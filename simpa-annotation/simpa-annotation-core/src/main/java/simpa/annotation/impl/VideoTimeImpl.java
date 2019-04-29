/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.impl;

import java.util.Date;
import simpa.annotation.VideoTime;
import simpa.core.GlobalParameters;

/**
 * Date bean that encapsulates a moment on a video tape. This moment is represented
 * by 2 pieces of information: Date and tape time-code.
 * 
 * @author brian
 */
public class VideoTimeImpl implements VideoTime {
    
    private final String timecode;
    private final Date date;


    public VideoTimeImpl(String timecode, Date date) {
        this.timecode = timecode;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getTimecode() {
        return timecode;
    }

    @Override
    public String toString() {
        return "<VideoTime date=\"" + GlobalParameters.DATE_FORMAT_UTC.format(date) + "\" timecode=\"" + timecode + "\" />";
    }
    
}
