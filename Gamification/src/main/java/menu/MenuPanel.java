package menu;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Class that represent the view when we are in the menu. 
 * Final buttons expected :
 * 
 * Play solo
 * Play Cooperative
 * Play Competitive
 * Options
 * Quit
 * 
 * @author jimmy
 */
public class MenuPanel extends JPanel {

    /**
     * Constructor
     * Build the menu buttons spaced vertically
     * 
     * @param menu instance of JFrame that called this instance (parent)
     */
    public MenuPanel(Menu menu) {
        
        Container cp = menu.getContentPane();
        
        BoxLayout vfLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(vfLayout);
        
        int vSpace = Toolkit.getDefaultToolkit().getScreenSize().height / 5;
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        JButton buttonPlay = addButton("PLAY");
        add(buttonPlay);
        
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        JButton buttonOptions = addButton("OPTIONS");
        add(buttonOptions);
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        JButton buttonQuit = addButton("QUIT");
        add(buttonQuit);
        
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        buttonPlay.addActionListener((ActionEvent e) -> {
            menu.play(menu);
        });
        
        buttonQuit.addActionListener((ActionEvent e) -> {
            menu.quit();
        });
        cp.add(this);
    }
    
    /**
     * Ease to add buttons just by giving his text.
     * 
     * @param text of the button to add
     * @return the new JButton created.
     */
    private JButton addButton(String text) {
        JButton button = new JButton(text){
            {
                setSize(400, 150);
                setMaximumSize(getSize());
            }
        };
        button.setAlignmentX(CENTER_ALIGNMENT);
        return button;
    }
}
