/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.VideoTime;
import simpa.annotation.VideoTile;
import simpa.core.CameraPosition;
import simpa.core.impl.CameraPositionImpl;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.CameraData;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;

/**
 *
 * @author brian
 */
public class VARSVideoTile implements VideoTile<VARSTileAnnotation> {

    private static final Logger log = LoggerFactory.getLogger(VARSVideoTile.class);

    /**
     * A {@link VARSVideoTile} is a facade over this {@link IVideoFrame}
     */
    private final VideoFrame videoFrame;

    /**
     * TapeTime is a facade over parameters in videoFrame
     */
    private VideoTime tapeTime;
    private final Set<VARSTileAnnotation> annotations = new AnnotationHashSet();
    private Observation tileObservation;  // Represents the center of the tile
    private Association xyzAssociation;   // Stores CameraPosition X Y Z
    private Association prhAssociation;   // Stores CameraPosition Pitch Roll Heading
    private Association infoAssociation;  // Stores width and height (meters) of tile image
    private Association indexAssociation; // Stores tile index
    private CameraPosition cameraPosition;
    private Integer widthInPixels;
    private Integer heightInPixels;
    private boolean gotImageSize = false;
    private URL url;
    private boolean gotURL = false;

    public VARSVideoTile(VideoFrame videoFrame, AnnotationFactory annotationFactory) {
        this.videoFrame = videoFrame;
        this.tapeTime = new VARSTapeTime();

        // Map Observations to TileAnnotations
        List<Observation> observations = new ArrayList<Observation>(videoFrame.getObservations());
        for (Observation obs : observations) {

            // If the obs is the key tile os, extract the VideoTile info from the associations
            if (obs.getConceptName().equals(VARSAnnotationGeneratorService.CONCEPTNAME_TILE)) {
                tileObservation = obs;

                // Extract appropriate associations that back the other fields
                Collection assocs = tileObservation.getAssociations();
                for (Object object1 : assocs) {
                    Association a = (Association) object1;
                    if (a.getLinkName().equals(VARSAnnotationGeneratorService.LINKNAME_CAMERA_PRH)) {
                        prhAssociation = a;
                    }
                    else if (a.getLinkName().equals(VARSAnnotationGeneratorService.LINKNAME_CAMERA_XYZ)) {

                        xyzAssociation = a;
                    }
                    else if (a.getLinkName().equals(VARSAnnotationGeneratorService.LINKNAME_IMAGE_INFO)) {
                        infoAssociation = a;
                    }
                    else if (a.getLinkName().equals(VARSAnnotationGeneratorService.LINKNAME_TILEINDEX)) {
                        indexAssociation = a;
                    }
                }

            }
            else {
                // convert each observation to a VideoTileAnnotation
                annotations.add(new VARSTileAnnotation(obs, annotationFactory));
            }
        }

        if (tileObservation == null) {
            tileObservation = annotationFactory.newObservation();
            tileObservation.setConceptName(VARSAnnotationGeneratorService.CONCEPTNAME_TILE);
            tileObservation.setObservationDate(new Date());
            videoFrame.addObservation(tileObservation);
        }


        // TODO create associations if missing
        if (xyzAssociation == null) {
            xyzAssociation = annotationFactory.newAssociation(VARSAnnotationGeneratorService.LINKNAME_CAMERA_XYZ,
                    VARSAnnotationGeneratorService.CONCEPTNAME_SELF, "0 0 0");
            tileObservation.addAssociation(xyzAssociation);
        }

        if (prhAssociation == null) {
            prhAssociation = annotationFactory.newAssociation(VARSAnnotationGeneratorService.LINKNAME_CAMERA_PRH,
                    VARSAnnotationGeneratorService.CONCEPTNAME_SELF, "0 0 0");
            tileObservation.addAssociation(prhAssociation);
        }

        if (infoAssociation == null) {
            infoAssociation = annotationFactory.newAssociation(VARSAnnotationGeneratorService.LINKNAME_IMAGE_INFO,
                    VARSAnnotationGeneratorService.CONCEPTNAME_SELF, "0 0");
            tileObservation.addAssociation(infoAssociation);
        }

        if (indexAssociation == null) {
            indexAssociation = annotationFactory.newAssociation(VARSAnnotationGeneratorService.LINKNAME_TILEINDEX,
                    VARSAnnotationGeneratorService.CONCEPTNAME_SELF, "0");
            tileObservation.addAssociation(indexAssociation);
        }

        String[] xyz = xyzAssociation.getLinkValue().split(" ");
        double x = Double.parseDouble(xyz[0]);
        double y = Double.parseDouble(xyz[1]);
        double z = Double.parseDouble(xyz[2]);
        String[] prh = prhAssociation.getLinkValue().split(" ");
        double p = Double.parseDouble(prh[0]);
        double r = Double.parseDouble(prh[1]);
        double h = Double.parseDouble(prh[2]);
        cameraPosition = new CameraPositionImpl(x, y, z, p, r, h);

    }

    public VideoTime getVideoTime() {
        return tapeTime;
    }

