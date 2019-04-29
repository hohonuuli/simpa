/**
 * Dump out information regarding an image mosaic from the persistence store
 * into a text file
 * Usage:
 *      info_dump [camera_id] [session_id] [target_file]
 */

import com.google.inject.Guice
import com.google.inject.Stage
import org.slf4j.LoggerFactory
import simpa.annotation.AnnotationLookupService
import simpa.annotation.ui.AppModule

def log = LoggerFactory.getLogger(this.class);

if (args.size() < 3) {
    println("Usage: info_dump <cameraId> <sessionId> <fileToWriteTo>")
    return
}

log.info("Excuting information dump")

def cameraID = args[0]
def sessionID = args[1]
def outputFile = new File(args[2])

/*
 * Activate dependency injection
 */
def injector = Guice.createInjector(Stage.PRODUCTION, new AppModule());
def annotationLookupService = injector.getInstance(AnnotationLookupService.class)
def mosaicAssembly = annotationLookupService.findByCameraAndSessionIdentifiers(cameraID, sessionID)
def informationResolver = injector.getInstance(InformationResolver.class)
informationResolver.writeMosaicAssembly(mosaicAssembly, outputFile)
