package simpa.annotation.basic

import simpa.annotation.AnnotationPersistenceService
import simpa.core.MosaicAssembly
import simpa.annotation.VideoTile
import simpa.annotation.TileAnnotation

/**
 *
 *
 * @author brian
 * @since Nov 8, 2008 5:22:04 PM
 */

public class BasicAnnotationPersistenceService implements AnnotationPersistenceService {


    public BasicAnnotationPersistenceService() {
        // Setup .simpa/basic-data directory
        if (!libraryHome.exists()) {
            libraryHome.mkdirs();
        }
    }

    public void makePersistent(MosaicAssembly mosaicAssembly) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void makePersistent(VideoTile videoTile) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void makePersistent(TileAnnotation tileAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void makeTransient(MosaicAssembly mosaicAssembly) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void makeTransient(VideoTile videoTile) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void makeTransient(TileAnnotation tileAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }



}