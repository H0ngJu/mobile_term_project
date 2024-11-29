package com.example.mobile_term_project;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EndofStepCounterActivity extends AppCompatActivity {

    private TextView stepCountTextView;
    private ListView rankingListView;
    private ArrayList<RankingItemModel> rankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_step_counter);

        stepCountTextView = findViewById(R.id.stepCountTextView);
        rankingListView = findViewById(R.id.rankingListView);

        // StepDataStore에서 현재 사용자의 걸음 수 가져오기
        int savedSteps = StepDataStoreModel.getStepCount();
        stepCountTextView.setText("내 걸음 수: " + savedSteps);

        // 더미 데이터 추가
        rankingList = new ArrayList<>();
        rankingList.add(new RankingItemModel("김민아", 1500));
        rankingList.add(new RankingItemModel("이홍주", 1200));
        rankingList.add(new RankingItemModel("윤성원", 800));
        rankingList.add(new RankingItemModel("김영봉", 500));

        // 현재 사용자 데이터 추가
        rankingList.add(new RankingItemModel("You", savedSteps));

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
    }
}
