package com.tofti.zcs;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ZCSClassifier implements Comparable {
    private static final char[] ALPHABET;
    private static final char[] ALPHABET_WO_WILDCARD;
    private static final char ZERO;
    private static final char ONE;
    private static final char WILCARD;

    static AtomicLong SEQUENCE = new AtomicLong();

    static {
        ZERO = '0';
        ONE = '1';
        WILCARD = '#';
        ALPHABET_WO_WILDCARD = new char[] {ZERO, ONE};
        ALPHABET = new char[] {ZERO, ONE, WILCARD};
    }

    private char[] condition;
    private char[] action;
    private double strength;

    private double totalTax;
    private double totalActionSetPayment;
    private double totalReward;

    private long id;

    // to generate a classifier call factory generateClassifier method(s)
    private ZCSClassifier(final char[] condition, final char[] action, final double s) {
        setCondition(condition);
        setAction(action);
        setStrength(s);
        totalTax = 0.0;
        totalActionSetPayment = 0.0;
        totalReward = 0.0;
        id = SEQUENCE.incrementAndGet();
    }

    private ZCSClassifier(String condition, String action, double s) {
        this(condition.toCharArray(), action.toCharArray(), s);
    }

    public char[] getCondition() {
        return condition;
    }

    private void setCondition(char[] condition) {
        this.condition = condition;
    }

    public char[] getAction() {
        return action;
    }

    private void setAction(char[] action) {
        this.action = action;
    }

    public double getStrength() {
        return strength;
    }

    private void setStrength(double strength) {
        this.strength = strength;
    }

    public double getInverseStrength() {
        return 1.0 / getStrength();
    }

    // this classifier is in the [A] set determine its contribution
    // to the bucket and reduce its strength accordingly
    public double getActionSetPayment(double beta) {
        double actionSetPayment = beta * strength;
        strength = strength - actionSetPayment;
        totalActionSetPayment = totalActionSetPayment + actionSetPayment;
        return actionSetPayment;
    }

    public void increaseStrength(double reward) {
        strength = strength + reward;
        if(strength < 0.0) {
            strength = 0.0;
        }
        totalReward = totalReward + reward;
    }

    public void tax(double tau) {
        double tax = tau * strength;
        strength = strength - tax;
        strength = Math.max(0.0, strength);
        totalTax += tax;
    }

    // does this classifier match the environment coded message
    public boolean isConditionMatched(char[] e) {
        if(e.length != condition.length) {
            return false;
        }
        // look for a non-matching element
        for(int i = 0; i < e.length; i++) {
            if(condition[i] != WILCARD && condition[i] != e[i]) {
                return false;
            }
        }
        // is a match
        return true;
    }

    // does 'this' classifier advocate the same action as other
    public boolean isActionMatched(ZCSClassifier other) {
        return Arrays.equals(getAction(), other.getAction());
    }

    public String toString() {
        return String.valueOf(getCondition()) + " | "
                + String.valueOf(action)
                + " [s=" + ZCSUtility.round(getStrength(), 0.01) + ", s^-1 = "
                + ZCSUtility.round(getInverseStrength(), 0.01)
                + ", BucketPay = " + ZCSUtility.round(totalActionSetPayment, 0.01)
                + ", Tax = " + ZCSUtility.round(totalTax, 0.01)
                + ", Reward = " + ZCSUtility.round(totalReward, 0.01) + "]";
    }

    // when a classifier is cloned its strength is set to half of its parent
    public ZCSClassifier cloneClassifier() {
        double halfStrength = getStrength() * 0.5;
        setStrength(halfStrength);

        char[] cloneCondition = cloneCharArr(getCondition());
        char[] cloneAction = cloneCharArr(getAction());
        return generateClassifier(cloneCondition, cloneAction, halfStrength);
    }

    private static char[] cloneCharArr(char[] orginal) {
        char[] clone = new char[orginal.length];
        System.arraycopy(orginal, 0, clone, 0, orginal.length);
        return clone;
    }

    // factory method for generating classifiers with condition length cLength
    // action length aLength and a probability p of having a '#' at any point in
    // the condition string with initial strength s
    public static ZCSClassifier generateClassifier(int cLength, int aLength, double strength, final double p) {
        char[] condition = new char[cLength];
        for(int i = 0; i < condition.length; i++) {
            if(ZCS.nextDouble() < p) {
                condition[i] = WILCARD;
            } else {
                condition[i] = getRandomCharFrom(ALPHABET_WO_WILDCARD);
            }
        }

        char[] action = new char[aLength];
        for(int i = 0; i < action.length; i++) {
            action[i] = getRandomCharFrom(ALPHABET_WO_WILDCARD);
        }
        return new ZCSClassifier(condition, action, strength);
    }

    private static char getRandomCharFrom(char [] source) {
        return source[ZCS.nextInt(source.length)];
    }

    public static ZCSClassifier generateClassifier(String condition, String action, double strength) {
        return new ZCSClassifier(condition, action, strength);
    }

    public static ZCSClassifier generateClassifier(char[] condition, char[] action, double strength) {
        return new ZCSClassifier(condition, action, strength);
    }

    public static ZCSClassifier generateCoveringClassifier(char[] input, int aLength, double strength, double p) {
        // make a copy of the env condition for the condition part of this covering classifier
        char[] condition = cloneCharArr(input);

        // flip some of the condition bits to # with a probability p
        for(int i = 0; i < condition.length; i++) {
            if(ZCS.nextDouble() < p) {
                condition[i] = WILCARD;
            }
        }
        // generate a random action
        char[] action = new char[aLength];
        for(int i = 0; i < action.length; i++) {
            action[i] = getRandomCharFrom(ALPHABET_WO_WILDCARD);
        }
        return generateClassifier(condition, action, strength);
    }

    public static void crossoverClassifiers(ZCSClassifier childA, ZCSClassifier childB) {
        // do some crossover
        double meanChildStrength = (childA.getStrength() + childB.getStrength()) * 0.5;

        // get the length of the action for splitting crossed strings
        // assume childA.getCondition().length == childB.getCondition().length
        final int conditionLength = childA.getCondition().length;

        // combine condition and action parts of classifier to form strings
        String childAStr = String.valueOf(childA.getCondition()) + String.valueOf(childA.getAction());
        String childBStr = String.valueOf(childB.getCondition()) + String.valueOf(childB.getAction());

        // assume childAStr.length() == childBStr.length()
        int crossoverIndex = (int)(ZCS.nextDouble() * childAStr.length()) + 1;

        // do the crossover
        String newChildAStr = childAStr.substring(0, crossoverIndex)
                + childBStr.substring(crossoverIndex, childBStr.length());

        String newChildBStr = childBStr.substring(0, crossoverIndex)
                + childAStr.substring(crossoverIndex, childAStr.length());

        // split the strings back into condition action parts and convert to char arrays
        // call appropriate mutator methods for the classifier
        childA.setCondition(newChildAStr.substring(0, conditionLength).toCharArray());
        childA.setAction(newChildAStr.substring(conditionLength, childAStr.length()).toCharArray());

        childB.setCondition(newChildBStr.substring(0, conditionLength).toCharArray());
        childB.setAction(newChildBStr.substring(conditionLength, childBStr.length()).toCharArray());

        // set the strengths to the mean of the strengths prior to crossover
        childA.setStrength(meanChildStrength);
        childB.setStrength(meanChildStrength);
    }

    public static void mutateClassifier(ZCSClassifier classifier, final double mu) {
        // for each allele test for mutation
        char[] condition = classifier.getCondition();
        for(int i = 0; i < condition.length; i++) {
            // mutate the i'th allele of the condition
            if(ZCS.nextDouble() < mu) {
                condition[i] = getRandomCharFrom(ALPHABET);
            }
        }
        char[] action = classifier.getAction();
        for(int i = 0; i < action.length; i++) {
            // mutate the ith allele of the action
            if(ZCS.nextDouble() < mu) {
                action[i] = getRandomCharFrom(ALPHABET_WO_WILDCARD);
            }
        }
    }

    public int compareTo(Object arg0) {
        if(!ZCSClassifier.class.isInstance(arg0)) {
            throw new IllegalArgumentException(String.format("Cannot compare classifier to : %s", arg0));
        }
        ZCSClassifier other = ZCSClassifier.class.cast(arg0);
        return (int) Math.signum(getStrength() - other.getStrength());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZCSClassifier that = (ZCSClassifier) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}