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
import java.util.HashMap;
import java.util.Map;

public class CommentsRecycleAdapter extends RecyclerView.Adapter<CommentsRecycleAdapter.CommentHolder> {

    ArrayList<HashMap<String,String>> comments = new ArrayList<HashMap<String,String>>();


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


        for (Map.Entry<String,String> entry:comments.get(position).entrySet())
        {
            holder.user.setText(
                    entry.getKey());
            holder.comment.setText(
                    entry.getValue());

            holder.deleteComment.setOnClickListener(v -> {
                Repository.deleteCommentFromMyProfile(entry.getValue(),entry.getKey());
                notifyItemRemoved(position);
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

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.friendUserNameText_cmnt);
            comment = itemView.findViewById(R.id.friendText_cmnt);
            deleteComment = itemView.findViewById(R.id.btnDeleteComment);


        }
    }

}
