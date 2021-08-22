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
    private boolean isDataShow = false;

    public RelevantDateAdapter(ArrayList<String> dates,boolean isDataShow) {
        this.dates = dates;
        this.isDataShow = isDataShow;
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
        if(!isDataShow)
        {
            holder.DeleteSub.setOnClickListener(v -> {
                int deletedPosition = holder.getAbsoluteAdapterPosition();
                dates.remove(dates.indexOf(holder.subtext.getText()));
                notifyItemRemoved(deletedPosition);
            });
        }
        else
        {
            holder.DeleteSub.setVisibility(View.GONE);
            holder.DeleteSub.setEnabled(false);
        }
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
