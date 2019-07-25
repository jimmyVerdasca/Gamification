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
 *
 * @author jimmy
 */
public class ImageJPanel extends JPanel {

    private BufferedImage image;

    public ImageJPanel(RulesName rulesName) {
       try {
            image = ImageIO.read(ImageJPanel.class
                    .getResource("/assets/ui/medals/" + rulesName.name() + ".png"));
       } catch (IOException ex) {
            // handle exception...
       }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image.getScaledInstance(getWidth(), getHeight(), java.awt.Image.SCALE_SMOOTH ), 0, 0, this);
    }

    public void setImage(RulesName rulesName) {
        try {
            image = ImageIO.read(ImageJPanel.class
                    .getResource("/assets/ui/medals/" + rulesName.name() + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(ImageJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}