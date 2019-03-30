/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heigvd.gamification;

import java.awt.AWTException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Robot;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author jimmy
 */
public class EffortCalculator implements MouseMotionListener {

    private int oldY = -1;
    private ScrollingBackground sb;
    private int fullSpeed;
    private Robot robot;
    private LinkedList<Integer> speedAverage;
    
    // plus on prend de mesure plus la nouvelle mesure de vitesse à peu d'impact
    private final int lengthAverage = 400;

    public EffortCalculator(ScrollingBackground sb) throws AWTException {
        this.sb = sb;
        fullSpeed = sb.getFullSpeed();
        speedAverage = new LinkedList();
        for (int i = 0; i < lengthAverage; i++) {
            speedAverage.add(0);
        }
        sb.addMouseMotionListener(this);
        robot = new Robot();
        replace();
        new Timer().schedule(new BrakeTimer(this) ,1000,10);
    }
    
    @Override
    public void mouseDragged(MouseEvent me) {
        
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        
        int newY = me.getY();
        
        if (oldY == -1) {
            oldY = newY;
            return;
        }
        synchronized(this) {
            speedAverage.remove();
            speedAverage.add(Math.abs(newY - oldY));
        }
        updateSpeed();
        oldY = newY;
        replace();
    }
    
    /**
     * Puisque les mesure de vitesses se font pour l'instant avec la souris
     * Nous sommes obligé de replacer la souris.
     * Malheureusement à chaque fois qu'on le fait, on doit désactiver
     * l'écoute des événement se qui fait perdre certaine mesures entre temps.
     */
    public void replace() {
        sb.removeMouseMotionListener(this);
        robot.mouseMove(200, 200);
        oldY = -1;
        sb.addMouseMotionListener(this);
    }
    
    public double calculateSpeed() {
        int average = 0;
        synchronized(this) {
            for (int speed : speedAverage) {
                average += speed;
            }
        }
        return (average / lengthAverage) / 100.;
    }
    
    public void updateSpeed() {
        sb.setSpeed((int)(calculateSpeed() * fullSpeed));
    }
    
    class BrakeTimer extends TimerTask {

        private EffortCalculator parent;
        
        public BrakeTimer(EffortCalculator parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            synchronized(parent) {
                speedAverage.remove();
                speedAverage.add(0);
            }
            updateSpeed();
        }
        
    }
}
