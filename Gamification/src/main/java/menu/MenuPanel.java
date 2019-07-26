package menu;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Class that represent the view when we are in the menu. 
 * buttons :
 * 
 * Play solo
 * Play Duo
 * Create Workout
 * Help
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
        
        int vSpace = Toolkit.getDefaultToolkit().getScreenSize().height / 9;
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        JButton buttonPlay = addButton("PLAY SOLO");
        add(buttonPlay);
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        
        JButton buttonPlayDuo = addButton("PLAY DUO");
        add(buttonPlayDuo);
        
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        JButton buttonCreateWorkout = addButton("CREATE WORKOUT");
        add(buttonCreateWorkout);
        
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        JButton buttonHelp = addButton("HOW TO PLAY");
        add(buttonHelp);
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        JButton buttonQuit = addButton("QUIT");
        add(buttonQuit);
        
        
        add(Box.createRigidArea(new Dimension(0, vSpace)));
        
        buttonPlay.addActionListener((ActionEvent e) -> {
            menu.load(menu);
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    menu.createGame(menu, 1);
                }
            };
            loop.start(); 
        });
        
        buttonPlayDuo.addActionListener((ActionEvent e) -> {
            menu.load(menu);
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    menu.createGame(menu, 2);
                }
            };
            loop.start();         
        });
        
        buttonCreateWorkout.addActionListener((ActionEvent e) -> {
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    menu.createWorkout(menu);
                }
            };
            loop.start(); 
        });
        
        buttonHelp.addActionListener((ActionEvent e) -> {
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    menu.help(menu);
                }
            };
            loop.start(); 
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
                setSize(600, 150);
                setMaximumSize(getSize());
            }
        };
        button.setFont(new Font("Papyrus", Font.BOLD, 40));
        button.setAlignmentX(CENTER_ALIGNMENT);
        return button;
    }
}
