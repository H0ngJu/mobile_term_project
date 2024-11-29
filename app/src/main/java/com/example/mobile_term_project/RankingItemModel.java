package com.example.mobile_term_project;

public class RankingItemModel {
    private String name;
    private int steps;

    public RankingItemModel(String name, int steps) {
        this.name = name;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public int getSteps() {
        return steps;
    }
}
