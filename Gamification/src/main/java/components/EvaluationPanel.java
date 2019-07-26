package components;

import Program.evaluation.EvaluationManager.ObservableEvaluation;
import heigvd.gamification.Background;
import Program.evaluation.EvaluationRate;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Panel where are drawn the smiley relatively to the Workout Evaluation
 * 
 * @author jimmy
 */
public class EvaluationPanel extends JPanel implements Observer {
    
    /**
     * current Evaluations to draw
     */
    private EvaluationRate[] evaluations;
    
    /**
     * Evaluation mapping image relatively to the enum EvaluationRate ordinal
     */
    private Image[] evaluationsImages;
    
    /**
     * buffer where we will draw
     */
    private Object bufferImage;

    /**
     * constructor
     * 
     * @param nbParts number of parts of the workout
     */
    public EvaluationPanel(int nbParts) {
        super();
        EvaluationRate[] listRate = EvaluationRate.values();
        evaluations = new EvaluationRate[nbParts];
        for (int i = 0; i < nbParts; i++) {
            evaluations[i] = EvaluationRate.NONE;
        }
        evaluationsImages = new Image[listRate.length];
        for (int i = 0; i < listRate.length; i++) {
            try {
                evaluationsImages[i] = ImageIO.read(Background.class.getResource("/assets/ui/" + listRate[i].name() + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(EvaluationPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * how is drawn the component
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D twoD = (Graphics2D)g;
 
        if (bufferImage == null) {
            bufferImage = (BufferedImage)(createImage(getWidth(), getHeight()));
        }
        
        int i = 0;
        for (EvaluationRate rate : evaluations) {
            int size = getHeight() / evaluations.length;
            Image image = evaluationsImages[rate.ordinal()].getScaledInstance(getWidth(), size, java.awt.Image.SCALE_SMOOTH);
            g.drawImage(image, 0, i * size, getWidth(), size, null);
            i++;
        }
    }

    /**
     * we are notified when an evaluation changes and we redraw the component
     * @param o
     * @param o1 
     */
    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof ObservableEvaluation) {
            evaluations = ((ObservableEvaluation)o).getEvaluations();
            repaint();
        }
    }
    
}
