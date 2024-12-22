package com.example.mobile_term_project;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_term_project.db.SQLiteHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EndofStepCounterActivity extends AppCompatActivity {

    private TextView stepCountTextView;
    private ListView rankingListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_step_counter);

        stepCountTextView = findViewById(R.id.stepCountTextView);
        rankingListView = findViewById(R.id.rankingListView);

        // StepDataStore에서 현재 사용자의 걸음 수 가져오기
        int savedSteps = StepDataStoreModel.getStepCount();
        stepCountTextView.setText("내 걸음 수: " + savedSteps);

        SQLiteHelper dbHelper = new SQLiteHelper(this);
        ArrayList<RankingItemModel> rankingList = new ArrayList<>(dbHelper.getAllUsersTopSteps());


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
