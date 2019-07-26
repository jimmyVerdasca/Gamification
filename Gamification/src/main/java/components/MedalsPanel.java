package components;

import heigvd.gamification.Background;
import heigvd.gamification.rules.RulesManager.WinRuleObservable;
import heigvd.gamification.rules.RulesName;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Cycling Medals Panel that show the 8 most recent medals won by a player
 * 
 * @author jimmy
 */
public class MedalsPanel extends JPanel implements Observer {

    private BufferedImage bufferImage;
    private List<RulesName> rules;
    private Image[] rulesImages;
    private Image medalImage;
    private final int MAX_MEDALS = 8;

    /**
     * constructor
     */
    public MedalsPanel() {
        super();
        rules = new ArrayList<>();
        RulesName[] rulesName = RulesName.values();
        int size = rulesName.length;
        rulesImages = new Image[size];
        for (int i = 0; i < size; i++) {
            try {
                rulesImages[i] = ImageIO.read(Background.class.getResource("/assets/ui/medals/" + rulesName[i].name() + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(EvaluationPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            medalImage = ImageIO.read(Background.class.getResource("/assets/ui/medals/winner.png"));
        } catch (IOException ex) {
            Logger.getLogger(MedalsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * how the component is drawn
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D twoD = (Graphics2D)g;
 
        if (bufferImage == null) {
            bufferImage = (BufferedImage)(createImage(getWidth(), getHeight()));
        }
        
        
        int i = rules.size() - 1;
        while (i >= 0 && i > rules.size() - 1 - MAX_MEDALS) {
            RulesName rule = rules.get(i);
            int size = getHeight() / MAX_MEDALS;
            Image image = rulesImages[rule.ordinal()].getScaledInstance(size, getHeight(), java.awt.Image.SCALE_SMOOTH);
            g.drawImage(medalImage, (rules.size() - i) * size, getHeight() / 3, size, getHeight() / 3, null);
            g.drawImage(image, (rules.size() - i) * size, 2 * getHeight() / 3, size, getHeight() / 3, null);
            i--;
        }
    }
    
    /**
     * is notified each time an objectif is succeeded, add the medal to the list
     * and repaint the component
     * 
     * @param o
     * @param o1 
     */
    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof WinRuleObservable) {
            rules = ((WinRuleObservable)o).getClearedRules();
            repaint();
        }
    }
    
}
