package components;

import Program.AbstractProgram.PartObservable;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author jimmy
 */
public class JListObserver extends JList implements Observer {

    public JListObserver(DefaultListModel dlm) {
        super(dlm);
        setFont(new Font("Papyrus", Font.BOLD, 30));
    }

    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof PartObservable) {
            PartObservable program = ((PartObservable)o);
            this.setSelectedIndex(program.getCurrentPart());
        }
    }
    
}
