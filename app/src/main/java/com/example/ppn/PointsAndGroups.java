package com.example.ppn;

import android.app.AlertDialog;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link PointsAndGroups#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointsAndGroups extends Fragment implements View.OnClickListener{

    private TextView myPointsText;
    private TextView groupPointsText;
    private RecyclerView commentsRecycle;
    private Button groupFriendsBtn;
    private Button addFriendBtn;
    private AlertDialog.Builder addUserActivityDialogBox;
    private EditText inputForAddUserDialog;
    private String userNameText="";
    private CommentsRecycleAdapter commentsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PointsAndGroups() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pointsAndGroups.
     */
    // TODO: Rename and change types and number of parameters
    public static PointsAndGroups newInstance(String param1, String param2) {
        PointsAndGroups fragment = new PointsAndGroups();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//recognizing what button was pushed

            case R.id.Btn_group_friends:
                //region save



                //endregion
                break;

            case R.id.Btn_add_to_group:
                //region add group

                addUserActivityDialogBox = new AlertDialog.Builder(getContext());
                addUserActivityDialogBox.setTitle("add user to your group");
                addUserActivityDialogBox.setMessage("user name to add: ");
                inputForAddUserDialog = new EditText(getContext());
                addUserActivityDialogBox.setView(inputForAddUserDialog);

                addUserActivityDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        userNameText = inputForAddUserDialog.getText().toString();
                        Repository.addUserToMyGroup(userNameText);
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
        View view = inflater.inflate(R.layout.fragment_points_and_groups, container, false);

        myPointsText = view.findViewById(R.id.TextView_MyPoint);
        groupPointsText = view.findViewById(R.id.TextView_GroupPoints);
        groupFriendsBtn = view.findViewById(R.id.Btn_group_friends);
        addFriendBtn = view.findViewById(R.id.Btn_add_to_group);
        commentsRecycle = view.findViewById(R.id.commentsRecycle);



        Repository.readGroup().addOnSuccessListener(documentSnapshot -> {
            long groupPoints=0;
            for(Map<String,Integer> individualComment:(ArrayList<Map<String,Integer>>)documentSnapshot
                    .getData()
                    .get("groupMembers"))
            {
                for (Map.Entry entry:individualComment.entrySet())
                {
                    if (((String)entry.getKey()).equals(Repository.getUser().getDisplayName()))
                    {
                        myPointsText.setText("My points: "+((long)entry.getValue()));
                        groupPoints+=((long)entry.getValue());
                    }
                    else
                    {
                        groupPoints+=((long)entry.getValue());
                    }
                }
            }
            groupPointsText.setText("group points: "+groupPoints);

            ArrayList<HashMap<String,String>> test =(ArrayList<HashMap<String,String>>)documentSnapshot
                    .getData()
                    .get("comments");

            commentsAdapter = new CommentsRecycleAdapter((ArrayList<HashMap<String,String>>)documentSnapshot
                    .getData()
                    .get("comments"));
            commentsRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
            commentsRecycle.setAdapter(commentsAdapter);
        });

        //region setOnClickListener

        addFriendBtn.setOnClickListener(this);
        groupFriendsBtn.setOnClickListener(this);

        //endregion


        return view;
    }

}