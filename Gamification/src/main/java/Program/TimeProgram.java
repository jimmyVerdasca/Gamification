package Program;

/**
 * Implement a sport program based on time.
 * The sportsman want to do sport while some time
 * 
 * @author jimmy
 */
public abstract class TimeProgram extends AbstractProgram {
    
    
    /**
     * constructor
     * @param parts
     */
    public TimeProgram(WorkoutPart[] parts) {
        super(parts);
    }
    
    /**
     * Start the workout and the timer.
     * A sleeping thread is started that will awake to
     * indicate the end of the workout.
     */
    @Override
    public void startProgram() {
        super.startProgram();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    for (WorkoutPart part : parts) {
                        Thread.sleep(part.getLength() * 1000);
                        nextPart();
                    }
                    nextPart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    
}
