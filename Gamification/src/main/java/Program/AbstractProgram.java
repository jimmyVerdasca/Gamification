package Program;

import java.util.Observable;

/**
 * Base class to implement new program classes. 
 * This class decide de sportif program cycle.
 * For example when it start, when and how it ends.
 * 
 * This class is observable if we want to check
 * the state of the workout by an other component.
 * 
 * @author jimmy
 */
public abstract class AbstractProgram extends Observable {
    
    /**
     * parameter indicating if we are doing sport or not
     */
    private boolean isRunning = false;
    
    private WorkoutIntensity currentIntensity;
    private int currentPart = 0;
    
    protected WorkoutPart[] parts;
    
    private Thread thread = new Thread();

    public AbstractProgram(WorkoutPart[] parts) {
        this.parts = parts;
        currentIntensity = parts[0].getIntensity();
    }
    
    /**
     * Method to call by subclass if we want to indicate
     * the beginning of the sport.
     * 
     * Be sure to not forget to call super.startProgram() if overrided
     */
    public void startProgram() {
        isRunning = true;
        setChanged();
        notifyObservers();
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
        setChanged();
        notifyObservers();
    }
    
    /**
     * Method to call by subclass if we want to indicate
     * the end of the sport.
     * 
     * Be sure to not forget to call super.startProgram() if overrided
     */
    private void endProgram() {
        isRunning = false;
        setChanged();
        notifyObservers();
    }
    
    /**
     * return the current status of the sport seance.
     * @return the current status of the sport seance.
     */
    public boolean getIsRunning() {
        return isRunning;
    }
    
    public WorkoutIntensity getIntensity() {
        return parts[Math.min(parts.length - 1, currentPart)].getIntensity();
    }
}
