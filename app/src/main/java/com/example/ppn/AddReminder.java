package com.example.ppn;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * A simple {@link Fragment} subclass used to show the add reminder page of the UI.<br><br>
 *
 * This fragment is a prat of the UI containing the elements that are used to set the data of a new reminder or to edit an existing reminder.<br>
 * The class is called by the {@link FragmentManager} class and replacing the {@link HomePage} fragment when the <b><i>Add new reminder</i></b> button in HomePage was clicked.<br><br>
 *
 * The fragment contains UI elements such as Buttons, RecyclerViews, TextViews ect to get an input and present an output of the data that will be inserted to the DB.<br><br>
 *
 */
public class AddReminder extends Fragment implements View.OnClickListener {

    private int activitytaskID; //Used to hold the reminder ID.
    private AlertDialog.Builder subActivityDialogBox; //Used to open a DialogBox to add an SubActivity.
    private String subactivitytext=""; //Used to contain the SubActivity text to add to the reminder.
    private EditText inputForSubActivityDialog; //Used for a text Input for subActivityDialogBox.
    private ArrayList<SubActivity> subActivities; //Holds all of the reminder SubActivities.
    private ArrayList<String> dates; //Holds all the dates the reminder is relevant to.
    private TimePack time;
    private boolean isEditFlag; //A flag that is True when in edit mode, get his status from Bundle.
    private boolean isDataShow; //A flag that is True when in data show mode, get his status from Bundle.
    private EditText editText;
    private Spinner repetitionSpinner;
    private Spinner categorySpinner;
    private TextView timeFromText;
    private TextView timeToText;
    private Button addDate;
    private Button addSubActivity;
    private Button cancel;
    private Button save;
    private RecyclerView subActivitiesRecyclerView; //A RecyclerView to show all of the SubActivities of the reminder
    private RecyclerView relevantDatesRecyclerView; //A RecyclerView to show all of the relevant dates of the reminder
    private RelevantDateAdapter relevantDateAdapter;
    private SubActivityAdapter recycleAdapter;
    private String fromDate,toDate;

    //for time dialog picker
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String timeChecker="";
    private Calendar calendar = Calendar.getInstance();
    private final int hour = calendar.get(calendar.HOUR_OF_DAY);
    private final int minute = calendar.get(calendar.MINUTE);
    private int mYear = calendar.get(calendar.YEAR);
    private int mMonth = calendar.get(calendar.MONTH);
    private int mDay = calendar.get(calendar.DAY_OF_MONTH);


    public AddReminder() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onClick(View view){

        switch (view.getId()) {//recognizing what button was pushed

            case R.id.Btn_save_reminder: //when the save button was clicked this will be the chosen case
                //region save

                if(editText.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "make sure to write a description to the reminder", Toast.LENGTH_LONG).show();
                    break;
                }
                if(timeFromText.getText().toString().equals("click to choose")||timeToText.getText().toString().equals("click to choose"))
                {
                    Toast.makeText(getActivity(), "make sure to choose time from and time to", Toast.LENGTH_LONG).show();
                    break;
                }
                //make sure that the time from and time to is a relevant date to prevent bugs with TimePack
                if(dates.indexOf(fromDate)==-1 && fromDate!=null)
                {
                    dates.add(fromDate);
                    relevantDateAdapter.notifyDataSetChanged();
                }
                if(dates.indexOf(toDate)==-1 && toDate!=null)
                {
                    dates.add(toDate);
                    relevantDateAdapter.notifyDataSetChanged();
                }

                //convert dates from string to localDateTime for the TimePack
                ArrayList<LocalDateTime> relevantDatesForTimePack=new ArrayList<>();
                DateTimeFormatter relevantDateFormatter =  new DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM-dd[ HH:mm:ss]")
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter();
                //DateTimeFormatter relevantDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (String date:dates){
                    relevantDatesForTimePack.add(LocalDateTime.parse(
                            date,
                            relevantDateFormatter));
                }

                //convert the starting time (from) and ending time (to) for TimePack
                DateTimeFormatter formatter=TimePack.getFormatter();
                LocalDateTime timeFrom = LocalDateTime.parse(
                        timeFromText.getText().toString(),
                        formatter);
                LocalDateTime timeTo = LocalDateTime.parse(
                        timeToText.getText().toString(),
                        formatter);
                if (LocalDateTime.now().isAfter(timeFrom) || LocalDateTime.now().isAfter(timeTo)){
                    Toast.makeText(getContext(), "the old time has passed, choose new time", Toast.LENGTH_SHORT).show();
                    break;
                }

