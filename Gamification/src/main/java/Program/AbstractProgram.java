package Program;

import java.util.Observable;
import java.util.Observer;

/**
 * Base class to implement new program classes. 
 * This class decide the sportif program cycle.
 * For example when it start, when and how it ends.
 * 
 * This class is observable if we want to check
 * the state of the workout by an other component.
 * 
 * @author jimmy
 */
public abstract class AbstractProgram {
    
    /**
     * parameter indicating if we are doing sport or not
     */
    private boolean isRunning = false;
    
    /**
     * the list of the parts in order for this workout
     */
    private WorkoutPart[] parts;
    
    /**
     * index of the current part
     */
    private int currentPart = 0;
    
    /**
     * Observable that notify when we switch between WorkoutPart
     */
    private final PartObservable partObs;
    
    /**
     * Observable that notify when we update the current length
     */
    private final LengthObservable lengthObs;
    
    // TO DELETE AND CHECK STILL WORKING BEGIN
    /**
     * obsolet should be deleted
     */
    private WorkoutIntensity currentIntensity;
    /**
     * obsolet should be deleted
     */
    private long currentLength = 0;
    /**
     * obsolet should be deleted
     */
    private Thread thread = new Thread();
    // TO DELETE AND CHECK STILL WORKING END

    /**
     * Constructor
     * 
     * @param parts array with the workout parts to go through
     */
    public AbstractProgram(WorkoutPart[] parts) {
        this.parts = parts;
        currentIntensity = parts[0].getIntensity();
        partObs = new PartObservable();
        lengthObs = new LengthObservable();
    }
    
    /**
     * Method to call by subclass if we want to indicate
     * the beginning of the sport.
     * 
     * Be sure to not forget to call super.startProgram() if overrided
     */
    public void startProgram() {
        isRunning = true;
        partObs.update();
    }
    
    /**
     * Update the state of this class to go to the next part of the workout.
     * If we were at last part, call endProgram.
     */
    public void nextPart() {
        if (currentPart >= parts.length) {
            endProgram();
        } else {
            currentIntensity = parts[currentPart].getIntensity();
        }
        currentPart++;
        partObs.update();
    }
    
    /**
     * Method to call by subclass if we want to indicate
     * the end of the sport.
     * 
     * Be sure to not forget to call super.startProgram() if overrided
     */
    private void endProgram() {
        if (isRunning) {
            isRunning = false;
            partObs.update();
        }
    }
    
    /**
     * return the current status of the sport seance.
     * @return the current status of the sport seance.
     */
    public boolean getIsRunning() {
        return isRunning;
    }
    
    /**
     * return the intensity of the current workout part
     * 
     * @return the intensity of the current workout part
     */
    public WorkoutIntensity getIntensity() {
        return parts[Math.min(parts.length - 1, currentPart)].getIntensity();
    }
    
    /**
     * return the kind of movement of the current workout part
     * 
     * @return the kind of movement of the current workout part
     */
    public Movement getMovement() {
        return parts[Math.min(parts.length - 1, currentPart)].getMovement();
    }

    /**
     * return all the parts of the workout program
     * 
     * @return all the parts of the workout program
     */
    public WorkoutPart[] getParts() {
        return parts;
    }

    /**
     * return the current workout part
     * 
     * @return the current workout part
     */
    public int getCurrentPart() {
        return currentPart;
    }
    
    /**
     * notify the observers of length changes. Should be called by the subclasses
     */
    public void updateCurrentLength() {
        // This line should be deleted
        currentLength = getCurrentLength();
        lengthObs.update();
    }
    
    /**
     * methode to define in subclass that will define the units of 1 length
     * For exemple a time program will define his second measure while a meter
     * program will define a measure of km
     * 
     * @return the current length of the program relatively to his definition of
     *         length
     */
    public abstract long getCurrentLength();
    
    /**
     * add an observer to part changes
     * 
     * @param o the new part observer
     */
    public void addPartObserver(Observer o) {
        partObs.addObserver(o);
    }
    
    /**
     * add an observer to length changes
     * 
     * @param o the new length observer
     */
    public void addLengthObserver(Observer o) {
        lengthObs.addObserver(o);
    }
    
    /**
     * end the program properly should no more be abstract and call isRunning = false
     */
    public abstract void stop();
    
    /**
     * inner observable class that notify when we switch the current workout part
     */
    public class PartObservable extends Observable {
        public Movement getMovement() {
            return AbstractProgram.this.getMovement();
        }
        
        public int getCurrentPart() {
            return AbstractProgram.this.getCurrentPart();
        }
        
        protected void update() {
            setChanged();
            notifyObservers();
        }
    }
    
    /**
     * inner observable class that notify when we modify the length status
     */
    public class LengthObservable extends Observable {
        public long getCurrentLength() {
            return AbstractProgram.this.getCurrentLength();
        }
        
        protected void update() {
            setChanged();
            notifyObservers();
        }
    }
}
