package com.example.mobile_term_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<Record> records;

    public RecordAdapter(List<Record> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = records.get(position);
        holder.timeTextView.setText(record.getTime());
        holder.distanceTextView.setText(record.getDistance());
        holder.stepsTextView.setText(record.getSteps());
        holder.routeImageView.setImageResource(record.getImageResId());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, distanceTextView, stepsTextView;
        ImageView routeImageView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            stepsTextView = itemView.findViewById(R.id.stepsTextView);
            routeImageView = itemView.findViewById(R.id.routeImageView);
        }
    }
}
