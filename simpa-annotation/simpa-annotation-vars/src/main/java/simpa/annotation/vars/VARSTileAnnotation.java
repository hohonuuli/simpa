/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.Date;
import simpa.annotation.TileAnnotation;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.Observation;

/**
 *
 * @author brian
 */
public class VARSTileAnnotation implements TileAnnotation {

    private int x;
    private int y;
    private final Observation observation;
    private Association pixelAssociation;
    //private final AnnotationFactory annotationFactory;


    @Inject
    public VARSTileAnnotation(Observation observation, AnnotationFactory annotationFactory) {
        this.observation = observation;
        //this.annotationFactory = annotationFactory;
        Collection assocs = observation.getAssociations();
        for (Object object : assocs) {
            Association a = (Association) object;
            if (a.getLinkName().equals(VARSAnnotationGeneratorService.LINKNAME_PIXEL_COORDINATES_XY)) {
                pixelAssociation = a;
                break;
            }
        }

        if (pixelAssociation == null) {
            pixelAssociation = annotationFactory.newAssociation();
            pixelAssociation.setLinkName(VARSAnnotationGeneratorService.LINKNAME_PIXEL_COORDINATES_XY);
            pixelAssociation.setToConcept(VARSAnnotationGeneratorService.CONCEPTNAME_SELF);
            pixelAssociation.setLinkValue("0 0");
            observation.addAssociation(pixelAssociation);
        }

        // extract assciation wtih X and Y pixel coordinate
        String[] coords = pixelAssociation.getLinkValue().split("\\W");
        x = Integer.parseInt(coords[0]);
        y = Integer.parseInt(coords[1]);

    }

    public VARSTileAnnotation(int x, int y, String conceptName, AnnotationFactory annotationFactory) {
        observation = annotationFactory.newObservation();
        observation.setConceptName(conceptName);
        observation.setObservationDate(new Date());
        pixelAssociation = annotationFactory.newAssociation();
        pixelAssociation.setLinkName(VARSAnnotationGeneratorService.LINKNAME_PIXEL_COORDINATES_XY);
        pixelAssociation.setToConcept(VARSAnnotationGeneratorService.CONCEPTNAME_SELF);
        pixelAssociation.setLinkValue(x + " " + y);
        observation.addAssociation(pixelAssociation);
        this.x = x;
        this.y = y;
        //this.annotationFactory = annotationFactory;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        String[] coords = pixelAssociation.getLinkValue().split("\\W");
        pixelAssociation.setLinkValue(x + " " + coords[1]);
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        String[] coords = pixelAssociation.getLinkValue().split("\\W");
        pixelAssociation.setLinkValue(coords[0] + " " + y);
        this.y = y;
    }

    public String getConceptName() {
        return observation.getConceptName();
    }

    public void setConceptName(String conceptName) {
        observation.setConceptName(conceptName);
    }

    /**
     *
     * @return The backing observation used to actually store all the data
     */
    public Observation getObservation() {
        return observation;
    }

    public String getAnnotator() {
        return observation.getObserver();
    }

    public void setAnnotator(String annotator) {
        observation.setObserver(annotator);
    }

    public Date getDate() {
        return observation.getObservationDate();
    }

    public void setDate(Date date) {
        observation.setObservationDate(date);
    }

}
