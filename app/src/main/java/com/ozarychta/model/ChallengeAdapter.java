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

import java.util.ArrayList;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private ArrayList<Challenge> dataSet;

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView repeatTextView;
        public TextView categoryTextView;
        public TextView cityTextView;
        public TextView goalTextView;

        public ChallengeViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            this.repeatTextView = (TextView) itemView.findViewById(R.id.repeatPeriodTextView);
            this.categoryTextView = (TextView) itemView.findViewById(R.id.categoryTextView);
            this.cityTextView = (TextView) itemView.findViewById(R.id.cityTextView);
            this.goalTextView = (TextView) itemView.findViewById(R.id.goalTextView);
            Log.d("constructor view holder", "constructor view holder");
        }
    }

    public ChallengeAdapter(ArrayList<Challenge> data) {
        this.dataSet = data;
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
        TextView titleTextView = holder.titleTextView;
        TextView repeatTextView = holder.repeatTextView;
        TextView categoryTextView = holder.categoryTextView;
        TextView cityTextView = holder.cityTextView;
        TextView goalTextView = holder.goalTextView;

        Log.d("on bind view holder", "on bind view holder \n" + dataSet.get(position));


        holder.titleTextView.setText(dataSet.get(position).getTitle());
        repeatTextView.setText("Powtórzeń w tygodniu: " + dataSet.get(position).getRepeatPeriod().getTimesPerWeek());
        categoryTextView.setText("Kategoria: " + dataSet.get(position).getCategory().toString());
        cityTextView.setText("Miasto: " + dataSet.get(position).getCity());
        if(dataSet.get(position).getGoal() == 0){
            goalTextView.setText("");
        } else {
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
