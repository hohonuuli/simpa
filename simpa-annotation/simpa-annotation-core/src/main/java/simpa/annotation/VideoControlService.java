package simpa.annotation;

import javax.swing.JDialog;

public interface VideoControlService {
    
    /**
     * Connect to your video controller. This method accepts a varargs
     * as the argument so you can feed in whatever params you need.
     * 
     * @param args The arguments need to connect to you video control service
     * 
     */
    void connect(Object... args);

    /**
     * Disconnect from your video controller
     */
    void disconnect();
    
    /**
     * Connect to your video service with input from a User Interface. This 
     * method should implement the UI needed to collect the parameters, then
     * call the connect method. The returned dialog may be requested once and then
     * subsequently reused by UI components so you should write it accordely.
     * 
     * @return A JDialog that can be used to connect to your video service
     */
    JDialog getConnectionDialog();
    
    boolean isConnected();
    
    void seek(VideoTime tapeTime);
    
    boolean isPlaying();
    
    boolean isStopped();
    
    VideoTime requestVideoTime();
    
    VideoConnectionInformation getVideoConnectionInformation();

}
