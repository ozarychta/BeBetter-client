package com.ozarychta.bebetter.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.bebetter.R;
import com.ozarychta.bebetter.activities.ProfileActivity;
import com.ozarychta.bebetter.models.User;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder>{
    private ArrayList<User> dataSet;

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        public TextView usernameTextView;
        public TextView pointsTextView;
        public TextView streakTextView;

        public FriendViewHolder(View itemView) {
            super(itemView);
            this.usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
            this.pointsTextView = (TextView) itemView.findViewById(R.id.pointsTextView);
            this.streakTextView = (TextView) itemView.findViewById(R.id.streakTextView);
        }
    }

    public FriendAdapter(ArrayList<User> data) {
        this.dataSet = data;
    }

    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_friend, parent, false);

        FriendAdapter.FriendViewHolder friendViewHolder = new FriendAdapter.FriendViewHolder(view);
        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendViewHolder holder, int position) {
        holder.usernameTextView.setText(dataSet.get(position).getUsername());
        holder.pointsTextView.setText(String.valueOf(dataSet.get(position).getRankingPoints()));
        holder.streakTextView.setText(String.valueOf(dataSet.get(position).getHighestStrike()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("USER_ID", dataSet.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
