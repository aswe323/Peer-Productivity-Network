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
 *  A class used for a {@link RecyclerView} for {@link SubActivity}.<br><br>
 *
 *  This class is used to create a view of a UI section list of SunActivities that is in {@link AddReminder}.
 *
 */
public class SubActivityAdapter extends RecyclerView.Adapter<SubActivityAdapter.SubActivityRecycleHolder> {
    private ArrayList<SubActivity> subActivities;
    private boolean dataShow = false;//A flag to mention if the data is for show only which means to hide and disable the delete button when true

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
        //set the subActivity text
        holder.subtext.setText(subActivities.get(position).getContent());

        if(!dataShow) //if the data show flag is false set the delete button
        {
            //when the delete button is pressed delete the subActivity from the ArrayList
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
            //if the flag is true disable and hide the delete button
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
