/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.mock;

import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.TileAnnotation;
import simpa.annotation.VideoTile;
import simpa.core.MosaicAssembly;

/**
 *
 * @author brian
 */
public class AnnotationPersistenceMock implements AnnotationPersistenceService {

    public void makePersistent(MosaicAssembly mosaicAssembly) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void makeTransient(VideoTile videoTile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void makeTransient(TileAnnotation tileAnnotation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void makePersistent(VideoTile videoTile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void makePersistent(TileAnnotation tileAnnotation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void makeTransient(MosaicAssembly mosaicAssembly) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
