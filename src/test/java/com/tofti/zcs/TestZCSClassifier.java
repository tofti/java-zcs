package com.tofti.zcs;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestZCSClassifier {

    @Test
    public void testGenerateClassifiers() {
        // create a specified classifier
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1111", "0000", 10.0);
        System.out.println("cs1 = " + cs1);

        // create a randomly generated classifier
        ZCSClassifier cs2 = ZCSClassifier.generateClassifier(4, 4, 12.0, 0.33);
        System.out.println("cs2 = " + cs2);

        // create a covering classifier
        char[] testEnv = "0110".toCharArray();
        ZCSClassifier cs3 = ZCSClassifier.generateCoveringClassifier(testEnv, 4, 15.0, 0.33);
        Assert.assertTrue(cs3.isConditionMatched(testEnv));
    }

    @Test
    public void testIsConditionMatched() {
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1#10", "0000", 10.0);
        Assert.assertTrue(cs1.isConditionMatched("1110".toCharArray()));
        Assert.assertFalse(cs1.isConditionMatched("0110".toCharArray()));
        Assert.assertTrue(cs1.isConditionMatched("1010".toCharArray()));
    }

    @Test
    public void testActionSetPayment() {
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1111", "0000", 10.0);
        System.out.println("Prior to action set payment cs1 = " + cs1);
        double payment = cs1.getActionSetPayment(0.1);
        Assert.assertEquals(1.0d, payment, 1E-6);
    }

    @Test
    public void testIncreaseStrength() {
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1111", "0000", 10.0);
        System.out.println("Prior to strength increase cs1 = " + cs1);
        cs1.increaseStrength(10.0);
        System.out.println("Post strength increase cs1 = " + cs1);
        Assert.assertEquals(20d, cs1.getStrength(), 1E-6);
    }

    @Test
    public void testTax() {
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1111", "0000", 10.0);
        cs1.tax(0.1);
        Assert.assertEquals(9d, cs1.getStrength(), 1E-6);
    }

    @Test
    public void testIsActionMatched() {
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1111", "0000", 10.0);

        ZCSClassifier a1 = ZCSClassifier.generateClassifier("1111", "0000", 10.0);
        ZCSClassifier a2 = ZCSClassifier.generateClassifier("1111", "0001", 10.0);
        ZCSClassifier a3 = ZCSClassifier.generateClassifier("1111", "1000", 10.0);
        ZCSClassifier a4 = ZCSClassifier.generateClassifier("1111", "1001", 10.0);

        Assert.assertTrue(cs1.isActionMatched(a1));
        Assert.assertFalse(cs1.isActionMatched(a2));
        Assert.assertFalse(cs1.isActionMatched(a3));
        Assert.assertFalse(cs1.isActionMatched(a4));
    }

    @Test
    public void testClone() {
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("1111", "0000", 10.0);
        System.out.println("Prior to clone...\ncs1 = " + cs1);
        ZCSClassifier cs1Clone = cs1.cloneClassifier();
        Assert.assertFalse(cs1.equals(cs1Clone));
        Assert.assertTrue(Arrays.equals(cs1.getCondition(), cs1Clone.getCondition()));
        Assert.assertTrue(Arrays.equals(cs1.getAction(), cs1Clone.getAction()));
        System.out.println("Post clone...\ncs1 = " + cs1 + "\ncs1Clone = " + cs1Clone);
    }

    @Test
    public void testCrossoverClassifiers() {
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("#11#", "0000", 10.0);
        ZCSClassifier cs2 = ZCSClassifier.generateClassifier("0##0", "1111", 10.0);
        System.out.println("Prior to crossover...\ncs1 = " + cs1 + "\ncs2 = " + cs2);
        ZCSClassifier.crossoverClassifiers(cs1, cs2);
        System.out.println("Post crossover...\ncs1 = " + cs1 + "\ncs2 = " + cs2);
    }

    @Test
    public void testMutateClassifier() {
        ZCSClassifier cs1 = ZCSClassifier.generateClassifier("#11#", "0000", 10.0);
        System.out.println("Prior to mutation...\ncs1 = " + cs1);
        cs1.mutateClassifier(0.33);
        System.out.println("Post mutation...\ncs1 = " + cs1);
    }
}
