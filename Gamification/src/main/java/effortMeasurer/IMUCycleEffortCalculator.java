package effortMeasurer;

import Program.Movement;
import imu.BluetoothIMUAPI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;
import util.ArrayUtil;
import static util.ArrayUtil.findIndexOfMaxIn;
import util.DataFileUtil;

/**
 * Implementation of the EffortCalculator that
 * detects frequence with a Shimmer3 accelerometer.
 * 
 * It use a double Threashold strategy.
 * The maximum and the minimum are median to avoid
 * extreme wrong measures to break the system.
 * The threasholds are dynamics to adapt to amplitude changes.
 * A system of prediction of the next period are implemented to speed up
 * the reaction of the thresholds.
 * 
 * Several mapping function are implemented between frequence and
 * effort (linear/ 1/e etc).
 * 
 * @author jimmy
 */
public class IMUCycleEffortCalculator extends EffortCalculator {

    
    /**
     * Shimmer3 accelerometer handler
     */
    private static BluetoothIMUAPI imu = null;
    static {
        try {
            imu = new BluetoothIMUAPI();
            imu.configure();
        } catch (IOException ex) {
            Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * axe used by the shimmer (0,1 or 2)
     */
    private int currentAxe = 1;
    
    /**
     * circular array that store acceleration measures
     */
    private final double[] accelerationMeasures;
    
    /**
     * circular array that store timestamp of the accelerations measures
     */
    private final long[] timeMeasure;
    
    /**
     * next index of the circulars arrays
     */
    private int currentIndex = 0;
    
    /**
     * parameter to indicate the start and stop of the capture
     */
    private boolean running = false;
    
    /**
     * List that memorize the indexes of the firsts datas passing the thresholds
     * up after that a data passed the threshold down.
     * (only the ones still contained in the circular buffer)
     */
    private final ArrayList<Integer> passingThresholdData;
    
    /**
     * list using passingThresholdData and timeMeasure to create a list of
     * frequences
     * (only the ones still contained in the circular buffer)
     */
    private final ArrayList<Double> frequences;
    
    /**
     * copy of frequences that we can sort/manipulate without break the logic
     */
    private ArrayList<Double> sortedFrequences;
    
    /**
     * true when the last data that exceeded a threshold was the up one
     */
    private boolean isUpTreshold = false;
    
    /**
     * true when the last data that exceeded a threshold was the down one
     */
    private boolean isDownTreshold = false;
    private double maxTreshold;
    private double minTreshold;
    
    /**
     * movement with less than MIN_AMPLITUDE are considered as noise
     */
    private final int MIN_AMPLITUDE = 5;
    
    /**
     * max value contained in accelerationMeasures (8th median value)
     */
    private double max;
    
    /**
     * min value contained in accelerationMeasures (8th median value)
     */
    private double min;
    
    /**
     * minimum percent between max and his threashold or min and his threshold,
     * when everything goes well (we detecte cycles).
     */
    private final double INIT_DELTA_ERROR = 0.15;
    
    /**
     * maximum percent between max and his threashold or min and his threshold,
     * when we don't detect cycles anymore.
     */
    private final double MAX_DELTA_ERROR = 0.45;
    
    /**
     * current percent between max and his threshold or min and his threshold.
     * The calcul is for minThreshold for example :
     * (max - min) * deltaError + min
     * 
     * If we don't detect cycles when we expect one to come, deltaError increase slowly,
     * else we move towards INIT_DELTA_ERROR
     */
    private double deltaError;
    
    /**
     * temp variable for passingThresholdData[last]
     */
    private int firstIndexNotFoundThreshold;
    
    /**
     * Value used to invalidate/restart the prediction logic variables
     */
    private final int INVALID_FIRST_NOT_FOUND = -1;
    
    /**
     * memorize the index when the prediction was wrong.
     * So thagt we know that the deltaError should grow.
     * If the prediction is true or we detect a new data passing the threshold,
     * this variable is set to INVYLID_FIRST_NOT_FOUND
     */
    private int deltaErrorLesser = -1;
    
    /**
     * Number of data that haven't passed any threshold since last one.
     */
    private int passingThresholdCounter = 0;
    
    /**
     * distance between passingThresholdData[last] and
     * passingThresholdData[last - 1]
     * Could be more precise to use timestamp for this functionnality
     */
    private int lengthBeforeNextExpectedThresholdPass;
    
    private double currentFrequence = 0;
    private double speedFrequence = 0.08;
    
    /**
     * constructor
     * 
     * @param movement kind of movement the detector mode starts with
     * @throws IOException If we can't reach the Shimmer3 accelerometer.
     * @throws FileNotFoundException If the Shimmer3 API has not found the
     *                               calibration file.
     * @throws ParseException If there is an parsing error in the calibration
     *                        file.
     */
    public IMUCycleEffortCalculator(Movement movement)
            throws IOException,
                   FileNotFoundException,
                   ParseException {
        super(new double[]{0.0123, 0.003, 0.1}, movement, 215); //0.0123 is my frequence at 12K speed
        
        accelerationMeasures = new double[getLENGTH_AVERAGE_LIST()];
        timeMeasure = new long[getLENGTH_AVERAGE_LIST()];
        for (int i = 0; i < getLENGTH_AVERAGE_LIST(); i++) {
            accelerationMeasures[i] = 0.0;
        }
        passingThresholdData = new ArrayList<>(getLENGTH_AVERAGE_LIST() / 2);
        frequences = new ArrayList<>(getLENGTH_AVERAGE_LIST() / 2);
        deltaError = INIT_DELTA_ERROR;
        firstIndexNotFoundThreshold = INVALID_FIRST_NOT_FOUND;
        lengthBeforeNextExpectedThresholdPass = INVALID_FIRST_NOT_FOUND;
        if (imu == null) {
            throw new IOException("impossible to link the Shimmer3");
        }
    }
    
    /**
     * methode that try to calculate the nbCycle in each axes
     * and return the axe where he found the most cycles.
     * 
     * currently never used and never tested. Will be usefull when we want the
     * device to adapt to the fitness machine at the beginning of a workout.
     */
    public int getBestAxe() {
        int temp = this.currentAxe;
        double[] nbCycleFound = new double[3];
        for (int i = 0; i < 3; i++) {
            try {
                this.wait(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
            setCurrentAxe(i);
            nbCycleFound[i] = passingThresholdData.size();
        }
        setCurrentAxe(temp);
        return findIndexOfMaxIn(nbCycleFound);
    }
    
    /**
     * add the launch of the accelerometer to the behaviour of the super.start()
     */
    @Override
    public void start() {
        if(!running) {
            super.start();
            running = true;
            try {
                imu.startCapture();
            } catch (IOException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            }
        }
    }
    
    /**
     * add the interruption of the accelerometer to the behaviour of
     * the super.stop()
     */
    @Override
    public void stop() {
        if(running) {
            running = false;
            try {
                imu.stopCapture();
            } catch (IOException ex) {
                Logger.getLogger(IMUCycleEffortCalculator.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * set the axe used to detect the frequence
     * 
     * @param currentAxe the new axe (0, 1 or 2)
     */
    public void setCurrentAxe(int currentAxe) {
        this.currentAxe = currentAxe;
    }
    
    /**
     * infinite loop that update the effort of the super class by 
     * 1) asking a new value to the IMU
     * 2) updating the state of this class (nbCycle, thresholds and frequence)
     * 3) calculating the effort and setting it
     */
    @Override
    public void run() {
        long time = System.nanoTime();
        long time2;
        double newValue;
        
        /*double tempo = 1.0;
        String fileName = "jimmyXmTapisXK.txt";
        double[] fileLine = new double[12];
        fileLine[6] = 18;
        try {
            DataFileUtil.writeToFile("acceleration;ThresholdUP;max;ThresholdDown;min;bufferTempo;minimumAmplitude;currentAmplitude;nbCycle;freq;MappedFreq;currentFreq", fileName);
        } catch (IOException ex) {
            Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        while (running) {
            try {
                // TODO fixer le timestamp reçu par imu et remplacer System.nanoTime() par pair.getValue();
                newValue = imu.registerDataIncoming()[currentAxe][0];
                addValue(newValue, System.currentTimeMillis());
                
                /*fileLine[0] = newValue;
                fileLine[1] = maxTreshold;
                fileLine[2] = max;
                fileLine[3] = minTreshold;
                fileLine[4] = min;
                if (currentIndex == 0) {
                    tempo *= -1;
                }
                fileLine[5] = tempo;
                fileLine[7] = (max - min);
                fileLine[8] = getNbCycle();
                fileLine[9] = getFrequence();
                fileLine[10] = mappingFunction(getFrequence());
                fileLine[11] = mappingFunction(currentFrequence);
                DataFileUtil.writeToFile(fileLine, fileName);*/
                currentFrequence += speedFrequence * (mappingFunction(getFrequence() / getCurrentFreqTargetted()) - currentFrequence) / 2;
                setEffort(currentFrequence);
                Thread.yield();
            } catch (IOException ex) {
                Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * calculate the number of cycle and update the frequence value
     * 
     * @return the frequence calculated. Or 0 if not enough data.
     */
    private double getFrequence() {
        // calcul the frequence we fond atleast two frequence
        if (passingThresholdData.size() >= 2) {
            // then we calculate the median of these frequences
            return medianFrequence();
        } else {
            return 0;
        }
    }
    
    /**
     * return the mid element -1 of the frequences array
     * or 0 if there is less than 2 element
     * 
     * @return the median frequence
     */
    private double medianFrequence() {
        if(frequences.size() < 1) {
            return 0.0;
        }
        sortedFrequences = new ArrayList<>(frequences);
        Collections.sort(sortedFrequences);
        return sortedFrequences.get(frequences.size() / 2);
    }
    
    /**
     * Return the mean frequence.
     * 
     * @return the mean frequence.
     */
    private double meanFrequence() {
        return ((double)getNbCycle()) / (timeMeasure[passingThresholdData.get(passingThresholdData.size() - 1)] - timeMeasure[passingThresholdData.get(0)]);
    }
    
    /**
     * return the number of period detected in the circular buffer.
     * concretely return the size of passingThresholdData
     * 
     * @return the number of cycle currently in the circular buffer.
     */
    private int getNbCycle() {
        return passingThresholdData.size();
    }
    
    /**
     * add properly a new entry to the accelerationMeasure.
     * Put his timestamp in timeMeasure at the same index.
     * 
     * update the prevision system state
     * update dynamic thresholds system state
     * 
     * @param newValue the new value
     */
    private void addValue(double newValue, long timestamp) {
        // erase if necessary old data (erased by circular buffer)
        if(passingThresholdData.size() > 0 && passingThresholdData.get(0) == currentIndex) {
            passingThresholdData.remove(0);
            try {
                frequences.remove(0);
            } catch(IndexOutOfBoundsException ex) {
                
            }
        }

        updateThresholds();

        // if the new value pass the threshold we add the index at the end of the arrayList
        // movement with less than MIN_AMPLITUDE are considered as noise
        if (max - min > MIN_AMPLITUDE) {
            if (!isUpTreshold && newValue > maxTreshold) {
                if (passingThresholdData.size() > 1) {
                    frequences.add(((double)getNbCycle()) / (timestamp - timeMeasure[passingThresholdData.get(passingThresholdData.size() - 1)]));
                }
                passingThresholdData.add(currentIndex);
                passThresholdUP();
            } else if (!isDownTreshold && newValue < minTreshold) {
                passThresholdDOWN();
            } else if (passingThresholdData.size() > 1) {
                /** We have not passed the threashold and have enough data to
                 * use the detection system
                 */
                
                // The first time we doesn't exceed any threshold we store the
                // index.
                if(passingThresholdCounter == INVALID_FIRST_NOT_FOUND) {
                    firstIndexNotFoundThreshold = currentIndex;
                }
                
                // We update prediction system state.
                passingThresholdCounter++;
                lengthBeforeNextExpectedThresholdPass = distanceBetweenIndex(passingThresholdData.get(passingThresholdData.size() - 1), passingThresholdData.get(passingThresholdData.size() - 2), accelerationMeasures.length);
                
                /** If currentIndex reach the prediction without finding any
                 * data that exceed the threshold, we update the prediction
                 * system to start the increase of deltaError.
                 */
                if (currentIndex == (firstIndexNotFoundThreshold + lengthBeforeNextExpectedThresholdPass) % accelerationMeasures.length) {
                    deltaErrorLesser = currentIndex;
                }
            }
        }
        
        timeMeasure[currentIndex] = timestamp;
        accelerationMeasures[currentIndex] = newValue;
        currentIndex = (currentIndex + 1) % accelerationMeasures.length;
        
        /**
         * Increase or deacrease deltaError depending on the state of the 
         * prediction system.
         */
        if (deltaErrorLesser != INVALID_FIRST_NOT_FOUND) {
            deltaError += (MAX_DELTA_ERROR - deltaError) / (3 *lengthBeforeNextExpectedThresholdPass);
        } else if (lengthBeforeNextExpectedThresholdPass != INVALID_FIRST_NOT_FOUND) {
            deltaError -= (deltaError - INIT_DELTA_ERROR) / (30 * lengthBeforeNextExpectedThresholdPass);
        }
    }
    
    /**
     * update the double threshold system to the state that a data exceeded
     * threshold up
     * update the prevision system too
     */
    private void passThresholdUP() {
        isUpTreshold = true;
        isDownTreshold = false;
        passingThresholdCounter = INVALID_FIRST_NOT_FOUND;
        deltaErrorLesser = INVALID_FIRST_NOT_FOUND;
    }
    
    /**
     * update the double threshold system to the state that a data exceeded
     * threshold down
     * update the prevision system too
     */
    private void passThresholdDOWN() {
        isUpTreshold = false;
        isDownTreshold = true;
        passingThresholdCounter = INVALID_FIRST_NOT_FOUND;
        deltaErrorLesser = INVALID_FIRST_NOT_FOUND;
    }
    
    /**
     * Method to ease the calcul of the difference between two index of a
     * circular buffer. 
     * 
     * @param first index of the beginning
     * @param second index of the end
     * @param length length of the circular buffer
     * 
     * @return the distance between first and second in a circular buffer.
     */
    private int distanceBetweenIndex(int first, int second, int length) {
        return (first - second + length) % length;
    }

    /**
     * recalculate the max, the min en the threshold.
     * max and min are the 8th bigger/smaller value in accelerationMeasures.
     */
    private void updateThresholds() {
        max = ArrayUtil.getKLargest(accelerationMeasures.clone(), accelerationMeasures.length, 8);
        min = ArrayUtil.getKSmallest(accelerationMeasures.clone(), accelerationMeasures.length, 8);
        maxTreshold = max - deltaError * (max - min);
        minTreshold = min + deltaError * (max - min);
    }

    /**
     * function that map the frequence into a function between 0-1-2
 Where 0 is when we have a frequence 0
 Where 1 is when we have a frequence getFreqAtVMASpeed()
 Where 2 is when we have a freqence of getMAX_REACHED() * getFreqAtVMASpeed()
     * @param frequence
     * @return 
     */
    private double mappingFunction(double frequence) {
        double powUp = 2;
        double powDown = 4;
        double average = getCurrentFreqTargetted();
        
        // uncomment to use curved function
        /*double maxReached = getMAX_REACHED() * getFreqAtVMASpeed();
        if (frequence < average) {
            return Math.pow(frequence / (Math.pow(average, 1 - 1/powDown)),
                    powDown);
        } else if (maxReached == average || frequence > maxReached) {
            return frequence;
        } else {
            return (Math.pow((frequence - average) * Math.pow(maxReached - average, powUp - 1), (1/powUp)) + average);    
        }*/
        
        // uncomment to use linear by part function
        /*if (frequence < average / 2) {
            return 0;
        } else if (frequence < average) {
            return frequence / 2;
        } else {
            return frequence;
        }*/
        
        return frequence;
    }
}