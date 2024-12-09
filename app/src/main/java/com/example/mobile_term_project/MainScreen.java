package com.example.mobile_term_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        TextView nicknameView = findViewById(R.id.nicknameTextView);
        RecyclerView recordRecyclerView = findViewById(R.id.recordRecyclerView);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button stepButton = findViewById(R.id.stepButton);

        // 닉네임 가져옴
        SharedPreferences login = getSharedPreferences("login", MODE_PRIVATE);
        String nickname = login.getString("nickname", "User");
        nicknameView.setText(nickname + "님의 기록");

        // RecyclerView
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Record> dummyData = new ArrayList<>(); // 더미 데이터
        dummyData.add(new Record("09:30 AM", "5.2 km", "6000 걸음", R.drawable.sample));
        dummyData.add(new Record("09:30 AM", "5.2 km", "6000 걸음", R.drawable.sample));
        dummyData.add(new Record("09:30 AM", "5.2 km", "6000 걸음", R.drawable.sample));
        dummyData.add(new Record("09:30 AM", "5.2 km", "6000 걸음", R.drawable.sample));
        dummyData.add(new Record("09:30 AM", "5.2 km", "6000 걸음", R.drawable.sample));
        dummyData.add(new Record("09:30 AM", "5.2 km", "6000 걸음", R.drawable.sample));
        dummyData.add(new Record("09:30 AM", "5.2 km", "6000 걸음", R.drawable.sample));
        dummyData.add(new Record("09:30 AM", "5.2 km", "6000 걸음", R.drawable.sample));

        RecordAdapter adapter = new RecordAdapter(dummyData);
        recordRecyclerView.setAdapter(adapter);

        // 로그아웃 버튼 클릭 이벤트
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences 로그아웃 처리
                SharedPreferences.Editor editor = login.edit();
                editor.clear(); // 저장된 로그인 정보 삭제
                editor.apply();

                // MainActivity로 이동
                Intent intent = new Intent(MainScreen.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // 백 스택 제거
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        stepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 측정 화면으로 이동
                Intent intent = new Intent(MainScreen.this, StepCounterActivity.class);
                startActivity(intent);
            }
        });

    }
}
