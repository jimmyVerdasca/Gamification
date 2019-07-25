package Program;

/**
 * This class represent a part of the workout.
 * It define his intensity and length
 * 
 * length is a generic name to let the AbstractProgram concrete implementation 
 * class decide how to use it. For exemple as a time or as kilometre etc.
 * 
 * @author jimmy
 */
public class WorkoutPart {
    /**
     * length of the part the unit is defined by the Program implementation
     */
    private final long length;
    
    /**
     * Intensity of this part, means the percent of the VMA for the given
     * movement that we want to practice
     */
    private final WorkoutIntensity intensity;
    
    /**
     * Type of movement of the part
     */
    private final Movement movement;

    /**
     * constructor
     * 
     * @param length of the part the unit is defined by the Program implementation
     * @param intensity of this part, means the percent of the VMA for the given
     *                  movement that we want to practice
     * @param movement Type of movement of the part
     */
    public WorkoutPart(long length, WorkoutIntensity intensity, Movement movement) {
        this.length = length;
        this.intensity = intensity;
        this.movement = movement;
    }

    /**
     * return the intensity
     * 
     * @return the intensity
     */
    public WorkoutIntensity getIntensity() {
        return intensity;
    }

    /**
     * return the length
     * 
     * @return the length
     */
    public long getLength() {
        return length;
    }

    public Movement getMovement() {
        return movement;
    }
    
    /**
     * define how should be printed a WorkoutPart
     * 
     * @return a String representing the WorkoutPart
     */
    @Override
    public String toString() { 
        return "intensity : " + intensity.getPercent() + " | length : " + length + "s"; 
    } 
}
