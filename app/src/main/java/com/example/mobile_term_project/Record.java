package com.example.mobile_term_project;

import android.graphics.Bitmap;

public class Record {
    String startTime;
    String endTime;
    String distance;
    int steps;

    public Record(String start, String end, String distance, int steps) {
        this.startTime = start;
        this.endTime = end;
        this.distance = distance;
        this.steps = steps;
    }

    public String getStartTime() {return startTime;}
    public String getEndTime() {return endTime;}
    public String getDistance() {return distance;}
    public int getSteps() {return steps;}
}

