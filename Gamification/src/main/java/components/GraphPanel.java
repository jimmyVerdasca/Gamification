package components;

import Program.AbstractProgram.LengthObservable;
import effortMeasurer.EffortCalculator.EffortObservable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 * Panel where is drawn the graph summarizing the workout and the history
 * efforts of the user 
 * 
 * @author jimmy
 */
public class GraphPanel extends JPanel implements Observer {
    private final Object lock = new Object();
    
    /**
     * workout plan (couple with intensities and length)
     */
    double[][] workoutData;
    
    /**
     * length of the workout
     */
    double workoutLength;
    
    /**
     * maximum intensity ever reached
     */
    double currentMaximumIntensity;
    
    /**
     * padding of the axes
     */
    final int PAD = 20;
    
    /**
     * current length progress of the workout
     */
    double currentLength = 0;
    
    /**
     * all the effort registred until now
     */
    List<Double> listEffortOverLength;

    /**
     * constructor
     * 
     * @param workoutPlan 
     */
    public GraphPanel(double[][] workoutPlan) {
        this.workoutData = workoutPlan;
        workoutLength = 0;
        currentMaximumIntensity = workoutPlan[0][0];
        for (double[] d : workoutPlan) {
            workoutLength += d[1];
            if(currentMaximumIntensity < d[0]) {
                currentMaximumIntensity = d[0];
            }
        }
        listEffortOverLength = new ArrayList();
    }
    
    /**
     * how the graph is drawn
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g2.drawLine(PAD, 0, PAD, h-PAD);
        g2.drawLine(PAD, h-PAD, w, h-PAD);
        double xScale = (w - 2*PAD)/workoutLength;
        double yScale = (h - 2*PAD)/currentMaximumIntensity;
        // The origin location.
        int x0 = PAD;
        int y0 = h-PAD;
        g2.setPaint(Color.blue);
        int xBefore = x0;
        for(int j = 0; j < workoutData.length; j++) {
            int xAfter = (int)(xScale * workoutData[j][1]);
            int y = (int)(yScale * workoutData[j][0]);
            g2.setPaint(Color.cyan);
            g2.fillRect(xBefore, y0 - y, xAfter, y);
            g2.setPaint(Color.BLACK);
            g2.drawRect(xBefore, y0 - y, xAfter, y);
            xBefore += xAfter;
        }
        int xPosition = (int)(x0 + currentLength * xScale);
        g2.setPaint(Color.red);
        g2.drawLine(xPosition, y0, xPosition, PAD);
        int i = 0;
        synchronized (lock) {
            for (Double effort : listEffortOverLength) {
                int yPosition = y0 - (int)(yScale * effort);
                g2.fillOval((int)(x0 + i * xScale - 1), yPosition, 2, 2);
                i++;
            }
        }
    }

    /**
     * We are notified when a new effort is registred to be able to draw the
     * history of effort
     * We are also notified when the length of the workout changes to update
     * the graph dynamically
     * 
     * @param o
     * @param o1 
     */
    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof LengthObservable) {
            LengthObservable program = (LengthObservable)o;
            currentLength = program.getCurrentLength();
            repaint();
        } else if (o instanceof EffortObservable){
            if (listEffortOverLength.size() < Math.floor(currentLength)) {
                EffortObservable detector = (EffortObservable)o;
                double newValue = detector.getEffort() * detector.getCurrentFreqTargetted();
                if (newValue > currentMaximumIntensity) {
                    currentMaximumIntensity = newValue;
                }
                
                synchronized (lock) {
                    listEffortOverLength.add(newValue);
                }
            }
            repaint();
        } 
    }
}
