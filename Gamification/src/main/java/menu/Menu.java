package menu;

import components.Slider;
import effortMeasurer.EffortCalculator;
import effortMeasurer.IMUCycleEffortCalculator;
import heigvd.gamification.CharacterControllerJoycon;
import heigvd.gamification.GameEngine;
import heigvd.gamification.GameLoop;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.json.simple.parser.ParseException;

/**
 *
 * @author jimmy
 */
public class Menu  extends JFrame {
    
    private final GameEngine gameEngine;
    private final MenuPanel menuPanel;
    private final JPanel sliderPanel;
    
    private final EffortCalculator effortCalculator;
    private GameLoop gameLoop;
    private final GamePanel gamePanel;
    
    public Menu() throws IOException, FileNotFoundException, ParseException {
        super();
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        
        gameEngine = new GameEngine();
        new CharacterControllerJoycon(gameEngine.getCharacter(), gameEngine.getCHARACTER_MAX_SPEED());
        effortCalculator = new IMUCycleEffortCalculator();
        effortCalculator.start();
        gamePanel = new GamePanel(this, gameEngine, effortCalculator);
        gameLoop = new GameLoop(gameEngine, effortCalculator, gamePanel);
        
        menuPanel = new MenuPanel(this);
        sliderPanel = new SliderPanel(effortCalculator);
        gameEngine.addScoreObserver((Observer) sliderPanel);
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                gameLoop.stop();
                effortCalculator.stop();
                System.exit(0);
            }
        });
    }
    
    public void play(JFrame window) throws IOException {
        window.getContentPane().removeAll();
        window.getContentPane().add(gamePanel, BorderLayout.CENTER);
        window.getContentPane().add(sliderPanel, BorderLayout.EAST);
        validate();
        gameLoop.runGameLoop();
    }
    
    /**
     * entry point of the program
     * 
     * @param args currently no needs arguments
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws org.json.simple.parser.ParseException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ParseException
    {
        //launch the effort calculator
        Menu menu = new Menu();
        menu.setVisible(true);
    }
    
}
