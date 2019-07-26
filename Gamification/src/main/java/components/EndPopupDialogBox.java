package components;

import heigvd.gamification.rules.RulesName;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import menu.Menu;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Pop up appearing when the game ends
 * Allow to see the medals received, the scores stored in the ranking.json file
 * And allow to store his own score in the same file
 * 
 * @author jimmy
 */
public class EndPopupDialogBox {
    
    /**
     * popup that we will create
     */
    JWindow w;
    int width = 600;
    int height = 900;
    long scoreFinal;
    
    int xPosition = (Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2;
    int yPosition = (Toolkit.getDefaultToolkit().getScreenSize().height - height ) / 2;
    
    /**
     * temp value of the horizontal position when we clicked the mouse.
     * allow to drag the popup
     */
    int xPressed;
    
    /**
     * temp value of the vertical position when we clicked the mouse.
     * allow to drag the popup
     */
    int yPressed;
    
    /**
     * path of the score file should be a DB in the futur
     */
    final String PATH_RANK = "ranking.json";
    
    /**
     * component where we will write our name to register our score
     */
    JTextField nameTextField;
    
    /**
     * parent JFrame that launched the popup
     * (necessary to be able to focus the popup)
     */
    private final Menu menu;
    
    /**
     * constructor
     * 
     * @param menu JFrame parent that launched this popup
     * @param score at the end of the game
     * @param medalsWon medals won in the game
     */
    public EndPopupDialogBox(Menu menu, long score, List<RulesName> medalsWon)
    {
        w = new JWindow(menu);
        w.setSize(width, height);
        w.setLocation(xPosition, yPosition);
        w.setFocusable(true);
        this.menu = menu;
        
        /**
         * store the ranking in a treemap
         */
        TreeMap<Long, List<String>> mapRanking = readRankFile(PATH_RANK);
        
        /**
         * iterate over medals, store them in a JScrollPane and calculate the
         * new score
         */
        DefaultListModel dlm = new DefaultListModel();
        int scoreRule;
        for (int i = 0; i < medalsWon.size(); i++) {
            scoreRule = medalsWon.get(i).getScore();
            score += scoreRule;
            try {
                dlm.addElement(new ListEntry("+" + Integer.toString(scoreRule) + " points " + medalsWon.get(i).name(),
                        new ImageIcon(ImageIO.read(EndPopupDialogBox.class.getResource("/assets/ui/medals/" + medalsWon.get(i).name() + ".png")).getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH))));
            } catch (IOException ex) {
                Logger.getLogger(EndPopupDialogBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.scoreFinal = score;
        
        
        JList list = new JList(dlm);
        list.setCellRenderer(new ListEntryCellRenderer());
        JScrollPane scrollableList = new JScrollPane(list);
        scrollableList.setPreferredSize(new Dimension(2 * width / 3, height / 3));
        scrollableList.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        DefaultListModel rankListModel = new DefaultListModel();
        for (Long rankScore : mapRanking.descendingKeySet()) {
            rankListModel.addElement(rankScore + " : " + mapRanking.get(rankScore));
        }
        JList rankList = new JList(rankListModel);
        JScrollPane rankScrollableList = new JScrollPane(rankList);
        scrollableList.setPreferredSize(new Dimension(2 * width / 3, height / 3));
        
        JLabel scoreLabel = new JLabel("<html>Congratulation you finished with the score of : " + score + "<html>");
        scoreLabel.setFont(new Font("Papyrus", Font.BOLD, 20));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nameTextField = new JTextField("ANONYME", 40);
        nameTextField.setPreferredSize(new Dimension(width / 2, 60));
        nameTextField.setMaximumSize(nameTextField.getPreferredSize());
        nameTextField.setMinimumSize(nameTextField.getPreferredSize());
        nameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameTextField.setFont(new Font("Papyrus", Font.BOLD, 20));
        // nameTextField can only have alphanumeric values
        nameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
                char c = ke.getKeyChar();
                if (!Character.isLetter(c) && !Character.isDigit(c)) {
                    ke.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                
            }
        
        });
        
        JLabel labelField = new JLabel("Pseudo : ", SwingConstants.RIGHT);
        labelField.setFont(new Font("Papyrus", Font.BOLD, 20));
        labelField.setPreferredSize(new Dimension(width / 4, 60));
        labelField.setMaximumSize(labelField.getPreferredSize());
        labelField.setMinimumSize(labelField.getPreferredSize());
        
        JButton registerButton = new JButton("Register score");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setFont(new Font("Papyrus", Font.BOLD, 20));
        
        JButton backButton = new JButton("Back to Menu");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font("Papyrus", Font.BOLD, 20));
        
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        
        panel.add(scrollableList);
        panel.add(scoreLabel);
        panel.add(rankScrollableList);
        JPanel panelNameField = new JPanel();
        BoxLayout fieldLayout = new BoxLayout(panelNameField,BoxLayout.X_AXIS);
        panelNameField.setLayout(fieldLayout);
        panelNameField.add(labelField);
        panelNameField.add(nameTextField);
        panel.add(panelNameField);
        JPanel panelButtons = new JPanel();
        BoxLayout buttonsLayout = new BoxLayout(panelButtons,BoxLayout.X_AXIS);
        panelButtons.setLayout(buttonsLayout);
        panelButtons.add(registerButton);
        panelButtons.add(backButton);
        panel.add(panelButtons);
        
        w.add(panel);
        
        /**
         * register the score with current name in text field if the register
         * button is clicked
         */
        registerButton.addActionListener((ActionEvent e) -> {
            menu.load(menu);
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    String newName = nameTextField.getText();
                    if(!mapContains(mapRanking, newName)) {
                        saveRank(mapRanking, newName, scoreFinal);
                        menu.menu(menu);
                        w.dispose();
                    } else {
                        if(mapContains(mapRanking, newName)) {
                            for (Long scoreKey : mapRanking.keySet()) {
                                if (mapRanking.get(scoreKey).contains(newName) && scoreFinal > scoreKey) {
                                    saveRank(mapRanking, newName, scoreFinal);
                                } else {
                                    nameTextField.setText("PSEUDO_EXIST_YET");
                                }
                            }
                        }
                    }
                }
            };
            loop.start(); 
        });
        
        /**
         * back to menu
         */
        backButton.addActionListener((ActionEvent e) -> {
            menu.load(menu); // to delete line
            Thread loop = new Thread()
            {
                @Override
                public void run()
                {
                    menu.menu(menu);
                    w.dispose();
                }
            };
            loop.start(); 
        });
        
        // alow to drag the popup
        w.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                xPressed = me.getX();
                yPressed = me.getY();
            }
        });
        w.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                Point p = w.getLocation();
                xPosition = xPosition + (me.getX()-xPressed);
                yPosition = yPosition + (me.getY()-yPressed);
                w.setLocation(xPosition, yPosition);
            }
        });
        w.setVisible(true);
    }
    
    /**
     * store the treemap in the ranking file with the new name and score
     * @param mapRanking treemap to register as ranking in a file
     * @param newName the new name to store
     * @param score the new score to store
     */
    public void saveRank(TreeMap<Long, List<String>> mapRanking, String newName, long score) {
        JSONObject sampleObject = new JSONObject();
        boolean added = false;
        for (Object object : mapRanking.descendingKeySet()) {
            if ((Long)object < score) {
                sampleObject.put(newName, score);
                added = true;
            }
            for (Object name : mapRanking.get((Long)object)) {
                sampleObject.put(name, object);
            }
        }
        if(!added) {
            sampleObject.put(newName, score);
        }
        try {
            Files.write(Paths.get(PATH_RANK), sampleObject.toJSONString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(EndPopupDialogBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * create and fill a TreeMap by reading the ranking given file
     * 
     * @return the TreeMap created
     */
    private TreeMap<Long, List<String>> readRankFile(String fileName) {
        JSONParser jsonParser = new JSONParser();
        TreeMap<Long, List<String>> mapRanking = new TreeMap();
        FileReader reader;
        try {
            reader = new FileReader(fileName);
            JSONObject ranks = (JSONObject)(jsonParser.parse(reader));
            for (Object rankName : ranks.keySet()) {
                long rankScore = (Long)ranks.get(rankName);
                List<String> listName;
                if(!mapRanking.containsKey(rankScore)) {
                    listName = new ArrayList();
                } else {
                    listName = mapRanking.get(rankScore);
                }
                listName.add(rankName.toString());
                mapRanking.put(rankScore, listName);
            }
        } catch (FileNotFoundException ex) {
            
        } catch (IOException | ParseException ex) {
            Logger.getLogger(EndPopupDialogBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapRanking;
    }
    
    /**
     * check if a name is contained in the treemap
     * @param map the treemap of rank
     * @param name the name we are looking for
     * @return true if the name is contained in the ranking treemap
     */
    private boolean mapContains(TreeMap<Long, List<String>> map, String name) {
        for (List<String> list : map.values()) {
            if(list.contains(name)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * list to store medals info and display them in a JScrollPane
     */
    class ListEntry
    {
        private String value;
        private ImageIcon icon;
        
        public ListEntry(String value, ImageIcon icon) {
            this.value = value;
            this.icon = icon;
        }
        
        public String getValue() {
            return value;
        }
        
        public ImageIcon getIcon() {
            return icon;
        }
        
        public String toString() {
            return value;
        }
    }
    /**
     * renderer to use to display medals
     */
    class ListEntryCellRenderer
            extends JLabel implements ListCellRenderer
    {
        private JLabel label;
        
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected,
                boolean cellHasFocus) {
            ListEntry entry = (ListEntry) value;
            
            setText(value.toString());
            setIcon(entry.getIcon());
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            setEnabled(list.isEnabled());
            setFont(new Font("Papyrus", Font.BOLD, 20));
            setOpaque(true);
            
            return this;
        }
    }
    
} 
