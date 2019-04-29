package simpa.annotation.jni;

import gnu.io.CommPortIdentifier;
import simpa.annotation.VideoConnectionInformation;
import simpa.annotation.VideoConnectionStatus;

/**
 * Bean representing the id and status of a VideoConnection
 * @author brian
 */
public class RS422VideoConnectionInformation implements VideoConnectionInformation {


    private final CommPortIdentifier commPortIdentifier;
    private final VideoConnectionStatus videoConnectionStatus;

    public RS422VideoConnectionInformation(final CommPortIdentifier commPortIdentifier, VideoConnectionStatus status) {
        this.commPortIdentifier = commPortIdentifier;
        this.videoConnectionStatus = status;
    }

    public String getVideoConnectionID() {
        return commPortIdentifier.getName();
    }

    public VideoConnectionStatus getVideoConnectionStatus() {
        return videoConnectionStatus;
    }

}
