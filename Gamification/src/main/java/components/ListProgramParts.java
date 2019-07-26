package components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Liste affichant les diverses parties du workout
 * 
 * @author jimmy
 */
public class ListProgramParts extends JLabel implements ListCellRenderer {
    
   Color selectColor = Color.RED;
   
   /**
    * define how is displayed an item of the list
    * 
    * @param list
    * @param value Item to display
    * @param index of the item in the list
    * @param isSelected is the item selected
    * @param cellHasFocus has the list the focus
    * @return 
    */
   @Override
   public Component getListCellRendererComponent(JList list, 
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
   {
      String s = value.toString();
      if (isSelected) {
         setBackground(list.getSelectionBackground());
         setForeground(selectColor);
      }else{
         setBackground(list.getBackground());
         setForeground(list.getForeground());
      }
      setText(index + " " + s);
      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setOpaque(true);
      
      return this;
   }
}
