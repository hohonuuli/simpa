/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import java.util.List;
import simpa.annotation.TileAnnotation;

/**
 *
 * @author brian
 */
public interface TileAnnotationSelectionEvent {
    
    List<TileAnnotation> getSelectedTileAnnotations();

}
