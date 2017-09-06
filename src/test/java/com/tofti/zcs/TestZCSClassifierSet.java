package com.tofti.zcs;

import org.junit.Assert;
import org.junit.Test;

public class TestZCSClassifierSet {

    @Test
    public void testZCSClassifierSet() {
        ZCSClassifierSet t1 = new ZCSClassifierSet();
        Assert.assertTrue(t1.isEmpty());

        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1111", "0000", 10.0);
        ZCSClassifier cs2 = ZCSClassifier.generateClassifier("1111", "0000", 20.0);

        t1.addClassifier(cs1);
        t1.addClassifier(cs2);

        Assert.assertEquals(cs2, t1.getStrongestClassifier());
        Assert.assertEquals(15d, t1.getMeanStrength(), 1E-6);
        Assert.assertEquals(30d, t1.getSumStrength(), 1E-6);
        Assert.assertEquals(0.1 + 0.05, t1.getSumInverseStrength(), 1E-6);

        ZCSClassifierSet t2 = new ZCSClassifierSet();
        ZCSClassifier cs3 = ZCSClassifier.generateClassifier("1111", "0000", 30.0);
        t2.addClassifier(cs3);
        t2.addClassifier(cs1);
        t2.addClassifier(cs2);

        ZCSClassifierSet m1 = t2.setMinus(t1);
        Assert.assertEquals(1, m1.size());
        Assert.assertTrue(m1.getClassifiers().contains(cs3));

        m1.removeClassifier(cs3);
        Assert.assertTrue(m1.isEmpty());

        t1.clearSet();
        Assert.assertTrue(t1.isEmpty());
    }

    @Test
    public void testZCSClassifierSetRemoval() {
        ZCSClassifierSet t1 = new ZCSClassifierSet();

        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1111", "0000", 0.000000000001);
        t1.addClassifier(cs1);

        Assert.assertEquals(1, t1.size());
        cs1.tax(0.1d);

        boolean removed = t1.getClassifiers().removeIf(c -> c.getStrength() < 1E-6);
        Assert.assertTrue(removed);
        Assert.assertTrue(t1.isEmpty());

    }
}
