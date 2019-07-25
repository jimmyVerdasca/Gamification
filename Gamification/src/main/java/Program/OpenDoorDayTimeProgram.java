package Program;

/**
 * First concrete Workout program used as default program for open door day
 * 
 * @author jimmy
 */
public class OpenDoorDayTimeProgram extends TimeProgram {

    /**
     * constructor
     */
    public OpenDoorDayTimeProgram() {
        super(new WorkoutPart[]{
            new WorkoutPart(3 * 60, WorkoutIntensity.POWER, Movement.RUNNING),
            new WorkoutPart(1 * 60, WorkoutIntensity.ENDURANCE, Movement.RUNNING),
            new WorkoutPart(3 * 60, WorkoutIntensity.STRENGTH, Movement.RUNNING),
            new WorkoutPart(1 * 60, WorkoutIntensity.ENDURANCE, Movement.RUNNING),
            new WorkoutPart(3 * 60, WorkoutIntensity.POWER, Movement.RUNNING),
        });
    }
}
