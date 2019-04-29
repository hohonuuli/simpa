/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.impl;

import java.util.ArrayList;
import java.util.List;
import simpa.core.MosaicAssembly;

/**
 *
 * @author brian
 */
public class MosaicAssemblyImp implements MosaicAssembly<VideoTileImpl> {
    
    private String cameraIdentifier;
    private String sessionIdentifier;
    private final List<VideoTileImpl> tiles = new ArrayList<VideoTileImpl>();

    public String getCameraIdentifier() {
        return cameraIdentifier;
    }

    public void setCameraIdentifier(String cameraIdentifier) {
        this.cameraIdentifier = cameraIdentifier;
    }

    public String getSessionIdentifier() {
        return sessionIdentifier;
    }

    public void setSessionIdentifier(String sessionIdentier) {
        this.sessionIdentifier = sessionIdentier;
    }

    public List<VideoTileImpl> getTiles() {
        return tiles;
    }

}
