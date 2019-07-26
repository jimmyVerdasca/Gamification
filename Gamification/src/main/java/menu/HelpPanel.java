package menu;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingConstants.CENTER;
import util.ImageUtil;

/**
 * help panel showing hot to play at the game
 * 
 * @author jimmy
 */
public class HelpPanel extends JPanel {

    private final ImageIcon helpMenuIcon;
    private final ImageIcon helpGameIcon;
    private final ImageIcon helpResultsIcon;
    private final ImageIcon helpCreateWorkoutIcon;
    
    private final int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final ImageIcon buddyIcon;
    private final Font font;
    private final ImageIcon shieldIcon;
    private final ImageIcon boostIcon;

    /**
     * constructor
     * 
     * @param menu parent JFrame
     */
    HelpPanel(Menu menu) {
        super();
        Container cp = menu.getContentPane();
        BoxLayout vfLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(vfLayout);
        
        
        font = new Font("Papyrus", Font.BOLD, 30);
        
        buddyIcon = new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/help/buddy.png")).getImage(), width / 2, height / 2));
        helpMenuIcon = new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/help/menu.png")).getImage(), width / 2, height / 2));
        helpGameIcon = new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/help/game.png")).getImage(), width / 2, height / 2));
        helpResultsIcon = new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/help/results.png")).getImage(), width / 2, height));
        helpCreateWorkoutIcon = new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/help/create.png")).getImage(), width / 2, height));
        shieldIcon = new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/background/shield.png")).getImage(), width / 20, height / 20));
        boostIcon = new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/background/powerup.png")).getImage(), width / 20, height / 20));
        
        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(font);
        
        JLabel introLabel = addLabel(buddyIcon,
                "<html>Welcome in BuddyTrunner !<br>"
                + "<br>"
                + "This application allows you to have fun while training.<br>"
                + "Buddy the little robot was given the mission to purify the air.<br>"
                + "To do this, he needs your energy and reflexes.<br><html>");
        
        JLabel menuLabel = addLabel(helpMenuIcon,
                "<html><br>To transmit your energy,<br> place "
                + "the accelerometer on the moving part of your body<br>"
                + "(the detected axis is the one connecting the two long parts "
                + "of the accelerometer).<br><br>"
                + "Be sure that the Joy-Con(s) and the accelerometer are connected!<br>"
                + "Now that you're equipped, you can either define your workout by<br>"
                + "going to \"create workout\"(14) or directly try the default program<br>"
                + "in \"Play solo\"(15) or \"Play duo\"(16)<br>"
                + "<html>");
        
        JLabel createLabel = addLabel(helpCreateWorkoutIcon,
                "<html><br>To create your own personal workout,<br>"
                + "you just need to create one by one the parts of your training by choosing for each part :<br>"
                + "(17) the movement you will train.<br>"
                + "(18) the intensity of the part.<br>"
                + "(19) the amount of minutes of this part.<br>"
                + "Once the part ready you can add it with the button \"Add Part\"<br>"
                + "Don't worry, if you missclick, you can remove the last part created with the button \"Remove Part\" (20)<br>"
                + "you can check at any moment your creation in the summary panel (21)<br>"
                + "After all parts created, if you click into create workout(22), Buddy will set his program up.<br><html>");
        
        JLabel gameLabel = addLabel(helpGameIcon,
                "<html><br>Once in game, you will meet Buddy!<br>"
                + "As you will see, he is not moving.<br>"
                + "If you start moving, the Buddy's effort bar will load.(1)<br>"
                + "The more the bar is full, the more Buddy will fly fast.<br>"
                + "But be careful, do not exceed Buddy's limit or he will no longer be able to avoid obstacles.<br>"
                + "Carefully follow the workout you have created and you should not have any problems.<br>"
                + "A graph(2)  and a summary(3) of the workout tells you your progress.<br>"
                + "Each time you finish a part, Buddy will evaluate your result with a cute smiley(7)<br>"
                + "Buddy will also receive some missions(4) be sure to help him complete them to receive medals at the end!<br>"
                + "You can leave the game at any time by clicking the back button(8)<br>"
                + "<br><html>");
        
        JLabel shieldLabel = addLabel(shieldIcon,
                "<html><br>You can find bottles on the floor.<br>"
                + "If you allow Buddy to pick them up, he will be able to cool his system and reactivate his shield.<br>"
                + "He will be able to cross the obstacles without being slowed down."
                + "<br><html>");
        
        JLabel boostLabel = addLabel(boostIcon,
                "<html><br>You can find hearts on the floor.<br>"
                + "If you allow Buddy to pick them up, he will be filled of energy.<br>"
                + "His speed will increase momentarily."
                + "<br><html>");
        
        JLabel resultsLabel = addLabel(helpResultsIcon,
                "<html><br>At the end of the program, Buddy will give you a score.<br>"
                + "Try to make a maximum distance and win a maximum of medals.<br>"
                + "So you can register your name proudly in the personal pantheon of Buddy!<br>"
                + "<br><html>");
        
        
        JScrollPane scrollPane = new JScrollPane(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(width, height);
        
        JPanel helpInfoPanel = new JPanel();
        helpInfoPanel.add(introLabel);
        helpInfoPanel.add(menuLabel);
        helpInfoPanel.add(createLabel);
        helpInfoPanel.add(gameLabel);
        helpInfoPanel.add(shieldLabel);
        helpInfoPanel.add(boostLabel);
        helpInfoPanel.add(resultsLabel);
        helpInfoPanel.setMaximumSize(new Dimension(width, height * 10));
        helpInfoPanel.setLayout(new BoxLayout(helpInfoPanel, BoxLayout.Y_AXIS));
        
        
        scrollPane.setViewportView(helpInfoPanel);
        add(backButton);
        add(scrollPane);
        cp.add(this);
        
        backButton.addActionListener((ActionEvent e) -> {
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    menu.menu(menu);
                }
            };
            loop.start();
        });
    }
    
    /**
     * utilitary method to create a button
     * 
     * @param icon of the button
     * @param text of the button
     * @return the button created
     */
    private JLabel addLabel(ImageIcon icon, String text) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setIcon(icon);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.NORTH);
        label.setHorizontalAlignment(CENTER);
        return label;
    }
    
}
