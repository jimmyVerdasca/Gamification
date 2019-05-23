/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heigvd.gamification;

/**
 *
 * @author jimmy
 */
public class AbstractCharacterController {

    private final Character character;
    private final int MAX_MOVE_SPEED;
    
    public AbstractCharacterController(Character character, int characterMaxSpeed) {
        this.character = character;
        MAX_MOVE_SPEED = characterMaxSpeed;
    }
    
    
    /**
     * method called by the subclass detectors to move the character in a side
     * If 1 or higher is called, then the character goes to his max speed to the right,
     * If -1 or lesser is called then the character goes to the left with his max speed,
     * 
     * @param evaluation percent of the speed how should start a movement the character
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
