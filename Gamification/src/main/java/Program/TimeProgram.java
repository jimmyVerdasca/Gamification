package Program;

/**
 * Implement a sport program based on time.
 * The sportsman want to do sport while some time
 * 
 * @author jimmy
 */
public class TimeProgram extends AbstractProgram {
    
    /**
     * Amount of seconds for the whole workout.
     */
    private final long time;
    
    /**
     * constructor
     * @param time Amount of seconds for the whole workout.
     */
    public TimeProgram(long time) {
        super();
        this.time = time * 1000;
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
                    Thread.sleep(time);
                    endProgram();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
