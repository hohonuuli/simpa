package simpa.annotation.jni;

import gnu.io.CommPortIdentifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JDialog;
import org.mbari.comm.CommUtil;
import org.mbari.movie.Timecode;
import org.mbari.util.LibPathHacker;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr.IVCRTimecode;
import org.mbari.vcr.IVCRUserbits;
import org.mbari.vcr.rs422.VCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.VideoConnectionInformation;
import simpa.annotation.VideoConnectionStatus;
import simpa.annotation.VideoControlService;
import simpa.annotation.VideoTime;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.core.CoreException;
import simpa.core.GlobalParameters;

/**
 *
 * @author brian
 */
public class RS422VideoControlService implements VideoControlService {

    private VCR vcr;
    private double frameRate;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private VideoConnectionInformation videoConnectionInformation;
    private RS422VideoControlServiceDialog connectionDialog;

    public RS422VideoControlService() {
        extractAndLoadNativeLibraries();
    }

    /**
     *
     * @param args String portName, Double frameRate
     * @throws CoreException Thrown if a problem occurs with connecting
     * @throws IllegalArgumentException if you don't get your args right
     */
    public void connect(Object... args) {

        if (args.length == 0 && !(args[0] instanceof String)) {
            throw new IllegalArgumentException("You didn't call this method correctly. Read the JavaDocs and check your arguments.");
        }

        String port = (String) args[0];
        Double fr = (Double) args[1];
        frameRate = fr.doubleValue();

        if (vcr != null) {
            vcr.disconnect();
        }

        try {
            vcr = new VCR(port);
        } catch (Exception ex) {
            throw new CoreException("Failed to connect to " + port, ex);
        }

        videoConnectionInformation = new RS422VideoConnectionInformation(vcr.getPortId(), VideoConnectionStatus.CONNECTED);
    }

    public void disconnect() {
        if (vcr != null) {
            vcr.disconnect();
            videoConnectionInformation = new NullVideoConnectionInformation();
        }
    }

    public JDialog getConnectionDialog() {
        if (connectionDialog == null) {
            connectionDialog = new RS422VideoControlServiceDialog(this);
            connectionDialog.setModal(true);
        }
        return connectionDialog;
    }

    public void seek(VideoTime videoTime) {
        // TODO Start a timer and listen for valid checksums. If a valid one
        // occurs reset the time. If timer ends throw a VideoControlException
        vcr.seekTimecode(new Timecode(videoTime.getTimecode(), frameRate));
    }

    public boolean isPlaying() {
        vcr.requestStatus();
        return vcr.getVcrState().isPlaying();
    }

    public boolean isStopped() {
        vcr.requestStatus();
        return vcr.getVcrState().isStopped();
    }

    public Set<CommPortIdentifier> findAvailableCommPorts() {
        return CommUtil.getAvailableSerialPorts();
    }

    public VideoTime requestVideoTime() {

        /*
         * The vcr.requestXXX methods block until the VCR responds so we don't
         * have to do anything fancy to get the correct timecode. We need to
         * request both the timecode and userbits.
         */
        if (vcr.getVcrState().isPlaying()) {
            vcr.requestVTimeCode();
        } else {
            vcr.requestLTimeCode();
        }
        vcr.requestUserbits();

        // Read the timecode
        IVCRTimecode vcrTimecode = vcr.getVcrTimecode();
        IVCRUserbits vcrUserbits = vcr.getVcrUserbits();

        final Timecode timecode = vcrTimecode.getTimecode();
        timecode.setFrameRate(frameRate);

        // Convert userbits from byte[]->long->Date
        final int epicSeconds = NumberUtilities.toInt(vcrUserbits.getUserbits(), true);
        final Date date = new Date((long) epicSeconds * 1000L);

        return new VideoTimeImpl(timecode.toString(), date);

    }

    private void extractAndLoadNativeLibraries() {


        // Map the operating system to the correct library
        Map<String, String> libraryNames = new HashMap<String, String>() {

            {
                put("Win", "rxtxSerial.dll");
                put("Lin", "librxtxSerial.so");
                put("Mac", "librxtxSerial.jnilib");
            }
        };


        String os = System.getProperty("os.name");

        String libraryName = libraryNames.get(os.substring(0, 3));

        if (libraryName != null) {

            // Location to extract the native libraries into $HOME/.simpa/native/Mac
            File libraryHome = new File(new File(GlobalParameters.PREFERENCES_DIRECTORY, "native"), os.substring(0, 3));
            if (!libraryHome.exists()) {
                libraryHome.mkdirs();
            }

            if (!libraryHome.canWrite()) {
                throw new CoreException("Unable to extract native libary to " + libraryHome +
                        ". Verify that you have write access to that directory");
            }

            // Full path to where we want the native library to be
            File libraryFile = new File(libraryHome, libraryName);
            //libraryFile.deleteOnExit();

            /*
             * Copy the library to a location where we can use it.
             *
             * TODO: What's the best way to update the existing library? I only
             * want to do that if needed.
             */
            if (libraryFile.exists()) {
                log.info("The native RXTX library " + libraryFile.getAbsolutePath() +
                        " already exists. Skipping extraction");
            } else {
                try {
                    log.info("Copying RXTX native library to " + libraryFile);
                    FileOutputStream out = new FileOutputStream(libraryFile);
                    InputStream in = getClass().getResourceAsStream("/native/" + libraryName);
                    byte[] data = new byte[in.available()];
                    in.read(data);
                    out.write(data);
                    in.close();
                    out.close();
                } catch (Exception e) {
                    throw new CoreException("Failed to extract RXTX native library to " + libraryFile.getAbsolutePath(), e);
                }
            }

            /*
             * HACK ALERT!! This will only work on Sun's JVM. We're hacking the
             * java.library.path variable so that we can modify it at runtime.
             */
            try {
                LibPathHacker.addDir(libraryHome.getAbsolutePath());
            } catch (Exception e) {
                log.warn("Unable to hack the java.library.path property. This " +
                        "may have occured if you aren't running on Sun's JVM. " +
                        "SIMPA may be unable to access the RXTX native libraries");
            }

            try {
                /*
                 * We'll run into problems if we try to load the library more
                 * than once into the JVM. I think the catch statement will trap
                 * that issue though.
                 */
                System.load(libraryFile.getAbsolutePath());
            } catch (Exception e) {
                log.warn("Failed to load the native library '" + libraryFile.getAbsolutePath());
            }

        } else {
            log.error("A native RXTX library for your platform is not available. You will not be able to control your VCR");
        }

    }

    public boolean isConnected() {
        return vcr != null;
    }

    public VideoConnectionInformation getVideoConnectionInformation() {
        if (videoConnectionInformation == null) {
            videoConnectionInformation = new NullVideoConnectionInformation();
        }
        return videoConnectionInformation;
    }

    /**
     * Represents an empty video connection
     */
    private class NullVideoConnectionInformation implements VideoConnectionInformation {

        public String getVideoConnectionID() {
            return "UNKNOWN";
        }

        public VideoConnectionStatus getVideoConnectionStatus() {
            return VideoConnectionStatus.DISCONNECTED;
        }
    }
}

