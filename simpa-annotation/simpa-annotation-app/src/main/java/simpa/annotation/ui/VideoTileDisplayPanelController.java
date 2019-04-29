/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import java.util.ArrayList;
import java.util.Collection;

import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.AnnotationLookupService;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.TileAnnotation;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public class VideoTileDisplayPanelController {
    
    private final AnnotationLookupService lookupService;
    
    private final AnnotationPersistenceService updateService;
    
    private final AnnotationGeneratorService generatorService;
    
    private final VideoTileDisplayPanel tileDisplayPanel;
        
    public VideoTileDisplayPanelController(VideoTileDisplayPanel tileDisplayPanel,
            AnnotationGeneratorService generatorService, AnnotationLookupService lookupService,
            AnnotationPersistenceService updateService) {
        
        this.tileDisplayPanel = tileDisplayPanel;
        this.lookupService = lookupService;
        this.updateService = updateService;
        this.generatorService = generatorService;
    }
    
    public void processTileAnnotations(Collection<TileAnnotation> tileAnnotations) {
        // TODO implement this
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    
    
    public Collection<TileAnnotation> lookupTileAnnotations(Tile tile) {
        // TODO implement this
        //throw new UnsupportedOperationException("Not yet implemented");
        return new ArrayList<TileAnnotation>();
    }
    
    /**
     * Generate a new empty {@link TileAnnotation} instance
     * @return
     */
    public TileAnnotation newTileAnnotation() {
        return generatorService.newTileAnnotation();
    }

    

}
