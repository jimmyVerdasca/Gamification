package heigvd.gamification;

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

    /**
     * constructor
     * 
     * @param firstMode mode with wich we start the game
     */
    public GameEngineDuo(Mode firstMode) {
        super(firstMode, 2);
    }
    
    /**
     * override the setSpeed solo mode to add a aerodynamic effect
     * The slow is calculated relatively to the dispersion between both players
     * 
     * @param percent effort received to set the speed
     * @param maxPossible maximum effort ever reached
     */
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
