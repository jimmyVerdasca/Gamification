package menu;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author jimmy
 */
public class MenuPanel extends JPanel {

    public MenuPanel(Menu menu) {
        
        Container cp = menu.getContentPane();
        cp.setLayout(new BorderLayout());
        JButton button = new JButton("PLAY");
        button.setPreferredSize(new Dimension(200, 50));
        cp.add(button, BorderLayout.CENTER);
        button.addActionListener((ActionEvent e) -> {
            try { 
                menu.play(menu);
            } catch (IOException ex) {
                Logger.getLogger(MenuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
}
