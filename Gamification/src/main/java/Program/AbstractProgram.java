package Program;

import java.util.Observable;

/**
 *
 * @author jimmy
 */
public abstract class AbstractProgram extends Observable {
    
    private boolean isRunning = false;
    
    public AbstractProgram() {
    }
    
    public void startProgram() {
        isRunning = true;
        setChanged();
        notifyObservers();
    }
    
    public void endProgram() {
        isRunning = false;
        setChanged();
        notifyObservers();
    }
    
    public boolean getIsRunning() {
        return isRunning;
    }
}
