package heigvd.gamification;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GameEngine for a game with 2 players
 * 
 * relatively to the simple GameEngine, it add 2 things :
 * -a second player to manipulate
 * -a speed/slow effect if the players are in line vertically or separate
 * 
 * @author jimmy
 */
public class GameEngineDuo extends GameEngine {

    public GameEngineDuo(Mode firstMode) {
        super(firstMode, 2);
    }
    
    @Override
    public void setSpeed(double percent, double maxPossible) {
        double sum = 0.0, standardDeviation = 0.0;
        double mean = 0.0;
        for (Character character : getCharacter()) {
            mean += character.getX();
        }
        mean /= getNB_PLAYERS();
        
        for (Character character : getCharacter()) {
            standardDeviation += Math.pow(character.getX() - mean, getNB_PLAYERS());
        }
        standardDeviation = Math.sqrt(standardDeviation / getNB_PLAYERS());
        
        super.setSpeed(Math.pow(1.015, -standardDeviation) * percent, maxPossible);
    }
    
    
    
}
