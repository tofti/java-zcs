package com.tofti.zcs;

import java.util.*;
import java.util.stream.Collectors;

public class ZCSClassifierSet {

    private Set<ZCSClassifier> classifiers;

    public ZCSClassifierSet() {
        classifiers = new HashSet<>();
    }

    public ZCSClassifierSet(ZCSClassifierSet copy) {
        classifiers = new HashSet<>(copy.getClassifiers());
    }

    public ZCSClassifierSet(Set<ZCSClassifier> classifiers) {
        this.classifiers = classifiers;
    }

    public void addClassifier(ZCSClassifier classifier) {
        classifiers.add(classifier);
    }

    public void removeClassifier(ZCSClassifier classifier) {
        classifiers.remove(classifier);
    }

    public void clearSet() {
        classifiers.clear();
    }

    public double getSumStrength() {
        return classifiers.stream().collect(Collectors.summingDouble(s -> s.getStrength()));
    }

    public double getSumInverseStrength() {
        return classifiers.stream().collect(Collectors.summingDouble(s -> s.getInverseStrength()));
    }

    public double getMeanStrength() {
        return getSumStrength() / classifiers.size();
    }

    public boolean isEmpty() {
        return classifiers.isEmpty();
    }

    public int size() {
        return classifiers.size();
    }

    public ZCSClassifier getStrongestClassifier() {
        return classifiers.stream().max((a, b) -> (int)Math.signum(a.getStrength() - b.getStrength())).orElse(null);
    }

    public Set<ZCSClassifier> getClassifiers() {
        return classifiers;
    }

    public ZCSClassifierSet setMinus(final ZCSClassifierSet other) {
        // take a copy of the classifiers in this set
        Set<ZCSClassifier> copy = new HashSet<>(getClassifiers());
        // remove the classifiers in other set from the copied list of classifiers
        copy.removeAll(other.getClassifiers());
        return new ZCSClassifierSet(copy);
    }

    public String toString() {
        return classifiers.stream().map(ZCSClassifier::toString).collect(Collectors.joining(System.lineSeparator()));
    }
}
