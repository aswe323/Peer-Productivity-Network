package com.example.ppn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleHolder> {

    private Context context;
    //private Map<String,>

    public RecycleAdapter(Context context) {

        this.context=context;

    }

    @NonNull
    @Override
    public RecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activitytask_recycleview,parent,false);

        return new RecycleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecycleHolder extends RecyclerView.ViewHolder{

        private TextView taskText,timeText;
        private Button deleteBtn,editBtn;

        public RecycleHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.textTask);
            timeText = itemView.findViewById(R.id.textTime);
            editBtn = itemView.findViewById(R.id.btnEdit);
            deleteBtn = itemView.findViewById(R.id.btnDelete);

        }
    }
}
