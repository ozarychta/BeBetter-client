package com.ozarychta.bebetter.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.models.Day;

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
        public LinearLayout counterLinearLayout;
        public TextView counterText;

        public DayViewHolder(View itemView) {
            super(itemView);
            this.dateText = itemView.findViewById(R.id.dateTextView);
            this.toggleDone = itemView.findViewById(R.id.toggleButton);
            this.counterLinearLayout = itemView.findViewById(R.id.counterLinearLayout);
            this.counterText = itemView.findViewById(R.id.counterTextView);
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
        Day d = dataSet.get(position);
        Log.d("on bind view holder", "on bind view holder \n" + d);

        holder.dateText.setText(simpleDateFormat.format(d.getDate()));
        holder.toggleDone.setChecked(d.getDone());
        holder.counterText.setText(String.valueOf(d.getCurrentStatus()));

        if(d.getConfirmationType() == ConfirmationType.CHECK_TASK){
            holder.toggleDone.setVisibility(View.VISIBLE);
            holder.counterLinearLayout.setVisibility(View.GONE);
        } else {
            holder.toggleDone.setVisibility(View.GONE);
            holder.counterLinearLayout.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
