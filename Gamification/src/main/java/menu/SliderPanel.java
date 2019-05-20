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
 *
 * @author jimmy
 */
public class SliderPanel extends JPanel implements Observer {
    private JLabel scoreLabel;
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
    
    
    public void updateScore(long newScore) {
        scoreLabel.setText(Long.toString(newScore));
    }

    @Override
    public void update(Observable o, Object o1) {
        
        if (o instanceof ScoreObservable) {
            ScoreObservable scoreObs = ((ScoreObservable)o);
            updateScore(scoreObs.getScore());
        }
    }
}
