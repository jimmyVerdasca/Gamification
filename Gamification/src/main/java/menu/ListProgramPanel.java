package menu;

import Program.AbstractProgram;
import Program.WorkoutPart;
import Program.evaluation.EvaluationManager;
import components.EvaluationPanel;
import components.GraphPanel;
import components.JListObserver;
import effortMeasurer.EffortCalculator;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import util.ImageUtil;

/**
 * Left panel in game showing the workout informations
 * mainly the dynamic graph, workout part list and the workout evaluation
 * 
 * @author jimmy
 */
public class ListProgramPanel extends JPanel {

    private final JListObserver listProgramPartsPanel;
    
    private ImageIcon[] icons; 
    private final GraphPanel graphPanel;
    
    /**
     * constructor
     * 
     * @param program workout
     * @param effortCalculator detector
     */
    public ListProgramPanel(AbstractProgram program, EffortCalculator effortCalculator) {
        BoxLayout vfLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(vfLayout);
        
        icons = new ImageIcon[] {
          new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/running.png")).getImage(), 40, 40)),
          new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/bike.png")).getImage(), 40, 40)),
          new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/rower.png")).getImage(), 40, 40))};
        
        DefaultListModel dlm = new DefaultListModel();
        listProgramPartsPanel = new JListObserver(dlm);
        double[][] data = new double[program.getParts().length][];
        int i = 0;
        for (WorkoutPart part : program.getParts()) {
            data[i] = new double[2];
            data[i][0] = part.getIntensity().getPercent() * effortCalculator.getFreqAtVMASpeedOfMovement(part.getMovement());
            data[i][1] = part.getLength();
            dlm.addElement(part);
            i++;
        }
        // avoid selecting by clicking (reselct the old value)
        listProgramPartsPanel.setSelectionMode(SINGLE_SELECTION);
        listProgramPartsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                listProgramPartsPanel.setSelectedIndex(program.getCurrentPart());
            }
        });
        // select the first element
        listProgramPartsPanel.setSelectedIndices(new int []{0});
        
        // allow to draw icons
        listProgramPartsPanel.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setIcon(icons[program.getParts()[index].getMovement().ordinal()]);
                return label;
            }
        });
        program.addPartObserver(listProgramPartsPanel);
        JPanel listProgramPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(listProgramPanel, BoxLayout.Y_AXIS);
        listProgramPanel.add(listProgramPartsPanel);
        int nbParts = program.getParts().length;
        EvaluationManager evaluationManager = new EvaluationManager(program, effortCalculator);
        EvaluationPanel evaluationPanel = new EvaluationPanel(nbParts);
        listProgramPartsPanel.setPreferredSize(new Dimension(500,nbParts * 50));
        evaluationPanel.setPreferredSize(new Dimension(50,nbParts * 50));
        listProgramPanel.add(evaluationPanel);
        evaluationManager.addEvalObserver(evaluationPanel);
        add(listProgramPanel);
        graphPanel = new GraphPanel(data);
        program.addLengthObserver(graphPanel);
        effortCalculator.addObserver(graphPanel);
        add(graphPanel);
    }
    
    
}
