package com.example.ppn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    ArrayList<HashMap<String,Long>> friends = new ArrayList<>();
    //friends profile variables
    private AlertDialog.Builder friendsProfile;
    private RecyclerView friendsFollowersRecycle;
    private FriendsFollowersAdapter friendsFollowersAdapter;

    public FriendsGroupAdapter(ArrayList<HashMap<String,Long>> friends) {
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
        for (Map.Entry<String,Long> entry:friends.get(position).entrySet())
        {
            holder.friendsUserName.setText(entry.getKey());
            holder.friendsPoints.setText(entry.getValue().toString());
            holder.deleteUserFromGroup.setOnClickListener(v -> {
                Repository.deleteUserFromMyGroup(entry.getKey()).addOnCompleteListener(task -> {
                    PointsAndGroups.displayGroupPoints();
                });
                friends.remove(friends.get(position));
                notifyItemRemoved(position);
                Toast.makeText(v.getContext(), entry.getKey()+" was deleted from your group", Toast.LENGTH_SHORT).show();
            });

            holder.friendsUserName.setOnClickListener(v -> {
                friendsProfile = new AlertDialog.Builder(v.getContext());
                Repository.getOtherUserGroup(entry.getKey()).addOnCompleteListener(task -> {
                    HashMap<String,Long> otherFriendsFollowers = task.getResult();
                    LinearLayout linearLayout = new LinearLayout(v.getContext());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(15,0,0,15);
                    TextView userdata = new TextView(v.getContext());

                    userdata.setTextSize(24);
                    userdata.setTextColor(Color.BLACK);
                    userdata.setText(entry.getKey()+":\t\t\t\t"+entry.getValue()+"\n\n\n\t\t\t\t\t\t\t\tfollowers:");
                    userdata.setLayoutParams(params);

                    friendsFollowersAdapter = new FriendsFollowersAdapter(otherFriendsFollowers);
                    friendsFollowersRecycle = new RecyclerView(v.getContext());
                    friendsFollowersRecycle.setLayoutManager(new LinearLayoutManager(v.getContext()));
                    friendsFollowersRecycle.setAdapter(friendsFollowersAdapter);
                    linearLayout.addView(userdata);
                    linearLayout.addView(friendsFollowersRecycle);
                    friendsProfile.setView(linearLayout);
                    friendsProfile.setNegativeButton("close",null).show();
                });


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
