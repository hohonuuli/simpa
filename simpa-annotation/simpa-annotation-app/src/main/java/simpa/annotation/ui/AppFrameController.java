/*
 * @(#)AppFrameController.java   2009.09.14 at 11:12:40 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package simpa.annotation.ui;

import foxtrot.Job;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.AnnotationLookupException;
import simpa.annotation.AnnotationLookupService;
import simpa.annotation.AnnotationPersistenceException;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.ImageCaptureException;
import simpa.annotation.ImageCaptureService;
import simpa.annotation.TileAnnotation;
import simpa.annotation.UserDefinedButtonInfo;
import simpa.annotation.UserLookupService;
import simpa.annotation.VideoConnectionInformation;
import simpa.annotation.VideoControlService;
import simpa.annotation.VideoTile;
import simpa.annotation.VideoTileTimecodeComparator;
import simpa.core.CoreException;
import simpa.core.DataIngestorService;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;
import vars.IUserAccount;

/**
 * The controller for the AppFrame. The methods in this class are meant to be called
 * off of the EventDispatchThread.
 * @author brian
 */
public class AppFrameController {

    public static final String LOGIN_CREDENTIAL_PROPERTY = "loginCredential";
    public static final String MOSAIC_ASSEMBLY_PROPERTY = "mosaicAssembly";
    public static final String SELECTED_CONCEPTNAME_PROPERTY = "selectedConceptName";
    public static final String VIDEO_CONNECTION_INFORMATION_PROPERTY = "videoConnectionInformation";
    private final Logger log = LoggerFactory.getLogger(AppFrameController.class);
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final AnnotationGeneratorService annotationGeneratorService;
    private final AnnotationLookupService annotationLookupService;
    private final AnnotationPersistenceService annotationPersistenceService;
    private final AppFrame appFrame;
    private final DataIngestorService dataIngestorService;
    private final ImageCaptureService imageCaptureService;

    /**
     * Class that encapsulates the user login information
     */
    private LoginCredential loginCredential;

    /**
     * The Assembly that is currently being annotated by this application
     */
    private MosaicAssembly mosaicAssembly;

    /**
     * Holds a reference to the concept name that should be used to generate
     * annotations when the TileDisplayPanel fires a MouseReleased event.
     */
    private String selectedConceptName;
    private final UserLookupService userLookupService;

    /**
     * Bean class encapsulating video connection information. There's no
     * functionality here though.
     */
    private VideoConnectionInformation videoConnectionInformation;
    private final VideoControlService videoControlService;

