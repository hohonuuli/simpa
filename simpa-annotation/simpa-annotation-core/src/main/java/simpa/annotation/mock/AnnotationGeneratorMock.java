/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.mock;

import java.util.Date;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.VideoTime;
import simpa.annotation.TileAnnotation;
import simpa.annotation.VideoTile;
import simpa.annotation.impl.MosaicAssemblyImp;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.annotation.impl.TileAnnotationImpl;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;
import simpa.core.impl.TileImpl;

/**
 *
 * @author brian
 */
public class AnnotationGeneratorMock implements AnnotationGeneratorService {


    public TileAnnotation newTileAnnotation() {
        return new TileAnnotationImpl();
    }

    public Tile newTile() {
        return new TileImpl();
    }

    public MosaicAssembly newMosaicAssembly() {
        return new MosaicAssemblyImp();
    }

    public VideoTime newTapeTime(String timecode, Date date) {
        return new VideoTimeImpl(timecode, date);
    }

    public VideoTile newVideoTile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
