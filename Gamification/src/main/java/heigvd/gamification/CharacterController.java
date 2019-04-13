package heigvd.gamification;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * class allowing to control the caracter by detecting key press
 * 
 * @author jimmy
 */
public class CharacterController {

    private final String MOVE_LEFT = "move left";
    private final String MOVE_RIGHT = "move right";
    private final Character character;
    private final int MAX_MOVE_SPEED = 10;
    
    /**
     * Constructor, we pass the JFrame where the key press should be detected
     * @param gameEngine 
     */
    CharacterController(GameEngine gameEngine) {
        this.character = gameEngine.getCharacter();
        //bind left
        gameEngine.getInputMap(gameEngine.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
        gameEngine.getActionMap().put(MOVE_LEFT, new MoveAction(true));
        //bind right
        gameEngine.getInputMap(gameEngine.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
        gameEngine.getActionMap().put(MOVE_RIGHT, new MoveAction(false));
    }
    
    /**
     * Class reacting to the key pressed event.
     * Instances are created when we press the left or right keyboard button.
     */
    private class MoveAction extends AbstractAction {

        boolean left;

        /**
         * constructor
         * @param left either true if we pressed left, false otherwise.
         */
        MoveAction(boolean left) {
            this.left = left;
        }

        /**
         * Update the character speed relatively
         * to the button that has been pressed.
         * @param e information of the event.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if(left) {
                character.setSpeed(-MAX_MOVE_SPEED);
            } else {
                character.setSpeed(MAX_MOVE_SPEED);
            }
        }
    }


}
