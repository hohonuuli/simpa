/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;

/**
 * Lookup service for quering the VARS (Castor-based) database. Also provides
 * annotation related lookup to the EXPD database.
 * @author brian
 */
public interface AnnotationLookupService {
    
    MosaicAssembly findByCameraAndSessionIdentifiers(String cameraIdentifier, String sessionIdentifier);
    
    /**
     * Find the timecode nearest to the given date for a cameraIdentifier. The timecode
     * nearest to the date within the given tlerance will be returned. If no
     * timecode is found within +/- millisecTolerance, then <b>null</b> will
     * be returned.
     * 
     * @param cameraIdentifier The name of the camera cameraIdentifier (e.g. 'Ventana' or 'Tiburon')
     * @param date The date to find the timecode for
     * @param millisecTolerance Match tolerance. The nearest timecode value within
     *      the tolerance to the date will be selected
     * @return A String formated like 'hh:mm:ss:ff' (e.g. 01:23:45:12). <b>null</b>
     *      if no match was found
     * @deprecated
     */
    VideoTime findTimecodeByDate(String cameraIdentifier, Date date, int millisecTolerance);
    
    /**
     * 
     * @param cameraIdentifier
     * @param date
     * @param millisecTolerance
     * @return
     * @deprecated
     */
    List<? extends VideoTime> findAllTimecodesByDate(String cameraIdentifier, Date date, int millisecTolerance);
    
    /**
     * Interpolate existing timecodes to the the current date.
     * 
     * @param cameraIdentifier The name of the camera cameraIdentifier (e.g. Ventana, Tiburon)
     * @param date The date to interpolate to
     * @param millisecTolerance Used to lookup the nearest values from the data store. 
     *      Only values within +/- this tolerance will be used in the interpolation
     * @param frameRate Frame-rate (frames per second) used to convert tape 
     *      time-codes to frames. For most video at MBARI it's 29.97 fps.
     * @return A TapeTime where the date is the same as the argument supplied,
     *      the timecode is interpolated from nearby values.
     * @deprecated
     */
    VideoTime interpolateTimecodeToDate(String cameraIdentifier, Date date, int millisecTolerance, double frameRate);
    
    
    /**
     * 
     * @param cameraIdentifier
     * @param sessionIdentifier
     * @param tiles
     * @return
     * @deprecated
     */
    MosaicAssembly convertToMosaicAssembly(String cameraIdentifier, String sessionIdentifier, Collection<? extends Tile> tiles);

    /**
     * Find all concepts that can be used for annotations
     * @return
     */
    Collection<String> findAllConcepts();
    
}
