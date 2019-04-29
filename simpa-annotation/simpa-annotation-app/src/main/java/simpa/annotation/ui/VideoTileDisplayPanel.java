/*
 * @(#)VideoTileDisplayPanel.java   2009.12.30 at 09:29:06 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package simpa.annotation.ui;

import com.google.inject.Inject;
import foxtrot.Job;
import foxtrot.Worker;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.AnnotationLookupService;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.TileAnnotation;
import simpa.annotation.TileAnnotationSpatialLocation;
import simpa.annotation.VideoTile;

/**
 * A {@link JPanel} for displaying a Tile and any annotations within the tile.
 * Allows for interactively creating, selecting annotations.
 * @author brian
 */
public class VideoTileDisplayPanel extends ImageWithCrossHairPanel {

    /** Length of arms on an annotation marker. Selected annotaitons use 2x this */
    final int crossHairArmLength = 7;
    private final StringBuilder locationString = new StringBuilder();

    /** A List of all annotations located within the bounds of the current tile **/
    private List<TileAnnotation> annotations = Collections.synchronizedList(new ArrayList<TileAnnotation>());

    /** A List of annotations tha were selected using the boundingBox */
    private List<TileAnnotation> selectedAnnotations = Collections.synchronizedList(new ArrayList<TileAnnotation>());
    private Map<TileAnnotation, TileAnnotationSpatialLocation> selectedAnnotationPositionMap = Collections
        .synchronizedMap(new HashMap<TileAnnotation, TileAnnotationSpatialLocation>());
    private final Logger log = LoggerFactory.getLogger(VideoTileDisplayPanel.class);

    /** Color for non-selected annotations */
    private final Color annotationColor = new Color(255, 0, 0, 180);

    /** Color for selected annotations */
    private final Color selectedColor = new Color(0, 255, 0, 180);
    private TileAnnotationSelectionEventSupport selectedAnnotationSupport = new TileAnnotationSelectionEventSupport();
    private NewTileAnnotationEventSupport newAnnotationSupport = new NewTileAnnotationEventSupport();

    /**
     * Record of the location of the most recent mousePress event. Used for
     * drawing the boundingBox
     */
    private Point2D clickPoint = new Point2D.Double();

    /**
     * This font is used to draw the concept name of selected concepts.
     * TODO add set/get for this and set in owner component
     */
    Font selectionFont = new Font("Sans Serif", Font.PLAIN, 10);

    /**
     * Bounding box is used in the UI to select annotation
     */
    private Rectangle2D boundingBox;

    /** MVC Controller for this UI widget **/
    private final VideoTileDisplayPanelController controller;

    /**
     * Formatter for the position (in meters). Don't monkey with it.
     */
    protected final NumberFormat numberFormat;

    /** The tile that is currently being represented in this UI widget */
    private VideoTile videoTile;

    /**
     * The only constructor. The dependencies can be injected using Guice if you
     * don't feel like passing them in by hand.
     *
     * @param annotationGeneratorService
     * @param annotationLookupService
     * @param annotationUpdateService
     */
    @Inject
    public VideoTileDisplayPanel(AnnotationGeneratorService annotationGeneratorService,
                                 AnnotationLookupService annotationLookupService,
                                 AnnotationPersistenceService annotationUpdateService) {
        super();
        controller = new VideoTileDisplayPanelController(this, annotationGeneratorService, annotationLookupService,
                annotationUpdateService);
        addMouseListener(new AnnotationMouseListener());
        addMouseMotionListener(new AnnotationMouseMotionListener());

        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        /*
         * When annotations are selected we want to keep track of their spatial
         * position within the display.
         */
        selectedAnnotationSupport.add(new TileAnnotationSelectionListener() {

            public void process(TileAnnotationSelectionEvent e) {
                List<TileAnnotation> sa = e.getSelectedTileAnnotations();
                selectedAnnotationPositionMap.clear();

                for (TileAnnotation tileAnnotation : sa) {
                    try {
                        selectedAnnotationPositionMap.put(tileAnnotation,
                                                          new TileAnnotationSpatialLocation(tileAnnotation, videoTile));
                    }
                    catch (Exception ex) {
                        log.warn("Failed to generate a spatial location", ex);
                    }
                }
            }

        });

    }

