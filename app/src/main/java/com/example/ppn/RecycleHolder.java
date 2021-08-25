package com.example.ppn;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * A class used for a {@link RecyclerView} for the reminders.<br><br>
 *
 * This class is used to create a view of a UI section list of reminders.<br>
 * Due to the use of a {@link com.firebase.ui.database.FirebaseRecyclerAdapter} in the {@link HomePage} this class will contain only the holder unlike the other RecyclerViews.
 *
 */
public class RecycleHolder extends RecyclerView.ViewHolder {

    TextView textTask,textTime;
    Button btnDelete,btnEdit;
    LinearLayout dataHolder;
    CheckBox checkBox;

    public RecycleHolder(@NonNull View itemView) {
        super(itemView);
        textTime = itemView.findViewById(R.id.textTime);
        textTask = itemView.findViewById(R.id.textTask);
        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        dataHolder = itemView.findViewById(R.id.reminderDataLayout);
        checkBox = itemView.findViewById(R.id.checkBox);

    }
}
