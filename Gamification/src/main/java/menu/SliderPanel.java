package menu;

import components.ImageJPanel;
import components.JButtonIconResizable;
import components.LabelObserver;
import components.MedalsPanel;
import components.Slider;
import effortMeasurer.EffortCalculator;
import heigvd.gamification.GameEngine.ScoreObservable;
import heigvd.gamification.rules.RulesManager;
import heigvd.gamification.rules.RulesManager.RuleObservable;
import heigvd.gamification.rules.RulesManager.WinRuleObservable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Array Panel where are display various game statistics
 * 
 * Currently we can see the Slider of effort and the score
 * We should still add the time elapsed vs time to end and in competitive mode,
 * we should add the 1st/2nd team status for example.
 *
 * @author jimmy
 */
public class SliderPanel extends JPanel implements Observer {
    
    private final int nbCol = 4;
    private final int nbLine = 4;
    private final int cellWidth = Toolkit.getDefaultToolkit().getScreenSize().width / (3 * nbCol);
    private final int cellHeight = Toolkit.getDefaultToolkit().getScreenSize().height / nbLine;
    
    /**
     * current score
     */
    private final JLabel scoreLabel;
    
    private final LabelObserver detectorLabel;
    private final JLabel objectifLabel;
    private final ImageJPanel objectifImage;
    
    /**
     * Constructor
     * 
     * create an array of "simple" view (score, effortSlider etc)
     * 
     * @param menu parent JFrame
     * @param effortCalculator instance of the effortCalculator that the slider
     *                         will observe.
     * @param ruleManager
     */
    public SliderPanel(Menu menu, EffortCalculator effortCalculator, RulesManager ruleManager) {
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridBag);
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        scoreLabel = new JLabel("score: " + "0");
        scoreLabel.setFont(new Font("Papyrus", Font.BOLD, 40));
        
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        scoreLabel.setPreferredSize(new Dimension(2 * cellWidth, cellHeight));
        add(scoreLabel, c);
        
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 0;
        MedalsPanel medalPanel = new MedalsPanel();
        medalPanel.setPreferredSize(new Dimension(cellWidth, cellHeight));
        add(medalPanel, c);
        ruleManager.addWinRuleObserver(medalPanel);
        
        JButtonIconResizable backButton;
        try {
            backButton = new JButtonIconResizable(ImageIO.read(getClass()
                    .getResource("/assets/ui/backButton.jpg")));
            backButton.setPreferredSize(new Dimension(cellWidth, cellHeight));
            c.gridwidth = 1;
            c.gridx = 3;
            c.gridy = 0;
            add(backButton, c);
            backButton.addActionListener((ActionEvent e) -> {
                menu.menu(menu);
                Thread loop = new Thread()
                {
                    @Override
                    public void run()
                    {
                        menu.resetGame();
                    }
                };
                loop.start(); 
            });
        } catch (IOException ex) {
            Logger.getLogger(SliderPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        objectifImage = new ImageJPanel(ruleManager.getCurrentObjectif().getName());
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        objectifImage.setPreferredSize(new Dimension(cellWidth, cellHeight));
        add(objectifImage, c);
        
        objectifLabel = new JLabel(ruleManager.getCurrentObjectif().getDescription());
        objectifLabel.setFont(new Font("Papyrus", Font.BOLD, 30));
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 1;
        objectifLabel.setPreferredSize(new Dimension((nbCol - 1) * cellWidth, cellHeight));
        add(objectifLabel, c);
        
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 2;
        Component comp = new Component(){};
        comp.setPreferredSize(new Dimension(nbCol * cellWidth, cellHeight));
        add(comp, c);
        
        Slider slider;
        try {
            slider = new Slider(0, 200);
            c.gridwidth = 1;
            c.gridx = 0;
            c.gridy = 3;
            slider.setPreferredSize(new Dimension(cellWidth, cellHeight));
            add(slider, c);
            effortCalculator.addObserver(slider);
        } catch (IOException ex) {
            Logger.getLogger(SliderPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        detectorLabel = new LabelObserver();
        detectorLabel.setFont(new Font("Papyrus", Font.BOLD, 30));
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 3;
        detectorLabel.setPreferredSize(new Dimension(cellWidth, cellHeight));
        add(detectorLabel, c);
        
        c.gridwidth = 2;
        c.gridx = 2;
        c.gridy = 3;
        Component comp2 = new Component(){};
        comp2.setPreferredSize(new Dimension((nbCol - 2) * cellWidth, cellHeight));
        add(comp2, c);
        
        Dimension dim = new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 3, Toolkit.getDefaultToolkit().getScreenSize().height);
        setPreferredSize(dim);
        effortCalculator.addObserver(detectorLabel);
        
        
        ruleManager.addRuleObserver(this);
        ruleManager.addWinRuleObserver(this);
    }
    
    /**
     * Update the score label with a new score.
     * 
     * @param newScore the new score.
     */
    public void updateScore(long newScore) {
        scoreLabel.setText("score: " + Long.toString(newScore));
    }

    /**
     * When we receive anotification as Observer, this method is called.
     * In our case it's a score notification coming from the GameEngine inner
     * observable inner class that indicate that the score has changed.
     * 
     * @param o instance that notified
     * @param o1 parameter of the event
     */
    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof ScoreObservable) {
            ScoreObservable scoreObs = ((ScoreObservable)o);
            updateScore(scoreObs.getScore());
        } else if (o instanceof RuleObservable) {
            RuleObservable ruleObs = ((RuleObservable)o);
            objectifLabel.setText(ruleObs.getCurrentRule().getDescription());
        } else if (o instanceof WinRuleObservable) {
            WinRuleObservable winRuleObs = ((WinRuleObservable)o);
            objectifImage.setImage(winRuleObs.getCurrentRule().getName());
        }
    }
}
