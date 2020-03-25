package com.ozarychta.model;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private static final String SIMPLE_DATE_FORMAT = "dd.MM";
    private SimpleDateFormat simpleDateFormat;

    private ArrayList<Day> dataSet;

    public static class DayViewHolder extends RecyclerView.ViewHolder {

        public TextView dateText;
        public ToggleButton toggleDone;

        public DayViewHolder(View itemView) {
            super(itemView);
            this.dateText = (TextView) itemView.findViewById(R.id.dateTextView);
            this.toggleDone = (ToggleButton) itemView.findViewById(R.id.toggleButton);
            Log.d("constructor view holder", "constructor view holder");
        }
    }

    public DayAdapter(ArrayList<Day> data) {
        this.dataSet = data;
        simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_day_inactive, parent, false);

        Log.d("on create view holder", "on create view holder");
        DayViewHolder DayViewHolder = new DayViewHolder(view);
        return DayViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Log.d("on bind view holder", "on bind view holder \n" + dataSet.get(position));

        holder.dateText.setText(simpleDateFormat.format(dataSet.get(position).getDate()));
        holder.toggleDone.setChecked(dataSet.get(position).getDone());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
