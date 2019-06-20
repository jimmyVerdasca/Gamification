package menu;

import components.Slider;
import effortMeasurer.EffortCalculator;
import heigvd.gamification.GameEngine.ScoreObservable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
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
    
    /**
     * current score
     */
    private JLabel scoreLabel;
    
    /**
     * Constructor
     * 
     * create an array of "simple" view (score, effortSlider etc)
     * 
     * @param effortCalculator instance of the effortCalculator that the slider
     *                         will observe.
     * @throws IOException if the slider doesn't reach his image in the
     *                     filesystem
     */
    public SliderPanel(EffortCalculator effortCalculator) throws IOException {
        GridLayout gl = new GridLayout(5, 4);
        setLayout(gl);
        Slider slider = new Slider(0, 20);
        scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Papyrus", Font.BOLD, 40));  
        add(scoreLabel);
        // add 2 void component to fill the gridlayout and put slider in right position
        for (int i = 0; i < 15; i++) {
            add(new Component(){});
        }
        add(slider);
        Dimension dim = new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 3, Toolkit.getDefaultToolkit().getScreenSize().height);
        setPreferredSize(dim);
        effortCalculator.addObserver(slider);
    }
    
    /**
     * Update the score label with a new score.
     * 
     * @param newScore the new score.
     */
    public void updateScore(long newScore) {
        scoreLabel.setText(Long.toString(newScore));
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
        }
    }
}
