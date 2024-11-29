package com.example.mobile_term_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepCountText;
    private Button stopButton; // 종료하기 버튼
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        stepCountText = findViewById(R.id.stepCountText);
        stopButton = findViewById(R.id.stopButton); // 종료하기 버튼

        // 권한 확인 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 이상에서 권한 확인
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
            } else {
                initializeSensor();
            }
        } else {
            initializeSensor();
        }

        // 종료하기 버튼 클릭 이벤트
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 걸음 수 저장
                StepDataStoreModel.setStepCount(stepCount);
                Toast.makeText(StepCounterActivity.this, "걸음 수 저장 완료: " + stepCount, Toast.LENGTH_SHORT).show();

                // 다음 Activity로 이동
                Intent intent = new Intent(StepCounterActivity.this, EndofStepCounterActivity.class);
                startActivity(intent);

                // 현재 Activity 종료
                finish();
            }
        });
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 허용됨", Toast.LENGTH_SHORT).show();
                initializeSensor();
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 센서 초기화 메서드
    private void initializeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        if (stepCounterSensor == null) {
            stepCountText.setText("Step Counter 센서를 지원하지 않습니다.");
            Log.e("StepCounterActivity", "Step Counter 센서를 지원하지 않음.");
        } else {
            Log.d("StepCounterActivity", "Step Counter 센서 초기화 완료");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("StepCounterActivity", "onResume 호출");

        if (sensorManager == null) {
            Log.e("StepCounterActivity", "SensorManager is NULL");
            return;
        }

        if (stepCounterSensor != null) {
            boolean isRegistered = sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            if (isRegistered) {
                Log.d("StepCounterActivity", "Sensor Listener 등록 완료");
            } else {
                Log.e("StepCounterActivity", "Sensor Listener 등록 실패");
            }
        } else {
            Log.e("StepCounterActivity", "Step Counter Sensor is NULL");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("StepCounterActivity", "onPause 호출");
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.d("StepCounterActivity", "Step Counter Event 감지: " + event.values[0]);
            stepCount = (int) event.values[0];
            stepCountText.setText(String.valueOf(stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("StepCounterActivity", "Sensor accuracy changed: " + accuracy);
    }
}
