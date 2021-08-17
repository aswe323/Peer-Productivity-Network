package com.example.ppn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

public class WordRecycleAdapter extends RecyclerView.Adapter<WordRecycleAdapter.WordRecycleHolder> {

    private Map<String,Integer> priorityWordMap;
    private Map<String,String> bucketWordMap;
    boolean isBucket;

    public WordRecycleAdapter(Map<String, Integer> priorityWordMap, Map<String,String> bucketWordMap, boolean isBucket) {
        this.isBucket = isBucket;
        if(isBucket)
            this.bucketWordMap = bucketWordMap;
        else
            this.priorityWordMap = priorityWordMap;


    }

    @NonNull
    @Override
    public WordRecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_recyclerview, parent, false);
        return new WordRecycleHolder(view);
    }

    @Override
    public int getItemCount() {
        if(isBucket)
            return bucketWordMap.size();
        else
            return priorityWordMap.size();
    }

    @Override
    public void onBindViewHolder(@NonNull WordRecycleHolder holder, int position) {

        int i = 0;

        if (isBucket)
        {
            for (Map.Entry<String, String> entry : bucketWordMap.entrySet()) {
                if (position == i) {
                    holder.wordTxt.setText(entry.getKey());
                    holder.priorityNrangeTxt.setText(entry.getValue().toString());
                    break;
                }
                i++;
            }

            holder.deleteButton.setOnClickListener(v -> {
                bucketWordMap.remove(holder.wordTxt.getText().toString());
                Repository.deleteBucketWord(holder.wordTxt.getText().toString());
            });
        }
        else
        {
            for (Map.Entry<String, Integer> entry : priorityWordMap.entrySet()) {
                if (position == i) {
                    holder.wordTxt.setText(entry.getKey());
                    holder.priorityNrangeTxt.setText(entry.getValue().toString());
                    break;
                }
                i++;
            }
            holder.deleteButton.setOnClickListener(v -> {
                priorityWordMap.remove(holder.wordTxt.getText().toString());
                Repository.deletePriorityWord(holder.wordTxt.getText().toString());
            });
        }
    }

    public class WordRecycleHolder extends RecyclerView.ViewHolder{

        TextView wordTxt,priorityNrangeTxt;
        Button deleteButton;

        public WordRecycleHolder(@NonNull View itemView) {
            super(itemView);
            wordTxt = itemView.findViewById(R.id.wordText);
            priorityNrangeTxt = itemView.findViewById(R.id.priorityNrangeText);
            deleteButton = itemView.findViewById(R.id.btnDeletePNBWord);
        }
    }
}
