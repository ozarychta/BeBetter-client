package com.ozarychta.bebetter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.models.Day;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryDayAdapter extends RecyclerView.Adapter<HistoryDayAdapter.DayViewHolder> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

    private ArrayList<Day> dataSet;

    public static class DayViewHolder extends RecyclerView.ViewHolder {

        public TextView dateText;
        public TextView weekdayText;
        public ImageView imageView;

        public DayViewHolder(View itemView) {
            super(itemView);
            this.dateText = itemView.findViewById(R.id.dateTextView);
            this.weekdayText = itemView.findViewById(R.id.weekDayTextView);
            this.imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public HistoryDayAdapter(ArrayList<Day> data) {
        this.dataSet = data;
    }

    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_history_day, parent, false);

        DayViewHolder DayViewHolder = new DayViewHolder(view);
        return DayViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day d = dataSet.get(position);

        holder.dateText.setText(d.getDate().format(formatter));
        String dayOfWeek = d.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
        holder.weekdayText.setText(dayOfWeek);

        switch (d.getConfirmationType()){
            case CHECK_TASK:
                if(d.getDone()) holder.imageView.setImageResource(R.drawable.ic_check_24);
                else holder.imageView.setImageResource(R.drawable.ic_clear_24);
                break;
            case COUNTER_TASK:
                if(d.getCurrentStatus() >= d.getGoal()) holder.imageView.setImageResource(R.drawable.ic_check_24);
                else holder.imageView.setImageResource(R.drawable.ic_clear_24);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
