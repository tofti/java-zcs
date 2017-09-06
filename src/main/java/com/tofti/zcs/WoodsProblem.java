package com.tofti.zcs;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class WoodsProblem {

    private static void executeWoodsProblem(ZCS classifierSystem,
                                            WoodsEnvironment woods,
                                            int woodsProblems,
                                            int woodsRuns,
                                            int movingAverage) throws Exception {

        double[][] movingAvgResults = new double[woodsRuns][woodsProblems];
        int[][] rawResults = new int[woodsRuns][woodsProblems];
        List<ZCSClassifierSet> classifierSets = new ArrayList<>();

        long lastUpdate = System.currentTimeMillis();

        for(int i = 0; i < woodsRuns; i++) {
            int[] stepsTaken = new int[woodsProblems];

            for(int j = 0; j < woodsProblems; j++) {
                lastUpdate = printUpdateMessage(woodsProblems, woodsRuns, lastUpdate, i, j);

                boolean isFoodFound = false;
                int problemSteps = 0;

                while(!isFoodFound) {
                    problemSteps = problemSteps + 1;
                    // get the woods sensory information
                    char[] woodsEnv = woods.senseLocation();
                    // get the move from the classifier system
                    char[] animatMove = classifierSystem.classify(woodsEnv);
                    // instruct the animat to move in woods
                    int reward = woods.move(animatMove);
                    if(reward > 0) {
                        isFoodFound = true;
                        woods.reset();
                        classifierSystem.reward(reward);
                        classifierSystem.clearPreviousActionSet();
                    }
                }
                stepsTaken[j] = problemSteps;
            }

            movingAvgResults[i] = ZCSUtility.calculateMovingAverage(stepsTaken, movingAverage);
            rawResults[i] = stepsTaken;
            classifierSets.add(new ZCSClassifierSet(classifierSystem.getClassifierPopulation()));

            classifierSystem.reset();
        }
        // cause the classifier system to finalise its log
        classifierSystem.finalise();

        writeResultsToFile(woodsProblems, woodsRuns, movingAvgResults, rawResults);
        writeFinalClassifierSystems(woodsRuns, classifierSets.get(0));
    }

    private static void writeFinalClassifierSystems(int woodsRuns, ZCSClassifierSet classifierPopulation) throws IOException {
        // write the results to a file
        String filename = ZCSUtility.getFileName("woods_population") + ".txt";
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));

        List<ZCSClassifier> sortedClassifiers =
            classifierPopulation.getClassifiers().stream()
                                .sorted((a , b) -> (int) Math.signum(b.getStrength() - a.getStrength())).collect(Collectors.toList());

        out.write(sortedClassifiers.stream().map(ZCSClassifier::toString).collect(Collectors.joining(System.lineSeparator())));

        out.close();
    }

    private static void writeResultsToFile(int woodsProblems, int woodsRuns,
                                           double[][] movingAvgResults,  int[][] rawResults) throws IOException {
        // write the results to a file
        String filename = ZCSUtility.getFileName("woods") + ".csv";
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));

        for(int i = 0; i < woodsRuns; i++) {
            out.write("Run " + (i + 1) + ",,");
        }
        out.newLine();
        for(int i = 0; i < woodsProblems; i++) {
            for(int j = 0; j < woodsRuns; j++) {
                out.write(String.valueOf(movingAvgResults[j][i]) + "," +  rawResults[j][i]+ ",");
            }
            out.newLine();
        }
        out.flush();
        out.close();
    }

    private static long printUpdateMessage(int woodsProblems, int woodsRuns, long lastUpdate, int i, int j) {
        if(System.currentTimeMillis() - lastUpdate > 2000) {
            lastUpdate = System.currentTimeMillis();
            double percentComplete = 100.0 * ((i * woodsProblems + j) / (double)(woodsRuns * woodsProblems));
            System.out.printf("Run %s Problem %s %.3f%% done....%s",
                             (i + 1) , j , percentComplete, System.lineSeparator());
        }
        return lastUpdate;
    }

    public static void main (String[] args) {
        try {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            configureWoodsProblem(args[0]);
        }
        catch (Exception e) {
            System.out.println("Woods problem failed with error...\n" + e);
            e.printStackTrace();
        }
    }

    private static void configureWoodsProblem(String problemFile) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(System.getProperty("user.dir") + problemFile));

        // read ZCS properties
        int cLength = Integer.parseInt(properties.getProperty("condition.length"));
        int aLength = Integer.parseInt(properties.getProperty("action.length"));
        int n = Integer.parseInt(properties.getProperty("classifiers"));
        double p = Double.parseDouble(properties.getProperty("p"));
        double s = Double.parseDouble(properties.getProperty("s"));
        double beta = Double.parseDouble(properties.getProperty("beta"));
        double gamma = Double.parseDouble(properties.getProperty("gamma"));
        double tau = Double.parseDouble(properties.getProperty("tau"));
        double chi = Double.parseDouble(properties.getProperty("chi"));
        double mu = Double.parseDouble(properties.getProperty("mu"));
        double rho = Double.parseDouble(properties.getProperty("rho"));
        double phi = Double.parseDouble(properties.getProperty("phi"));
        boolean debugZCS = Boolean.parseBoolean(properties.getProperty("debug.zcs"));

        ZCS classifierSystem = new ZCS(cLength, aLength, n, p, s, beta, gamma, tau, chi, mu, rho, phi, true, debugZCS);
        WoodsEnvironment woods;

        // read woods properties
        if(properties.getProperty("woods.mapfile") == null) {
            woods = WoodsEnvironment.getDefaultWoodsEnvironment();
        }
        else {
            String woodsFile = properties.getProperty("woods.mapfile");
            woods = WoodsEnvironment.getWoodsEnvironment(woodsFile);
        }

        int woodsProblems = Integer.parseInt(properties.getProperty("woods.problems"));
        int movingAverage = Integer.parseInt(properties.getProperty("moving.avg"));
        int runs = Integer.parseInt(properties.getProperty("runs"));

        executeWoodsProblem(classifierSystem, woods, woodsProblems, runs, movingAverage);
    }
}