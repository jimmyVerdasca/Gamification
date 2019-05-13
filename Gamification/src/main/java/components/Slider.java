package components;

import effortMeasurer.EffortCalculator;
import effortMeasurer.EffortCalculator.EffortObservable;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.imageio.ImageIO;
import javax.swing.JSlider;

/**
 *
 * @author jimmy
 */
public class Slider extends JSlider implements Observer {
    
    private Image full = null;
    private Image empty = null;
    private BufferedImage fullCutted = null;
    private BufferedImage fullCopy = null;
    private double percentEffort = 0.0;
    private int tempValue = 0;
    private boolean firstPaint = true;
    
    public Slider(int min, int max) throws IOException {
        super(JSlider.VERTICAL, min, max, 0);
        setOpaque(false);
        full = ImageIO.read(getClass().getResource("/assets/ui/fullSpeedBar.png"));
        empty = ImageIO.read(getClass().getResource("/assets/ui/emptySpeedBar.png"));
    }

    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof EffortObservable) {
            EffortObservable effortObs = ((EffortObservable)o);
            percentEffort = effortObs.getEffort();
            if (percentEffort > 1) {
                tempValue = (int)(((percentEffort - 1) / (effortObs.getMAX_REACHED() - 1) + 1) * (getMaximum() / 2.0));
            } else {
                tempValue = (int)(percentEffort * getMaximum() / 2.0);
            }
            if (tempValue > getMaximum()) {
                tempValue = getMaximum();
            } else if (tempValue < getMinimum()) {
                tempValue = getMinimum();
            }
            setValue(tempValue);
        }
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        if (firstPaint) {
            firstPaint = false;
            full = full.getScaledInstance(getWidth() / 2, getHeight(), Image.SCALE_DEFAULT);
            empty = empty.getScaledInstance(getWidth() / 2, getHeight(), Image.SCALE_DEFAULT);
            
            fullCopy = new BufferedImage(getWidth() / 2, getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics gTemp = fullCopy.createGraphics();
            gTemp.drawImage(full, 0, 0, null);
            gTemp.dispose();
        }
        g.drawImage(empty, 0, 0, null);
        int yCut = getHeight() - (int)(((double)tempValue) / getMaximum() * getHeight());
        if (yCut != getHeight()) { 
            fullCutted = ((BufferedImage)fullCopy).getSubimage(0,
                yCut,
                getWidth() / 2,
                getHeight() - yCut);


            g.drawImage(fullCutted, 0, yCut, null);
        }
    }
}
