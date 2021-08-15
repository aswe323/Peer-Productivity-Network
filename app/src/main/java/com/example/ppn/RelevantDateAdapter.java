package com.example.ppn;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RelevantDateAdapter extends RecyclerView.Adapter<RelevantDateAdapter.RelevantDateRecycleHolder>{
    private ArrayList<String> dates;

    public RelevantDateAdapter(ArrayList<String> dates) {
        this.dates = dates;
    }

    @NonNull
    @Override
    public RelevantDateRecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subactivity_recycleview, parent, false);

        return new RelevantDateRecycleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelevantDateRecycleHolder holder, int position) {
        holder.subtext.setText(dates.get(position));
        holder.DeleteSub.setOnClickListener(v -> {
            int deletedPosition = holder.getAbsoluteAdapterPosition();
            dates.remove(dates.indexOf(holder.subtext.getText()));
            notifyItemRemoved(deletedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class RelevantDateRecycleHolder extends RecyclerView.ViewHolder{
        TextView subtext;
        Button DeleteSub;
        public RelevantDateRecycleHolder(@NonNull View itemView) {
            super(itemView);
            subtext = itemView.findViewById(R.id.subtext);
            DeleteSub = itemView.findViewById(R.id.btnDeleteSub);
        }
    }
}
