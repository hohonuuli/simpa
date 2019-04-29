/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author brian
 */
public class ImageWithCrossHairPanel extends JPanel {
    
    private Shape crossHair;
    
    private BufferedImage image;


    public ImageWithCrossHairPanel() {
        initialize();
    }

    protected void initialize() {
        addMouseMotionListener(new CrossHairMouseMotionListener());
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            g2.drawImage(image, null, this);
        }
        else {
            g2.setPaint(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.setXORMode(Color.WHITE);
        
        if (crossHair != null) {
            g2.draw(crossHair);
        }
        g2.setPaintMode();
        
    }
    
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }
    
    public static void main(String[] args) throws IOException {
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        URL url = f.getClass().getResource("/images/BrianSchlining.jpg");
        BufferedImage image = ImageIO.read(url);
        ImageWithCrossHairPanel p = new ImageWithCrossHairPanel();
        p.setImage(image);
        
        f.setLayout(new BorderLayout());
        f.add(p, BorderLayout.CENTER);
        f.setSize(image.getWidth(), image.getHeight());
        f.setVisible(true);
    }
    
    /**
     * Creates a cross hair based on the location of the mouse.
     */
    private class CrossHairMouseMotionListener implements MouseMotionListener {

        public void mouseDragged(MouseEvent e) {
            // Do nothing 
        }

        public void mouseMoved(MouseEvent e) {
            
            if (image != null) {
                int x = e.getX();
                int y = e.getY();
                int w = image.getWidth();
                int h = image.getHeight();

                /* 
                 * Create new crossHair. Only draw portions that are over the image!
                 */
                GeneralPath gp  = new GeneralPath();
                if (y <= h) {
                //if (x <= w) {
                    gp.moveTo(0, y);
                    gp.lineTo(w, y);
                }
                if (x <= w) {
                    gp.moveTo(x, 0);
                    gp.lineTo(x, h);
                }
                crossHair = gp;

                /*
                 * Redraw as little as possible!
                 */
                repaint(x, 0, 3, h);
                repaint(0, y, w, 3);
            }
            
        }
        
    }  
    
    

}