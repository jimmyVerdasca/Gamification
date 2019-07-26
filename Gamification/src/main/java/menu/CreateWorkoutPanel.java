package menu;

import Program.AbstractProgram;
import Program.Movement;
import Program.TimeProgram;
import Program.WorkoutIntensity;
import Program.WorkoutPart;
import components.ListProgramParts;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import util.ImageUtil;

/**
 * Panel of creation of the current workout
 *
 * @author jimmy
 */
public class CreateWorkoutPanel extends JPanel {
    
    private ImageIcon[] movements;
    private int width = Toolkit.getDefaultToolkit().getScreenSize().width / 3;
    private int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final JScrollPane movementScrollableList;
    private final JScrollPane intensityScrollableList;
    private final DefaultListModel listProgram;
    private final JList movementList;
    private final DefaultListModel listIntensity;
    private final JSpinner spinner;
    private final JList intensityList;
    private final JList programPartsList;
    
    /**
     * constructor
     * 
     * @param menu parent JFrame
     */
    CreateWorkoutPanel(Menu menu) {
        super();
        Font font = new Font("Papyrus", Font.BOLD, 30);
        
        movements = new ImageIcon[] {
            new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/running.png")).getImage(), 40, 40)),
            new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/bike.png")).getImage(), 40, 40)),
            new ImageIcon(ImageUtil.getScaledImage(new ImageIcon(getClass().getResource("/assets/ui/rower.png")).getImage(), 40, 40))
        };
        
        
        Container cp = menu.getContentPane();
        
        BoxLayout vfLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(vfLayout);
        
        DefaultListModel listMovement = new DefaultListModel();
        for (Movement movement : Movement.values()) {
            listMovement.addElement(movement);
        }
        movementList = new JList(listMovement);
        movementList.setFont(font);
        movementList.setSelectionMode(SINGLE_SELECTION);
        movementList.setSelectedIndex(0);
        movementScrollableList = new JScrollPane(movementList);
        movementScrollableList.setPreferredSize(new Dimension(width, height / 6));
        movementScrollableList.setMaximumSize(movementScrollableList.getPreferredSize());
        
        
        listIntensity = new DefaultListModel();
        for (WorkoutIntensity intensity : WorkoutIntensity.values()) {
            listIntensity.addElement(intensity);
        }
        intensityList = new JList(listIntensity);
        intensityList.setFont(font);
        intensityList.setSelectionMode(SINGLE_SELECTION);
        intensityList.setSelectedIndex(0);
        intensityScrollableList = new JScrollPane(intensityList);
        intensityScrollableList.setPreferredSize(new Dimension(width, height / 5));
        intensityScrollableList.setMaximumSize(intensityScrollableList.getPreferredSize());
        
        listProgram = new DefaultListModel();
        programPartsList = new JList(listProgram);
        programPartsList.setCellRenderer(new ListProgramParts());
        programPartsList.setFont(font);
        programPartsList.setSelectionMode(SINGLE_SELECTION);
        JScrollPane resultWorkoutScrollableList = new JScrollPane(programPartsList);
        resultWorkoutScrollableList.setPreferredSize(new Dimension(width, height / 2));
        resultWorkoutScrollableList.setMaximumSize(resultWorkoutScrollableList.getPreferredSize());
        
        JPanel panelTime = new JPanel();
        BoxLayout layoutTime = new BoxLayout(panelTime, BoxLayout.X_AXIS);
        panelTime.setLayout(layoutTime);
        JLabel labelTime = new JLabel("length : ");
        labelTime.setFont(font);
        SpinnerModel timeSpinnerModel = new SpinnerNumberModel(5, 0, 60, 1);
        spinner = new JSpinner(timeSpinnerModel);
        spinner.setFont(font);
        spinner.setPreferredSize(new Dimension(width / 10, height / 20));
        spinner.setMaximumSize(spinner.getPreferredSize());
        
        JButton addPartButton = createButton("Add Part", font);
        JButton removePartButton = createButton("Remove Part", font);
        JPanel panelPartButtons = new JPanel();
        BoxLayout layoutPartPanelButtons = new BoxLayout(panelPartButtons, BoxLayout.X_AXIS);
        panelPartButtons.setLayout(layoutPartPanelButtons);
        JButton cancelButton = createButton("Cancel", font);
        JButton createButton = createButton("Create Workout", font);
        JPanel panelButtons = new JPanel();
        BoxLayout layoutPanelButtons = new BoxLayout(panelButtons, BoxLayout.X_AXIS);
        panelButtons.setLayout(layoutPanelButtons);
        
        add(movementScrollableList);
        add(intensityScrollableList);
        panelTime.add(labelTime);
        panelTime.add(spinner);
        add(panelTime);
        panelPartButtons.add(addPartButton);
        panelPartButtons.add(removePartButton);
        add(panelPartButtons);
        add(resultWorkoutScrollableList);
        panelButtons.add(cancelButton);
        panelButtons.add(createButton);
        add(panelButtons);
        
        setPreferredSize(new Dimension(width, height));
        cp.add(this);
        
        // allow to draw icons
        programPartsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setIcon(movements[((WorkoutPart)value).getMovement().ordinal()]);
                return label;
            }
        });
        
        addPartButton.addActionListener((ActionEvent e) -> {
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    addPart();
                }
            };
            loop.start();
        });
        
        addPartButton.addActionListener((ActionEvent e) -> {
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    removePart();
                }
            };
            loop.start();
        });
        
        cancelButton.addActionListener((ActionEvent e) -> {
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    menu.menu(menu);
                }
            };
            loop.start();
        });
        
        createButton.addActionListener((ActionEvent e) -> {
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    if (listProgram.getSize() > 0) {
                        menu.setProgram(menu, createProgram());
                    }
                    menu.menu(menu);
                }
            };
            loop.start();
        });
    }
    
    /**
     * utilitary method to create a button
     * 
     * @param text of the button
     * @param font of the button
     * @return the button created
     */
    private JButton createButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        return button;
    }
    
    /**
     * add a part if possible in the workout in creation progress
     */
    private void addPart() {
        if (intensityList.getSelectedValue() != null
                && movementList.getSelectedValue() != null
                && (int)spinner.getValue() != 0) {
            WorkoutPart newPart = new WorkoutPart(
                    (int)spinner.getValue() * 60,
                    (WorkoutIntensity)intensityList.getSelectedValue(),
                    (Movement)movementList.getSelectedValue()
            );
            listProgram.addElement(newPart);
        }
    }
    
    /**
     * remove a part of the workout in creation progress
     */
    private void removePart() {
        if (programPartsList.getSelectedIndex() != -1) {
            listProgram.remove(programPartsList.getSelectedIndex());
        }
    }
    
    /**
     * create a workout with the workoutPart list content
     * 
     * @return the new workout created
     */
    private AbstractProgram createProgram() {
        WorkoutPart[] listParts = new WorkoutPart[listProgram.getSize()];
        for (int i = 0; i < listProgram.getSize(); i++) {
            listParts[i] = (WorkoutPart)listProgram.get(i);
        }
        return new TimeProgram(listParts);
    }
}
