package com.ozarychta.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.R;
import com.ozarychta.activities.ChallengeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private static final String SIMPLE_DATE_FORMAT = "dd.MM";
    private SimpleDateFormat simpleDateFormat;

    private ArrayList<Challenge> dataSet;

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView repeatTextView;
        public TextView categoryTextView;
        public TextView cityTextView;
        public TextView goalTextView;
        public TextView startDateTextView;
        public TextView endDateTextView;
        public Context ctx;

        public ChallengeViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            this.repeatTextView = (TextView) itemView.findViewById(R.id.repeatPeriodTextView);
            this.categoryTextView = (TextView) itemView.findViewById(R.id.categoryTextView);
            this.cityTextView = (TextView) itemView.findViewById(R.id.cityTextView);
            this.goalTextView = (TextView) itemView.findViewById(R.id.goalTextView);
            this.startDateTextView = (TextView) itemView.findViewById(R.id.startDayTextView);
            this.endDateTextView = (TextView) itemView.findViewById(R.id.endDayTextView);
            this.ctx = itemView.getContext();
            Log.d("constructor view holder", "constructor view holder");
        }
    }

    public ChallengeAdapter(ArrayList<Challenge> data) {
        this.dataSet = data;
        simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_challenge, parent, false);

        Log.d("on create view holder", "on create view holder");
        ChallengeViewHolder challengeViewHolder = new ChallengeViewHolder(view);
        return challengeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Log.d("on bind view holder", "on bind view holder \n" + dataSet.get(position));
        TextView goalTextView = holder.goalTextView;

        holder.titleTextView.setText(dataSet.get(position).getTitle());
        holder.repeatTextView.setText(holder.ctx.getText(R.string.times_per_week).toString() + " " + dataSet.get(position).getRepeatPeriod().getTimesPerWeek());
        holder.categoryTextView.setText(holder.ctx.getText(R.string.category) + " " + dataSet.get(position).getCategory().getLabel(holder.ctx));
        holder.cityTextView.setText(holder.ctx.getText(R.string.city) + " " + dataSet.get(position).getCity());
        holder.startDateTextView.setText(holder.ctx.getText(R.string.start_date) + " " + simpleDateFormat.format(dataSet.get(position).getStartDate()));
        holder.endDateTextView.setText(holder.ctx.getText(R.string.end_date) + " " + simpleDateFormat.format(dataSet.get(position).getEndDate()));
        if(dataSet.get(position).getGoal() == 0){
            goalTextView.setVisibility(View.GONE);
            goalTextView.setText("");
        } else {
            goalTextView.setVisibility(View.VISIBLE);
            goalTextView.setText("Przez: " + dataSet.get(position).getGoal() + " min");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ChallengeActivity.class);
                intent.putExtra("CHALLENGE", dataSet.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
