/**
 * Fixes data that was loaded with width and height in meters swapped. Simply swaps the width and height data and
 * saves it into the database
 */

import com.google.inject.Guice
import com.google.inject.Stage
import org.slf4j.LoggerFactory
import simpa.annotation.AnnotationLookupService
import simpa.annotation.ui.AppModule
import simpa.annotation.AnnotationPersistenceService

def log = LoggerFactory.getLogger(this.class);

if (args.size() < 2) {
    println("Usage: swap_width_height <cameraId> <sessionId>")
    return
}

log.info("Excuting information dump")

def cameraID = args[0]
def sessionID = args[1]

/*
 * Activate dependency injection
 */
def injector = Guice.createInjector(Stage.PRODUCTION, new AppModule());
def annotationLookupService = injector.getInstance(AnnotationLookupService.class)
def annotationPersistenceService = injector.getInstance(AnnotationPersistenceService.class)
def mosaicAssembly = annotationLookupService.findByCameraAndSessionIdentifiers(cameraID, sessionID)

mosaicAssembly.tiles.each { tile ->
    
    def h = tile.heightInMeters
    def w = tile.widthInMeters

    tile.heightInMeters = w
    tile.widthInMeters  = h
}

annotationPersistenceService.makePersistent(mosaicAssembly); 