    /**
     *
     * @param listener
     */
    public void addNewTileAnnotationListener(NewTileAnnotationListener listener) {
        newAnnotationSupport.add(listener);
    }

    /**
     *
     * @param listener
     */
    public void addTileAnnotationSelectionListener(TileAnnotationSelectionListener listener) {
        selectedAnnotationSupport.add(listener);
    }

    /**
     * @return The list of annotations found within this tile. This list can be
     * modified, but you should call the {@link VideoTileDisplayPanel#repaint() }
     * method so that
     * the user interface is updated.
     */
    public Collection<TileAnnotation> getAnnotations() {
        return annotations;
    }

    /**
     *
     * @return The list of selected annotations. This list can be modified, but
     * you should call the {@link VideoTileDisplayPanel#repaint() } method so that
     * the user interface is updated.
     */
    public Collection<TileAnnotation> getSelectedAnnotations() {
        return selectedAnnotations;
    }

    /**
     * @return
     */
    public Font getSelectionFont() {
        return selectionFont;
    }

    /**
     * @return
     */
    public VideoTile getVideoTile() {
        return videoTile;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw the tiles
        g2.setPaintMode();    // Make sure XOR is turned off

        g2.setStroke(new BasicStroke(3));

        for (int i = 0; i < annotations.size(); i++) {
            TileAnnotation tileAnnotation = annotations.get(i);
            int armLength = crossHairArmLength;    // Cross hair arm length
            if (selectedAnnotations.contains(tileAnnotation)) {

                g2.setPaint(selectedColor);
                armLength = crossHairArmLength * 2;

                // Draw name of concept for selected annotations
                String conceptName = tileAnnotation.getConceptName();
                if (conceptName != null) {

                    // Calculate postion of annotation and write the position
                    // Position to draw the concept name
                    float xf = tileAnnotation.getX() + 5;
                    float yf = tileAnnotation.getY();

                    g2.setFont(selectionFont);
                    g2.drawString(conceptName, xf, yf);

                    /*
                     * Draw the position of the annotation below the concept name
                     */
                    TileAnnotationSpatialLocation spatialLocation = selectedAnnotationPositionMap.get(tileAnnotation);
                    if (spatialLocation != null) {

                        // Draw the position in meters below the concept name
                        FontRenderContext fontRenderContext = g2.getFontRenderContext();
                        LineMetrics lineMetrics = g2.getFont().getLineMetrics(conceptName, fontRenderContext);
                        yf = yf + lineMetrics.getHeight() + 2;                // Position below the concept name
                        locationString.delete(0, locationString.length());    // Clear the stringbuilder
                        locationString.append("(");
                        locationString.append(numberFormat.format(spatialLocation.getXInMeters()));
                        locationString.append(", ");
                        locationString.append(numberFormat.format(spatialLocation.getYInMeters()));
                        locationString.append(")");
                        g2.drawString(locationString.toString(), xf, yf);
                    }
                }
            }
            else {
                g2.setPaint(annotationColor);
            }

            int x = tileAnnotation.getX();
            int y = tileAnnotation.getY();
            GeneralPath gp = new GeneralPath();
            gp.moveTo(x - armLength, y - armLength);
            gp.lineTo(x + armLength, y + armLength);
            gp.moveTo(x + armLength, y - armLength);
            gp.lineTo(x - armLength, y + armLength);
            g2.draw(gp);

        }

        if (boundingBox != null) {

            // Draw the bounding box
            g2.setPaint(Color.MAGENTA);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 2, 2 }, 2));
            g2.draw(boundingBox);
        }

    }

    /**
     *
     * @param selectionFont
     */
    public void setSelectionFont(Font selectionFont) {
        this.selectionFont = selectionFont;
        repaint();
    }

    /**
     *
     * @param videoTile
     */
    public void setVideoTile(final VideoTile videoTile) {

        /**
         * Invoke all the swing relates stuff on the EventDispath Thread
         */
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                annotations.clear();
                selectedAnnotations.clear();
                selectedAnnotationSupport.notifyListeners(new SelectionEvent());

                VideoTileDisplayPanel.this.videoTile = videoTile;
                setImage(null);

                if (videoTile != null) {
                    final URL url = videoTile.getUrl();


                    if (url != null) {

                        // Show a wait indicator so that the user knows that the app is working
                        WaitIndicator waitIndicator = new SpinningDialWaitIndicator(VideoTileDisplayPanel.this);

                        // Load image off of EventDispatch thread
                        BufferedImage image = (BufferedImage) Worker.post(new Job() {

                            @Override
                            public Object run() {
                                BufferedImage im = null;
                                try {
                                    im = ImageIO.read(url);
                                }
                                catch (IOException ex) {
                                    log.warn("Unable to read image from " + url.toExternalForm(), ex);

                                    // TODO throw CoreException so app can catch it and notify user the image wouldn't load
                                }

                                return im;
                            }
                        });

                        setImage(image);
                        waitIndicator.dispose();
                    }

                    // Add the annotations from the videoTile to this UI component
                    annotations.addAll(videoTile.getAnnotations());
                }

                /* Load all TileAnnotations that may occur within the bound of this tile
                 * even if they were made on a different tile that overlaps wth this one
                 */

                //annotations.addAll(controller.lookupTileAnnotations(tile));
                VideoTileDisplayPanel.this.repaint();
            }
        });

    }

    /**
     * This MouseListener handles the drawing of annotations and the selection
     * of annotaitons within a bonding box.
     */
    private class AnnotationMouseListener extends MouseAdapter {

        /**
         *
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {

            // Set corner of boundng box
            clickPoint.setLocation((double) e.getX(), (double) e.getY());
        }

        /**
         *
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {

            if (e.getButton() != MouseEvent.BUTTON1) {
                selectedAnnotations.clear();
                selectedAnnotationSupport.notifyListeners(new SelectionEvent());
                repaint();
            }
            else {

                /*
                 * When the mouse is released we need to check if we were creating a
                 * boundingBox for selecting annotations (i.e. if the mouse was
                 * dragged). The order here is:
                 * 1) Redraw all the deselected annotations
                 * 2) if no bounding box, create a new annotation
                 * 3) If bounding box, find all the annotations within it's bounds.
                 *    Select them and redraw them.
                 */

                /*
                 * Redraw deselected annotations
                 */
                int box = crossHairArmLength * 3;    // defines width, height to be redrawn
                List<TileAnnotation> s = new ArrayList<TileAnnotation>(selectedAnnotations);
                selectedAnnotations.clear();         // Deselect all previously selected annotations

                for (TileAnnotation tileAnnotation : s) {
                    repaint(tileAnnotation.getX() - box, tileAnnotation.getY() - box, box * 2, box * 2);
                }

                if (boundingBox == null) {

                    /*
                     * If the mouse is NOT being dragged then create a TileAnnotation
                     */
                    BufferedImage image = getImage();
                    if (image != null) {
                        int w = image.getWidth();
                        int h = image.getHeight();
                        int x = e.getX();
                        int y = e.getY();

                        // Ony mark annotations within the image boudnaries
                        if ((x < w) && (y < h)) {
                            TileAnnotation a = controller.newTileAnnotation();
                            a.setX(x);
                            a.setY(y);
                            annotations.add(a);
                            newAnnotationSupport.notifyListeners(new TileAnnotationEvent(a));
                            selectedAnnotations.add(a);
                            selectedAnnotationSupport.notifyListeners(new SelectionEvent());
                            repaint(a.getX() - box, a.getY() - box, box * 2, box * 2);
                        }
                    }
                }
                else {

                    /*
                     * If mouse was being dragged then make the boundng box disappear
                     * on mouse release. Select all annotations that were with in the
                     * bounding box and redraw all the annotations.
                     */
                    Rectangle r = boundingBox.getBounds();
                    boundingBox = null;

                    //  select all tiles within bounds
                    for (TileAnnotation tileAnnotation : annotations) {
                        if (r.contains(tileAnnotation.getX(), tileAnnotation.getY())) {
                            selectedAnnotations.add(tileAnnotation);
                        }
                    }

                    selectedAnnotationSupport.notifyListeners(new SelectionEvent());

                    repaint();
                }
            }
        }
    }


