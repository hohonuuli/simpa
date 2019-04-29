/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.util.Date;


/**
 *
 * @author brian
 */
public interface TileAnnotation {
    
    /**
     * Return the X pixel coordinate for the center of the annotation. This 
     * describes the location of the annotation within an image.
     * 
     * @return x in pixels of the center of the annotation
     */
    int getX();
    
    void setX(int x);
    
    /**
     * Return the X pixel coordinate for the center of the annotation. This 
     * describes the location of the annotation within an image.
     * 
     * @return y in pixels of the center of the annotation
     */
    int getY();
    
    void setY(int y);
    
    /**
     * @return The name used to describe the annotation
     */
    String getConceptName();

    /**
     * @param conceptName The name used to describe the annotation
     */
    void setConceptName(String conceptName);
    
    /**
     * 
     * @return The name or identifier that represents the person that created
     *      the annotation
     */
    String getAnnotator();
    void setAnnotator(String annotator);
    
    /**
     * 
     * @return The date that the annotation was created.
     */
    Date getDate();
    void setDate(Date date);
    

}
