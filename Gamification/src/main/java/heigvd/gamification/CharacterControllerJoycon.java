package heigvd.gamification;

import java.util.Map;
import java.util.Set;
import joyconController.Joycon;
import joyconController.JoyconConstant;
import joyconController.JoyconEvent;
import joyconController.JoyconListener;

/**
 * Implementation of the character handler with a joy-con controller.
 * 
 * Allow to move a given character
 * with the acceleration given by the controller.
 * 
 * @author jimmy
 */
public class CharacterControllerJoycon extends AbstractCharacterController {

    /**
     * max average acceleration ever reched
     */
    private final double MAX_POSSIBLE_ACCEL = 2.25;
    
    /**
     * array where we store the acceleration of a movement.
     */
    private final double[] movement = new double[100];
    
    /**
     * current number of pertinent data in the buffer
     */
    private int index = 0;
    
    /**
     * joycon handler API
     */
    private final Joycon joycon;
    
    /**
     * state of the UP button of the joycon (true pressed, false otherwise)
     */
    private boolean isUPPressed = false;
    
    /**
     * constructor
     * 
     * @param character that we manipulate.
     * @param maxSpeed the maximum speed reachable by the character.
     */
    public CharacterControllerJoycon(Character character, int maxSpeed) {
        super(character, maxSpeed);
        for (int i = 0; i < movement.length; i++) {
            movement[i] = 0;
        }
        
        joycon = new Joycon(JoyconConstant.JOYCON_LEFT);
        joycon.setListener(new JoyconListener() {
            
            /**
             * behaviour when we receive a new data from the controller
             * @param je Joycon Event parameters
             */
            @Override
            public void handleNewInput(JoyconEvent je) {
                //inputs joycon map
                Set<Map.Entry<String, Boolean>> entrySet = je.getNewInputs().entrySet();
                for (Map.Entry<String, Boolean> entry : entrySet) {
                        
                    if (entry.getKey().equals(JoyconConstant.UP) && entry.getValue() && index < movement.length) {
                        isUPPressed = true;
                    } else if (entry.getKey().equals(JoyconConstant.UP) && !entry.getValue()) {
                        isUPPressed = false;
                        for (int i = 0; i < movement.length; i++) {
                            movement[i] = 0;
                        }
                        index = 0;
                    }
                }
                if (entrySet.isEmpty() && isUPPressed) {
                    addEntryAccel(je.getAccelX());
                }
                if (isUPPressed) {
                    setSpeed(evalMovement());
                }
            }
        });
    }
    
    /**
     * evaluate the movement by comparing the average acceleration
     * with MAX_POSSIBLE_ACCEL.
     * @return a double representing the "perfection" of the movement.
     */
    private double evalMovement() {
        double total = 0;
        for (double s : movement) {
            total += s;
        }
        return -(total / index) / MAX_POSSIBLE_ACCEL;
    }
    
    /**
     * add some accelerations in the buffer at the next free indexes
     * @param accel 
     */
    private void addEntryAccel(double[] accel) {
        index += Math.min(accel.length, movement.length);
        try {
            System.arraycopy(accel, 0, movement, index, accel.length);
        } catch (IndexOutOfBoundsException ex) {
            //We stop copying elements if the movement is too long
        }
    }
}
