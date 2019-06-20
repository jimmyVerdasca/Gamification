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
     * Method to call by subclass if we want to indicate
     * the end of the sport.
     * 
     * Be sure to not forget to call super.startProgram() if overrided
     */
    public void endProgram() {
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
}
