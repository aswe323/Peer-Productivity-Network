package com.example.ppn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsGroupAdapter extends RecyclerView.Adapter<FriendsGroupAdapter.FriendsGroupHolder> {

    private AlertDialog.Builder commentDialogBox;
    private EditText inputForCommentDialog;
    private String commentText="";
    ArrayList<HashMap<String,Integer>> friends = new ArrayList<>();

    public FriendsGroupAdapter(ArrayList<HashMap<String,Integer>> friends) {
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
        for (Map.Entry<String,Integer> entry:friends.get(position).entrySet())
        {
            holder.friendsUserName.setText(entry.getKey());
            holder.friendsPoints.setText(entry.getValue());
            holder.deleteUserFromGroup.setOnClickListener(v -> {
                Repository.deleteUserFromMyGroup(entry.getKey());
                Toast.makeText(v.getContext(), entry.getKey()+" was deleted from your group", Toast.LENGTH_SHORT).show();
            });

            holder.leaveComment.setOnClickListener(v -> {
                commentDialogBox = new AlertDialog.Builder(v.getContext());
                commentDialogBox.setTitle("leave a comment");
                commentDialogBox.setMessage("write your comment here: ");
                inputForCommentDialog = new EditText(v.getContext());
                commentDialogBox.setView(inputForCommentDialog);

                commentDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        commentText = inputForCommentDialog.getText().toString();
                        Repository.addCommentToAnotherUser(entry.getKey(),commentText);

                        Toast.makeText(v.getContext(), "commented: "+commentText +" to "+entry.getKey(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
                commentDialogBox.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                inputForCommentDialog.setText("");
                                return;
                            }
                        });
                commentDialogBox.show();


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
        Button deleteUserFromGroup,leaveComment;


        public FriendsGroupHolder(@NonNull View itemView) {
            super(itemView);

            friendsUserName = itemView.findViewById(R.id.friendUserNameText);
            friendsPoints = itemView.findViewById(R.id.friendPoints);
            deleteUserFromGroup = itemView.findViewById(R.id.btnDeleteFriend);
            leaveComment = itemView.findViewById(R.id.leaveCommentText);

        }
    }

}
