/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import com.google.inject.Inject;
import java.util.Date;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.VideoTime;
import simpa.annotation.TileAnnotation;
import simpa.annotation.VideoTile;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;
import vars.annotation.AnnotationFactory;

/**
 *
 * @author brian
 */
public class VARSAnnotationGeneratorService implements AnnotationGeneratorService {


    public static final String CONCEPTNAME_DEFAULT = "Object";
    public static final String CONCEPTNAME_TILE = "Mosaic Tile Center";
    public static final String CONCEPTNAME_SELF = "self";
    public static final String LINKNAME_CAMERA_XYZ = "SIMPA: Relative camera position [X Y Z] in meters";
    public static final String LINKNAME_CAMERA_PRH = "SIMPA: Camera [pitch roll heading] in radians";
    public static final String LINKNAME_IMAGE_INFO = "SIMPA: Image size [width height] in meters";
    public static final String LINKNAME_PIXEL_COORDINATES_XY = "SIMPA: Pixel coordinates [X Y]";
    public static final String LINKNAME_TILEINDEX = "SIMPA: Tile index";

    public static final double FRAMERATE_NTSC = 29.97;

    private final AnnotationFactory annotationFactory;

    @Inject
    public VARSAnnotationGeneratorService(AnnotationFactory annotationFactory) {
        this.annotationFactory = annotationFactory;
    }

    public TileAnnotation newTileAnnotation() {
        return new VARSTileAnnotation(0, 0, CONCEPTNAME_DEFAULT, annotationFactory);
    }

    public Tile newTile() {
        return new VARSTile(annotationFactory.newVideoFrame());
    }

    public MosaicAssembly newMosaicAssembly() {
        return new VARSMosaicAssembly(annotationFactory.newVideoArchive(), annotationFactory);
    }

    public VideoTile newVideoTile() {
        return new VARSVideoTile(annotationFactory.newVideoFrame(), annotationFactory);
    }

    public VideoTime newTapeTime(String timecode, Date date) {
        return new VideoTimeImpl(timecode, date);
    }

}
