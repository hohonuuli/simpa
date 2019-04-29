package simpa.map.core.impl;

import org.mbari.geometry.Point2D;
import simpa.map.core.AnnotatedPoint;

/**
 * Represents a point in space. We'll attach a name to it too.
 */
public class AnnotatedPointImpl implements AnnotatedPoint<String> {

    private String annotation;
    private Point2D location;

    public AnnotatedPointImpl(String name) {
        this(name, new Point2D<Double>(Double.NaN, Double.NaN));
    }

    public AnnotatedPointImpl(String name, Point2D location) {
        this.annotation = name;
        this.location = location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public Point2D getLocation() {
        return location;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