    /**
     * Constructs ...
     *
     * @param appFrame
     * @param annotationGeneratorService
     * @param annotationLookupService
     * @param annotationUpdateService
     * @param dataIngestorService
     * @param imageCaptureService
     * @param userLookupService
     * @param videoControlService
     */
    public AppFrameController(AppFrame appFrame, AnnotationGeneratorService annotationGeneratorService,
                              AnnotationLookupService annotationLookupService,
                              AnnotationPersistenceService annotationUpdateService,
                              DataIngestorService dataIngestorService, ImageCaptureService imageCaptureService,
                              UserLookupService userLookupService, VideoControlService videoControlService) {

        this.appFrame = appFrame;
        this.annotationGeneratorService = annotationGeneratorService;
        this.annotationLookupService = annotationLookupService;
        this.annotationPersistenceService = annotationUpdateService;
        this.dataIngestorService = dataIngestorService;
        this.imageCaptureService = imageCaptureService;
        this.userLookupService = userLookupService;
        this.videoControlService = videoControlService;

        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Initializing the following services:");

            sb.append("\n\tAnnotationGeneratorService   = ").append(annotationGeneratorService);
            sb.append("\n\tAnnotationLookupService      = ").append(annotationLookupService);
            sb.append("\n\tAnnotationPersistenceService = ").append(annotationUpdateService);
            sb.append("\n\tImageCaptureService          = ").append(imageCaptureService);
            sb.append("\n\tUserLookupService            = ").append(userLookupService);
            sb.append("\n\tDataIngestorService          = ").append(dataIngestorService);
            sb.append("\n\tVideoControlService          = ").append(videoControlService);
            log.debug(sb.toString());
        }

    }

    /**
     *
     * @param videoTile The {@link VideoTile} whose image we want to capture.
     * @throws ImageCaptureException thrown if a problem occurs wile trying to
     * capture the image
     */
    public void captureImage(final VideoTile videoTile) {

        URL url = null;

        try {

            /*
             * Capture image in backgroud thread and return the URL that can
             * be used to read the image
             *
             */
            url = (URL) Worker.post(new Task() {

                @Override
                public Object run() throws IOException {
                    BufferedImage image = imageCaptureService.capture(videoTile.getVideoTime());

                    // Save image locally
                    MosaicAssembly mosaicAssembly = getMosaicAssembly();
                    String filename = videoTile.getVideoTime().getTimecode().replace(":", "_");

                    // Parse imageTarget retrieved from UserLookupServicde
                    URL baseImageTarget = userLookupService.readImageTarget(loginCredential.getLogin(),
                        loginCredential.getHostName());

                    /*
                     * The method for saving images is dependant on the URL protocol.
                     * So far, I've only implemented 'file'
                     */
                    URL imageTarget = null;

                    if (baseImageTarget.getProtocol().equalsIgnoreCase("file")) {

                        /*
                         * Build a path like [imageTarget]/[cameraId]/[sessionId]/[timecode].png
                         * where we'll save the file to.
                         */
                        File targetDirectory = new File(new File(baseImageTarget.getFile(),
                            mosaicAssembly.getCameraIdentifier()), mosaicAssembly.getSessionIdentifier());

                        if (!targetDirectory.exists()) {
                            targetDirectory.mkdirs();
                        }

                        File output = new File(targetDirectory, filename + ".png");

                        // Write out the file and return the target URL
                        if (ImageIO.write(image, "png", output)) {
                            imageTarget = output.toURI().toURL();
                        }

                        // Need to remap the imageTarget to the imageTargetMapping
                        URL imageTargetMapping = userLookupService.readImageTargetMapping(loginCredential.getLogin());
                        String imageTargetName = imageTarget.toExternalForm();
                        String mappedImageTarget = imageTargetName.replace(baseImageTarget.toExternalForm(),
                            imageTargetMapping.toExternalForm());

                        imageTarget = new URL(mappedImageTarget);


                    }
                    else {
                        throw new ImageCaptureException("The URL protocol '" + baseImageTarget.getProtocol() +
                                                        "' can not be handled for file uploading");
                    }

                    return imageTarget;
                }
            });

        }
        catch (Exception ex) {
            throw new ImageCaptureException("Failed to capture image for " + videoTile, ex);
        }

        // Set image in videoTile
        videoTile.setUrl(url);

        // Set image in UI
        appFrame.getVideoTileDisplayPanel().setVideoTile(videoTile);
    }

    /**
     * Deletes a Collection of {@link TileAnnotation}s from VARS. This will
     * delete them off in a background thread.
     * @param tileAnnotations
     */
    public void deleteTileAnnotations(final VideoTile videoTile, final Collection<TileAnnotation> tileAnnotations) {

        // Delete in background thread
        if (SwingUtilities.isEventDispatchThread()) {
            Worker.post(new Job() {

                @Override
                public Object run() {
                    for (TileAnnotation tileAnnotation : tileAnnotations) {
                        annotationPersistenceService.makeTransient(tileAnnotation);
                        videoTile.getAnnotations().remove(tileAnnotation);
                    }

                    return null;
                }

            });
        }
        else {
            for (TileAnnotation tileAnnotation : tileAnnotations) {
                annotationPersistenceService.makeTransient(tileAnnotation);
                videoTile.getAnnotations().remove(tileAnnotation);
            }
        }

    }

    public AnnotationGeneratorService getAnnotationGeneratorService() {
        return annotationGeneratorService;
    }

    public AnnotationLookupService getAnnotationLookupService() {
        return annotationLookupService;
    }

    public AnnotationPersistenceService getAnnotationPersistenceService() {
        return annotationPersistenceService;
    }

    public DataIngestorService getDataIngestorService() {
        return dataIngestorService;
    }

    public LoginCredential getLoginCredential() {
        return loginCredential;
    }

    public MosaicAssembly getMosaicAssembly() {
        return mosaicAssembly;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public String getSelectedConceptName() {
        return selectedConceptName;
    }

    public UserLookupService getUserLookupService() {
        return userLookupService;
    }

    public VideoConnectionInformation getVideoConnectionInformation() {
        return videoConnectionInformation;
    }

    public VideoControlService getVideoControlService() {
        return videoControlService;
    }

    public boolean isValidLogin(String username, char[] password) {
        return getUserLookupService().isValidUser(username, password);
    }

    public void loadTileFile(final String cameraIdentifier, final String sessionIdentifier, final URL file) {

        // TODO save old annotations.

        // Remove all items from the list in the UI
        appFrame.getVideoTileCellRenderer().clearThumbnailCache();

        DefaultListModel listModel = appFrame.getTileListModel();

        listModel.clear();

        // Create annotations off of EDT
        MosaicAssembly aMosaicAssembly = (MosaicAssembly) Worker.post(new Job() {

            @Override
            public Object run() {
                List<Tile> tiles = dataIngestorService.loadTiles(file);

                /**
                 * Convert to videoTiles and Create a MosaicAssembly
                 */
                return annotationLookupService.convertToMosaicAssembly(cameraIdentifier, sessionIdentifier, tiles);

            }
        });

        // Display annotations in a list in the UI.
        setMosaicAssembly(aMosaicAssembly);
    }

    /**
     * This method shoudl be called on the Swing EventDispatchThread
     * @param username
     * @param password
     * @throws CoreException Thrown if unable to complete login process
     */
    public void login(final String username, char[] password) {

        // TODO fetch useDefinedBUttons
        if (!isValidLogin(username, password)) {
            setLoginCredential(null);
            appFrame.getTabbedPane().removeAll();

            throw new CoreException("Login failed");
        }


        /*
         * Store the login information
         */
        String hostname = null;

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex) {
            throw new CoreException("Unable to get hostname", ex);
        }

        final LoginCredential aLoginCredential = new LoginCredential(username, password, hostname);

        setLoginCredential(aLoginCredential);

        /*
         * Look up the users predefined buttons and configure the UI
         */
        List<UserDefinedButtonInfo> buttonInfo = (List<UserDefinedButtonInfo>) Worker.post(new Job() {

            @Override
            public Object run() {
                return getUserLookupService().findButtonInformation(username);
            }
        });

        appFrame.configureTabbedPanel(buttonInfo);
    }

    /**
     *
     * @param cameraID
     * @param sessionID
     * @throws CoreException Thrown if database lookup fails. This method
     *      should handle the failure gracefully but the exception is thrown so
     *      that the UI can notify the user
     */
    public void openMosaicAssembly(final String cameraID, final String sessionID) {
        MosaicAssembly aMosaicAssembly = null;

        try {
            aMosaicAssembly = (MosaicAssembly) Worker.post(new Job() {

                @Override
                public Object run() {
                    return annotationLookupService.findByCameraAndSessionIdentifiers(cameraID, sessionID);
                }
            });

            setMosaicAssembly(aMosaicAssembly);
        }
        catch (AnnotationLookupException e) {
            setMosaicAssembly(null);

            String msg = "Failed to retrieve mosaic for '" + cameraID + "-" + sessionID + "'";

            throw new CoreException(msg, e);
        }
    }

    public void persistMosaicAssembly(final MosaicAssembly mosaicAssembly) {

        // Execute update off of EDT
        Worker.post(new Job() {

            @Override
            public Object run() {
                annotationPersistenceService.makePersistent(mosaicAssembly);

                return null;
            }
        });

    }

    /**
     * Save/updates a {@link VideoTile} and any contained {@link TileAnnotation}s.
     * The work is split off into a background thread so the main UI should
     * redraw during this operation.
     *
     * @param videoTile The {@link VideoTile} to save or update
     */
    public void persistTile(final VideoTile videoTile) {

        /*
         * Save in background thread if on the EDT.
         */
        if (SwingUtilities.isEventDispatchThread()) {
            Worker.post(new Job() {

                @Override
                public Object run() {
                    annotationPersistenceService.makePersistent(videoTile);

                    return null;
                }

            });
        }
        else {
            annotationPersistenceService.makePersistent(videoTile);
        }
    }

    protected void setLoginCredential(LoginCredential loginCredential) {
        final LoginCredential oldLoginCredential = this.loginCredential;

        this.loginCredential = loginCredential;
        propertyChangeSupport.firePropertyChange(LOGIN_CREDENTIAL_PROPERTY, oldLoginCredential, loginCredential);
    }

    public void setMosaicAssembly(MosaicAssembly<VideoTile> mosaicAssembly) {
        MosaicAssembly oldMosaicAssembly = this.mosaicAssembly;

        this.mosaicAssembly = mosaicAssembly;

        /*
         * Clear the list and Poplate the list with all the new tiles
         */
        DefaultListModel listModel = appFrame.getTileListModel();

        listModel.clear();

        if (mosaicAssembly != null) {
            List<VideoTile> videoTiles = mosaicAssembly.getTiles();

            Collections.sort(videoTiles, new VideoTileTimecodeComparator());

            for (VideoTile tile : videoTiles) {
                listModel.addElement(tile);
            }
        }

        propertyChangeSupport.firePropertyChange(MOSAIC_ASSEMBLY_PROPERTY, oldMosaicAssembly, mosaicAssembly);

    }

    public void setSelectedConceptName(String selectedConceptName) {
        final String oldSelectedConceptName = this.selectedConceptName;

        this.selectedConceptName = selectedConceptName;
        propertyChangeSupport.firePropertyChange(SELECTED_CONCEPTNAME_PROPERTY, oldSelectedConceptName,
                selectedConceptName);
    }

    protected void setVideoConnectionInformation(VideoConnectionInformation videoConnectionInformation) {
        final VideoConnectionInformation oldConnectionInfo = this.videoConnectionInformation;

        this.videoConnectionInformation = videoConnectionInformation;
        propertyChangeSupport.firePropertyChange(VIDEO_CONNECTION_INFORMATION_PROPERTY, oldConnectionInfo,
                videoConnectionInformation);
    }

    public void uploadImage(VideoTile videoTile) {

        // DO nothing. Image upload and URL remapping is handled in captureImage
    }

    /**
     * This listener will update the old {@link MosaicAssembly} in the data store
     * when it's closed
     */
    private class ChangeMosaicPropertyListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            MosaicAssembly old = (MosaicAssembly) evt.getOldValue();

            getAnnotationPersistenceService().makePersistent(old);
        }
    }


    /**
     * Base class for managing database transactions for the SIMPA application.
     * @param <T>
     */
    private abstract class DatabaseTransactionSubscriber<T> implements EventTopicSubscriber<T> {

        abstract void databaseTransaction(T object);

        abstract void handlePersistenceException(AnnotationPersistenceException ex);

        public void onEvent(String arg0, final T arg1) {

            /*
             * Save in background thread if on the EDT.
             */
            if (SwingUtilities.isEventDispatchThread()) {
                Worker.post(new Job() {

                    @Override
                    public Object run() {
                        try {
                            databaseTransaction(arg1);
                        }
                        catch (AnnotationPersistenceException ex) {
                            handlePersistenceException(ex);
                        }

                        return null;
                    }

                });
            }
            else {
                try {
                    databaseTransaction(arg1);
                }
                catch (AnnotationPersistenceException ex) {
                    handlePersistenceException(ex);
                }
            }
        }
    }


    private class DeleteTileAnnotationSubscriber extends DatabaseTransactionSubscriber<List<TileAnnotation>> {

        @Override
        void databaseTransaction(List<TileAnnotation> object) {
            for (TileAnnotation videoTile : object) {
                annotationPersistenceService.makeTransient(videoTile);
            }
        }

        @Override
        void handlePersistenceException(AnnotationPersistenceException ex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }


    private class DeleteVideoTileSubscriber extends DatabaseTransactionSubscriber<List<VideoTile>> {

        @Override
        void databaseTransaction(List<VideoTile> object) {
            for (VideoTile videoTile : object) {
                annotationPersistenceService.makeTransient(videoTile);
            }
        }

        @Override
        void handlePersistenceException(AnnotationPersistenceException ex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }


    private class PersistLoginCredentialSubscriber extends DatabaseTransactionSubscriber<IUserAccount> {

        @Override
        void databaseTransaction(IUserAccount object) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        void handlePersistenceException(AnnotationPersistenceException ex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }


    private class PersistTileAnnotationSubscriber extends DatabaseTransactionSubscriber<List<TileAnnotation>> {

        @Override
        void databaseTransaction(List<TileAnnotation> object) {
            for (TileAnnotation videoTile : object) {
                annotationPersistenceService.makePersistent(videoTile);
            }
        }

        @Override
        void handlePersistenceException(AnnotationPersistenceException ex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }


    private class PersistVideoTileSubscriber extends DatabaseTransactionSubscriber<List<VideoTile>> {

        @Override
        void databaseTransaction(List<VideoTile> object) {
            for (VideoTile videoTile : object) {
                annotationPersistenceService.makePersistent(videoTile);
            }
        }

        @Override
        void handlePersistenceException(AnnotationPersistenceException ex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
