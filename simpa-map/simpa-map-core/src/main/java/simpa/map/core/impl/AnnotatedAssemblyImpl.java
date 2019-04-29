package simpa.map.core.impl;

import simpa.map.core.AnnotatedAssembly;
import simpa.map.core.AnnotatedPoint;
import simpa.core.Tile;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import org.mbari.geometry.Point2D;

/**
 * Implementation of {@link simpa.map.core.AnnotatedAssembly}. The {@link simpa.core.CameraPosition} x, y and z units
 * should be meters.
 */
public class AnnotatedAssemblyImpl implements AnnotatedAssembly {

    private final List<Tile> tiles = new ArrayList<Tile>();
    private final Set<AnnotatedPoint> annotatedPoints = new HashSet<AnnotatedPoint>();

    private String cameraIdentifier;
    private String sessionIdentifier;

    public AnnotatedAssemblyImpl() {
    }

    public Set<AnnotatedPoint> getAnnotatedPoints() {
        return annotatedPoints;
    }

    public Point2D<Double> getMin() {

        Point2D<Double> point;

        if (tiles.isEmpty()) {
            point = new Point2D<Double>(Double.NaN, Double.NaN);
        }
        else {

            Double minX = Double.MAX_VALUE;
            Double minY = Double.MAX_VALUE;

            for (Tile tile : tiles) {

                // Need to account for rotation of tile to get bounds

                double x = tile.getCameraPosition().getX() - tile.getWidthInMeters() / 2D;
                double y = tile.getCameraPosition().getY() - tile.getHeightInMeters() / 2D;
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
            }

            point = new Point2D<Double>(minX, minY);
        }

        return point;
    }

    public Point2D<Double> getMax() {
        Point2D<Double> point;

        if (tiles.isEmpty()) {
            point = new Point2D<Double>(Double.NaN, Double.NaN);
        }
        else {

            Double maxX = Double.MIN_VALUE;
            Double maxY = Double.MIN_VALUE;

            for (Tile tile : tiles) {
                double x = tile.getCameraPosition().getX() + tile.getWidthInMeters() / 2D;
                double y = tile.getCameraPosition().getY() + tile.getHeightInMeters() / 2D;
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }

            point = new Point2D<Double>(maxX, maxY);
        }

        return point;
    }

    public String getCameraIdentifier() {
        return cameraIdentifier;
    }

    public void setCameraIdentifier(String cameraIdentifier) {
        this.cameraIdentifier = cameraIdentifier;
    }

    public String getSessionIdentifier() {
        return sessionIdentifier;
    }

    public void setSessionIdentifier(String sessionIdentifier) {
        this.sessionIdentifier = sessionIdentifier;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

}
