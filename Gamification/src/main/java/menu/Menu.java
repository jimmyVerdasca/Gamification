package menu;

import Program.AbstractProgram;
import Program.EnduranceTimeProgram;
import Program.TimeProgram;
import effortMeasurer.EffortCalculator;
import effortMeasurer.IMUCycleEffortCalculator;
import heigvd.gamification.CharacterControllerJoycon;
import heigvd.gamification.GameEngine;
import heigvd.gamification.GameLoop;
import heigvd.gamification.Mode;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.json.simple.parser.ParseException;

/**
 * Main Class instanciating the view/controllers and the logiques entity.
 * 
 * It's the entry point from where we can switch between different views.
 * 
 * In future versions we should split the view code of the
 * "workout-game handling" code.
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
    
    /**
     * constructor
     * 
     * @throws IOException should be handled soon
     * @throws FileNotFoundException should be handled soon
     * @throws ParseException should be handled soon
     */
    public Menu() throws IOException, FileNotFoundException, ParseException {
        super();
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        
        AbstractProgram program = new EnduranceTimeProgram(); 
        gameEngine = new GameEngine(Mode.values()[program.getIntensity().ordinal()]);
        new CharacterControllerJoycon(gameEngine.getCharacter(), gameEngine.getCHARACTER_MAX_SPEED());
        effortCalculator = new IMUCycleEffortCalculator();
        effortCalculator.start();
        gamePanel = new GamePanel(this, gameEngine, effortCalculator);
        gameLoop = new GameLoop(gameEngine, effortCalculator, gamePanel, program);
        
        menuPanel = new MenuPanel(this);
        sliderPanel = new SliderPanel(effortCalculator);
        gameEngine.addScoreObserver((Observer) sliderPanel);
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                quit();
            }
        });
    }
    
    /**
     * Method to call when we want to swith the view to the game mode.
     * 
     * @param window instance of Menu that handle the view.
     */
    public void play(Menu window) {
        window.getContentPane().removeAll();
        window.setLayout(new BorderLayout());
        window.getContentPane().add(gamePanel, BorderLayout.CENTER);
        window.getContentPane().add(sliderPanel, BorderLayout.EAST);
        validate();
        gameLoop.runGameLoop();
    }
    
    /**
     * Method to call when we want to leave the program properly.
     */
    public void quit() {
        gameLoop.stop();
        effortCalculator.stop();
        System.exit(0);
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
        Menu menu = new Menu();
        menu.setVisible(true);
    }
    
}
