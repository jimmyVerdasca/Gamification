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
    private long length;
    private WorkoutIntensity intensity;

    public WorkoutPart(long length, WorkoutIntensity intensity) {
        this.length = length;
        this.intensity = intensity;
    }

    public WorkoutIntensity getIntensity() {
        return intensity;
    }

    public long getLength() {
        return length;
    }
}
