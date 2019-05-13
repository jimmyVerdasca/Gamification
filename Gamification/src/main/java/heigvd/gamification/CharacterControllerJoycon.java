package heigvd.gamification;

import java.util.Map;
import java.util.Set;
import joyconController.Joycon;
import joyconController.JoyconConstant;
import joyconController.JoyconEvent;
import joyconController.JoyconListener;

/**
 *
 * @author jimmy
 */
public class CharacterControllerJoycon {

    private final Character character;
    private final int MAX_MOVE_SPEED;
    private final double MAX_POSSIBLE_ACCEL = 2.25;
    private final double[] movement = new double[100];
    private int index = 0;
    private final Joycon joycon;
    private boolean isUPPressed = false;
    
    public CharacterControllerJoycon(Character character, int characterMaxSpeed) {
        this.character = character;
        MAX_MOVE_SPEED = characterMaxSpeed;
        for (int i = 0; i < movement.length; i++) {
            movement[i] = 0;
        }
        
        joycon = new Joycon(JoyconConstant.JOYCON_LEFT);
        joycon.setListener(new JoyconListener() {
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
    
    private double evalMovement() {
        double total = 0;
        for (double s : movement) {
            total += s;
        }
        return -(total / index) / MAX_POSSIBLE_ACCEL;
    }
    
    private void setSpeed(double evaluation) {
        if(evaluation > 1) {
            evaluation = 1;
        } else if (evaluation < -1) {
            evaluation = -1;
        }
        character.setSpeed((int)(MAX_MOVE_SPEED * evaluation));
    }
    
    private void addEntryAccel(double[] accel) {
        index += Math.min(accel.length, movement.length);
        try {
            System.arraycopy(accel, 0, movement, index, accel.length);
        } catch (IndexOutOfBoundsException ex) {
            //We stop copying elements if the movement is too long
        }
    }
}
