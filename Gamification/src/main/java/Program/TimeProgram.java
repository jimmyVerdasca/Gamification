package Program;

/**
 * Implement a sport program based on time.
 * The sportsman want to do sport while some time
 * 
 * @author jimmy
 */
public class TimeProgram extends AbstractProgram {
    
    /**
     * store the current time when the program is launched
     */
    private long startingTime;
    
    /**
     * true when the program is launched until he ends
     */
    private boolean running = false;
    
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
                running = true;
                startingTime = System.currentTimeMillis();
                long nextPartTime = 0;
                try {
                    for (WorkoutPart part : getParts()) {
                        nextPartTime += part.getLength();
                        do {
                            if(running) {
                                Thread.sleep(300);
                            }
                            updateCurrentLength();
                        } while(getCurrentLength() < nextPartTime && running);
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
    
    /**
     * stop properly the TimeProgram
     */
    @Override
    public void stop(){
        running = false;
    }
    
    /**
     * define the unit of length
     * 
     * @return the current length value
     */
    @Override
    public long getCurrentLength() {
        return (System.currentTimeMillis() - startingTime) / 1000;
    }
}
