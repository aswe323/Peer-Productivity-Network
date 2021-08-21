package com.example.ppn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class FriendsFollowersAdapter extends RecyclerView.Adapter<FriendsFollowersAdapter.FriendsFollowersHolder> {
    HashMap<String,Long> otherFriendsFollowers;

    public FriendsFollowersAdapter(HashMap<String, Long> otherFriendsFollowers) {
        this.otherFriendsFollowers = otherFriendsFollowers;
    }

    @NonNull
    @Override
    public FriendsFollowersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_followers_recyclerview, parent, false);

        return new FriendsFollowersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsFollowersHolder holder, int position) {
        holder.user.setText(""+ otherFriendsFollowers.keySet().toArray()[position]);
        holder.points.setText(""+ otherFriendsFollowers.values().toArray()[position]);
    }

    @Override
    public int getItemCount() {
        return otherFriendsFollowers.size();
    }


    class FriendsFollowersHolder extends RecyclerView.ViewHolder {

        TextView user,points;

        public FriendsFollowersHolder(@NonNull View itemView) {
            super(itemView);
            user =itemView.findViewById(R.id.followerUseName);
            points =itemView.findViewById(R.id.followerPoints);
        }
    }
}
