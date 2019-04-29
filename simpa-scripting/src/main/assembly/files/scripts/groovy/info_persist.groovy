/**
 * Read mosaic information from a text file and a directory of refernce images
 * and load them into the persistence store
 *
 * Usage:
 *    info_persist [data_file] [camera_id] [session_id] [image_dir]
 */
import com.google.inject.Guice
import com.google.inject.Stage
import org.slf4j.LoggerFactory
import simpa.InformationResolver
import simpa.annotation.ui.AppModule
import simpa.annotation.AnnotationPersistenceService



def log = LoggerFactory.getLogger(this.class);
log.info("Excuting information generation")

def file = null
def url = null

// Resolve the URL. The user may enter a file path or URL on the command line
try {
    file = new File(args[0])
}
catch (Exception e) {
    // DO nothing. It's not a file
}
if (file?.exists()) {
    url = file.toURI().toURL()
}
else {
    url = args[0]
}

def cameraID = args[1]
def sessionID = args[2]
def imageDir = new File(args[3])

/*
 * Activate dependency injection
 */
def injector = Guice.createInjector(Stage.PRODUCTION, new AppModule());
def informationResolver = injector.getInstance(InformationResolver.class)
def mosaicAssembly = informationResolver.resolveAndAssociateImages(url, cameraID, sessionID, imageDir)

/*
 * save to the persistent storage
 */
def annotationPersistenceService = injector.getInstance(AnnotationPersistenceService.class)
annotationPersistenceService.makePersistent(mosaicAssembly)