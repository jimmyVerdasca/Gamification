package joyconController;

/**
 * <b>The listener for the joycon</b>
 * <p>
 * Give this listener to the joycon to handle his inputs</p>
 * <p>
 * Refer to the example to learn how to use it</p>
 *
 * @version 1.0
 * @author goupil
 */
public interface JoyconListener {

    /**
     *
     *
     * @param e The event object
     */
    public void handleNewInput(JoyconEvent e);
}
