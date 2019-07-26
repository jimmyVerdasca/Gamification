package menu;

import Program.AbstractProgram;
import Program.OpenDoorDayTimeProgram;
import effortMeasurer.EffortCalculator;
import effortMeasurer.IMUCycleEffortCalculator;
import heigvd.gamification.CharacterControllerJoycon;
import heigvd.gamification.GameEngine;
import heigvd.gamification.GameEngineDuo;
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
    
    private GameEngine gameEngine;
    private MenuPanel menuPanel;
    private JPanel sliderPanel;
    private JPanel listProgramPanel;
    private LoadingPanel loadingPanel;
    
    private EffortCalculator effortCalculator;
    private GameLoop gameLoop;
    private GamePanel gamePanel;
    private AbstractProgram program;
    
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
        
        
        program = new OpenDoorDayTimeProgram();
        menuPanel = new MenuPanel(this);
        
        
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
        window.getContentPane().add(listProgramPanel, BorderLayout.WEST);
        validate();
        gameLoop.runGameLoop();
    }
    
    /**
     * launch the loading panel
     * @param window main JFrame
     */
    public void load(Menu window) {
        loadingPanel = new LoadingPanel(window);
        window.getContentPane().removeAll();
        window.setLayout(new BorderLayout());
        window.getContentPane().add(loadingPanel, BorderLayout.CENTER);
        validate();
    }
    
    /**
     * Method to call when we want to leave the program properly.
     */
    public void quit() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (effortCalculator != null) {
            effortCalculator.stop();
        }
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
    
    /**
     * launch a game with the specified number of players
     * currently support one or two players
     * 
     * @param window main JFrame
     * @param nbPlayers number of players wished
     */
    void createGame(Menu window, int nbPlayers) {
        loadingPanel.setState(0, "creating the Workout");
        loadingPanel.setState(10, "setting up the game engine");
        if (nbPlayers == 1) {
            gameEngine = new GameEngine(Mode.values()[program.getIntensity().ordinal()]);
        } else if (nbPlayers == 2) {
            gameEngine = new GameEngineDuo(Mode.values()[program.getIntensity().ordinal()]);
        }
        
        loadingPanel.setState(20, "connecting to Joy-Con left");
        try {
            new CharacterControllerJoycon(gameEngine.getCharacter()[0], gameEngine.getCHARACTER_MAX_SPEED(), true);
        } catch (IndexOutOfBoundsException ex) {
            loadingPanel.setERROR("impossible to connect to Joy-Con LEFT, you can try to forget/resynchronize the joy-con to the computer's bluetooth parameter and then reload this app");
            return;
        }
        if (gameEngine.getNB_PLAYERS() == 2) {
            loadingPanel.setState(20, "connecting to Joy-Con right");
            try {
                new CharacterControllerJoycon(gameEngine.getCharacter()[1], gameEngine.getCHARACTER_MAX_SPEED(), false);
            } catch (IndexOutOfBoundsException ex) {
                loadingPanel.setERROR("impossible to connect to Joy-Con RIGHT, you can try to forget/resynchronize the joy-con to the computer's bluetooth parameter and then reload this app");
                return;
            }
        }
        
        try {
            loadingPanel.setState(30, "connecting to Accelerometer");
            effortCalculator = new IMUCycleEffortCalculator(program.getMovement());
        } catch (IOException ex) {
            loadingPanel.setERROR("impossible to communicate with the Shimmer3, is he paired with the computer ?");
        } catch (ParseException ex) {
            loadingPanel.setERROR("impossible to parse the two Shimmer calibration file : calibration.json and IMUConfig.properties should exist in src\\main\\java\\imu");
        }
        loadingPanel.setState(40, "launching the Accelerometer");
        effortCalculator.start();
        
        loadingPanel.setState(50, "loading the UI");
        gamePanel = new GamePanel(this, gameEngine, effortCalculator);
        
        loadingPanel.setState(60, "setting up the game Loop");
        gameLoop = new GameLoop(gameEngine, effortCalculator, gamePanel, program);
        
        loadingPanel.setState(70, "setting up the detectors UI");
        sliderPanel = new SliderPanel(window, effortCalculator, gameEngine.getRuleManager());
        
        loadingPanel.setState(80, "setting up the workout UI");
        listProgramPanel = new ListProgramPanel(program, effortCalculator);
        
        loadingPanel.setState(90, "setting up the observers Pattern");
        gameEngine.addScoreObserver((Observer) sliderPanel);
        
        loadingPanel.setState(100, "Ready");
        play(window);
    }
    
    /**
     * launch the menu panel
     * @param window main JFrame
     */
    public void menu(Menu window) {
        resetDisplay(window);
        window.getContentPane().add(menuPanel, BorderLayout.CENTER);
        window.validate();
        window.repaint();
    }
    
    /**
     * stop properly the game
     */
    public void resetGame() {
        if (program != null && program.getIsRunning()) {
            program.stop();
        }
        effortCalculator.stop();
    }
    
    /**
     * clear the main JFrame
     * @param window 
     */
    private void resetDisplay(Menu window) {
        window.getContentPane().removeAll();
    }

    /**
     * launch the create workout panel
     * @param menu mainJFrame
     */
    void createWorkout(Menu menu) {
        resetDisplay(menu);
        menu.getContentPane().add(new CreateWorkoutPanel(menu), BorderLayout.CENTER);
        menu.validate();
        menu.repaint();
    }

    /**
     * set the workout of the game
     * @param menu main JFrame
     * @param program the new workout
     */
    void setProgram(Menu menu, AbstractProgram program) {
        menu.program = program;
    }

    /**
     * launch the help panel
     * @param menu main JFrame
     */
    void help(Menu menu) {
        resetDisplay(menu);
        menu.getContentPane().add(new HelpPanel(menu), BorderLayout.CENTER);
        menu.validate();
        menu.repaint();
    }
    
}
