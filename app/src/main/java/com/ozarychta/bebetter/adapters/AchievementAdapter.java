package com.ozarychta.bebetter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.models.Achievement;

import java.util.ArrayList;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>{
    private ArrayList<Achievement> dataSet;

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView descTextView;

        public AchievementViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            this.descTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
        }
    }

    public AchievementAdapter(ArrayList<Achievement> data) {
        this.dataSet = data;
    }

    @Override
    public AchievementAdapter.AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_achievement, parent, false);

        return new AchievementAdapter.AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementAdapter.AchievementViewHolder holder, int position) {
        holder.titleTextView.setText(dataSet.get(position).getTitle());
        holder.descTextView.setText(dataSet.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}