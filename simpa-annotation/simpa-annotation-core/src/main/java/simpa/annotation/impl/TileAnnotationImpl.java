/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.impl;

import java.util.Date;
import simpa.annotation.TileAnnotation;

/**
 *
 * @author brian
 */
public class TileAnnotationImpl  implements TileAnnotation {
    
    private int x;
    private int y;
    private String conceptName;
    private String annotator;
    private Date date;

    public TileAnnotationImpl() {
    }

    public TileAnnotationImpl(int x, int y, String conceptName) {
        this.x = x;
        this.y = y;
        this.conceptName = conceptName;
    }
    

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getAnnotator() {
        return annotator;
    }

    public void setAnnotator(String annotator) {
        this.annotator = annotator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
