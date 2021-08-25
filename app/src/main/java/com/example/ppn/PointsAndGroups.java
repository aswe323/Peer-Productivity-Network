package com.example.ppn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;

import org.stringtemplate.v4.ST;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * A simple {@link Fragment} subclass used to show the groups and points of the user in the UI.<br><br>
 *
 * The groups and points is a UI element that is responsible to the whole interaction of the user with the people he follow and his points.<br>
 * The class is responsible to interact with the UI to get the followers, comments and the points from the DB using the {@link Repository} methods.<br><br>
 *
 * Using buttons to add follow and check the followed users, TextViews to show the user points and combined points of the user and the followed points,
 * a {@link RecyclerView} to show comments that were left to the user.<br><br>
 *
 * Adding a user, seeing followed list use a costume {@link AlertDialog} box containing {@link RecyclerView}.<br>
 * Pressing the <i><b>group friends</b></i> button open a {@link AlertDialog} box that contain followed list {@link RecyclerView},
 * it allows to see other's profile when clicking on their user name, delete the followed from the list or to leave a comment to that user.<br><br>
 *
 * Pressing the <i><b>add follower</b></i> button open a costume {@link AlertDialog} box with a TextView to insert user name.
 *
 */
public class PointsAndGroups extends Fragment implements View.OnClickListener{

    private static TextView myPointsText;
    private static TextView groupPointsText;
    private static View view;
    private RecyclerView commentsRecycle;
    private Button groupFriendsBtn;
    private Button addFriendBtn;
    private AlertDialog.Builder addUserActivityDialogBox; //DialogBox to add user to me
    private EditText inputForAddUserDialog; //used in the adding user DialogBox
    private String userNameText="";
    private CommentsRecycleAdapter commentsAdapter;
    //dialog box of group friends
    private AlertDialog.Builder groupsFriendsDialog; //DialogBox to show my group friends (followed users)
    private RecyclerView friendsRecyclerView; //a recyclerview used in the DialogBox that show my group friends
    private FriendsGroupAdapter friendsGroupAdapter; //adapter for the friends list of the DialogBox

    public PointsAndGroups() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//recognizing what button was pushed

            case R.id.Btn_group_friends: //when the group friends was clicked this will be the chosen case
                //region friends in group

                groupsFriendsDialog = new AlertDialog.Builder(getContext()); //setting the DialogBox
                groupsFriendsDialog.setTitle("group friends:\n");

                //call the Repository method that fetch all of the users in my groups
                Repository.readGroup().addOnCompleteListener(task -> {
                    ArrayList<HashMap<String,Long>> groupFriendsArray = (ArrayList<HashMap<String,Long>>) task.getResult().getData().get("groupMembers");
                    //make sure to not show the control users and myself.
                    groupFriendsArray.removeIf(stringLongHashMap -> {
                        boolean containstDoNotShow = stringLongHashMap.containsKey("doNotShow");
                        boolean containstDoNotShowTwo = stringLongHashMap.containsKey("doNotShow2");
                        boolean containsUserDisplayName = stringLongHashMap.containsKey(Repository.getUser().getDisplayName());

                        return containstDoNotShow || containstDoNotShowTwo || containsUserDisplayName;
                    });

                    //creating the RecyclerView for the followed users and his adapter and connecting between them
                    friendsGroupAdapter = new FriendsGroupAdapter(groupFriendsArray);
                    friendsRecyclerView = new RecyclerView(getContext());
                    friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    friendsRecyclerView.setAdapter(friendsGroupAdapter);

                    groupsFriendsDialog.setView(friendsRecyclerView);
                    groupsFriendsDialog.show();
                });

                groupsFriendsDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                return;
                            }
                        });



                //endregion
                break;

            case R.id.Btn_add_to_group: //when the add follower was clicked this will be the chosen case
                //region add group

                addUserActivityDialogBox = new AlertDialog.Builder(getContext()); //setting the DialogBox
                addUserActivityDialogBox.setTitle("add user to your group");
                addUserActivityDialogBox.setMessage("user name to add: ");
                inputForAddUserDialog = new EditText(getContext());
                addUserActivityDialogBox.setView(inputForAddUserDialog);

                addUserActivityDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        userNameText = inputForAddUserDialog.getText().toString();
                        //call the Repository method to add the user to me in the DB, after it call the method updating the points Textview.
                        Repository.addUserToMyGroup(userNameText).addOnCompleteListener(task -> {
                            PointsAndGroups.displayGroupPoints();
                        });

                        return;
                    }
                });
                addUserActivityDialogBox.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                inputForAddUserDialog.setText("");
                                return;
                            }
                        });
                addUserActivityDialogBox.show();

                //endregion
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_points_and_groups, container, false);
        myPointsText = view.findViewById(R.id.TextView_MyPoint);
        groupPointsText = view.findViewById(R.id.TextView_GroupPoints);
        groupFriendsBtn = view.findViewById(R.id.Btn_group_friends);
        addFriendBtn = view.findViewById(R.id.Btn_add_to_group);
        commentsRecycle = view.findViewById(R.id.commentsRecycle);

        //call the Repository method that fetch all of the users in my groups
        Repository.getUserGroupRef().addSnapshotListener((value, error) -> {
            ArrayList<HashMap<String,String>> dataComments = (ArrayList<HashMap<String,String>>)value.getData().get("comments");
            //make sure to not show the control comment.
            dataComments.removeIf(stringStringHashMap -> {
                return stringStringHashMap.containsKey("doNotShow") || stringStringHashMap.containsKey("doNotShow2");
            });
            //creating the  for the comments and his adapter and connecting between them
            commentsAdapter = new CommentsRecycleAdapter(dataComments);
            commentsRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
            commentsRecycle.setAdapter(commentsAdapter);
        });
        //call the method updating the points Textview.
        PointsAndGroups.displayGroupPoints();

        Repository.getUserGroupRef().addSnapshotListener((value, error) -> {
            PointsAndGroups.displayGroupPoints();
        });

        //region setOnClickListener

        addFriendBtn.setOnClickListener(this);
        groupFriendsBtn.setOnClickListener(this);

        //endregion


        return view;
    }

    /**
     *
     * A privet method to make sure the TextView variables has the elements ID.
     *
     * @return void
     *
     */
    private static void setIdTextView()
    {
        myPointsText = view.findViewById(R.id.TextView_MyPoint);
        groupPointsText = view.findViewById(R.id.TextView_GroupPoints);
    }

    /**
     *
     * A static method to set the points TextViews in the UI.<br>
     * used to update the UI from the data in the DB.
     *
     * @return void
     *
     */
    public static void displayGroupPoints()
    {
        if(myPointsText==null || groupPointsText==null){ //if the variables of the TextViews are null call setIdTextview()
            setIdTextView();
        }

        //get the users from the database using a Repository method
        Repository.readGroup().addOnSuccessListener(documentSnapshot -> {
            long groupPoints=0;
            for(Map<String,Integer> usersList:(ArrayList<Map<String,Integer>>)documentSnapshot
                    .getData()
                    .get("groupMembers"))
            {
                for (Map.Entry entry:usersList.entrySet()) //take each user map to check it in the loop
                {
                    if (((String)entry.getKey()).equals(Repository.getUser().getDisplayName())) //if the user is me, set myPointsText TextView.
                    {
                        myPointsText.setText("My points: "+((long)entry.getValue()));
                    }
                    groupPoints+=((long)entry.getValue());
                }
            }
            groupPointsText.setText("group points: "+groupPoints);

        });
    }

}