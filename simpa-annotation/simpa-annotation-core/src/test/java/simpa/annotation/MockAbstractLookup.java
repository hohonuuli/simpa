/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.mbari.movie.Timecode;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public class MockAbstractLookup extends AbstractAnnotationLookupService {
    
    private final int size;

    /**
     * Constructor
     * @param size Specifies the number of items that should be returned by 
     *      methods in the mock that return lists or arrays
     */
    public MockAbstractLookup(int size) {
        this.size = size;
    }
    


    public List<VideoTime> findAllTimecodesByDate(String platform, Date date, int millisecTolerance) {
        List<VideoTime> tapeTimes = new ArrayList<VideoTime>(size);
        Timecode initTimecode = new Timecode("01:00:00:00", 29.97);
        if (size == 1) {
            tapeTimes.add(new VideoTimeImpl(initTimecode.toString(), date));
        }
        else {
            int start = -(size / 2);
            int end = size + start - 1;
            for (int i = start; i <= end; i++) {

                Date d = new Date(date.getTime() + 1000 * i);
                String timecode = (new Timecode(initTimecode.getFrames() + 37 * i)).toString();

                tapeTimes.add(new VideoTimeImpl(timecode, d));

            }
        }
        
        return tapeTimes;
    }

    public VideoTile convertToVideoTile(String cameraIdentifier, String sessionIdentifier, Tile tile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MosaicAssembly convertToMosaicAssembly(String cameraIdentifier, String sessionIdentifier, Collection<? extends Tile> tiles) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MosaicAssembly findByCameraAndSessionIdentifiers(String cameraIdentifier, String sessionIdentifier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<TileAnnotation> findAllTileAnnotationsWithinTile(String cameraIdentifier, Date date, int millisecTolerance, Tile tile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<String> findAllConcepts() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
