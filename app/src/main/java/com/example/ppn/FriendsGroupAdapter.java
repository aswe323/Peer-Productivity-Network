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

/**
 *
 *  A class used for a {@link RecyclerView} for the people the user is following.<br><br>
 *
 *  This class is used to create a view of a UI section list of people the user follows .<br>
 *
 */
public class FriendsGroupAdapter extends RecyclerView.Adapter<FriendsGroupAdapter.FriendsGroupHolder> {

    private AlertDialog.Builder commentDialogBox; //A DialogBox to add a comment to a followed user.
    private EditText inputForCommentDialog; //An Edit Text for the adding comment DialogBox.
    private String commentText="";
    ArrayList<HashMap<String,Long>> friends = new ArrayList<>(); //Holding a list of the users followed by the user.
    //friends profile variables
    private AlertDialog.Builder friendsProfile; // A DialogBox to show other users profile.
    private RecyclerView friendsFollowersRecycle; //A recyclerview to show the followed user follows in his profile, used by the show friends profile DialogBox.
    private FriendsFollowersAdapter friendsFollowersAdapter; //the adapter of the friendsFollowersRecycle.

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
            //set TextView of the followed user and his points
            holder.friendsUserName.setText(entry.getKey());
            holder.friendsPoints.setText(entry.getValue().toString());

            //when the delete button was pressed the followed user is deleted from the DB by a Repository method, then calling the points UI updater.
            holder.deleteUserFromGroup.setOnClickListener(v -> {
                Repository.deleteUserFromMyGroup(entry.getKey()).addOnCompleteListener(task -> {
                    PointsAndGroups.displayGroupPoints();
                });
                friends.remove(friends.get(position));
                notifyItemRemoved(position);
                Toast.makeText(v.getContext(), entry.getKey()+" was deleted from your group", Toast.LENGTH_SHORT).show();
            });

            //when a followed user name was pressed it will open his profile in a DialogBox
            holder.friendsUserName.setOnClickListener(v -> {

                friendsProfile = new AlertDialog.Builder(v.getContext()); //set the DialogBox
                //get all of his followed users with a Repository method.
                Repository.getOtherUserGroup(entry.getKey()).addOnCompleteListener(task -> {
                    //setting dynamically the UI elements of the users.
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

                    //creating the RecyclerView for the users followed by the user I check the profile of and his adapter and connecting between them
                    friendsFollowersAdapter = new FriendsFollowersAdapter(otherFriendsFollowers);
                    friendsFollowersRecycle = new RecyclerView(v.getContext());
                    friendsFollowersRecycle.setLayoutManager(new LinearLayoutManager(v.getContext()));
                    friendsFollowersRecycle.setAdapter(friendsFollowersAdapter);

                    //adding to a main layout all of the user data elements
                    linearLayout.addView(userdata);
                    linearLayout.addView(friendsFollowersRecycle);

                    friendsProfile.setView(linearLayout);
                    friendsProfile.setNegativeButton("close",null).show();
                });


            });

            //when pressing the comment button this will open a DialogBox with an EditText to allow the user leave a comment
            holder.leaveComment.setOnClickListener(v -> {
                commentDialogBox = new AlertDialog.Builder(v.getContext());
                commentDialogBox.setTitle("leave a comment");
                commentDialogBox.setMessage("write your comment here: ");
                inputForCommentDialog = new EditText(v.getContext());
                commentDialogBox.setView(inputForCommentDialog);

                commentDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        commentText = inputForCommentDialog.getText().toString();
                        if(commentText.length()<=25) //the comment is limited to 25 characters to prevent spamming, if less then 25 characters call the Repository method to add it to the DB
                        {
                            Repository.addCommentToAnotherUser(entry.getKey(), commentText);
                            Toast.makeText(v.getContext(), "commented: " + commentText + " to " + entry.getKey(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(v.getContext(), "commented is too long, restricted to 25", Toast.LENGTH_SHORT).show();
                        }
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
