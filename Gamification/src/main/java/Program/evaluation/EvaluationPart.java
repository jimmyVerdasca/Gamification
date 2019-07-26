package Program.evaluation;

/**
 * Class allowing to evaluate in real time a WorkoutPart.
 * It store one by one the efforts received and calculate an average to evaluate
 * the WorkoutPart when we call the stop method.
 * 
 * @author jimmy
 */
public class EvaluationPart {

    /**
     * current total of effort received
     */
    private double total = 0;
    
    /**
     * quantity of effort received until now (allows to calculate the mean)
     */
    private long nbData = 0;
    
    /**
     * the current mean
     */
    private double currentEvaluation = 0;
    
    /**
     * is the workout part still running (true) or can we evaluate (false)
     */
    private boolean isRunning = true;
    
    /**
     * The average target to be perfect in the WorkoutPart we evaluate
     * A perfect effort should ever be 1 if we use an EffortCalculator detector
     */
    private final double TARGET_DATA;

    /**
     * constructor default usable if the detector is an EffortCalculator sub-class
     */
    public EvaluationPart() {
        this(1);
    }
    
    /**
     * constructor to use if the perfect effort is not 1.
     * @param targetData the perfect effort mean to target for the WorkoutPart
     *                   that this instance evaluate
     */
    public EvaluationPart(double targetData) {
        this.TARGET_DATA = targetData;
    }
    
    /**
     * add a new effort value to considerate in the evaluation
     * 
     * @param newData the new effort value to considerate
     */
    public void addData(double newData) {
        total += newData;
        nbData++;
        currentEvaluation = total / nbData;
    }
    
    /**
     * indicate that the WorkoutPart is finished. Then we can evaluate the part.
     */
    public void stop() {
        isRunning = false;
    }
    
    /**
     * calculate the current result of the evaluation
     * 
     * @return the current result of the evaluation
     */
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
    
    /**
     * check if the mean is between a delta error percent
     * 
     * @param data the mean
     * @param targetData the targeted mean
     * @param percentError the delta error accepted
     * @return true if data close enough of targetData
     */
    private boolean inBetween(double data, double targetData, double percentError) {
        if (targetData * (1 - percentError) < data && targetData * (1 + percentError) > data) {
            return true;
        } else {
            return false;
        }
    }
    
}
