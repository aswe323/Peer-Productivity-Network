package com.example.ppn;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddReminder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddReminder extends Fragment implements View.OnClickListener {

    private int activitytaskID;
    private AlertDialog.Builder subActivityDialogBox;
    private String subactivitytext="";
    private EditText inputForSubActivityDialog;
    private ArrayList<SubActivity> subActivities;
    private ArrayList<String> dates;
    private TimePack time;
    private boolean isEditFlag;
    private EditText editText;
    private Spinner repetitionSpinner;
    private Spinner categorySpinner;
    private TextView timeFromText;
    private TextView timeToText;
    private Button addDate;
    private Button addSubActivity;
    private Button cancel;
    private Button save;
    private RecyclerView subActivitiesRecyclerView;
    private RecyclerView relevantDatesRecyclerView;
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

                if(!isEditFlag) {

                    Repository.createActivityTask(activitytaskID,
                            MasloCategory.valueOf(categorySpinner.getSelectedItem().toString()),
                            editText.getText().toString(),
                            subActivities,
                            time);
                    Repository.refreshNotifications();
                    getParentFragmentManager().beginTransaction().remove(this).commit();
                }
                else{
                    Repository.deleteActivivtyTask(getArguments().getInt("activityTaskID"))
                            .addOnCompleteListener(task -> {
                                task.addOnCompleteListener(task1 -> {
                                    Repository.createActivityTask(getArguments().getInt("activityTaskID"),
                                            MasloCategory.valueOf(categorySpinner.getSelectedItem().toString()),
                                            editText.getText().toString(),
                                            subActivities,
                                            time);
                                    Repository.refreshNotifications();
                                    Toast.makeText(getContext(), "updated the task", Toast.LENGTH_SHORT).show();
                                    getParentFragmentManager().beginTransaction().remove(this).commit();
                                });
                            });

                }
                //endregion
                break;

            case R.id.Btn_cancel_reminder:
                //region cancel

                Toast.makeText(getContext(), "canceled", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction().remove(this).commit();

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
                        Task t=Repository.getActivityTaskCollection().orderBy("activityTaskID", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener(
                                queryDocumentSnapshots ->{
                                    activitytaskID = queryDocumentSnapshots.getDocuments().get(0).toObject(ActivityTask.class).getActivityTaskID()+1;
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

            case R.id.btnRelevantDates:
                //region add date

                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

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
                                else if(dates.indexOf(timeForIf)==-1) //check if the date exist add, if not toast
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

            case R.id.TextView_from_time:
                //region add time from
                timeChecker="";

                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

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


                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

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

            case R.id.TextView_to_time:
                //region add time to
                timeChecker="";

                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

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


                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

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

        isEditFlag=getArguments().getBoolean("isEdit");
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



        recycleAdapter = new SubActivityAdapter(subActivities);
        subActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        subActivitiesRecyclerView.setAdapter(recycleAdapter);

        relevantDateAdapter = new RelevantDateAdapter(dates);
        relevantDatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        relevantDatesRecyclerView.setAdapter(relevantDateAdapter);

        if(isEditFlag){
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