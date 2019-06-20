package heigvd.gamification;

/**
 * Enum that could be used to represent the direction(left right)
 * by the character controllers.
 * 
 * @author jimmy
 */
public enum Direction {
    LEFT(-1), RIGHT(1); 
    
    private int value;
    
    private Direction(int value) 
    { 
        this.value = value;
    } 

    public int getValue() 
    { 
        return value;
    } 
}
