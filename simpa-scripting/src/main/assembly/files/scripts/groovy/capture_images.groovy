import simpa.annotation.ui.AppModule
import simpa.core.DataIngestorService
import simpa.annotation.ImageCaptureService
import simpa.annotation.VideoTile
import org.mbari.awt.image.ImageUtilities
import org.slf4j.LoggerFactory
import com.google.inject.Guice
import com.google.inject.Stage
import simpa.annotation.vars.VARSModelConverter
import simpa.annotation.jni.ScriptingModule
import simpa.annotation.VideoControlService

if (args.size() < 4) {
    println("""
Usage:
    capture_images rov dive# infile outdir
Arguments:
    com    = Serial port
    rov    = The name of the rov: Ventana, Tiburon or 'Doc Ricketts'
    dive#  = The dive number
    infile = the simpa data file to read in
    outdir = the directory to write all the captured images to
    """)
    System.exit(0)
}
def log = LoggerFactory.getLogger("capture_images")

/*
 * Parse command line args
 */
def serialPort = args[0]
def cameraIdentifier = args[1]
def sessionIdentifier = args[2]
def dataUrl = (new File(args[3])).toURI().toURL()
def targetDirectory = new File(args[4])

log.info """

  COMMPORT:        ${serialPort}
  CAMERA:          ${cameraIdentifier}
  SESSION:         ${sessionIdentifier}
  DATA SOURCE:     ${dataUrl}
  IMAGE DIRECTORY: ${targetDirectory.absolutePath}

"""

/*
 * Configure SIMPA environment using GUICE
 */
def injector = Guice.createInjector(Stage.PRODUCTION, new ScriptingModule());
def videoControlService = injector.getInstance(VideoControlService.class)
videoControlService.connect(serialPort, 29.97D)
def dataIngestorService = injector.getInstance(DataIngestorService.class)
def imageCaptureService = injector.getInstance(ImageCaptureService.class)
def pojoUtils = injector.getInstance(VARSModelConverter.class)

/*
 * Read data file
 */
def tiles = dataIngestorService.loadTiles(dataUrl) // List of Tiles
log.info("Found ${tiles.size()} tiles")

def videoTiles = null
try {
    videoTiles = pojoUtils.convertToMosaicAssembly(cameraIdentifier, sessionIdentifier, tiles)
}
catch (Exception e) {
    log.error("Failed to merge tile data with EXPD data", e)
}

/*
 * Process each tile
 */
def delta = 10
videoTiles.each { VideoTile tile ->
    try {
        def bufferedImage = imageCaptureService.capture(tile.videoTime)
        def file = new File(targetDirectory, "${tile.videoTime.timecode.replace(':', '_')}.png")
        ImageUtilities.saveImage(bufferedImage, file)
    }
    catch (Exception e) {
        log.warn("Failed to capture image at ${tile.videoTime.timecode}")
    }
}

System.exit(0)

