package com.example.mobile_term_project;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_term_project.db.SQLiteHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EndofStepCounterActivity extends AppCompatActivity {

    private TextView stepCountTextView;
    private ListView rankingListView;
    private Button backToMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_step_counter);

        stepCountTextView = findViewById(R.id.stepCountTextView);
        rankingListView = findViewById(R.id.rankingListView);
        backToMainButton = findViewById(R.id.backToMainButton);

        // StepDataStore에서 현재 사용자의 걸음 수 가져오기
        int savedSteps = StepDataStoreModel.getStepCount();
        stepCountTextView.setText("내 걸음 수: " + savedSteps);

        SQLiteHelper dbHelper = new SQLiteHelper(this);
        ArrayList<RankingItemModel> rankingList = new ArrayList<>(dbHelper.getAllUsersTopSteps());

        // 더미 데이터 추가
        rankingList.addAll(getDummyRankingData());

        // 걸음 수에 따라 정렬
        Collections.sort(rankingList, new Comparator<RankingItemModel>() {
            @Override
            public int compare(RankingItemModel o1, RankingItemModel o2) {
                return o2.getSteps() - o1.getSteps();
            }
        });

        // 어댑터를 통해 리스트뷰에 데이터 연결
        RankingAdapter adapter = new RankingAdapter(this, rankingList);
        rankingListView.setAdapter(adapter);

        // "마이페이지로 돌아가기" 버튼 클릭 이벤트
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainScreen으로 이동
                Intent intent = new Intent(EndofStepCounterActivity.this, MainScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private ArrayList<RankingItemModel> getDummyRankingData() {
        ArrayList<RankingItemModel> dummyData = new ArrayList<>();
        dummyData.add(new RankingItemModel("minha@gmail.com", 12000));
        dummyData.add(new RankingItemModel("test@gamil.com", 9000));
        dummyData.add(new RankingItemModel("mobile@gmail.com", 6600));
        dummyData.add(new RankingItemModel("programming@gmail.com", 5000));
        return dummyData;
    }
}
