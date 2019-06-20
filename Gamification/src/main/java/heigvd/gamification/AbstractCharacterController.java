package heigvd.gamification;

/**
 * Class to extend by detectors to move the character.
 * 
 * @author jimmy
 */
public class AbstractCharacterController {

    /**
     * the Character that we manipulate
     */
    private final Character character;
    
    /**
     * the maximum speed reachable by the character.
     */
    private final int MAX_MOVE_SPEED;
    
    /**
     * Constructor
     * 
     * @param character that we manipulate.
     * @param characterMaxSpeed the maximum speed reachable by the character.
     */
    public AbstractCharacterController(Character character, int characterMaxSpeed) {
        this.character = character;
        MAX_MOVE_SPEED = characterMaxSpeed;
    }
    
    
    /**
     * Method called by the subclass detectors to move the character in a side
     * If 1 or higher is called, then the character goes to his max speed
     * to the right,
     * If -1 or lesser is called then the character goes to the left with his
     * max speed,
     * 
     * @param evaluation percent of the speed how should start a movement the
     *                   character [0.0, 1.0]
     */
    protected void setSpeed(double evaluation) {
        if(evaluation > 1) {
            evaluation = 1;
        } else if (evaluation < -1) {
            evaluation = -1;
        }
        character.setSpeed((int)(MAX_MOVE_SPEED * evaluation));
    }
}
