/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import org.mbari.movie.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.VideoTile;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.annotation.impl.VideoTileImpl;
import simpa.core.DataIngestorService;
import simpa.core.Tile;
import simpa.core.impl.DataIngestorServiceImpl02;

/**
 *
 * @author brian
 */
public class VideoTileCellRendererDemo extends JFrame {
    
    private static final Logger log = LoggerFactory.getLogger(VideoTileCellRendererDemo.class);

    public VideoTileCellRendererDemo(List<VideoTile> tiles) {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);
        
        JList list = new JList();
        ListCellRenderer cellRenderer = new VideoTileCellRenderer();
        list.setCellRenderer(cellRenderer);
        list.setListData(tiles.toArray());
        scrollPane.setViewportView(list);

    }
    
    
    
    public static void main(String[] args) throws Exception {
        
        /*
         * Read in a test file
         */
        String resource = "/20080505mosaicData.csv";
        URL url = resource.getClass().getResource(resource);
        DataIngestorService ingest = new DataIngestorServiceImpl02();
        List<Tile> tiles = null;
        try {
            tiles = ingest.loadTiles(url);
        }
        catch (Exception ex) {
            log.error("Failed to read '" + url + "'", ex);
        }
        
        /*
         * Convert Tile to VideoTiles 
         */
        List<VideoTile> videoTiles = new ArrayList<VideoTile>(tiles.size());
        int i = 0;
        URL imageUrl = resource.getClass().getResource("/images/refImage34.png");
        for (Tile tile : tiles) {
            Timecode timecode = new Timecode(tile.getTileIndex() * 4);
            VideoTile videoTile = new VideoTileImpl(tile, new VideoTimeImpl(timecode.toString(), new Date()));
            
            videoTile.setUrl(imageUrl);
            videoTiles.add(videoTile);
        }
        
        // Show list in frame
        JFrame f = new VideoTileCellRendererDemo(videoTiles);
        f.pack();
        f.setVisible(true);
        
    }

}
