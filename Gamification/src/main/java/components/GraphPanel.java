package components;

import Program.AbstractProgram;
import Program.AbstractProgram.LengthObservable;
import Program.AbstractProgram.PartObservable;
import effortMeasurer.EffortCalculator;
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
 *
 * @author jimmy
 */
public class GraphPanel extends JPanel implements Observer {
    private final Object lock = new Object();
    double[][] data = {{1.0,60}, {1.2,60}, {0.6,600}};
    double xTotal;
    double yMax;
    final int PAD = 20;
    double currentLength = 0;
    List<Double> listEffortOverLength;

    public GraphPanel(double[][] data) {
        this.data = data;
        xTotal = 0;
        yMax = data[0][0];
        for (double[] d : data) {
            xTotal += d[1];
            if(yMax < d[0]) {
                yMax = d[0];
            }
        }
        listEffortOverLength = new ArrayList();
    }
    
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
        double xScale = (w - 2*PAD)/xTotal;
        double yScale = (h - 2*PAD)/yMax;
        // The origin location.
        int x0 = PAD;
        int y0 = h-PAD;
        g2.setPaint(Color.blue);
        int xBefore = x0;
        for(int j = 0; j < data.length; j++) {
            int xAfter = (int)(xScale * data[j][1]);
            int y = (int)(yScale * data[j][0]);
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
                if (newValue > yMax) {
                    yMax = newValue;
                }
                
                synchronized (lock) {
                    listEffortOverLength.add(newValue);
                }
            }
            repaint();
        } 
    }
}
