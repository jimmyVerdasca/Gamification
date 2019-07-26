package components;

import Program.AbstractProgram.PartObservable;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * JList that observ a workout to permanently set the selected value to the
 * current activ workout part
 * 
 * @author jimmy
 */
public class JListObserver extends JList implements Observer {

    /**
     * constructor
     * 
     * @param dlm model list containing the WorkoutParts
     */
    public JListObserver(DefaultListModel dlm) {
        super(dlm);
        setFont(new Font("Papyrus", Font.BOLD, 30));
    }

    /**
     * set the selected part to the current WorkoutPart of the program
     * that sent the notification
     * 
     * @param o workout sending the notification
     * @param o1 
     */
    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof PartObservable) {
            PartObservable program = ((PartObservable)o);
            this.setSelectedIndex(program.getCurrentPart());
        }
    }
    
}
