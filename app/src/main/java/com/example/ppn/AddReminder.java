package com.example.ppn;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddReminder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddReminder extends Fragment implements View.OnClickListener {

    private int activitytaskID; //TODO: fix it to get the last id and increment
    private AlertDialog.Builder subActivityDialogBox;
    private String subactivitytext="";
    private EditText inputForSubActivityDialog;
    private ArrayList<SubActivity> subActivities;
    private TimePack time;
    private boolean isEditFlag;
    private EditText editText;
    private Switch aSwitch;
    private Spinner repetitionSpinner;
    private Spinner categorySpinner;
    private TextView timeFromText;
    private TextView timeToText;
    private Button addSubActivity;
    private Button cancel;
    private Button save;
    private RecyclerView recyclerView;

    //for time dialog picker
    private TimePickerDialog timePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    final int hour=calendar.get(calendar.HOUR_OF_DAY);
    final int minute=calendar.get(calendar.MINUTE);


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddReminder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddReminder.
     */
    // TODO: Rename and change types and number of parameters
    public static AddReminder newInstance(String param1, String param2) {
        AddReminder fragment = new AddReminder();
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

    public void onClick(View view){

        switch (view.getId()) {//recognizing what button was pushed

            case R.id.Btn_save_reminder:
                //region save
                if(!isEditFlag) {
                    if(editText.getText().toString().equals("")){
                        Toast.makeText(getActivity(), "make sure to write a description to the reminder", Toast.LENGTH_LONG).show();
                        break;
                    }
                    Repository.createActivityTask(activitytaskID,
                            MasloCategory.valueOf(categorySpinner.getSelectedItem().toString()),
                            editText.getText().toString(),
                            subActivities,
                            time);
                    Repository.refreshNotifications();
                }
                //endregion
                break;
            case R.id.Btn_add_subReminder:
                //region add sub reminder region
                subActivityDialogBox = new AlertDialog.Builder(getContext());
                subActivityDialogBox.setTitle("sub reminder:");
                subActivityDialogBox.setMessage("enter the sub reminder here: ");
                inputForSubActivityDialog = new EditText(getContext());
                subActivityDialogBox.setView(inputForSubActivityDialog);

                subActivityDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        subactivitytext = inputForSubActivityDialog.getText().toString();
                        if(!isEditFlag)
                            subActivities.add(new SubActivity(subactivitytext,activitytaskID));
                        //else TODO: add editing
                            //subActivities.add(new SubActivity(0,EditedActivityTask.getActivityTaskID(),subactivitytext));
                        Toast.makeText(getActivity(), "sub reminder was added: "+subactivitytext, Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
                subActivityDialogBox.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                inputForSubActivityDialog.setText("");
                                return;
                            }
                        });
                subActivityDialogBox.show();
                //endregion
                break;

            case R.id.TextView_from_time:
                //region add time from

                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeChecker="";
                        if(hourOfDay<10)
                            timeChecker+="0"+hourOfDay+":";
                        else
                            timeChecker+=hourOfDay+":";
                        if(minute<10)
                            timeChecker+="0"+minute;
                        else
                            timeChecker+=minute;

                        timeFromText.setText("time from:\n" + timeChecker);
                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();
                //endregion
                break;
                
            case R.id.TextView_to_time:
                //region add time to

                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeChecker="";
                        if(hourOfDay<10)
                            timeChecker+="0"+hourOfDay+":";
                        else
                            timeChecker+=hourOfDay+":";
                        if(minute<10)
                            timeChecker+="0"+minute;
                        else
                            timeChecker+=minute;

                        timeToText.setText("time to:\n" + timeChecker);
                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();
                //endregion
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_add_reminder, container, false);

        subActivities=new ArrayList<>();
        isEditFlag=false;
        editText=view.findViewById(R.id.EditText_reminder_content);
        aSwitch=view.findViewById(R.id.Switch_AI_enabler);
        repetitionSpinner=view.findViewById(R.id.Spinner_repetition);
        categorySpinner=view.findViewById(R.id.Spinner_category);
        timeFromText=view.findViewById(R.id.TextView_from_time);
        timeToText=view.findViewById(R.id.TextView_to_time);
        addSubActivity=view.findViewById(R.id.Btn_add_subReminder);
        cancel=view.findViewById(R.id.Btn_cancel_reminder);
        save=view.findViewById(R.id.Btn_save_reminder);
        recyclerView = view.findViewById(R.id.subRecyclerView);

        SubActivityAdapter recycleAdapter = new SubActivityAdapter(subActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recycleAdapter);

        //region OnClickListeners

        addSubActivity.setOnClickListener(this);
        timeFromText.setOnClickListener(this);
        timeToText.setOnClickListener(this);

        //endregion

        return view;
    }



}