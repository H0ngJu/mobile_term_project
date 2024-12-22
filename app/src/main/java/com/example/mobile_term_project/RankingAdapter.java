package com.example.mobile_term_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RankingAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RankingItemModel> rankingList;

    public RankingAdapter(Context context, ArrayList<RankingItemModel> rankingList) {
        this.context = context;
        this.rankingList = rankingList;
    }

    @Override
    public int getCount() {
        return rankingList.size();
    }

    @Override
    public Object getItem(int position) {
        return rankingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ranking_item, parent, false);
        }

        TextView rankTextView = convertView.findViewById(R.id.rankNum);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView stepsTextView = convertView.findViewById(R.id.stepsTextView);

        RankingItemModel item = rankingList.get(position);
        rankTextView.setText(String.valueOf(position+1));
        nameTextView.setText(item.getName());
        stepsTextView.setText(String.valueOf(item.getSteps()));

        return convertView;
    }
}
