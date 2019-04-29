/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import simpa.core.MosaicAssembly;

/**
 *
 * @author brian
 */
public interface AnnotationPersistenceService {
        
    void makePersistent(MosaicAssembly mosaicAssembly);
    
    void makePersistent(VideoTile videoTile);
    
    void makePersistent(TileAnnotation tileAnnotation);
    
    void makeTransient(MosaicAssembly mosaicAssembly);
    
    void makeTransient(VideoTile videoTile);
    
    void makeTransient(TileAnnotation tileAnnotation);

}
