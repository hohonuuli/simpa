import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * THe reference images that Steve Rock's group give to us have the letterbox borders. Probably
 * added by the video downconverter. The borders need to be removed in order to correctly
 * size the images. This script nicely removes them.
 *
 * <div>
 * Usage:
 *  <pre>crop_image [image_dir] [output_dir] [upper_left_x] [upper_left_y] [width] [height]</pre>
 * Arguments:
 * <pre>
 *  image_dir = the string directory containing the letterboxed images
 *  output_dir = The string dirctory to write the cropped images into
 *  upper_left_x = the coordiante to start the crop at (in pixels)
 *  upper_left_y = the coordinate to start the crop at (in pixels)
 *  width = The width of the crop, in pixels
 *  height = The height of the crop, in pixels
 * </pre>
 * </div>
 *
 * @author brian
 * @since Oct 31, 2008 9:35:07 AM
 */
File imageDir = new File(args[0])
File outputDir = new File(args[1])
outputDir.mkdirs()

def upperLeftX = Integer.valueOf(args[2])
def upperLeftY = Integer.valueOf(args[3])
def width = Integer.valueOf(args[4])
def height = Integer.valueOf(args[5])

def imageFiles = imageDir.listFiles().findAll { it.name.endsWith(".png") }

imageFiles.each { file ->

    // Read BufferedImage
    BufferedImage image = ImageIO.read(file);

    // Make sure image is bigger than width and height. If not print warning and skip
    if (image.width >= width && image.height >= height) {

        // Crop image
        BufferedImage croppedImage = image.getSubimage(upperLeftX, upperLeftY, width, height)

        // Write image out to target directory
        def newFile = new File(outputDir, file.name)
        ImageIO.write(croppedImage, "png", newFile)
    }
    else {
        println("WARNING! ${file.absolutePath} is smaller than the [$width, $height]. Skipping")
    }

}
