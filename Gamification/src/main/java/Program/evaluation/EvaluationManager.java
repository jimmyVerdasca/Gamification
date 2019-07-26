package Program.evaluation;

import Program.AbstractProgram;
import Program.AbstractProgram.PartObservable;
import effortMeasurer.EffortCalculator;
import effortMeasurer.EffortCalculator.EffortObservable;
import java.util.Observable;
import java.util.Observer;

/**
 * Classe managing the several EvaluationPart to update the evaluation of the
 * workout. 
 * It reads the effort entries data and distribute the values to the
 * EvaluationPart concerned.
 * 
 * When a part of the workout ends, ask to the EvaluationPart corresponding the
 * evaluation.
 * 
 * @author jimmy
 */
public class EvaluationManager implements Observer {
    
    /**
     * EvaluationParts mapping the WorkoutParts
     */
    private final EvaluationPart[] evaluations;
    
    /**
     * workout to evaluate
     */
    private final AbstractProgram program;
    
    /**
     * observable notifying when the status of an evaluation change
     */
    private final ObservableEvaluation obs;

    /**
     * constructor
     * 
     * @param program workout to evaluate
     * @param detector to observe the effort
     */
    public EvaluationManager(AbstractProgram program, EffortCalculator detector) {
        this.program = program;
        evaluations = new EvaluationPart[program.getParts().length];
        for (int i = 0; i < program.getParts().length; i++) {
            evaluations[i] = new EvaluationPart();
        }
        obs = new ObservableEvaluation();
        detector.addObserver(this);
        program.addPartObserver(this);
    }
    
    /**
     * add the data to the current (same index that the current WorkoutPart)
     * EvaluationPart
     * @param data the new effort to register to evaluate
     */
    public void addData(double data) {
        if (program.getIsRunning()) {
            evaluations[program.getCurrentPart()].addData(data);
            obs.update();
        }
    }
    
    /**
     * add an evaluation observer that will be notified when the status of an
     * evaluation changes
     * @param o Observer to add
     */
    public void addEvalObserver(Observer o) {
        obs.addObserver(o);
    }

    /**
     * return the current result of the evaluation of each part
     * 
     * @return the current result of the evaluation of each part
     */
    public EvaluationRate[] getEvaluations() {
        EvaluationRate[] eval = new EvaluationRate[evaluations.length];
        int i = 0;
        for (EvaluationPart evaluation : evaluations) {
            eval[i++] = evaluation.getRate();
        }
        return eval;
    }
    
    /**
     * indicate to the part that the result of the evaluation can now be
     * calculated
     * @param partToStop index of the part that finished
     */
    private void stop(int partToStop) {
        evaluations[partToStop].stop();
    }

    /**
     * update the evaluation system relatively to the notification we received
     * 
     * manage the data providing of the detector or
     * update the status of EvaluationPart when we are notified that a
     * WorkoutPart has finished
     * 
     * @param o notifier
     * @param o1 parameters of the notification
     */
    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof EffortObservable) {
            addData(((EffortObservable)o).getEffort());
        } else if (o instanceof PartObservable) {
            int partToStop = ((PartObservable)o).getCurrentPart() - 1;
            if (partToStop >= 0 && partToStop < evaluations.length) {
                stop(partToStop);
                obs.update();
            }
        }
    }
    
    /**
     * inner class to observe if we want to be notified when an evaluation has
     * changed
     */
    public class ObservableEvaluation extends Observable {
        public EvaluationRate[] getEvaluations() {
            return EvaluationManager.this.getEvaluations();
        }
        
        public void update() {
            setChanged();
            notifyObservers();
        }
    }
}