                time = new TimePack(timeFrom,
                        timeTo,
                        timeFrom.getMonthValue(),
                        Repetition.valueOf(repetitionSpinner.getSelectedItem().toString()),
                        relevantDatesForTimePack);

                if(!isEditFlag) { //if it's not an edit mode then call the Repository method that creates the reminder and when done doing so update the notifications

                    Repository.createActivityTask(activitytaskID,
                            MasloCategory.valueOf(categorySpinner.getSelectedItem().toString()),
                            editText.getText().toString(),
                            subActivities,
                            time).addOnCompleteListener(task -> {
                        Repository.refreshNotifications();
                    });
                    //Remove this fragment from the backstack and go back.
                    getParentFragmentManager().beginTransaction().remove(this).commit();
                }
                else{ //if in edit mode, delete the reminder from the DB, then recreate it whit the new data, then when it's done update the notifications
                    Repository.deleteActivityTask(getArguments().getInt("activityTaskID"))
                            .addOnCompleteListener(task -> {
                                task.addOnCompleteListener(task1 -> {
                                    Repository.createActivityTask(getArguments().getInt("activityTaskID"),
                                            MasloCategory.valueOf(categorySpinner.getSelectedItem().toString()),
                                            editText.getText().toString(),
                                            subActivities,
                                            time).addOnCompleteListener(taskNotification -> {
                                        Repository.refreshNotifications();
                                    });
                                    Toast.makeText(getContext(), "updated the task", Toast.LENGTH_SHORT).show();
                                    //Remove this fragment from the backstack and go back.
                                    getParentFragmentManager().beginTransaction().remove(this).commit();
                                });
                            });

                }
                //endregion
                break;

            case R.id.Btn_cancel_reminder: //when the cancel button was clicked this will be the chosen case
                //region cancel

                Toast.makeText(getContext(), "canceled", Toast.LENGTH_SHORT).show();
                //Remove this fragment from the backstack and go back.
                getParentFragmentManager().beginTransaction().remove(this).commit();

                //endregion
                break;

            case R.id.Btn_add_subReminder: //when the Add SubReminder button was clicked this will be the chosen case
                //region add sub reminder region

                subActivityDialogBox = new AlertDialog.Builder(getContext()); //create the DialogBox
                subActivityDialogBox.setTitle("sub reminder:");
                subActivityDialogBox.setMessage("enter the sub reminder here: ");
                inputForSubActivityDialog = new EditText(getContext());
                subActivityDialogBox.setView(inputForSubActivityDialog); //add the EditText to the DialogBox to get the SubReminder content

                subActivityDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() { //setting action for the ok button
                    public void onClick(DialogInterface dialog, int whichButton) {

                        subactivitytext = inputForSubActivityDialog.getText().toString(); //get the content of the EditText
                        //get the next reminder ID that will be created, or the ID of the edited reminder
                        Task t=Repository.getActivityTaskCollection().orderBy("activityTaskID", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener(
                                queryDocumentSnapshots ->{
                                    if(!isEditFlag)
                                        activitytaskID = queryDocumentSnapshots.getDocuments().get(0).toObject(ActivityTask.class).getActivityTaskID()+1;
                                    else
                                        activitytaskID = getArguments().getInt("activityTaskID");
                                    subActivities.add(new SubActivity(subactivitytext, activitytaskID));
                                    recycleAdapter.notifyDataSetChanged();
                                });
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

            case R.id.btnRelevantDates: //when the click to choose relevant dates button was clicked this will be the chosen case
                //region add date

                //date picker DialogBox
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //make the time string correct to avoid any bugs when formatting to LocalDateTime
                                String timeForIf;
                                if (monthOfYear<10)
                                    timeForIf=""+year + "-0" + (monthOfYear + 1);
                                else
                                    timeForIf=""+year + "-" + (monthOfYear + 1);

                                if(dayOfMonth<10)
                                    timeForIf+="-0"+dayOfMonth;
                                else
                                    timeForIf+="-"+dayOfMonth;

                                if(LocalDate.now().isAfter(LocalDate.parse(timeForIf)))
                                {
                                    Toast.makeText(getActivity(),"this date has passed, can't choose it",Toast.LENGTH_SHORT).show();
                                }
                                else if(dates.indexOf(timeForIf)==-1) //check if the date exist add, if not add it
                                {
                                    dates.add(timeForIf);
                                    relevantDateAdapter.notifyDataSetChanged();
                                }
                                else
                                    Toast.makeText(getActivity(),"this date is already exist",Toast.LENGTH_SHORT).show();
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();


                //endregion
                break;

            case R.id.TextView_from_time: //when the click to choose TextView of time from was clicked this will be the chosen case
                //region add time from
                timeChecker="";

                //time Picker DialogBox
                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //make the time string correct to avoid any bugs when formatting to LocalDateTime
                        if(hourOfDay<10)
                            timeChecker+="0"+hourOfDay+":";
                        else
                            timeChecker+=hourOfDay+":";
                        if(minute<10)
                            timeChecker+="0"+minute;
                        else
                            timeChecker+=minute;

                        if(LocalDateTime.now().isAfter(LocalDateTime.parse(timeChecker,TimePack.getFormatter())))
                        {
                            Toast.makeText(getActivity(),"this time has passed, can't choose it",Toast.LENGTH_SHORT).show();
                        }
                        else
                            timeFromText.setText(timeChecker);
                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));

                //date picker DialogBox
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //make the time string correct to avoid any bugs when formatting to LocalDateTime
                                String timeForIf;
                                if (monthOfYear<9)
                                    timeForIf=""+year + "-0" + (monthOfYear + 1);
                                else
                                    timeForIf=""+year + "-" + (monthOfYear + 1);

                                if(dayOfMonth<10)
                                    timeForIf+="-0"+dayOfMonth;
                                else
                                    timeForIf+="-"+dayOfMonth;

                                if(LocalDate.now().isAfter(LocalDate.parse(timeForIf)))
                                {
                                    Toast.makeText(getActivity(),"this date has passed, can't choose it",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    timeChecker = timeForIf+" ";
                                    fromDate = timeForIf;
                                    timePickerDialog.show();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

                //endregion
                break;

            case R.id.TextView_to_time: //when the click to choose TextView of time to was clicked this will be the chosen case
                //region add time to
                timeChecker="";
                //time picker DialogBox
                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //make the time string correct to avoid any bugs when formatting to LocalDateTime
                        if(hourOfDay<10)
                            timeChecker+="0"+hourOfDay+":";
                        else
                            timeChecker+=hourOfDay+":";
                        if(minute<10)
                            timeChecker+="0"+minute;
                        else
                            timeChecker+=minute;

                        if(LocalDateTime.now().isAfter(LocalDateTime.parse(timeChecker,TimePack.getFormatter())))
                        {
                            Toast.makeText(getActivity(),"this time has passed, can't choose it",Toast.LENGTH_SHORT).show();
                        }
                        else
                            timeToText.setText(timeChecker);
                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));

                //date picker DialogBox
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //make the time string correct to avoid any bugs when formatting to LocalDateTime
                                String timeForIf;
                                if (monthOfYear<9)
                                    timeForIf=""+year + "-0" + (monthOfYear + 1);
                                else
                                    timeForIf=""+year + "-" + (monthOfYear + 1);

                                if(dayOfMonth<10)
                                    timeForIf+="-0"+dayOfMonth;
                                else
                                    timeForIf+="-"+dayOfMonth;

                                if(LocalDate.now().isAfter(LocalDate.parse(timeForIf)))
                                {
                                    Toast.makeText(getActivity(),"this date has passed, can't choose it",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    timeChecker = timeForIf+" ";
                                    toDate = timeForIf;
                                    timePickerDialog.show();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                //endregion
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_add_reminder, container, false);

        isEditFlag = getArguments().getBoolean("isEdit");
        isDataShow = getArguments().getBoolean("isDataShow");
        subActivities=new ArrayList<>();
        dates = new ArrayList<>();
        editText=view.findViewById(R.id.EditText_reminder_content);
        addDate = view.findViewById(R.id.btnRelevantDates);
        repetitionSpinner=view.findViewById(R.id.Spinner_repetition);
        categorySpinner=view.findViewById(R.id.Spinner_category);
        timeFromText=view.findViewById(R.id.TextView_from_time);
        timeToText=view.findViewById(R.id.TextView_to_time);
        addSubActivity=view.findViewById(R.id.Btn_add_subReminder);
        cancel=view.findViewById(R.id.Btn_cancel_reminder);
        save=view.findViewById(R.id.Btn_save_reminder);
        subActivitiesRecyclerView = view.findViewById(R.id.subRecyclerView);
        relevantDatesRecyclerView = view.findViewById(R.id.datesRecyclerView);


        //creating the RecyclerView for the SubActivities and his adapter and connecting between them
        recycleAdapter = new SubActivityAdapter(subActivities,isDataShow);
        subActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        subActivitiesRecyclerView.setAdapter(recycleAdapter);

        //creating the RecyclerView for the relevant dates and his adapter and connecting between them
        relevantDateAdapter = new RelevantDateAdapter(dates,isDataShow);
        relevantDatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        relevantDatesRecyclerView.setAdapter(relevantDateAdapter);

        if(isEditFlag){ //if edit flag is true then fetch from the DB the reminder data and set it to the elements
            Task t=Repository.getActivityTaskCollection()
                    .whereEqualTo("activityTaskID",getArguments()
                            .getInt("activityTaskID")).get()
                    .addOnSuccessListener(queryDocumentSnapshots ->{

                        ActivityTask activityTask = queryDocumentSnapshots.getDocuments().get(0).toObject(ActivityTask.class);
                        editText.setText(activityTask.getContent());
                        for (String date:activityTask.getTimePack().getStrigifiedRelaventDates())
                        {
                            DateTimeFormatter formatter = TimePack.getFormatter();
                            LocalDateTime localDateTime = LocalDateTime.parse(date,formatter);
                            String time;
                            if (localDateTime.getMonthValue()<10)
                                time=""+ localDateTime.getYear() + "-0" + localDateTime.getMonthValue();
                            else
                                time=""+ localDateTime.getYear() + "-" + localDateTime.getMonthValue();

                            if(localDateTime.getDayOfMonth()<10)
                                time+="-0"+localDateTime.getDayOfMonth();
                            else
                                time+="-"+localDateTime.getDayOfMonth();

                            dates.add(time);
                        }
                        repetitionSpinner.setSelection(activityTask.getTimePack().getRepetition().ordinal());
                        categorySpinner.setSelection(activityTask.getMasloCategory().ordinal());
                        timeFromText.setText(activityTask.getTimePack().getStartingTime());
                        timeToText.setText(activityTask.getTimePack().getEndingTime());
                        subActivities.addAll(activityTask.getSubActivitys());

                        recycleAdapter.notifyDataSetChanged();
                        relevantDateAdapter.notifyDataSetChanged();
                    } );

        }
        else if(isDataShow) //if data show flag is true then fetch from the DB the reminder data and set it to the elements,disable the elements and hide the buttons to prevent data manipulation
        {
            Task t=Repository.getActivityTaskCollection()
                    .whereEqualTo("activityTaskID",getArguments()
                            .getInt("activityTaskID")).get()
                    .addOnSuccessListener(queryDocumentSnapshots ->{

                        ActivityTask activityTask = queryDocumentSnapshots.getDocuments().get(0).toObject(ActivityTask.class);
                        editText.setText(activityTask.getContent());
                        editText.setEnabled(false);
                        for (String date:activityTask.getTimePack().getStrigifiedRelaventDates())
                        {
                            DateTimeFormatter formatter = TimePack.getFormatter();
                            LocalDateTime localDateTime = LocalDateTime.parse(date,formatter);
                            String time;
                            if (localDateTime.getMonthValue()<10)
                                time=""+ localDateTime.getYear() + "-0" + localDateTime.getMonthValue();
                            else
                                time=""+ localDateTime.getYear() + "-" + localDateTime.getMonthValue();

                            if(localDateTime.getDayOfMonth()<10)
                                time+="-0"+localDateTime.getDayOfMonth();
                            else
                                time+="-"+localDateTime.getDayOfMonth();

                            dates.add(time);
                        }
                        repetitionSpinner.setSelection(activityTask.getTimePack().getRepetition().ordinal());
                        repetitionSpinner.setEnabled(false);

                        categorySpinner.setSelection(activityTask.getMasloCategory().ordinal());
                        categorySpinner.setEnabled(false);

                        timeFromText.setText(activityTask.getTimePack().getStartingTime());
                        timeFromText.setEnabled(false);

                        timeToText.setText(activityTask.getTimePack().getEndingTime());
                        timeToText.setEnabled(false);

                        subActivities.addAll(activityTask.getSubActivitys());

                        addDate.setEnabled(false);
                        addDate.setVisibility(View.GONE);

                        addSubActivity.setEnabled(false);
                        addSubActivity.setVisibility(View.GONE);

                        cancel.setEnabled(false);
                        cancel.setVisibility(View.GONE);

                        save.setEnabled(false);
                        save.setVisibility(View.GONE);


                        recycleAdapter.notifyDataSetChanged();
                        relevantDateAdapter.notifyDataSetChanged();
                    });
        }
        //region OnClickListeners

        addSubActivity.setOnClickListener(this);
        timeFromText.setOnClickListener(this);
        timeToText.setOnClickListener(this);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        addDate.setOnClickListener(this);


        //endregion

        return view;
    }



}