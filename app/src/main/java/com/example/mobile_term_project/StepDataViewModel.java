package com.example.mobile_term_project;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StepDataViewModel extends ViewModel {

    //초기화
    public void initialStepData(){
        StepDataStoreModel.setMemberId(0);
        StepDataStoreModel.setStepCount(0);
        StepDataStoreModel.setStartTime(null);
        StepDataStoreModel.setEndTime(null);
        StepDataStoreModel.setDistance(null);
    }
    //측정 시작 시
    public void startStepData(int memberId){
        StepDataStoreModel.setMemberId(memberId);
        StepDataStoreModel.setStartTime(currentTime());
    }
    //측정 종료 시
    public void saveStepData(int steps, double distance) {
        StepDataStoreModel.setStepCount(steps);
        StepDataStoreModel.setEndTime(currentTime());
        StepDataStoreModel.setDistance(formatDistance(distance));
    }

    public String currentTime(){
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
    }

    public String formatDistance(double distance) {
        if(distance >= 1000){ //1키로 이상
            return String.format("%.1f", (distance/1000)) + " KM";
        }else if(distance < 1) { //1미터 미만
            return String.format("%.1f", distance) + " M";
        } else {
            return (int)distance + " M";
        }
    }
}
