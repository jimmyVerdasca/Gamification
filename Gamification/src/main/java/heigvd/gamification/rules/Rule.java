package heigvd.gamification.rules;

/**
 * This class represent a Rule on how to win a medal.
 * 
 * 
 * @author jimmy
 */
public class Rule implements Cloneable {
    private int currentValue;
    private final int TARGET_OBJECTIF
;    private boolean cleared = false;
    private RulesName name;
    private String description;

    public Rule(RulesName name, String description) {
        this(name, description, 0, 1);
    }

    public Rule(RulesName name, String description, int targetObjectif) {
        this(name, description, 0, targetObjectif);
    }
    
    public Rule(RulesName name, String description, int objectifs, int targetObjectifs) {
        this.currentValue = objectifs;
        this.TARGET_OBJECTIF = targetObjectifs;
        this.name = name;
        this.description = description;
    }
    
    public void addObjectif(int amount) {
        currentValue += amount;
        if(currentValue >= TARGET_OBJECTIF) {
            cleared = true;
        }
    }
    
    public void addObjectif() {
        addObjectif(1);
    }
    
    public void substractObjectif(int amount) {
        currentValue = Math.max(0, currentValue - amount);
    }
    
    public void substractObjectif() {
        Rule.this.substractObjectif(1);
    }
    
    public boolean isCleared() {
        return cleared;
    }
    
    public void resetObjectifs() {
        currentValue = 0;
    }
    
    public RulesName getName() {
        return name;
    }
    
    public String getDescription() {
        return "<html>" + description + " " + currentValue + "/" + TARGET_OBJECTIF + "<html>";
    }
    
    protected Rule clone() {
        Rule rule = null;
        try {
            rule = (Rule) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return rule;
    }

    void setObjectif(int amount) {
        currentValue = amount;
        if(currentValue >= TARGET_OBJECTIF) {
            cleared = true;
        }
    }
}
