package Program;

import effortMeasurer.EffortCalculator;
import heigvd.gamification.AbstractCharacterController;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author jimmy
 */
public class TimeProgram extends AbstractProgram {
    
    private long time;
    
    public TimeProgram(long time) {
        super();
        this.time = time * 1000;
    }
    
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
