/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.util.Set;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public interface NavigationLookupService {
    
    /**
     * Find all annotations within a MosaicAssembly that are spatially located 
     * with a tile. This includes the annotations of the tile itself.
     * 
     * @param mosaicAssembly
     * @param tile
     * @return
     */
    //Set<TileAnnotation> findTileAnnotationsWithinTile(MosaicAssembly mosaicAssembly, Tile tile);

}
