package components;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author jimmy
 */
public class JButtonIconResizable extends JButton {
    private final Image img;
    public JButtonIconResizable(Image image) throws IOException {
        img = image;
    }
    
    
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setIcon(new ImageIcon(img.getScaledInstance(getWidth(), getHeight(), java.awt.Image.SCALE_SMOOTH )));
    }
}
