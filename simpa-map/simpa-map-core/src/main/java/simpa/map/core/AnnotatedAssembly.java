package simpa.map.core;

import simpa.core.MosaicAssembly;
import simpa.core.Tile;

import java.util.Set;

import org.mbari.geometry.Point2D;

/**
 * A {@link simpa.core.MosaicAssembly} with Annotations applied to the entire assembly (this is in contrast to
 * annotations applied to a particular tile.
 */
public interface AnnotatedAssembly extends MosaicAssembly<Tile> {

    /**
     *
     * @return The Annotated Points within this mosaic
     */
    Set<AnnotatedPoint> getAnnotatedPoints();

    Point2D<Double> getMin();

    Point2D<Double> getMax();
    

}
