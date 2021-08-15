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

public class SubActivityAdapter extends RecyclerView.Adapter<SubActivityAdapter.SubActivityRecycleHolder> {
    private ArrayList<SubActivity> subActivities;

    public SubActivityAdapter(ArrayList<SubActivity> subActivities) {
        this.subActivities=subActivities;
    }

    @NonNull
    @Override
    public SubActivityRecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subactivity_recycleview, parent, false);

        return new SubActivityRecycleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubActivityRecycleHolder holder, int position) {
        //sub holder
        holder.subtext.setText(subActivities.get(position).getContent());
        holder.DeleteSub.setOnClickListener(v -> {
            for (SubActivity subActivity: subActivities)
                if (subActivity.getContent()==holder.subtext.getText().toString()) {
                    subActivities.remove(subActivity);
                    int deletedPosition = holder.getAbsoluteAdapterPosition();
                    notifyItemRemoved(deletedPosition);
                    break;
                }
        });
    }

    @Override
    public int getItemCount() {
        return subActivities.size();
    }

    public class SubActivityRecycleHolder extends RecyclerView.ViewHolder{
        TextView subtext;
        Button DeleteSub;
        public SubActivityRecycleHolder(@NonNull View itemView) {
            super(itemView);
            subtext = itemView.findViewById(R.id.subtext);
            DeleteSub = itemView.findViewById(R.id.btnDeleteSub);

        }
    }
}
