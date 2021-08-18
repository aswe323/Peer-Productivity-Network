package com.example.ppn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class FriendsGroupAdapter extends RecyclerView.Adapter<FriendsGroupAdapter.FriendsGroupHolder> {

    Map<String,Integer> friends = new HashMap<>();

    public FriendsGroupAdapter(Map<String, Integer> friends) {
        this.friends = friends;
    }

    @NonNull
    @Override
    public FriendsGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friendsgroup_recycler, parent, false);

        return new FriendsGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsGroupHolder holder, int position) {
        for (Map.Entry<String,Integer> entry:friends.entrySet())
        {
            holder.friendsUserName.setText(entry.getKey());
            holder.friendsPoints.setText(entry.getValue());
            holder.deleteUserFromGroup.setOnClickListener(v -> {
                Repository.deleteUserFromMyGroup(entry.getKey());
                Toast.makeText(v.getContext(), entry.getKey()+" was deleted from your group", Toast.LENGTH_SHORT).show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendsGroupHolder extends RecyclerView.ViewHolder
    {
        TextView friendsUserName,friendsPoints;
        Button deleteUserFromGroup;


        public FriendsGroupHolder(@NonNull View itemView) {
            super(itemView);

            friendsUserName = itemView.findViewById(R.id.friendUserNameText);
            friendsPoints = itemView.findViewById(R.id.friendPoints);
            deleteUserFromGroup = itemView.findViewById(R.id.btnDeleteFriend);

        }
    }

}
