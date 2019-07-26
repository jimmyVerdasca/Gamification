package components;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * JButton that possess an icone. The icone is permanently scaled to the same
 * size that the button
 * 
 * @author jimmy
 */
public class JButtonIconResizable extends JButton {
    
    /**
     * image to scale to the button size
     */
    private final Image img;
    
    /**
     * constructor
     * @param image icone to scale in the button
     */
    public JButtonIconResizable(Image image) {
        img = image;
    }
    
    
    /**
     * draw the button and scale his icone to his own size
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setIcon(new ImageIcon(img.getScaledInstance(getWidth(), getHeight(), java.awt.Image.SCALE_SMOOTH )));
    }
}
