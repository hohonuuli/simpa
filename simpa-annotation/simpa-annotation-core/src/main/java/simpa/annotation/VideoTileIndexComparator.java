package simpa.annotation;

import java.util.Comparator;

/**
 * @author brian
 * @since Sep 2, 2008 1:24:07 PM
 */
public class VideoTileIndexComparator implements Comparator<VideoTile> {

    public int compare(VideoTile o1, VideoTile o2) {
        return o1.getTileIndex() - o2.getTileIndex();
    }

}