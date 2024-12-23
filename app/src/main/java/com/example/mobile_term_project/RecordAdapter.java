package com.example.mobile_term_project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        String formattedTime = formatTimeRange(record.getStartTime(), record.getEndTime());
        holder.TimeTextView.setText(formattedTime);

        holder.distanceTextView.setText(record.getDistance());
        holder.stepsTextView.setText(record.getSteps() + " 보");

        Bitmap image = record.getImage();
        if (image != null) {
            Bitmap scale = Bitmap.createScaledBitmap(image, 100,100,true);
            System.out.println("Bitmap 크기: " + scale.getWidth() + "x" + scale.getHeight());
            System.out.println(scale);
            holder.routeImageView.setImageBitmap(scale); // Bitmap 설정

        } else {
            holder.routeImageView.setImageResource(R.drawable.sample); // 기본 이미지
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView TimeTextView, distanceTextView, stepsTextView;
        ImageView routeImageView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            TimeTextView = itemView.findViewById(R.id.timeTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            stepsTextView = itemView.findViewById(R.id.stepsTextView);
            routeImageView = itemView.findViewById(R.id.routeImageView);
        }
    }

    private String formatTimeRange(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault());
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년MM월dd일", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("a h시mm분", Locale.getDefault()); // 오전/오후 h:mm

            String startDateStr = dateFormat.format(startDate);
            String startTimeStr = timeFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);
            String endTimeStr = timeFormat.format(endDate);

            if (startDateStr.equals(endDateStr)) {
                // 날짜가 같은 경우
                return startDateStr + "\n" + startTimeStr + " ~ " + endTimeStr;
            } else {
                // 날짜가 다른 경우
                return startDateStr + "\n" + startTimeStr + " ~ " + endDateStr + "\n" + endTimeStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return start + " ~ " + end; // 에러 발생 시 원래 문자열 반환
        }
    }
}
