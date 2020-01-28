package com.ozarychta;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<Challenge> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView repeatTextView;
        public TextView categoryTextView;
        public TextView cityTextView;
        public TextView goalTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            this.repeatTextView = (TextView) itemView.findViewById(R.id.repeatPeriodTextView);
            this.categoryTextView = (TextView) itemView.findViewById(R.id.categoryTextView);
            this.cityTextView = (TextView) itemView.findViewById(R.id.cityTextView);
            this.goalTextView = (TextView) itemView.findViewById(R.id.goalTextView);
            Log.d("constructor view holder", "constructor view holder");
        }
    }

    public CustomAdapter(ArrayList<Challenge> data) {
        this.dataSet = data;
    }

    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_challenge, parent, false);

//        view.setOnClickListener(MainActivity.myOnClickListener);

        Log.d("on create view holder", "on create view holder");
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        TextView titleTextView = holder.titleTextView;
        TextView repeatTextView = holder.repeatTextView;
        TextView categoryTextView = holder.categoryTextView;
        TextView cityTextView = holder.cityTextView;
        TextView goalTextView = holder.goalTextView;

        Log.d("on bind view holder", "on bind view holder \n" + dataSet.get(position));


        holder.titleTextView.setText(dataSet.get(position).getTitle());
        repeatTextView.setText(dataSet.get(position).getRepeatPeriod().toString());
        categoryTextView.setText(dataSet.get(position).getCategory().toString());
        cityTextView.setText(dataSet.get(position).getCity());
        if(dataSet.get(position).getGoal() == 0){
            goalTextView.setText("");
        } else {
            goalTextView.setText("For: " + dataSet.get(position).getGoal() + " min");
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
