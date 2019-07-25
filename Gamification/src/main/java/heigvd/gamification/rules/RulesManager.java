package heigvd.gamification.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 *
 * @author jimmy
 */
public class RulesManager {
    private final List<RulesName> clearedRules;
    private final Rule[] possibleRules;
    private Rule currentRule;
    private final Random rand;
    private final RuleObservable obsCurrentRule;
    private final WinRuleObservable obsWinRule;

    public RulesManager() {
        rand = new Random();
        clearedRules = new ArrayList<>();
        
        // create the existing posssible rules and calibrate them
        possibleRules = new Rule[RulesName.values().length];
        possibleRules[RulesName.AVOID_ROCKS.ordinal()] = new Rule(RulesName.AVOID_ROCKS, "avoid a rock", 20);
        possibleRules[RulesName.DESTROY_ROCKS.ordinal()] = new Rule(RulesName.DESTROY_ROCKS, "destroy with shield a rock", 10);
        possibleRules[RulesName.DOUBLE_SPEED_BOOST.ordinal()] = new Rule(RulesName.DOUBLE_SPEED_BOOST, "activate double speed boost", 1);
        possibleRules[RulesName.EXCELLENT_EFFORT.ordinal()] = new Rule(RulesName.EXCELLENT_EFFORT, "keep the perfect wall speed", 60);
        possibleRules[RulesName.RUN_N_PIXELS.ordinal()] = new Rule(RulesName.RUN_N_PIXELS, "move the wall forward", 10000);
        possibleRules[RulesName.ZIGZAG_ROCKS.ordinal()] = new Rule(RulesName.ZIGZAG_ROCKS, "weave at each rock", 8);
        
        //choose the first rule
        currentRule = possibleRules[rand.nextInt(possibleRules.length)].clone();
        
        obsCurrentRule = new RuleObservable();
        obsWinRule = new WinRuleObservable();
    }
    
    public void addObjectif(RulesName name, int amount) {
        if (currentRule.getName() == name) {
            currentRule.addObjectif(amount);
            obsCurrentRule.update();
            if (currentRule.isCleared()) {
                newRule();
            }
        }
    }
    
    public void addObjectif(RulesName name) {
        RulesManager.this.addObjectif(name, 1);
    }
    
    public void substractObjectif(RulesName name, int amount) {
        if (currentRule.getName() == name) {
            currentRule.substractObjectif(amount);
            obsCurrentRule.update();
        }
    }
    
    public void substractObjectif(RulesName name) {
            RulesManager.this.substractObjectif(name, 1);
    }

    public void resetObjectif(RulesName name) {
        if (currentRule.getName() == name) {
            currentRule.resetObjectifs();
            obsCurrentRule.update();
        }
    }
    
    private void newRule() {
        if (currentRule.isCleared()) {
            clearedRules.add(currentRule.getName());
            currentRule = possibleRules[rand.nextInt(possibleRules.length)].clone();
            obsCurrentRule.update();
            obsWinRule.update();
        }
    }
    
    public void addWinRuleObserver(Observer o) {
        obsWinRule.addObserver(o);
    }
    
    public void addRuleObserver(Observer o) {
        obsCurrentRule.addObserver(o);
    }

    public Rule getCurrentObjectif() {
        return currentRule;
    }

    public void setObjectif(RulesName name, int amount) {
        if (currentRule.getName() == name) {
            currentRule.setObjectif(amount);
            obsCurrentRule.update();
            if (currentRule.isCleared()) {
                newRule();
            }
        }
    }

    public List<RulesName> getRulesCleared() {
        return clearedRules;
    }
    
    public class RuleObservable extends Observable {
        /**
         * method to call when we change the currentRule value to
         * ensure that all observers are notified.
         */
        protected void update() {
            setChanged();
            notifyObservers();
        }
        
        public List<RulesName> getClearedRules() {
            return RulesManager.this.clearedRules;
        }
        
        public Rule getCurrentRule() {
            return RulesManager.this.currentRule;
        }
    }
    
    public class WinRuleObservable extends Observable {
        
        /**
         * method to call when the currentRule is cleared
         */
        protected void update() {
            setChanged();
            notifyObservers();
        }
        
        
        public List<RulesName> getClearedRules() {
            return RulesManager.this.clearedRules;
        }
        
        public Rule getCurrentRule() {
            return RulesManager.this.currentRule;
        }
    }
}
