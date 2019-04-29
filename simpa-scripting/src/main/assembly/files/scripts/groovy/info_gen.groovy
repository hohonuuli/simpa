/**
 * Read a mosaic file, associate it with metadata and dump out the information
 * regarding the mosaic into a text file
 *
 * Usage:
 *      info_gen [mosaic_file] [camera_id] [session_id] [target_file]
 */

import com.google.inject.Guice
import com.google.inject.Stage
import org.slf4j.LoggerFactory
import simpa.annotation.ui.AppModule

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

cameraID = args[1]
sessionID = args[2]
outputFile = new File(args[3])

/*
 * Activate dependency injection
 */
def injector = Guice.createInjector(Stage.PRODUCTION, new AppModule());
def informationResolver = injector.getInstance(InformationResolver.class)
informationResolver.resolveAndWrite(url, cameraID, sessionID, outputFile)
