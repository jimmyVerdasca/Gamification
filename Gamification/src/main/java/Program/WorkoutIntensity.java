package Program;

/**
 * represent the percent of the VMA we will go for
 * 
 * @author jimmy
 */
public enum WorkoutIntensity {
    ENDURANCE(0.6),
    RESISTANCE(0.8),
    POWER(1),
    STRENGTH(1.2);
    
    /**
     * percent of the VMA
     */
    private double percent;

    /**
     * constructor
     * @param percent of the VMA
     */
    private WorkoutIntensity(double percent) {
        this.percent = percent;
    }

    /**
     * return the percent of the VMA for this enum value
     * 
     * @return the percent of the VMA for this enum value
     */
    public double getPercent() {
        return percent;
    }
}
