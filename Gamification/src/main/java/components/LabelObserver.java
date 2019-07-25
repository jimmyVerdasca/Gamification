package components;

import effortMeasurer.EffortCalculator.EffortObservable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JLabel;

/**
 * Label that observ a detector to display "current effort / expected effort"
 * 
 * @author jimmy
 */
public class LabelObserver extends JLabel implements Observer {

    private double currentEffort;
    private double expectedEffort;
    private BigDecimal bd;
    private final int PLACE = 5;

    /**
     * update the text relatively to the detector value changes
     * 
     * @param o observable sending the changes events
     * @param o1 Additional parameters of the notification call.
     */
    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof EffortObservable) {
            EffortObservable detector = ((EffortObservable)o);
            expectedEffort = detector.getCurrentFreqTargetted();
            currentEffort = expectedEffort * detector.getEffort();
            setText("<html>" + round(currentEffort, PLACE) +
                    "<br>---------<br>" +
                    round(expectedEffort, PLACE) + "<html>");
        }
    }
    
    /**
     * method to round a decimal number to places digits. Found at :
     * https://www.baeldung.com/java-round-decimal-number
     * 
     * @param value the number we want to round
     * @param places number of digit we want
     * @return the value rounded to places digit
     */
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