//    public static void main(String[] args) throws IOException {
//
//        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        URL url = f.getClass().getResource("/images/refImage34.png");
//        BufferedImage image = ImageIO.read(url);
//        Injector injector = Guice.createInjector(new MockModule());
//        VideoTileDisplayPanel p = injector.getInstance(VideoTileDisplayPanel.class);
//
//        // Add a default concept name to new TileAnnotations
//        p.addNewTileAnnotationListener(new NewTileAnnotationListener() {
//
//            public void process(NewTileAnnotationEvent e) {
//                e.getTileAnnotation().setConceptName("Object");
//            }
//        });
//
//        Tile tile = new TileImpl(1, 2, 3, url, null, new Date());
//        VideoTile videoTile = new VideoTileImpl(tile, new VideoTimeImpl("00:00:00:00", new Date()));
//        p.setVideoTile(videoTile);
//
//        f.setLayout(new BorderLayout());
//        f.add(p, BorderLayout.CENTER);
//        f.setSize(image.getWidth(), image.getHeight());
//        f.setVisible(true);
//    }

    /**
     * Draws a boundng box when the mouse is dragged
     */
    private class AnnotationMouseMotionListener implements MouseMotionListener {

        /**
         *
         * @param e
         */
        public void mouseDragged(MouseEvent e) {

            // Draw bounding box
            int x1 = e.getX();
            int y1 = e.getY();
            int x0 = (int) clickPoint.getX();
            int y0 = (int) clickPoint.getY();
            int w = Math.abs(x1 - x0);
            int h = Math.abs(y1 - y0);

            int x = Math.min(x0, x1);
            int y = Math.min(y0, y1);

            // Minimize he redraw area
            Rectangle2D b = boundingBox;
            boundingBox = new Rectangle2D.Double(x, y, w, h);

            if (b == null) {
                repaint(boundingBox.getBounds());
            }
            else {
                repaint();
            }

        }

        /**
         *
         * @param e
         */
        public void mouseMoved(MouseEvent e) {

            // Do nothing
        }
    }


    /**
     * Support for NewTileAnnotationEvent notification
     */
    private class NewTileAnnotationEventSupport {

        private Collection<NewTileAnnotationListener> listeners = Collections.synchronizedList(
            new ArrayList<NewTileAnnotationListener>());

        void add(NewTileAnnotationListener listener) {
            listeners.add(listener);
        }

        void notifyListeners(NewTileAnnotationEvent e) {
            for (NewTileAnnotationListener listener : listeners) {
                listener.process(e);
            }
        }

        void remove(NewTileAnnotationListener listener) {
            listeners.remove(listener);
        }
    }


    /**
     * Implementation of TileAnnotationSelectionEvent
     */
    protected class SelectionEvent implements TileAnnotationSelectionEvent {

        private final List<TileAnnotation> selectedTileAnnotations;

        SelectionEvent() {
            selectedTileAnnotations = new ArrayList<TileAnnotation>(selectedAnnotations);
        }

        /**
         * @return
         */
        public List<TileAnnotation> getSelectedTileAnnotations() {
            return selectedTileAnnotations;
        }
    }


    /**
     * Implementation of NewTileAnnotationEvent
     */
    protected class TileAnnotationEvent implements NewTileAnnotationEvent {

        private final TileAnnotation tileAnnotation;

        TileAnnotationEvent(TileAnnotation tileAnnotation) {
            this.tileAnnotation = tileAnnotation;
        }

        /**
         * @return
         */
        public TileAnnotation getTileAnnotation() {
            return tileAnnotation;
        }
    }


    /**
     * Support for TileAnnotationSelectionEvent noification
     */
    private class TileAnnotationSelectionEventSupport {

        private Collection<TileAnnotationSelectionListener> listeners = Collections.synchronizedList(
            new ArrayList<TileAnnotationSelectionListener>());

        void add(TileAnnotationSelectionListener listener) {
            listeners.add(listener);
        }

        void notifyListeners(TileAnnotationSelectionEvent e) {
            for (TileAnnotationSelectionListener listener : listeners) {
                listener.process(e);
            }
        }

        void remove(TileAnnotationSelectionListener listener) {
            listeners.remove(listener);
        }
    }
}
