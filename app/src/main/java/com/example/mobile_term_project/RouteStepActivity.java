package com.example.mobile_term_project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_term_project.db.SQLiteHelper;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import com.kakao.vectormap.label.TrackingManager;
import com.kakao.vectormap.route.RouteLine;
import com.kakao.vectormap.route.RouteLineLayer;
import com.kakao.vectormap.route.RouteLineOptions;
import com.kakao.vectormap.route.RouteLineSegment;
import com.kakao.vectormap.route.RouteLineStyle;
import com.kakao.vectormap.route.RouteLineStyles;
import com.kakao.vectormap.route.RouteLineStylesSet;

import java.util.ArrayList;

public class RouteStepActivity extends AppCompatActivity implements SensorEventListener {

    SQLiteHelper sqLiteHelper;
    MapView mapView;
    KakaoMap kakaoMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private TextView stepCountText, distanceText;
    private Button stopButton;

    private StepDataViewModel stepDataViewModel;

    ArrayList<LatLng> routePath = new ArrayList<>();
    private int stepCount = 0;
    private double totalDistance = 0.0; //미터 단위

    private int initialStepCount = -1; // 초기값은 -1로 설정하여 아직 설정되지 않았음을 나타냄

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_step);

        sqLiteHelper = new SQLiteHelper(this);

        mapView = findViewById(R.id.map_view);
        stepCountText = findViewById(R.id.stepCountText);
        distanceText = findViewById(R.id.DistanceText);
        stopButton = findViewById(R.id.stopButton);

        stepDataViewModel = new ViewModelProvider(this).get(StepDataViewModel.class);

        //locationManager 초기화
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //kakaoMapSDK 초기화
        try {
            KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY);
            Log.d(TAG, "Kakao Map SDK 초기화 성공");
        } catch (Exception e) {
            Log.e(TAG, "Kakao Map SDK 초기화 실패: " + e.getMessage(), e);
        }

        //locationListener 초기화
        locationListener = location -> {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Log.d("location", "업데이트 된 위치: 위도: " + lat + ", 경도: " + lng);

            if(routePath.size() <= 1){ //routeline이 초기화 되지 않음
                setInitialRoute(location);
            }else{
                routePath.add(LatLng.from(lat,lng));
                updateRoutePath(routePath);
            }

            String formatDistance = calculateDistance();
            distanceText.setText("이동 거리 : " + formatDistance);
        };

        //gps, sensor 권한 확인 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 이상에서 권한 확인
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACTIVITY_RECOGNITION},
                        100);
            } else {
                initializeMapView(); //권한이 이미 허용된 경우 지도 초기화
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 3000,1,locationListener);
                initializeSensor();
            }
        } else {
            initializeMapView(); //권한이 이미 허용된 경우 지도 초기화
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 3000,1,locationListener);
            initializeSensor();
        }

        //종료하기 버튼 클릭 이벤트
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 데이터 저장
                stepDataViewModel.saveStepData(stepCount, totalDistance);
                sqLiteHelper.addStepData();

                Toast.makeText(RouteStepActivity.this, "걸음 수 저장 완료: " + stepCount, Toast.LENGTH_SHORT).show();

                // 초기값 업데이트 (다음 측정을 위해)
                initialStepCount = -1; // 초기화

                // 다음 Activity로 이동
                Intent intent = new Intent(RouteStepActivity.this, EndofStepCounterActivity.class);
                startActivity(intent);

                // 현재 Activity 종료
                finish();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) { // 권한 요청 코드: 걸음 수 및 위치
            boolean locationPermissionGranted = false;
            boolean activityRecognitionPermissionGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    locationPermissionGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (permissions[i].equals(Manifest.permission.ACTIVITY_RECOGNITION)) {
                    activityRecognitionPermissionGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }

            if (locationPermissionGranted) {
                // 위치 권한이 허용된 경우
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    initializeMapView();
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, locationListener);
                    Toast.makeText(this, "위치 권한 허용", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            if (activityRecognitionPermissionGranted) {
                // 걸음 수 권한이 허용된 경우
                initializeSensor();
                Toast.makeText(this, "걸음 수 권한 허용", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "걸음 수 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        //mapView.resume();     // MapView 의 resume 호출
        if (mapView != null) {
            mapView.resume(); // MapView의 resume 호출
        } else {
            Log.e("MapViewLifecycle", "MapView resume 호출 실패: mapView is null");
        }

        if (sensorManager == null) {
            Log.e("StepCounterActivity", "SensorManager is NULL");
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 3000, 1, locationListener);
            Log.d("location", "LocationListener 재등록 완료");
        } else {
            Log.e("location", "위치 권한이 없습니다.");
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
    public void onPause() {
        super.onPause();

        if (mapView != null) {
            mapView.pause(); // MapView의 pause 호출
        } else {
            Log.e("MapViewLifecycle", "MapView pause 호출 실패: mapView is null");
        }

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == -1) {
                // 측정을 처음 시작하는 경우 현재 값을 초기값으로 설정
                initialStepCount = (int) event.values[0];
            }
            // 현재 걸음 수 = 센서 값 - 초기값
            stepCount = (int) event.values[0] - initialStepCount;
            stepCountText.setText(String.valueOf(stepCount));
            Log.d("StepCounterActivity", "Step Counter Event 감지: " + event.values[0] + ", 초기값: " + initialStepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("StepCounterActivity", "Sensor accuracy changed: " + accuracy);
    }

    private void initializeMapView() {
        //kakao map
        mapView.start(new MapLifeCycleCallback() {
                          @Override
                          public void onMapDestroy() {
                              // 지도 API 가 정상적으로 종료될 때 호출됨
                              Log.d("MapLifeCycle", "Map is destroyed");

                          }

                          @Override
                          public void onMapError(Exception e) {
                              // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                              Log.e("MapLifeCycle", "Map error occurred: " + e.getMessage(), e);
                          }
                      },

                new KakaoMapReadyCallback() {
                    @Override
                    public void onMapReady(KakaoMap map) {
                        // 인증 후 API가 정상적으로 실행될 때 호출됨
                        kakaoMap = map;

                        if (ActivityCompat.checkSelfPermission(RouteStepActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            //최신 위치 가져오기
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                locationManager.getCurrentLocation(LocationManager.NETWORK_PROVIDER, null, getMainExecutor(), location -> {
                                    if (location != null) {
                                        setInitialRoute(location);
                                    } else {
                                        Log.e(TAG, "현재 위치를 네트워크로 가져올 수 없습니다!");
                                    }
                                });
                            } else {
                                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, location -> {
                                    if (location != null) {
                                        setInitialRoute(location);
                                    } else {
                                        Log.e(TAG, "현재 위치를 가져올 수 없습니다.");
                                    }
                                }, null);
                            }
                        } else {
                            Log.e(TAG, "위치 권한이 없습니다.");
                        }
                    }

                    @Override
                    public int getZoomLevel() {
                        // 지도 시작 시 확대/축소 줌 레벨 설정
                        return 15;
                    }

                    @Override
                    public boolean isVisible() {
                        // 지도 시작 시 visible 여부를 설정
                        return true;
                    }
                });
    }

    private void setInitialRoute(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng initialPoint = LatLng.from(latitude, longitude);
        routePath.add(initialPoint);

        //routeLine 그리기 위해서 두번째 점 추가
        routePath.add(LatLng.from(latitude + 0.00001, longitude + 0.00001));

        //routeLine 그리기
        RouteLineLayer layer = kakaoMap.getRouteLineManager().getLayer(); //디폴트로 생성된 layer를 가져옴

        RouteLineStylesSet stylesSet = RouteLineStylesSet.from("defaultStyle",
                RouteLineStyles.from(RouteLineStyle.from(16, Color.RED))); //모든 줌레벨, 라인두께 16px, 파란색 스타일

        RouteLineSegment segment = RouteLineSegment.from(routePath)
                .setStyles(stylesSet.getStyles(0));

        RouteLineOptions options = RouteLineOptions.from("default",segment).setStylesSet(stylesSet);
        RouteLine routeLine = layer.addRouteLine(options);
        routeLine.show();

        setUpLabel(routePath.get(routePath.size()-1));

        Log.d("location", "초기 위치: 위도: " + latitude + ", 경도: " + longitude);

        SharedPreferences login = getSharedPreferences("login", MODE_PRIVATE);
        int memberId = login.getInt("id", 0);

        stepDataViewModel.initialStepData(); //초기화
        stepDataViewModel.startStepData(memberId); //저장 시작
    }

    public void updateRoutePath(ArrayList<LatLng> newPath) {
        RouteLineLayer layer = kakaoMap.getRouteLineManager().getLayer();

        RouteLineStylesSet stylesSet = RouteLineStylesSet.from("defaultStyle",
                RouteLineStyles.from(RouteLineStyle.from(16, Color.RED)));

        RouteLineSegment newSegment = RouteLineSegment.from(newPath).setStyles(stylesSet.getStyles(0));

        RouteLine routeLine = layer.getRouteLine("default");
        routeLine.changeSegments(newSegment);

        updateLabel(newPath.get(newPath.size()-1));
    }

    //label
    public void setUpLabel (LatLng startPoint) {
        LabelStyles styles = kakaoMap.getLabelManager().addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.image2)));
        LabelOptions options = LabelOptions.from("default", startPoint).setStyles(styles);

        LabelLayer layer = kakaoMap.getLabelManager().getLayer(); //디폴트로 생성된 레이어
        Label label = layer.addLabel(options);

        TrackingManager trackingManager = kakaoMap.getTrackingManager();
        trackingManager.startTracking(label);
    }

    public void updateLabel (LatLng newPoint) { //TODO: try-catch 추가
        LabelLayer layer = kakaoMap.getLabelManager().getLayer();
        Label label = layer.getLabel("default");
        label.moveTo(newPoint);

        TrackingManager trackingManager = kakaoMap.getTrackingManager();
        trackingManager.startTracking(label);
    }

    public String calculateDistance (){ //두 좌표간의 거리 구하기
        LatLng point1 = routePath.get(routePath.size()-2);
        LatLng point2 = routePath.get(routePath.size()-1);

        float[] distance = new float[1];
        Location.distanceBetween(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude(), distance);

        totalDistance += distance[0];
        return stepDataViewModel.formatDistance(totalDistance);
    }

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
}