    public Set<VARSTileAnnotation> getAnnotations() {
        return annotations;
    }

    public URL getUrl() {
        if (!gotURL) {
            try {
                String stillImage = videoFrame.getCameraData().getImageReference();
                if (stillImage != null) {
                    url = new URL(videoFrame.getCameraData().getImageReference());
                }
            } catch (MalformedURLException ex) {
                log.warn("Unable to parse '" + url + "'");
            }
            gotURL = true;
        }
        return url;
    }

    public void setUrl(URL newUrl) {
        CameraData cameraData = videoFrame.getCameraData();
        String stillImage = (newUrl == null) ? null : newUrl.toExternalForm();
        cameraData.setImageReference(stillImage);
        url = newUrl;
        gotImageSize = false;
    }

    public int getTileIndex() {
        return Integer.parseInt(indexAssociation.getLinkValue());
    }

    public void setTileIndex(int tileIndex) {
        indexAssociation.setLinkValue(tileIndex + "");
    }

    public double getWidthInMeters() {
        String[] wh = infoAssociation.getLinkValue().split(" ");
        return Double.parseDouble(wh[0]);
    }

    public void setWidthInMeters(double width) {
        String[] wh = infoAssociation.getLinkValue().split(" ");
        infoAssociation.setLinkValue(width + " " + wh[1]);
    }

    public double getHeightInMeters() {
        String[] wh = infoAssociation.getLinkValue().split(" ");
        return Double.parseDouble(wh[1]);
    }

    public void setHeightInMeters(double height) {
        String[] wh = infoAssociation.getLinkValue().split(" ");
        infoAssociation.setLinkValue(wh[0] + " " + height);
    }

    public CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraPosition(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
        /*
         * Need to update the releant associations if this is set. CameraPosition
         * is immutable so we only have to worry about updating this when it's
         * explicitly set in this method calls
         */
        String xyzLinkValue = cameraPosition.getX() +  " " + cameraPosition.getY() +
                " " + cameraPosition.getZ();

        String prhLinkValue = cameraPosition.getPitch() +  " " + cameraPosition.getRoll() +
                " " + cameraPosition.getHeading();

        xyzAssociation.setLinkValue(xyzLinkValue);
        prhAssociation.setLinkValue(prhLinkValue);
    }

    public Date getDate() {
        return videoFrame.getRecordedDate();
    }

    public void setDate(Date date) {
        videoFrame.setRecordedDate(date);
    }

    public Integer getWidthInPixels() {
        if (!gotImageSize) {
            processUrl();
        }
        return widthInPixels;
    }

    public Integer getHeightInPixels() {
        if (!gotImageSize) {
            processUrl();
        }
        return heightInPixels;
    }

    private void processUrl() {
        URL imageURL = getUrl();
        if (imageURL != null) {
            try {
                // Read image
                BufferedImage image = ImageIO.read(imageURL);

                // TODO get width and height in pixels
                widthInPixels = image.getWidth();
                heightInPixels = image.getHeight();
            } catch (IOException ex) {
                log.warn("Unable to read " + imageURL, ex);
            }
        }

        /*
         * TODO We're only making one attempt to read the image. If there's network
         * issues they may not work since it won't reattempt to get the image
         * dimensions. This may need to be changed in the future.
         */
        gotImageSize = true;
    }


    /**
     *
     * @return Returns the backing VideoFrame used to store info
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public void setVideoTime(VideoTime tapeTime) {
        videoFrame.setRecordedDate(tapeTime.getDate());
        videoFrame.setTimecode(tapeTime.getTimecode());
    }

    public class VARSTapeTime implements VideoTime {

        public Date getDate() {
            return videoFrame.getRecordedDate();
        }

        public String getTimecode() {
            return videoFrame.getTimecode();
        }

    }

    /**
     * When adding or removing TileAnnotations we need to be sure to
     * also remove them from the VARS VideoFrame used as the backing
     * store. This Set helps pass on the add/remove calls to the VARS
     * objects
     */
    private class AnnotationHashSet extends HashSet<VARSTileAnnotation> {

        @Override
        public boolean add(VARSTileAnnotation e) {
            boolean ok = super.add(e);
            videoFrame.addObservation(e.getObservation());
            return ok;
        }

        @Override
        public void clear() {
            super.clear();
            Collection<? extends Observation> observations = videoFrame.getObservations();
            for (Observation obs : observations) {
                videoFrame.removeObservation(obs);
            }
        }

        @Override
        public boolean remove(Object o) {
            boolean ok = super.remove(o);
            videoFrame.removeObservation(((VARSTileAnnotation) o).getObservation());
            return ok;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean ok = false;
            for (Object object : c) {
                ok = remove(object);
                if (!ok) {
                    break;
                }
            }
            return ok;
        }

        @Override
        public boolean addAll(Collection<? extends VARSTileAnnotation> c) {
            boolean ok = false;
            for (Object object : c) {
                ok = add((VARSTileAnnotation) object);
                if (!ok) {
                    break;
                }
            }
            return ok;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return super.retainAll(c);
        }

    };

}
