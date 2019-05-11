/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joyconController;

import java.util.HashMap;

/**
 * <b>The object that will be sent when an input is triggered</b>
 * <p>
 * <b>newInputs</b> contains the new inputs with this format:
 * <b>NAME_OF_THE_INPUT</b>:<b>STATE_OF_THE_INPUT</b> (true/false)</p>
 *
 * <p>
 * <b>joystick</b> contains the value of the joystick (0 - 8)</p>
 *
 * @version 1.0
 * @author goupil
 */
public class JoyconEvent {

    private HashMap<String, Boolean> newInputs;
    private float horizontal;
    private float vertical;
    private byte battery;
    private final double[] accelX;

    public JoyconEvent(HashMap<String, Boolean> newInputs, double[] accelX, float horizontal, float vertical, byte battery) {
        this.newInputs = newInputs;
        this.accelX = accelX;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.battery = battery;
    }

    public HashMap<String, Boolean> getNewInputs() {
        return newInputs;
    }

    public float getHorizontal() {
        return horizontal;
    }

    public float getVertical() {
        return vertical;
    }

    public byte getBattery() {
        return battery;
    }
    
    public double[] getAccelX() {
        return accelX;
    }
}
