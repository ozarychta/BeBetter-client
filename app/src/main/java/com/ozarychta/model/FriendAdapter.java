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
import com.ozarychta.activities.ProfileActivity;

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
            Log.d("constructor view holder", "constructor view holder");
        }
    }

    public FriendAdapter(ArrayList<User> data) {
        this.dataSet = data;
    }

    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_friend, parent, false);

        Log.d("on create view holder", "on create view holder");
        FriendAdapter.FriendViewHolder friendViewHolder = new FriendAdapter.FriendViewHolder(view);
        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendViewHolder holder, int position) {
        Log.d("on bind view holder", "on bind view holder \n" + dataSet.get(position));

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
