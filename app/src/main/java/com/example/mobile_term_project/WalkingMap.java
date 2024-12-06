package com.example.mobile_term_project;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.MapViewInfo;
import com.kakao.vectormap.route.RouteLine;
import com.kakao.vectormap.route.RouteLineLayer;
import com.kakao.vectormap.route.RouteLineOptions;
import com.kakao.vectormap.route.RouteLineSegment;
import com.kakao.vectormap.route.RouteLineStyle;
import com.kakao.vectormap.route.RouteLineStyles;
import com.kakao.vectormap.route.RouteLineStylesSet;

import java.util.ArrayList;
import java.util.List;

public class WalkingMap extends AppCompatActivity {

    MapView mapView;
    KakaoMap kakaoMap;

    //GPS 구현 전 임시로
    EditText coordinate;
    Button btn;

    List<LatLng> routePath = new ArrayList<>();

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
        coordinate = findViewById(R.id.Coordinate);
        btn = findViewById(R.id.Btn);

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
                routePath.add(LatLng.from(37.401750, 127.109656));
                routePath.add(LatLng.from(37.396374, 127.109653)); //잘 보이게 하려고 일단 기본으로 넣어둠
                drawRoutePath(routePath);
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

        //GPS 구현 전 RouteLine 변경 테스트를 위해서 사용
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = coordinate.getText().toString().trim();

                String[] tmp = text.split(",\\s*");
                if(tmp.length == 2) {
                    double latitude = Double.parseDouble(tmp[0]);
                    double longitude = Double.parseDouble(tmp[1]);

                    routePath.add(LatLng.from(latitude,longitude));
                    updateRoutePath(routePath);

                    coordinate.setText(null);

                } else {
                    Log.d("EditTextInput", "input error");
                }
            }
        });

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


    public void drawRoutePath(List<LatLng> startPoint) {
        RouteLineLayer layer = kakaoMap.getRouteLineManager().getLayer(); //디폴트로 생성된 layer를 가져옴

        RouteLineStylesSet stylesSet = RouteLineStylesSet.from("defaultStyle",
                RouteLineStyles.from(RouteLineStyle.from(16, Color.BLUE))); //모든 줌레벨, 라인두께 16px, 파란색 스타일

        RouteLineSegment segment = RouteLineSegment.from(startPoint)
                .setStyles(stylesSet.getStyles(0));

        RouteLineOptions options = RouteLineOptions.from("default",segment).setStylesSet(stylesSet);
        RouteLine routeLine = layer.addRouteLine(options);
        routeLine.show();
    }

    public void updateRoutePath(List<LatLng> newPath) {
        RouteLineLayer layer = kakaoMap.getRouteLineManager().getLayer();

        RouteLineStylesSet stylesSet = RouteLineStylesSet.from("defaultStyle",
                RouteLineStyles.from(RouteLineStyle.from(16, Color.BLUE)));

        RouteLineSegment newSegment = RouteLineSegment.from(newPath).setStyles(stylesSet.getStyles(0));

        RouteLine routeLine = layer.getRouteLine("default");
        routeLine.changeSegments(newSegment);
    }


}