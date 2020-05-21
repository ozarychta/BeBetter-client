package com.ozarychta.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.R;
import com.ozarychta.model.User;

import java.util.ArrayList;

class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private ArrayList<User> dataSet;

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView usernameTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
        }
    }

    public UserAdapter(ArrayList<User> data) {
        this.dataSet = data;
    }

    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_user, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        holder.usernameTextView.setText(dataSet.get(position).getUsername());

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