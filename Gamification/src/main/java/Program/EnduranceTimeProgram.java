package Program;

/**
 * First concrete Workout program who train oriented to train the endurance
 * 
 * for the tests it currently use 3 differents WorkoutIntensity
 * 
 * @author jimmy
 */
public class EnduranceTimeProgram extends TimeProgram {

    public EnduranceTimeProgram() {
        super(new WorkoutPart[]{
            new WorkoutPart(1 * 20, WorkoutIntensity.FORCE),
            new WorkoutPart(1 * 20, WorkoutIntensity.PUISSANCE),
            new WorkoutPart(1 * 20, WorkoutIntensity.ENDURANCE),
        });
    }
    
}
