/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import simpa.core.MosaicAssembly;
import vars.annotation.AnnotationFactory;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;

/**
 *
 * @author brian
 */
public class VARSMosaicAssembly implements MosaicAssembly<VARSVideoTile> {

    /**
     * The Mosaic assembly is backed by a video archive. This app doesn't know
     * anything about tape ID. So for now all annotations will go onto a single
     * video archive. They'll have to be moved in VARS later on.
     */
    private final VideoArchive videoArchive;

    private final List<VARSVideoTile> tiles = new ArrayList<VARSVideoTile>();

    public VARSMosaicAssembly(VideoArchive videoArchive, AnnotationFactory annotationFactory) {
        this.videoArchive = videoArchive;
        /*
         * Generate tiles from the VideoFrames
         */
        VideoArchiveSet videoArchiveSet = videoArchive.getVideoArchiveSet();
        Collection videoFrames = videoArchiveSet.getVideoFrames();
        for (Object obj : videoFrames) {
            VideoFrame videoFrame = (VideoFrame) obj;
            tiles.add(new VARSVideoTile(videoFrame, annotationFactory));
        }
    }

    public String getCameraIdentifier() {
        return videoArchive.getVideoArchiveSet().getPlatformName();
    }

    public void setCameraIdentifier(String cameraIdentifier) {
        videoArchive.getVideoArchiveSet().setPlatformName(cameraIdentifier);
    }

    public String getSessionIdentifier() {
        CameraDeployment cpd = videoArchive.getVideoArchiveSet().getCameraDeployments().iterator().next();
        return cpd.getSequenceNumber() + "";
    }

    public void setSessionIdentifier(String sessionIdentier) {
        CameraDeployment cpd = videoArchive.getVideoArchiveSet().getCameraDeployments().iterator().next();
        cpd.setSequenceNumber(Integer.parseInt(sessionIdentier));
    }

    public List<VARSVideoTile> getTiles() {
        return tiles;
    }

    public VideoArchive getVideoArchive() {
        return videoArchive;
    }

}
