package com.example.mobile_term_project;

import static android.content.ContentValues.TAG;
import static com.kakao.vectormap.MapType.NORMAL;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.MapViewInfo;

public class WalkingMap extends AppCompatActivity {

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_map);

        //kakaoMapSDK 초기화
        try {
            KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY);
            Log.d(TAG, "Kakao Map SDK 초기화 성공");
        } catch (Exception e) {
            Log.e(TAG, "Kakao Map SDK 초기화 실패: " + e.getMessage(), e);
        }

        mapView = findViewById(R.id.map_view);
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
            public void onMapReady(KakaoMap kakaoMap) {
                // 인증 후 API가 정상적으로 실행될 때 호출됨
                if (kakaoMap != null) {
                    Log.d("KakaoMapReady", "Map is ready");
                } else {
                    Log.e("KakaoMapReady", "Map is not ready - KakaoMap object is null");
                }


            }

            @Override
            public LatLng getPosition() {
                // 지도 시작 시 위치 좌표를 설정
                return LatLng.from(37.406960, 127.115587);
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
}