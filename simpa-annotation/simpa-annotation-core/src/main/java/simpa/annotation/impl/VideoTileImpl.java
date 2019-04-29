package simpa.annotation.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import simpa.core.impl.TileImpl;
import simpa.core.Tile;
import simpa.core.CameraPosition;

import java.net.URL;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.TileAnnotation;
import simpa.annotation.VideoTile;
import simpa.annotation.VideoTime;

/**
 * An extension of the default Tile. This class adds support for {@link VideoTime} objects so
 * that we index into the media. IT also provides a container for Annotations associated with a
 * particular tile
 *
 * @author brian
 * @since Jun 5, 2008 2:07:04 PM
 */
public class VideoTileImpl extends TileImpl implements VideoTile {

    private VideoTime videoTime;
    private final Set<TileAnnotation> annotations = new HashSet<TileAnnotation>();
    private static final Logger log = LoggerFactory.getLogger(VideoTileImpl.class);
    boolean isProcessed = false;
    Integer widthInPixels;
    Integer heightInPixels;

    public VideoTileImpl(int tileIndex, double width, double height, URL url, CameraPosition cameraPosition, Date date, VideoTime videoTime) {
        super(tileIndex, width, height, url, cameraPosition, date);
        this.videoTime = videoTime;
    }

    public VideoTileImpl(Tile tile, VideoTime videoTime) {
        this(tile.getTileIndex(), tile.getWidthInMeters(), tile.getHeightInMeters(), tile.getUrl(), tile.getCameraPosition(), tile.getDate(), videoTime);
    }

    public VideoTime getVideoTime() {
        return videoTime;
    }

    public Set<TileAnnotation> getAnnotations() {
        return annotations;
    }

    public void setVideoTime(VideoTime videoTime) {
        this.videoTime = videoTime;
    }

    public Integer getWidthInPixels() {
        if (!isProcessed) {
            processUrl();
        }
        return widthInPixels;
    }

    public Integer getHeightInPixels() {
        if (!isProcessed) {
            processUrl();
        }
        return heightInPixels;
    }

    private void processUrl() {
        try {
            // Read image
            BufferedImage image = ImageIO.read(getUrl());

            // TODO get width and height in pixels
            widthInPixels = image.getWidth();
            heightInPixels = image.getHeight();
            
        } catch (IOException ex) {
            log.warn("Unable to read " + getUrl(), ex);
        }
        isProcessed = true;
    }
}
