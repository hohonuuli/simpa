/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import simpa.core.CameraPosition;
import simpa.core.CoreException;
import simpa.core.Tile;
import vars.annotation.VideoFrame;

/**
 * @author brian
 * @since Jun 5, 2008 3:34:53 PM
 */
public class VARSTile implements Tile {

    private final VideoFrame videoFrame;

    public VARSTile(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    /**
     * @return The url to the image referenced by this tile.
     */
    public URL getUrl() {
        URL url = null;
        try {
            url = new URL(videoFrame.getCameraData().getImageReference());
        }
        catch (MalformedURLException e) {
            throw new CoreException("Failed to parse image URL", e);
        }
        return url;
    }

    public void setUrl(URL url) {
        videoFrame.getCameraData().setImageReference(url.toExternalForm());
    }

    /**
     * @return The index of the tile in a sequence of tiles
     */
    public int getTileIndex() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTileIndex(int tileIndex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @return The width (x-axis) of the tile in meters
     */
    public double getWidthInMeters() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setWidthInMeters(double width) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @return The height (y-axis) of the tile in meters
     */
    public double getHeightInMeters() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setHeightInMeters(double height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @return The position of the camera when the image for this tile was
     *         captured.
     */
    public CameraPosition getCameraPosition() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setCameraPosition(CameraPosition cameraPosition) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Dates are all caputred as GMT
     *
     * @return The date when the tile was captured.
     */
    public Date getDate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDate(Date date) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
