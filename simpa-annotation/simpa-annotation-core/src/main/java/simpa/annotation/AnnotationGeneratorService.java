/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.util.Date;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;

/**
 * Generates annotations base on various properties
 * @author brian
 */
public interface AnnotationGeneratorService {
    
//    public static final String CONCEPTNAME_TILE = "Seafloor TileImpl Center";
//    public static final String CONCEPTNAME_SELF = "self";
//    public static final String LINKNAME_CAMERA_XYZ = "SIMPA: Relative camera position [X Y Z] in meters";
//    public static final String LINKNAME_CAMERA_PRH = "SIMPA: Camera orientation [pitch roll heading] in degrees";
//    public static final String LINKNAME_IMAGE_INFO = "SIMPA: Image size [width height] in meters ";
//    public static final String LINKNAME_PIXEL_COORDINATES_XY = "SIMPA: Pixel coordinates [X Y]"; 
//    
//    /**
//     * Generates a {@link IVideoArchive} containing an {@link IVideoFrame} and
//     * {@link IObservation} for each tile. 
//     * 
//     * @param tiles The tiles to generate annotations for
//     * @param platform The name of the cameraPlatform (i.e. Ventana or Tiburon)
//     * @param diveNumber The dive number
//     * @param videoArchiveName The name of the video tape. An example would be
//     *          V1234-01 for tape #1 for Ventana #1234
//     * @return A IVideoArchive containing a videoframe/observation for each tile.
//     */
//    IVideoArchive generateAnnotations(List<Tile> tiles, String platform, int diveNumber, String videoArchiveName);
//    
//    IVideoFrame generateAnnotation(Tile tile, String platform, int diveNumber);
//    
//    /**
//     * Create a new empty {@link IVideoFrame} instance
//     * @return An empty IVideoFrame
//     */
//    IVideoFrame newVideoFrame();
//    
//    /**
//     * Create a new empty {@link IObservation} instance
//     * @return
//     */
//    IObservation newObservation();
//    
//    /**
//     * Create a new empty {@link IAssociation} instance
//     * @return
//     */
//    IAssociation newAssociation();
    
    /**
     * Create a new empty {@link TileAnnotation} instance
     * @return
     */
    TileAnnotation newTileAnnotation();
    
    Tile newTile();
    
    MosaicAssembly newMosaicAssembly();
    
    VideoTime newTapeTime(String timecode, Date date);
    
    VideoTile newVideoTile();
    
    
        
}
