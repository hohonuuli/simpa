package simpa.annotation.basic;

import simpa.annotation.VideoControlService;
import simpa.annotation.VideoTime;
import simpa.annotation.VideoConnectionInformation;

import javax.swing.*;

/**
 * @author brian
 * @since Nov 9, 2008 8:18:16 PM
 */
public class BasicVideoControlService implements VideoControlService {

    private final BasicVideoControlServiceImpl impl = new BasicVideoControlServiceImpl();
    
    public void connect(Object... args) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void disconnect() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public JDialog getConnectionDialog() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isConnected() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void seek(VideoTime tapeTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isPlaying() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isStopped() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public VideoTime requestVideoTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public VideoConnectionInformation getVideoConnectionInformation() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
