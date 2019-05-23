package menu;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.Border;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author jimmy
 */
public class MenuPanel extends JPanel {

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
            try { 
                menu.play(menu);
            } catch (IOException ex) {
                Logger.getLogger(MenuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttonQuit.addActionListener((ActionEvent e) -> {
            menu.quit();
        });
        cp.add(this);
    }
    
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
