/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mbari.movie.Timecode;
import org.mbari.util.MathUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.core.CameraPosition;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public abstract class AbstractAnnotationLookupService implements AnnotationLookupService {
    
    private final Logger log = LoggerFactory.getLogger(AnnotationLookupService.class);

    
    /**
     * Retrive the HD timecode nearest to the given date for a platform
     * @param platform
     * @param date
     * @param millisecTolerance
     * @return The HD Timcode closest to the Date. <b>null</b> is returned if no
     *      timcode is found within the tolerance bounds
     */
    public VideoTime findTimecodeByDate(String platform, Date date, int millisecTolerance) {
        
        List<? extends VideoTime> tapeTimes = findAllTimecodesByDate(platform, date, millisecTolerance);
        
        /*
         * Find the nearest time to our date
         */
        Date nearestDate = null;
        long dtMin = Long.MAX_VALUE;
        for (VideoTime tapeTime: tapeTimes) {
            Date d = tapeTime.getDate();
            long dt = Math.abs(d.getTime() - date.getTime());
            if (dt < dtMin) {
                dtMin = dt;
                nearestDate = d;
            }
        }
        
        VideoTime nearestTapeTime = null;
        for (VideoTime tapeTime : tapeTimes) {
            if (tapeTime.getDate().equals(nearestDate)) {
                nearestTapeTime = tapeTime;
                break;
            }
        }
        
        return nearestTapeTime;
    }
    
    /**
     * Interpolates a given date to a timecode from data stored in the 
     * datastore. 
     * 
     * @param cameraIdentifier The cameraIdentifier (e.g. Ventana or Tiburon)
     * @param date The date of that we're interested in
     * @param millisecTolerance Specifies the widthInMeters of the time window to pull samples
     *  from the database
     * @param frameRate The Frame rate to use. At MBARI, we use NTSC (29.97 fps)
     * @return The interpolated tape time value. <b>null</b> is returned if no
     *      data was retrieved to interpolate to.
     */
    public VideoTime interpolateTimecodeToDate(String cameraIdentifier, Date date, int millisecTolerance, double frameRate) {
        
        /*
         * Retrive the tapetimes and sort by date
         */
        List<? extends VideoTime> videoTimes = findAllTimecodesByDate(cameraIdentifier, date, millisecTolerance);
        
        VideoTime returnTapeTime = null;
        if (videoTimes.size() > 1) {
            Collections.sort(videoTimes, new VideoTimeDateComparator());

            /*
             * Extract the dates and timecodes (as frames) for interpolation
             */
            BigDecimal[] dates = new BigDecimal[videoTimes.size()];
            BigDecimal[] frames = new BigDecimal[videoTimes.size()];
            for (int i = 0; i < videoTimes.size(); i++) {
                VideoTime tapeTime = videoTimes.get(i);
                dates[i] = new BigDecimal(tapeTime.getDate().getTime());
                Timecode timecode = new Timecode(tapeTime.getTimecode(), frameRate);
                frames[i] = new BigDecimal(timecode.getFrames());
            }      

            /*
             * Interpolate to the new frame
             */
            BigDecimal[] iFrame = MathUtilities.interpLinear(dates, frames, new BigDecimal[] {new BigDecimal(date.getTime())});
            if (iFrame != null && iFrame.length > 0 && iFrame[0] != null) {
                Timecode timecode = new Timecode(iFrame[0].doubleValue(), frameRate);
                returnTapeTime = new VideoTimeImpl(timecode.toString(), date);
                if (log.isDebugEnabled()) {
                    log.debug("Interpolated timecode at " + date + " is " + timecode +
                        " using frameRate of " + frameRate + "[fps]");
                }
            }
        }
        return returnTapeTime;
        
    }
    
}
