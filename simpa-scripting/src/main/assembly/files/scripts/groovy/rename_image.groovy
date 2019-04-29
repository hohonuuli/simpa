/**
 * Rename the images from whatever they currently are to [timecode].png.
 * This assumes that the original images have the sequence number embedded
 * into them.
 */
 
import com.google.inject.Guice
import com.google.inject.Stage
import simpa.annotation.AnnotationLookupService
import simpa.annotation.ui.AppModule
import simpa.core.DataIngestorService
import org.slf4j.LoggerFactory
 
/* ********************************************************************
 * Parse arguments and do setup
 */
if (args.size() < 5) {
    print("Usage:\n\trename_image [mosaic_file] [camera_id] [session_id] [image_in_dir] [image_out_dir\n");
}
 
def mosaicFile = new File(args[0])
def cameraIdentifier = args[1]
def sessionIdentifier = args[2]
def imageSource = new File(args[3])
def imageTarget = new File(args[4])

def log = LoggerFactory.getLogger("rename_image")
def injector = Guice.createInjector(Stage.PRODUCTION, new AppModule())


/* ********************************************************************
 * Convert the data to a mosaic
 */
def ingestor = injector.getInstance(DataIngestorService.class)
def tiles = ingestor.loadTiles(mosaicFile.toURI().toURL());
def lookup = injector.getInstance(AnnotationLookupService.class)
def mosaicAssembly = lookup.convertToMosaicAssembly(cameraIdentifier, sessionIdentifier, tiles);

/* ********************************************************************
 * Read in the images and parse out the ID
 */
def map = [:]
imageSource.eachFile { file ->
    if (file.name.endsWith('.png') || file.name.endsWith('.jpg')) {
        def m = (file.name =~ /\d+/)
        if (m.find()) {
            def idx = m.group() as Integer
            def matchingTile = mosaicAssembly.tiles.find { tile -> tile.tileIndex == idx }
            
            /*
             * If the image has a valid ID, copy it to the target directory
             * but rename it using the timecode.
             */
            if (matchingTile) {
                def parts = file.name.split(/\./)
                def timecode = matchingTile.videoTime.timecode.replace(":", "_")
                def target = new File(imageTarget, timecode + "." + parts[-1])
                def src = new FileInputStream(file)
                def dst = new FileOutputStream(target) 
                dst << src
            }
            else {
                log.info("No matching record was found for ${file}")   
            }
            
        }
    }
}
