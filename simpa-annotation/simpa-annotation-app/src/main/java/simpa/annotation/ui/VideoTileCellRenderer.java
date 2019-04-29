package simpa.annotation.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.concurrent.ExecutionException;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.mbari.swingworker.SwingWorker;
import org.slf4j.LoggerFactory;
import simpa.annotation.VideoTime;
import simpa.annotation.VideoTile;
import simpa.core.CameraPosition;
import simpa.core.GlobalParameters;

/**
 * @author brian
 * @since Jun 5, 2008 2:17:45 PM
 */
public class VideoTileCellRenderer extends JLabel implements ListCellRenderer {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(VideoTileCellRenderer.class);
    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);
    private static final Color LOWLIGHT_COLOR = new Color(0, 0, 0);
    private static final Color NOT_VALID_COLOR = new Color(128, 0, 0);
    private final Map<URL, ImageIcon> thumbnailMap = new HashMap<URL, ImageIcon>();
    private static final int ICON_WIDTH = 120;    // Represents and empty image
    private ImageIcon nullImage;
    private StringBuilder stringBuilder = new StringBuilder();

    /**
     * Creates a <code>JLabel</code> instance with
     * no image and with an empty string for the title.
     * The label is centered vertically
     * in its display area.
     * The label's contents, once set, will be displayed on the leading edge
     * of the label's display area.
     */
    public VideoTileCellRenderer() {
        super();
        setOpaque(true);
        setIconTextGap(12);
        setForeground(Color.WHITE);
    }

    /**
     * 
     * @return An iamge icon to be used if we ail to load the original
     */
    public ImageIcon getNullImage() {
        if (nullImage == null) {
            // TODO draw null image
            int width = ICON_WIDTH;
            int height = ICON_WIDTH;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2 = image.createGraphics();
            g2.setPaint(Color.BLACK);
            g2.drawRect(0, 0, width, height);
            g2.dispose();
            nullImage = new ImageIcon(image);
        }
        return nullImage;
    }

    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     *
     * @param list         The JList we're painting.
     * @param value        The value returned by list.getModel().getElementAt(index).
     * @param index        The cells index.
     * @param isSelected   True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     * @see javax.swing.JList
     * @see javax.swing.ListSelectionModel
     * @see javax.swing.ListModel
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        VideoTile tile = (VideoTile) value;

        // We want to display a thumbnail
        final URL url = tile.getUrl();
        if (url == null) {
            setIcon(getNullImage());
        }
        else {
            ImageIcon imageIcon = thumbnailMap.get(url);
            if (imageIcon == null) {
                setIcon(getNullImage());

                // TODO SIMPA has to read each image twice. Once to generate a
                //   thumbnail and then a second time to get the image size.
                //   Should add a get thumbnail method to VideoTile so that it
                //   can be done in one pass.
                // split the loader off into a seperate thread
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {

                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        ImageIcon icon = null;
                        try {
                            final BufferedImage originalImage = ImageIO.read(url);
                            icon = new ImageIcon(generateThumbnail(originalImage));
                        }
                        catch (IOException e) {
                            icon = getNullImage();
                        }
                        return icon;
                    }

                    @Override
                    protected void done() {
                        try {
                            ImageIcon icon = get();
                            thumbnailMap.put(url, get());
                            setIcon(icon);
                            repaint();
                        } catch (InterruptedException ex) {
                            log.error("Interrupted while loading '" + url + "'", ex);
                        } catch (ExecutionException ex) {
                            log.error("Excution failed while loading '" + url + "'", ex);
                        }
                    }
                };

                worker.execute();
            }
            else {
                setIcon(imageIcon);
            }
        }

        final VideoTime tapeTime = tile.getVideoTime();
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append("<html><b>");
        stringBuilder.append(tapeTime.getTimecode());
        // tapeTime.getDate() can be null. Trap for that here.
        String dateString = (tapeTime.getDate() == null) ? "No Date Available" : 
                GlobalParameters.DATE_FORMAT_UTC.format(tapeTime.getDate());
        stringBuilder.append("</b><br>").append(dateString);
        stringBuilder.append("</html>");
        setText(stringBuilder.toString());
        
        if (isSelected) {
            setBackground(HIGHLIGHT_COLOR);
        }
        else if (!isValidVideoTile(tile)) {
            setBackground(NOT_VALID_COLOR);
        }
        else {
            setBackground(LOWLIGHT_COLOR);
            setToolTipText(tapeTime.getTimecode());
        }

        return this;
    }
    
    /**
     * HACK!! Need a way to indicate if the video tile is no really a valid one.
     * FOr example, SIMPA may load a bunch of annotations made in VARS that 
     * aren't really appropriate for annotating in SIMPA. If a tile isn't really
     * valid all we do is mark it red in the JList.
     * 
     * @param videoTile The videoTile to validate
     * @return <b>true</b> if he tile is considered valid for annotation. <b>false</b>
     *      otherwise.
     */
    public static boolean isValidVideoTile(final VideoTile videoTile) {
        final CameraPosition cameraPosition = videoTile.getCameraPosition();
        return (videoTile.getTileIndex() > 0) && (cameraPosition.getZ() != 0);
    }

    protected static BufferedImage generateThumbnail(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int targetWidth = ICON_WIDTH;
        int targetHeight = (int) Math.round((double) height * ((double) targetWidth / (double) width));
        double scaleWidth = (double) targetWidth / (double) width;
        double scaleHeight = (double) targetHeight / (double) height;

        BufferedImage thumbnailImage;
        AffineTransform transform = new AffineTransform();
        transform.scale(scaleWidth, scaleHeight);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        // FIXME eror is being thrown here. negative index into aray
        thumbnailImage = op.filter(originalImage, null);
        return thumbnailImage;
    }

    /**
     * This CellRenderer caches thumbnail images for display. We may want to 
     * clear the thumbnail cache if we use a new set of Tiles
     */
    public void clearThumbnailCache() {
        thumbnailMap.clear();
    }
}
