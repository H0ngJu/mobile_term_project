package com.example.mobile_term_project;

public class Record {
    String time;
    String distance;
    String steps;
    int imageResId;

    public Record(String time, String distance, String steps, int imageResId) {
        this.time = time;
        this.distance = distance;
        this.steps = steps;
        this.imageResId = imageResId;
    }

    public String getTime() { return time; }
    public String getDistance() { return distance; }
    public String getSteps() { return steps; }
    public int getImageResId() { return imageResId; }
}

