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

/**
 *
 *  A class used for a {@link RecyclerView} for the followed people by the friend.<br><br>
 *
 *  This class is used to create a view of a UI section list of followed people by the friend you checked his profile.<br>
 *  The the profile will have this {@link RecyclerView} as a list of the people he follows.
 *
 */
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
        //set the TextView of the user and his points.
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
