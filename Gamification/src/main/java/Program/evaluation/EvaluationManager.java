package Program.evaluation;

import Program.AbstractProgram;
import Program.AbstractProgram.PartObservable;
import effortMeasurer.EffortCalculator;
import effortMeasurer.EffortCalculator.EffortObservable;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author jimmy
 */
public class EvaluationManager implements Observer {
    
    private final EvaluationPart[] evaluations;
    private final AbstractProgram program;
    private final ObservableEvaluation obs;

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
    
    public void addData(double data) {
        if (program.getIsRunning()) {
            evaluations[program.getCurrentPart()].addData(data);
            obs.update();
        }
    }
    
    public void addEvalObserver(Observer o) {
        obs.addObserver(o);
    }

    public EvaluationRate[] getEvaluations() {
        EvaluationRate[] eval = new EvaluationRate[evaluations.length];
        int i = 0;
        for (EvaluationPart evaluation : evaluations) {
            eval[i++] = evaluation.getRate();
        }
        return eval;
    }
    
    private void stop(int partToStop) {
        evaluations[partToStop].stop();
    }

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
