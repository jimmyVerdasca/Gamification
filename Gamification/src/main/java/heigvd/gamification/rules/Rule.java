package heigvd.gamification.rules;

/**
 * This class represent a Rule on how to win a medal.
 * 
 * @author jimmy
 */
public class Rule implements Cloneable {
    
    /**
     * simple counter trying to reach TARGET_OBJECTIF
     */
    private int currentValue;
    
    /**
     * value to reach to success the rule
     */
    private final int TARGET_OBJECTIF;
    
    /**
     * true if the rule is successfull
     */
    private boolean cleared = false;
    
    /**
     * name of the Rule
     */
    private RulesName name;
    
    /**
     * description of the objectif
     */
    private String description;

    /**
     * constructor
     * 
     * @param name of the Rule
     * @param description of the objectif
     */
    public Rule(RulesName name, String description) {
        this(name, description, 0, 1);
    }

    /**
     * constructor
     * 
     * @param name of the Rule
     * @param description of the objectif
     * @param targetObjectif target counter of the objectif to success it
     */
    public Rule(RulesName name, String description, int targetObjectif) {
        this(name, description, 0, targetObjectif);
    }
    
    /**
     * constructor
     * 
     * @param name of the Rule
     * @param description of the objectif
     * @param objectifs starting counter value
     * @param targetObjectif target counter of the objectif to success it
     */
    public Rule(RulesName name, String description, int objectifs, int targetObjectif) {
        this.currentValue = objectifs;
        this.TARGET_OBJECTIF = targetObjectif;
        this.name = name;
        this.description = description;
    }
    
    /**
     * add amount to the objectif counter
     * @param amount to add
     */
    public void addObjectif(int amount) {
        currentValue += amount;
        if(currentValue >= TARGET_OBJECTIF) {
            cleared = true;
        }
    }
    
    /**
     * add 1 to the objectif counter
     */
    public void addObjectif() {
        addObjectif(1);
    }
    
    /**
     * substract amount to the objectif counter
     * @param amount to substract
     */
    public void substractObjectif(int amount) {
        currentValue = Math.max(0, currentValue - amount);
    }
    
    /**
     * substract 1 to the objectif counter
     */
    public void substractObjectif() {
        Rule.this.substractObjectif(1);
    }
    
    /**
     * return the success status of the rule
     * @return the success status of the rule
     */
    public boolean isCleared() {
        return cleared;
    }
    
    /**
     * set to 0 the counter of the rule
     */
    public void resetObjectifs() {
        currentValue = 0;
    }
    
    /**
     * return the name of the rule
     * @return  the name of the rule
     */
    public RulesName getName() {
        return name;
    }
    
    /**
     * return a string explaining the rule objectif
     * @return a string explaining the rule objectif
     */
    public String getDescription() {
        return "<html>" + description + " " + currentValue + "/" + TARGET_OBJECTIF + "<html>";
    }
    
    /**
     * allow to clone properly the rule
     * @return the rule cloned
     */
    protected Rule clone() {
        Rule rule = null;
        try {
            rule = (Rule) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return rule;
    }

    /**
     * set the counter of the rule to amount
     * @param amount new value to set the counter of the rule
     */
    void setObjectif(int amount) {
        currentValue = amount;
        if(currentValue >= TARGET_OBJECTIF) {
            cleared = true;
        }
    }
}
