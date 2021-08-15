package com.example.ppn;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage extends Fragment implements View.OnClickListener{

    private Task task;
    private Button addReminder;
    private ScrollView scrollView;
    private ArrayList<ActivityTask> activityTasks;
    private LinearLayout hoster; //can't add more then one layout to ScrollView so the hoster will hold all the data lines to print (like a collection for layouts).
    //those ArrayLists will hold the buttons for edit/delete and content of the reminder ordered,
    //so every reminder will have matching button indexes for the program to easily know what reminder to work on.
    private ArrayList<Button> deleteReminderButton;
    private ArrayList<Button> editReminderButton;
    private ArrayList<CheckBox> checkBoxs;
    private ArrayList<TextView> reminderText;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homePage.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePage newInstance(String param1, String param2) {
        HomePage fragment = new HomePage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //unable the menu onOptionsItemSelected method to work in the fragment.
        Log.d("#$%^&*((*)%^&$@#$@$&^*(&)&*(^&*$%^$$#@@!@#()__+(&#@!@!@#%$^%^%   ", "   onCreate: reloaded fragment");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {//recognizing what button was pushed
            case R.id.Btn_add_reminder:
                //region add reminder

                AddReminder addReminder = new AddReminder();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(((ViewGroup)(getView().getParent())).getId(), addReminder)
                        .setReorderingAllowed(true)
                        .addToBackStack("addReminder") // name can be null
                        .commit();
                //endregion
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        scrollView = view.findViewById(R.id.scroll_Homepage);
        addReminder = view.findViewById(R.id.Btn_add_reminder);

        activityTasks = new ArrayList<>();
        hoster = new LinearLayout(getActivity());
        hoster.setOrientation(LinearLayout.VERTICAL);
        hoster.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        editReminderButton = new ArrayList<>();
        deleteReminderButton = new ArrayList<>();
        checkBoxs = new ArrayList<>();
        reminderText = new ArrayList<>();

        try {
            task = Repository.getAllUserActivityTasks();
            //task=Repository.getThisDayActivityTasks();

            task.addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                if (task.isSuccessful()) {
                    int activityTaskID=0;
                    for (DocumentSnapshot entry :
                            task.getResult().getDocuments()) {
                        ActivityTask activityTask= entry.toObject(ActivityTask.class);
                        if(addWordToScrollViewFuture(activityTask))

                            activityTasks.add(activityTask);
                            setDeleteButton(activityTask,deleteReminderButton.get(activityTaskID),activityTaskID);
                            //TODO: add edit button setter
                            activityTaskID++;
                    }
                }

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        scrollView.addView(hoster);

        //region button click listeners

        //tasks buttons setup


        addReminder.setOnClickListener(this);

        //endregion

        return view;
    }

    /**
     * this is used to activate the upper bar in the app
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu: //if clicked the search icon it will move the user into the search fragment
                /*FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                Search erf = new Search();
                ft.replace(getView().getId(), erf).commit();*/

                //getChildFragmentManager().beginTransaction().add(R.id.Fragment_Search,)
                return true;
            case R.id.user_menu: //TODO:this will allow the user to login\logout of the google user
                //Toast.makeText(this.getContext(), "this is not a bug... it's a feature, read comment to learn more", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), ""+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /*     genera UI element hierarchy

     *  outerLayout(horizontal)
     *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  * checkbox  innerTextLayout(vertical)  innerButtonLayout(horizontal)  *
     *  * |V|       * * * * * * * * * * * *    * * * * * * * * * * * * * * *  *
     *  *           * taskTextView        *    * editButton   deleteButton *  *
     *  *           * timeTextView        *    * * * * * * * * * * * * * * *  *
     *  *           * * * * * * * * * * * *                                   *
     *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     */
    private boolean addWordToScrollViewFuture(ActivityTask activityTask){ //this method dynamically creates the elements of the reminders on our home page,called in onCreateView
        //hierarchy holder of our elements please look up for the schema
        LinearLayout outerLayout = new LinearLayout(getActivity());
        LinearLayout innerTextLayout = new LinearLayout(getActivity());
        LinearLayout innerButtonLayout = new LinearLayout(getActivity());
        CheckBox checkBoxDone = new CheckBox(getActivity());
        Button btnEdit = new Button(getActivity());
        Button btnDelete = new Button(getActivity());
        TextView reminderText = new TextView(getActivity());
        TextView timeText = new TextView(getActivity());


        outerLayout.setOrientation(LinearLayout.HORIZONTAL);
        outerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        innerTextLayout.setOrientation(LinearLayout.VERTICAL);
        innerTextLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));

        innerButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerButtonLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        checkBoxDone.setChecked(false);
        checkBoxs.add(checkBoxDone);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 0, 5, 0);

        btnEdit.setBackgroundResource(R.color.purple_500);
        btnEdit.setTextColor(Color.WHITE);
        btnEdit.setTransformationMethod(null);
        btnEdit.setText("Edit");
        btnEdit.setLayoutParams(btnParams);
        editReminderButton.add(btnEdit);

        btnDelete.setBackgroundResource(R.color.purple_500);
        btnDelete.setTextColor(Color.WHITE);
        btnDelete.setTransformationMethod(null);
        btnDelete.setText("Delete");
        deleteReminderButton.add(btnDelete);

        reminderText.setText(""+activityTask.getContent());
        reminderText.setTextColor(Color.BLACK);
        reminderText.setTextSize(24);
        LinearLayout.LayoutParams paramstxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        reminderText.setLayoutParams(paramstxt);

        timeText.setText(""+activityTask.getTimePack().getStartingTime()+" - "+activityTask.getTimePack().getEndingTime());
        timeText.setTextColor(Color.BLACK);
        timeText.setTextSize(12);
        timeText.setLayoutParams(paramstxt);

        if(innerTextLayout!=null && innerButtonLayout!=null && outerLayout!=null && scrollView != null){
            outerLayout.addView(checkBoxDone);
            outerLayout.addView(innerTextLayout);
            outerLayout.addView(innerButtonLayout);

            innerTextLayout.addView(reminderText);
            innerTextLayout.addView(timeText);

            innerButtonLayout.addView(btnEdit);
            innerButtonLayout.addView(btnDelete);

            hoster.addView(outerLayout);
            return true;
        }
        else
            return false;

    }

    /**
     * this method is used to create a setOnClickListener for edit button
     *
     * @param activityTask get the activity which the button is belong to
     * @param Editbtn get the button to set id on
     */
    /*private void setEditButton(ActivityTask activityTask,Button Editbtn){

        Editbtn.setOnClickListener(view1 -> caller(topActivities.get(Editbtn.getId()))); //TODO set id, create the caller, add activityIDinArray
    }*/

    /**
     * this method is used to create a setOnClickListener for delete button
     *
     * @param activityTask get the activity which the button is belong to
     * @param Deletebtn get the button to set id on
     * @param activityIDinArray set to each button the correct activityTask id it's belongs to
     */
    private void setDeleteButton(ActivityTask activityTask,Button Deletebtn, int activityIDinArray){

        Deletebtn.setId(activityIDinArray);

        Deletebtn.setOnClickListener(view->{
            Repository.deleteActivivtyTask(activityTask.getActivityTaskID());
            Toast.makeText(getActivity(), "deleted " + activityTask.getContent(), Toast.LENGTH_SHORT).show();
            //final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            //ft.detach(this).attach(this).commit();

            /*Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.layout.fragment_home_page);
            ft.detach(fragment);
            ft.attach(fragment);
            ft.commit();*/
            //getActivity().getSupportFragmentManager().beginTransaction().detach(HomePage.this);
        });
    }
}