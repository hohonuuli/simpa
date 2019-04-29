package simpa.annotation;

import simpa.core.MosaicAssembly;

import javax.swing.JDialog;

/**
 * @author brian
 * @since Nov 8, 2008 7:39:50 PM
 */
public interface MosaicAssemblyIngestService {

    MosaicAssembly load(Object... args);

    /**
     * Connect to your video service with input from a User Interface. This
     * method should implement the UI needed to collect the parameters, then
     * call the connect method. The returned dialog may be requested once and then
     * subsequently reused by UI components so you should write it accordely.
     *
     * @return A JDialog that can be used to connect to your video service
     */
    JDialog getLoadDialog();


}
