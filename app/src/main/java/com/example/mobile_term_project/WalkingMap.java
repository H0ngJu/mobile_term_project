package com.example.mobile_term_project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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

public class WalkingMap extends AppCompatActivity {

    MapView mapView;
    KakaoMap kakaoMap;
    LocationManager locationManager;
    LocationListener locationListener;

    ArrayList<LatLng> routePath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_map);

        mapView = findViewById(R.id.map_view);
        //locationManager 초기화
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //locationListener 초기화
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                routePath.add(LatLng.from(lat,lng));
                Log.d("location", "업데이트 된 위치: 위도: " + lat + ", 경도: " + lng);
                updateRoutePath(routePath);
            }
        };

        //kakaoMapSDK 초기화
        try {
            KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY);
            Log.d(TAG, "Kakao Map SDK 초기화 성공");
        } catch (Exception e) {
            Log.e(TAG, "Kakao Map SDK 초기화 실패: " + e.getMessage(), e);
        }


        //권한 확인 및 사용자에게 권한 요청
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            //return;
        }else {
            locationManager.requestLocationUpdates(
                    locationManager.GPS_PROVIDER, 3000,-1,locationListener
            );
        }


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

                if (ActivityCompat.checkSelfPermission(WalkingMap.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    /*Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (lastKnownLocation != null) {
                        LatLng initialPoint = LatLng.from(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                        routePath.add(initialPoint);
                        Log.d("location", "위도 : " + initialPoint.getLatitude() + ", 경도 : " + initialPoint.getLongitude());


                        drawRoutePath(routePath);
                    } else {
                        Log.e(TAG, "최초 위치 정보를 가져오지 못했습니다.");
                    }*/

                    // 최신 위치 가져오기
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        locationManager.getCurrentLocation(LocationManager.NETWORK_PROVIDER, null, getMainExecutor(), location -> {
                            if (location != null) {
                                setInitialRoute(location);
                            } else {
                                Log.e(TAG, "현재 위치를 네트워크로 가져올 수 없습니다!");
                            }
                        });
                    } else {
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, location -> {
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

        }
        );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){

            // 허용하지 않았으면, 다시 허용하라는 알러트 띄운다.
            if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                finish();
                return;
            }

            // 허용했으면, GPS 정보 가져오는 코드 넣는다.
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, -1,
                    locationListener
            );

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
    }

    @Override
    public void onPause() {
        super.onPause();
        //mapView.pause();    // MapView 의 pause 호출
        if (mapView != null) {
            mapView.pause(); // MapView의 pause 호출
        } else {
            Log.e("MapViewLifecycle", "MapView pause 호출 실패: mapView is null");
        }

    }


    public void drawRoutePath(ArrayList<LatLng> startPoint) {
        RouteLineLayer layer = kakaoMap.getRouteLineManager().getLayer(); //디폴트로 생성된 layer를 가져옴

        RouteLineStylesSet stylesSet = RouteLineStylesSet.from("defaultStyle",
                RouteLineStyles.from(RouteLineStyle.from(16, Color.RED))); //모든 줌레벨, 라인두께 16px, 파란색 스타일

        RouteLineSegment segment = RouteLineSegment.from(startPoint)
                .setStyles(stylesSet.getStyles(0));

        RouteLineOptions options = RouteLineOptions.from("default",segment).setStylesSet(stylesSet);
        RouteLine routeLine = layer.addRouteLine(options);
        routeLine.show();

        setUpLabel(startPoint.get(startPoint.size()-1));
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

    private void setInitialRoute(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng initialPoint = LatLng.from(latitude, longitude);
        routePath.add(initialPoint);

        //routeLine 그리기 위해서 두번째 점 추가
        routePath.add(LatLng.from(latitude + 0.00001, longitude + 0.00001));

        drawRoutePath(routePath);
        Log.d("location", "초기 위치: 위도: " + latitude + ", 경도: " + longitude);
    }


}