package simpa.map.core;

import org.mbari.geometry.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 11, 2009
 * Time: 11:47:24 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AnnotatedPoint<T> {

    void setLocation(Point2D location);

    Point2D getLocation();

    T getAnnotation();

    void setAnnotation(T annotation);

}
