package simpa.annotation.basic

import simpa.annotation.AnnotationGeneratorService
import simpa.annotation.TileAnnotation
import simpa.core.Tile
import simpa.core.MosaicAssembly
import simpa.annotation.VideoTime
import simpa.annotation.VideoTile

/**
 *
 *
 * @author brian
 * @since Nov 8, 2008 5:21:39 PM
 */

public class BasicAnnotationGeneratorService implements AnnotationGeneratorService {

    public TileAnnotation newTileAnnotation() {
        return new BasicTileAnnotation(date: new Date())
    }

    public Tile newTile() {
        return new BasicTile()
    }

    public MosaicAssembly newMosaicAssembly() {
        return new BasicMosaicAssembly()
    }

    public VideoTime newTapeTime(String timecode, Date date) {
        return new BasicVideoTime(timecode: timecode, date: date)
    }

    public VideoTile newVideoTile() {
        return new BasicVideoTile()
    }

}