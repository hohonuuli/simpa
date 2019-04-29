package simpa.annotation.basic

/**
 * Does some setup for the Basic annotaiton package AND provides common parameters shared between classes.
 *
 * @author brian
 * @since Nov 9, 2008 8:24:13 PM
 */

public class GlobalParameters {

    static final File LIBRARY_HOME = new File(new File(GlobalParameters.PREFERENCES_DIRECTORY, "basic-data"))

    static {
        if (!LIBRARY_HOME.exists()) {
            LIBRARY_HOME.mkdirs()
        }
    }

    static File resolveDataStore(String cameraIdentifier, String sessionIdentifier) {
        return new File(new File(LIBRARY_HOME, cameraIdentifier), sessionIdentifier);
    }

}