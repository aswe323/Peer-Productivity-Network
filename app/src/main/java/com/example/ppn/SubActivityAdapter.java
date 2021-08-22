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
    private boolean dataShow = false;

    public SubActivityAdapter(ArrayList<SubActivity> subActivities,boolean dataShow) {
        this.subActivities=subActivities;
        this.dataShow = dataShow;
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
        if(!dataShow)
        {
            holder.DeleteSub.setOnClickListener(v -> {
                for (SubActivity subActivity : subActivities)
                    if (subActivity.getContent() == holder.subtext.getText().toString()) {
                        subActivities.remove(subActivity);
                        int deletedPosition = holder.getAbsoluteAdapterPosition();
                        notifyItemRemoved(deletedPosition);
                        break;
                    }
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
