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
import com.ozarychta.bebetter.models.Comment;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm  dd.MM.yyyy");

    private ArrayList<Comment> dataSet;

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView usernameTextView;
        public TextView createdAtTextView;
        public TextView commentTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            this.usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
            this.createdAtTextView = (TextView) itemView.findViewById(R.id.createdAtTextView);
            this.commentTextView = (TextView) itemView.findViewById(R.id.commentTextView);
        }
    }

    public CommentAdapter(ArrayList<Comment> data) {
        this.dataSet = data;
    }

    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_comment, parent, false);

        CommentAdapter.CommentViewHolder CommentViewHolder = new CommentAdapter.CommentViewHolder(view);
        return CommentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        holder.usernameTextView.setText(dataSet.get(position).getCreatorUsername());
        holder.createdAtTextView.setText(dataSet.get(position).getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).format(formatter));
        holder.commentTextView.setText(dataSet.get(position).getText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("USER_ID", dataSet.get(position).getCreatorId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
