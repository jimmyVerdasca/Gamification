package components;

import heigvd.gamification.rules.RulesName;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * JPanel containing the image of an objectif and drawing it
 * 
 * @author jimmy
 */
public class ImageJPanel extends JPanel {

    /**
     * image of the objectif
     */
    private BufferedImage image;

    /**
     * constructor looking for the image relatively to the rule name given in
     * the medls folder
     * 
     * @param rulesName Rule that we will show 
     */
    public ImageJPanel(RulesName rulesName) {
       try {
            image = ImageIO.read(ImageJPanel.class
                    .getResource("/assets/ui/medals/" + rulesName.name() + ".png"));
       } catch (IOException ex) {
           
       }
    }

    /**
     * draw the image of the objectiv at the same size that the panel
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image.getScaledInstance(getWidth(), getHeight(), java.awt.Image.SCALE_SMOOTH ), 0, 0, this);
    }

    /**
     * set the rule shown
     * 
     * @param rulesName the new rule to draw the image
     */
    public void setImage(RulesName rulesName) {
        try {
            image = ImageIO.read(ImageJPanel.class
                    .getResource("/assets/ui/medals/" + rulesName.name() + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(ImageJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}