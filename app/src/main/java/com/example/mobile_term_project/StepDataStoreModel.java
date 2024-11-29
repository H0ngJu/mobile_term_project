package com.example.mobile_term_project;

public class StepDataStoreModel {
    private static int stepCount;

    public static void setStepCount(int steps) {
        stepCount = steps;
    }

    public static int getStepCount() {
        return stepCount;
    }
}
