package com.ozarychta.bebetter.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.models.Day;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class HistoryDayAdapter extends RecyclerView.Adapter<HistoryDayAdapter.DayViewHolder> {

    private static final String SIMPLE_DATE_FORMAT = "dd.MM";
    private SimpleDateFormat simpleDateFormat;

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
            Log.d("constructor view holder", "constructor view holder");
        }
    }

    public HistoryDayAdapter(ArrayList<Day> data) {
        this.dataSet = data;
        simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_history_day, parent, false);

        Log.d("on create view holder", "on create view holder");
        DayViewHolder DayViewHolder = new DayViewHolder(view);
        return DayViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day d = dataSet.get(position);
        Log.d("on bind view holder", "on bind view holder \n" + d);

        holder.dateText.setText(simpleDateFormat.format(d.getDate()));
        Calendar c = Calendar.getInstance();
        c.setTime(d.getDate());
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(d.getDate()).substring(0,3);
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
