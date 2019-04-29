package simpa.annotation.basic

import simpa.annotation.MosaicAssemblyIngestService
import simpa.core.MosaicAssembly
import simpa.core.GlobalParameters
import javax.swing.JDialog

/**
 * {@link BasicMosaicAssemblyIngestService} delegates calls to this class. It was simpler to write
 * the parsing code in groovy
 *
 * @author brian
 * @since Nov 8, 2008 7:42:30 PM
 */
public class BasicMosaicAssemblyIngestServiceImpl {

    /**
     * Load a mosaic assembly from an external representation
     *
     * @param url The File to read in
     * @param imageDir The directory containing images
     */
    public MosaicAssembly load(URL url, File imageDir) {

        def input = new BufferedReader(InputStreamReader(url.openStream()))
        def mosaicAssembly = new BasicMosaicAssembly()
        input.readLines().eachWithIndex { line, index ->
            switch (index) {
                case 2:
                    def parts = line.split(":")
                    mosaicAssembly.cameraIdentifier = parts[-1].trim()
                    break
                case 3:
                    def parts = line.split(":")
                    mosaicAssembly.sessionIdentifier = parts[-1].trim()
                    break
                default:
                    if (!line.trim().startsWith("#")) {

                        def parts = line.split(",")
                        def idx = Integer.valueOf(parts[0])
                        def date = GlobalParameters.DATE_FORMAT_UTC.parse(parts[1])
                        def videoDate = GlobalParameters.DATE_FORMAT_UTC.parse(parts[2])
                        def timecode = parts[3]
                        def x = Double.valueOf(parts[4])
                        def y = Double.valueOf(parts[5])
                        def z = Double.valueOf(parts[6])
                        def width = Double.valueOf(parts[7])
                        def height = Double.valueOf(parts[8])
                        def pitch = Double.valueOf(parts[9])
                        def roll = Double.valueOf(parts[10])
                        def heading = Double.valueOf(parts[11])

                        def cameraPosition = new BasicCameraPosition(x: x, y: y, z: z, pitch: pitch, roll: roll, heading: heading)
                        def videoTime = new BasicVideoTime(date: videoDate, timecode: timecode)
                        mosaicAssembly.tiles << new BasicVideoTile(cameraPosition: cameraPosition, videoTime: videoTime, widthInMeters: width,
                                heightInMeters: height, tileIndex: idx, date: date)

                        // TODO Check in image Dir for the correct image
                    }
            }
        }

        return mosaicAssembly

    }


}