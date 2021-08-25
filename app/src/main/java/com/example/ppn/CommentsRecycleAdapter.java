package com.example.ppn;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *  A class used for a {@link RecyclerView} for comments.<br><br>
 *
 *  This class is used to create a view of a UI section list of comments.
 *
 */
public class CommentsRecycleAdapter extends RecyclerView.Adapter<CommentsRecycleAdapter.CommentHolder> {

    ArrayList<HashMap<String,String>> comments = new ArrayList<HashMap<String,String>>(); //an ArrayList that represent users, in it contains may that represent a user name (key) and the comment (value)


    public CommentsRecycleAdapter(ArrayList<HashMap<String,String>> comments) {
        this.comments=comments;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments_recycle, parent, false);

        return new CommentHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        //get the map of the users
        for (Map.Entry<String,String> entry:comments.get(position).entrySet())
        {
            //set the TextView of the user name and the comment.
            holder.user.setText(
                    entry.getKey());
            holder.comment.setText(
                    entry.getValue());
            //when the delete button of a comment was hit, call the Repository method that deletes it from the DB.
            holder.deleteComment.setOnClickListener(v -> {
                Repository.deleteCommentFromMyProfile(entry.getValue(), entry.getKey());
            });

        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder
    {
        TextView user,comment;
        Button deleteComment;
        LinearLayout commentMainLayout;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.friendUserNameText_cmnt);
            comment = itemView.findViewById(R.id.friendText_cmnt);
            deleteComment = itemView.findViewById(R.id.btnDeleteComment);
            commentMainLayout = itemView.findViewById(R.id.commentMainLayout);


        }
    }

}
