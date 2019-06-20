package components;

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
 * improvement of a JSlider to
 * put our own images and Observe an EffortCalculator.
 * 
 * @author jimmy
 */
public class Slider extends JSlider implements Observer {
    
    /**
     * Image when the slider is full.
     */
    private Image full = null;
    
    /**
     * Image when the slider is empty.
     */
    private Image empty = null;
    
    /**
     * Full image result when scalled and cutted.
     */
    private BufferedImage fullCutted = null;
    
    /**
     * Full image from where we take a subpart each time we need to redraw.
     */
    private BufferedImage fullCopy = null;
    
    /**
     * copy of the effort from the EffortCalculator
     */
    private double percentEffort = 0.0;
    
    /**
     * Current y size of the full part of the
     * slider relatively to the effort value.
     */
    private int tempValue = 0;
    
    /**
     * to avoid recreate graphics after first draw
     */
    private boolean firstPaint = true;
    
    /**
     * constructor
     * If minimum or maximum are exceeded. We just put the values to MAX or MIN.
     * 
     * @param min minimum value accepted by the slider
     * @param max maximum value of the slider
     * @throws IOException 
     */
    public Slider(int min, int max) throws IOException {
        super(JSlider.VERTICAL, min, max, 0);
        setOpaque(false);
        full = ImageIO.read(getClass()
                .getResource("/assets/ui/fullSpeedBar.png"));
        empty = ImageIO.read(getClass()
                .getResource("/assets/ui/emptySpeedBar.png"));
    }

    /**
     * As observer, this method is call when the effort value of the
     * EffortCalculator changes.
     * 
     * @param observable Instance of the observable that called this method.
     * @param param Additional parameters of the notification call.
     */
    @Override
    public void update(Observable observable, Object param) {
        if (observable instanceof EffortObservable) {
            EffortObservable effortObs = ((EffortObservable)observable);
            percentEffort = effortObs.getEffort();
            
            /**
             * 0 to 1 is half of the slider. 1 to getMAXREACHED() is the other half.
             */
            if (percentEffort > 1) {
                tempValue = (int)(((percentEffort - 1) / (effortObs.getMAX_REACHED() - 1) + 1) * (getMaximum() / 2.0));
            } else {
                tempValue = (int)(percentEffort * getMaximum() / 2.0);
            }
            
            // protection against exceeding max or min.
            if (tempValue > getMaximum()) {
                tempValue = getMaximum();
            } else if (tempValue < getMinimum()) {
                tempValue = getMinimum();
            }
            setValue(tempValue);
        }
    }
    
    /**
     * Method called to refresh the display when we call the repaint() method.
     * 
     * @param g graphics where we draw
     */
    @Override
    public void paintComponent(Graphics g)
    {
        // At first call we scale the images
        if (firstPaint) {
            firstPaint = false;
            full = full.getScaledInstance(getWidth() / 2, getHeight(), Image.SCALE_DEFAULT);
            empty = empty.getScaledInstance(getWidth() / 2, getHeight(), Image.SCALE_DEFAULT);
            
            fullCopy = new BufferedImage(getWidth() / 2, getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics gTemp = fullCopy.createGraphics();
            gTemp.drawImage(full, 0, 0, null);
            gTemp.dispose();
        }
        // draw the empty image
        g.drawImage(empty, 0, 0, null);
        
        int yCut = getHeight() - (int)(((double)tempValue) / getMaximum() * getHeight());
        // If y size = 0 we dont need to draw the full image at all.
        if (yCut != getHeight()) { 
            // Copy the part of the full image that we need
            fullCutted = ((BufferedImage)fullCopy).getSubimage(0,
                yCut,
                getWidth() / 2,
                getHeight() - yCut);

            // draw the copy
            g.drawImage(fullCutted, 0, yCut, null);
        }
    }
}
