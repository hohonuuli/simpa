/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import simpa.annotation.AbstractAnnotationLookupService;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.VideoTime;
import simpa.annotation.TileAnnotation;
import simpa.annotation.VideoTile;
import simpa.annotation.impl.MosaicAssemblyImp;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.annotation.impl.VideoTileImpl;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public class AnnotationLookupMock extends AbstractAnnotationLookupService {
    
    private final AnnotationGeneratorService annotationGeneratorService;

    public AnnotationLookupMock() {
        this.annotationGeneratorService = new AnnotationGeneratorMock();
    }

    @Override
    public VideoTime findTimecodeByDate(String cameraIdentifier, Date date, int millisecTolerance) {
        return new VideoTimeImpl("00:00:00:00", new Date());
    }

    public List<VideoTime> findAllTimecodesByDate(String cameraIdentifier, Date date, int millisecTolerance) {
        return new ArrayList<VideoTime>();
    }

    @Override
    public VideoTime interpolateTimecodeToDate(String cameraIdentifier, Date date, int millisecTolerance, double frameRate) {
        return new VideoTimeImpl("00:00:00:00", new Date());
    }

    public Set<TileAnnotation> findAllTileAnnotationsWithinTile(String cameraIdentifier, Date date, int millisecTolerance, Tile tile) {
        return new HashSet<TileAnnotation>();
    }

    public VideoTile convertToVideoTile(String cameraIdentifier, String sessionIdentifier, Tile tile) {
        return new VideoTileImpl(tile, annotationGeneratorService.newTapeTime("00:00:00:00", new Date()));
    }

    public MosaicAssembly<VideoTileImpl> convertToMosaicAssembly(String cameraIdentifier, String sessionIdentifier, Collection<? extends Tile> tiles) {
        MosaicAssembly<VideoTileImpl> mosaicAssembly = new MosaicAssemblyImp();
        List<VideoTileImpl> videoTiles = mosaicAssembly.getTiles();
        for (Tile tile : tiles) {
            videoTiles.add(new VideoTileImpl(tile, new VideoTimeImpl(cameraIdentifier, new Date())));
        }
        return mosaicAssembly;
    }

    public MosaicAssembly findByCameraAndSessionIdentifiers(String cameraIdentifier, String sessionIdentifier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<String> findAllConcepts() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
