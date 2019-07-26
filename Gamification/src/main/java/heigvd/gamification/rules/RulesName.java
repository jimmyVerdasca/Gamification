package heigvd.gamification.rules;

/**
 * enum with all the rules name of the game
 * 
 * @author jimmy
 */
public enum RulesName {
    AVOID_ROCKS(5000),
    RUN_N_PIXELS(1000),
    DESTROY_ROCKS(2000),
    DOUBLE_SPEED_BOOST(10000),
    ZIGZAG_ROCKS(20000),
    EXCELLENT_EFFORT(30000);
    
    /**
     * the score obtained when the Rule is succeeded
     */
    private int score;
    
    /**
     * private constructor
     * @param score obtained when the Rule is succeeded
     */
    private RulesName(int score) {
        this.score = score;
    }

    /**
     * return the score obtained when the Rule is succeeded
     * @return the score obtained when the Rule is succeeded
     */
    public int getScore() {
        return score;
    }
}
