/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heigvd.gamification;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author jimmy
 */
public class CharacterController {

    private final String MOVE_LEFT = "move left";
    private final String MOVE_RIGHT = "move right";
    private final Character character;
    
    CharacterController(ScrollingBackground back) {
        this.character = back.getCharacter();
        back.getInputMap(back.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
        back.getActionMap().put(MOVE_LEFT, new MoveAction(true));
        back.getInputMap(back.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
        back.getActionMap().put(MOVE_RIGHT, new MoveAction(false));
    }
    
    public void characterMoveLeft() {
        character.moveLeft();
    }
    
    private class MoveAction extends AbstractAction {

        boolean left;

        MoveAction(boolean left) {

            this.left = left;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if(left) {
                character.moveLeft();
            } else {
                character.moveRight();
            }
        }
    }


}
