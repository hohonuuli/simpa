package simpa.annotation.basic

import simpa.annotation.AbstractAnnotationLookupService
import simpa.core.MosaicAssembly
import simpa.annotation.VideoTime
import simpa.core.Tile
import simpa.annotation.TileAnnotation
import simpa.annotation.VideoTile

/**
 *
 * @author brian
 */
class BasicAnnotationLookupService extends AbstractAnnotationLookupService  {

    
    public MosaicAssembly findByCameraAndSessionIdentifiers(String cameraIdentifier, String sessionIdentifier) {
        throw new UnsupportedOperationException("Not implemented yet")
    }

    public List<? extends VideoTime> findAllTimecodesByDate(String cameraIdentifier, Date date, int millisecTolerance) {
        throw new UnsupportedOperationException("Not implemented yet")
    }

    public Set<TileAnnotation> findAllTileAnnotationsWithinTile(String cameraIdentifier, Date date, int millisecTolerance, Tile tile) {
        throw new UnsupportedOperationException("Not implemented yet")
    }

    public MosaicAssembly convertToMosaicAssembly(String cameraIdentifier, String sessionIdentifier, Collection<? extends Tile> tiles) {

        throw new UnsupportedOperationException("Not implemented yet")
    }

    public Collection<String> findAllConcepts() {
        throw new UnsupportedOperationException("Not implemented yet")
    }

}

