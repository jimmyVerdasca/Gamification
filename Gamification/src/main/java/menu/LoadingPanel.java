package menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Loading Panel while we create a game
 * 
 * show a load bar and a text to display how far in the preparation of the game
 * we are.
 * 
 * If an error occur (for example we can't reach a Joy-Con). It display the
 * error. Wait some seconds and return to the menu.
 * 
 * @author jimmy
 */
public class LoadingPanel extends JPanel {

    private JProgressBar bar;
    private JLabel label;
    private final Menu window;
    
    /**
     * constructor
     * 
     * @param window parent JFrame
     */
    public LoadingPanel(Menu window) {
        super();
        this.window = window;
        
        BoxLayout vfLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(vfLayout);
        
        int vSpace = Toolkit.getDefaultToolkit().getScreenSize().height / 3;
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        bar = new JProgressBar(0, 100);
        final int PAD = 10;
        Dimension barDim = new Dimension(
                Toolkit.getDefaultToolkit().getScreenSize().height - 2 * PAD,
                Toolkit.getDefaultToolkit().getScreenSize().height / 30);
        bar.setPreferredSize(barDim);
        bar.setMaximumSize(barDim);
        bar.setAlignmentX(CENTER_ALIGNMENT);
        
        label = new JLabel();
        label.setFont(new Font("Papyrus", Font.BOLD, 40));
        label.setAlignmentX(CENTER_ALIGNMENT);
        
        add(bar);
        add(label);
    }
    
    /**
     * set the progress bar to a percent of his load and update the JLabel text
     * 
     * @param percent we want the progress bar filled
     * @param text the new state text
     */
    public void setState(int percent, String text) {
        bar.setValue(percent);
        label.setText(text);
    }

    /**
     * display an error message, wait 5 seconds and go back to the menu
     * 
     * @param i
     * @param errorMsg 
     */
    public void setERROR(String errorMsg) {
        label.setForeground(Color.red);
        label.setText("<html>" + errorMsg + "<html>");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(LoadingPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        window.menu(window);
    }
}
