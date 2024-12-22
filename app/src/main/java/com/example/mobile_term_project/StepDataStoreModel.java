package com.example.mobile_term_project;

import com.kakao.vectormap.LatLng;

public class StepDataStoreModel {
    private static int memberId;
    private static int stepCount;
    private static String startTime;
    private static String endTime;
    private static String distance;
    private static byte[] mapImage;

    public static void setMemberId(int id){
        memberId = id;
    }
    public static int getMemberId(){
        return memberId;
    }

    public static void setStepCount(int steps) {
        stepCount = steps;
    }
    public static int  getStepCount() {
        return stepCount;
    }

    public static void setStartTime(String start){
        startTime = start;
    }
    public static String getStartTime(){
        return startTime;
    }

    public static void setEndTime(String end){
        endTime = end;
    }
    public static String getEndTime(){
        return endTime;
    }

    public static void setDistance(String totalDistance){
        distance = totalDistance; //단위까지 저장됨
    }
    public static String getDistance(){
        return distance;
    }

    public static void setMapImage(byte[] Image){
        mapImage = Image;
    }
    public static byte[] getMapImage(){
        return mapImage;
    }

}
