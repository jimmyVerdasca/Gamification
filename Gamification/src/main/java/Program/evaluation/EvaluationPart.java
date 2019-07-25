package Program.evaluation;

/**
 *
 * @author jimmy
 */
public class EvaluationPart {

    private double total = 0;
    private long nbData = 0;
    private double currentEvaluation = 0;
    private boolean isRunning = true;
    private final double TARGET_DATA;

    public EvaluationPart() {
        this(1);
    }
    
    public EvaluationPart(double targetData) {
        this.TARGET_DATA = targetData;
    }
    
    public void addData(double newData) {
        total += newData;
        nbData++;
        currentEvaluation = total / nbData;
    }
    
    public void stop() {
        isRunning = false;
    }
    
    public EvaluationRate getRate() {
        if (nbData == 0) {
            return EvaluationRate.NONE;
        } else if (nbData > 0 && isRunning) {
            return EvaluationRate.CALCULATING;
        } else if (inBetween(currentEvaluation, TARGET_DATA, 0.2)) {
            return EvaluationRate.EXCELLENT;
        } else if (inBetween(currentEvaluation, TARGET_DATA, 0.4)) {
            return EvaluationRate.GOOD;
        } else if (inBetween(currentEvaluation, TARGET_DATA, 0.6)) {
            return EvaluationRate.AVERAGE;
        } else {
            return EvaluationRate.BAD;
        }
    }
    
    private boolean inBetween(double data, double targetData, double percentError) {
        if (targetData * (1 - percentError) < data && targetData * (1 + percentError) > data) {
            return true;
        } else {
            return false;
        }
    }
    
}
