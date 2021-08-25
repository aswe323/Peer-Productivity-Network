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
/**
 *
 *  A class used for a {@link RecyclerView} for relevant dates.<br><br>
 *
 *  This class is used to create a view of a UI section list of relevant dates that is in {@link AddReminder}.
 *
 */
public class RelevantDateAdapter extends RecyclerView.Adapter<RelevantDateAdapter.RelevantDateRecycleHolder>{
    private ArrayList<String> dates;
    private boolean isDataShow = false;//A flag to mention if the data is for show only which means to hide and disable the delete button when true

    public RelevantDateAdapter(ArrayList<String> dates,boolean isDataShow) {
        this.dates = dates;
        this.isDataShow = isDataShow;
    }

    @NonNull
    @Override
    public RelevantDateRecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subactivity_recycleview, parent, false); //this is using the same layout as SubActivityAdapter

        return new RelevantDateRecycleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelevantDateRecycleHolder holder, int position) {

        //set the date.
        holder.subtext.setText(dates.get(position));

        if(!isDataShow) //if the data show flag is false set the delete button
        {
            holder.DeleteSub.setOnClickListener(v -> {
                int deletedPosition = holder.getAbsoluteAdapterPosition();
                dates.remove(dates.indexOf(holder.subtext.getText()));
                notifyItemRemoved(deletedPosition);
            });
        }
        else
        {
            //if the flag is true disable and hide the delete button
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
