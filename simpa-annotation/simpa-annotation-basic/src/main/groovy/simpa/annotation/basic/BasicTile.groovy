package simpa.annotation.basic

import simpa.core.Tile
import simpa.core.CameraPosition

/**
 *
 *
 * @author brian
 * @since Nov 8, 2008 5:25:55 PM
 */

public class BasicTile implements Tile {

    URL url
    int tileIndex
    double widthInMeters
    double heightInMeters
    CameraPosition cameraPosition
    Date date

}