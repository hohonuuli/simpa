/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.jni;

import com.google.inject.Inject;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.mbari.awt.image.ImageUtilities;
import org.mbari.framegrab.GrabberException;
import org.mbari.framegrab.IGrabber;
import org.mbari.framegrab.VideoChannelGrabber;
import org.mbari.movie.Timecode;
import org.slf4j.LoggerFactory;
import simpa.annotation.ImageCaptureException;
import simpa.annotation.ImageCaptureService;
import simpa.annotation.VideoControlService;
import simpa.annotation.VideoTime;
import simpa.core.CoreException;

/**
 *
 * @author brian
 */
public class QTVideoChannelImageCaptureService implements ImageCaptureService {

    private IGrabber grabber;
    private final VideoControlService videoControlService;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(QTVideoChannelImageCaptureService.class);

    /**
     * Frams per second. Used for calculations. Most MBARI video is around this
     * so we're just going to run with it for now
     */
    private static final double NOMINAL_FRAME_RATE = 29.97;

    private static final double FRAME_RATE_TOLERANCE = NOMINAL_FRAME_RATE * 0.5;

    /**
     * @param videoControlService Should be injected by Guice on startup.
     */
    @Inject
    public QTVideoChannelImageCaptureService(VideoControlService videoControlService) {
        this.videoControlService = videoControlService;
        try {
            grabber = new VideoChannelGrabber();
        } catch (Exception e) {
            throw new CoreException("Failed to initialize frame grabber", e);
        }
    }



    /**
     * Capture an image at a specified video time.
     *
     * @param videoTime The time on the VideoSource to capture
     * @throws ImageCaptureException Thrown if the frame is outside acceptable time
     *      tolerance or if the image capture fails.
     * @return AN Image of the specified frame
     */
    public BufferedImage capture(VideoTime videoTime) {

        /*
         * Seek to the correct timecode
         */
        videoControlService.seek(videoTime);

        // Timeout if we go over 15 seconds
        long timeout = 15000L;
        long elapsedTime = 0;
        long sleepInterval = 250;
        while (!videoControlService.isStopped()) {
            try {
                Thread.sleep(sleepInterval);
                elapsedTime += sleepInterval;
                if (elapsedTime >= timeout) {
                    break;
                }
            } catch (InterruptedException ex) {
                log.error("error while waiting for timcode", ex);
                throw new ImageCaptureException("The image capture thread was " +
                        "interrupted while waiting for the specified timecode of " +
                        videoTime.getTimecode() + " to be cued up.", ex);
            }
        }
        VideoTime currentVideoTime = videoControlService.requestVideoTime();

        /*
         * Check that video frame diff is within tolerance. If not throw an exception
         */
        Timecode t0 = new Timecode(videoTime.getTimecode(), NOMINAL_FRAME_RATE);
        Timecode t1 = new Timecode(currentVideoTime.getTimecode(), NOMINAL_FRAME_RATE);
        double dt = Math.abs(t0.diffFrames(t1));
        if (dt > FRAME_RATE_TOLERANCE) {
            throw new ImageCaptureException("The frames timecode of " + currentVideoTime.getTimecode() +
                    " is not within +/- " + FRAME_RATE_TOLERANCE + " seconds of the expected timecode of " +
                    videoTime.getTimecode());
        }

        /*
         * Grab the image
         */
        Image image = null;
        try {
            image = grabber.grab();
        } catch (GrabberException ex) {
            log.error("Failed to capture image using " + grabber, ex);
            throw new ImageCaptureException("The image grabber barfed while trying to grab a frame at" +
                    videoTime.getTimecode(), ex);
        }
        return ImageUtilities.toBufferedImage(image);

    }

}
