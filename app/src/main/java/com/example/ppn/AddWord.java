package com.example.ppn;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * A simple {@link Fragment} subclass used to show the add word page of the UI.<br><br>
 *
 * This fragment is a part of the UI containing elements that are used to set the data of a new word or to edit an existing word.<br>
 * The class is called by the {@link FragmentManager} class and replacing the {@link KeyWords} fragment when the <b><i>Add new word</i></b> button in KeyWords was clicked.<br><br>
 *
 * The fragment contains EditText, switch, Seekbar, TextViews, Spinners and Buttons to get the data that will create the word.
 *
 */
public class AddWord extends Fragment implements View.OnClickListener{

    private EditText wordTxt;
    private Switch bucketWordSwitch;
    private SeekBar seekBarPriority;
    private TextView priorityOnSeekBar;
    private LinearLayout bucketTimeSetLayout;
    private TextView bucketTimeFrom;
    private TextView bucketTimeTo;
    private Button save,cancel;
    private Spinner repetitionSpinner;
    private boolean isBucket=false;
    private String timeChecker="";
    private TimePickerDialog timePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    private final int hour = calendar.get(calendar.HOUR_OF_DAY);
    private final int minute = calendar.get(calendar.MINUTE);


    public AddWord() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//recognizing what button was pushed

            case R.id.Btn_save_word: //when the save was clicked this will be the chosen case
                //region save

                if(!wordTxt.getText().toString().matches("[a-zA-Z]+")) { //make sure it's a single word with characters only
                    Toast.makeText(getActivity(), "has to be one word and contain only letters", Toast.LENGTH_SHORT).show();
                    break;
                }

                if(isBucket)  //if it's a bucket word then enter
                {
                    if(!bucketTimeFrom.getText().toString().matches("[0-9:]+") || !bucketTimeTo.getText().toString().matches("[0-9:]+")) //make sure time was picked
                    {
                        Toast.makeText(getActivity(), "choose time", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    String timeCheckerFrom="";
                    String timeCheckerTo="";

                    //make the time string correct to avoid any bugs when formatting to LocalDateTime
                    if (LocalDateTime.now().getMonthValue()<10)
                    {
                        timeCheckerFrom = ""+ LocalDateTime.now().getYear() + "-0" + LocalDateTime.now().getMonthValue();
                        timeCheckerTo = ""+ LocalDateTime.now().getYear() + "-0" + LocalDateTime.now().getMonthValue();
                    }
                    else
                        {
                        timeCheckerFrom = "" + LocalDateTime.now().getYear() + "-" + LocalDateTime.now().getMonthValue();
                        timeCheckerTo = "" + LocalDateTime.now().getYear() + "-" + LocalDateTime.now().getMonthValue();
                    }
                    if(LocalDateTime.now().getDayOfMonth()<10) {

                        timeCheckerFrom += "-0" + LocalDateTime.now().getDayOfMonth();
                        timeCheckerTo += "-0" + LocalDateTime.now().getDayOfMonth();
                    }
                    else
                    {
                        timeCheckerFrom += "-" + LocalDateTime.now().getDayOfMonth();
                        timeCheckerTo += "-" + LocalDateTime.now().getDayOfMonth();
                    }

                    timeCheckerFrom+=" "+bucketTimeFrom.getText().toString();
                    timeCheckerTo+=" "+bucketTimeTo.getText().toString();

                    //convert the starting time (from) and ending time (to) for TimePack
                    DateTimeFormatter formatter=TimePack.getFormatter();
                    LocalDateTime timeFrom = LocalDateTime.parse(
                            timeCheckerFrom,
                            formatter);
                    LocalDateTime timeTo = LocalDateTime.parse(
                            timeCheckerTo,
                            formatter);

                    TimePack time = new TimePack(timeFrom,
                            timeTo,
                            timeFrom.getMonthValue(),
                            Repetition.valueOf(repetitionSpinner.getSelectedItem().toString()),
                            new ArrayList<LocalDateTime>() );

                    //call the Repository method to create the bucket word in the DB.
                    Repository.createBucketWord(wordTxt.getText().toString(),time);
                    //Remove this fragment from the backstack and go back.
                    getParentFragmentManager().beginTransaction().remove(this).commit();
                }
                else //it it's not a bucket word then enter here
                {
                    //call the Repository method to create the priority word in the DB.
                    Repository.createPriorityWord(wordTxt.getText().toString(),seekBarPriority.getProgress());
                    //Remove this fragment from the backstack and go back.
                    getParentFragmentManager().beginTransaction().remove(this).commit();
                }

                //endregion
                break;
            case R.id.Btn_cancel_word://when the cancel was clicked this will be the chosen case
                //region cancel

                //Remove this fragment from the backstack and go back.
                getParentFragmentManager().beginTransaction().remove(this).commit();

                //endregion
                break;

            case R.id.TextView_from_time_word: //when the click to choose was clicked in the time from this will be the chosen case
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

                        bucketTimeFrom.setText(timeChecker);
                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();

                //endregion
                break;
            case R.id.TextView_to_time_word: //when the click to choose was clicked in the time to this will be the chosen case
                //region add time to

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

                        bucketTimeTo.setText(timeChecker);
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
        View view=inflater.inflate(R.layout.fragment_add_word, container, false);

        wordTxt = view.findViewById(R.id.EditText_word_content);
        bucketWordSwitch = view.findViewById(R.id.Switch_key_or_bucket);
        seekBarPriority = view.findViewById(R.id.SeekBar);
        priorityOnSeekBar = view.findViewById(R.id.TextView_priority_seekbar);
        repetitionSpinner = view.findViewById(R.id.Spinner_repetition_bucket);
        bucketTimeSetLayout = view.findViewById(R.id.bucketTimeLayout);
        bucketTimeFrom=view.findViewById(R.id.TextView_from_time_word);
        bucketTimeTo=view.findViewById(R.id.TextView_to_time_word);
        save = view.findViewById(R.id.Btn_save_word);
        cancel = view.findViewById(R.id.Btn_cancel_word);

        seekBarPriority.setProgress(0); //make sure the seekbar progress is set to 0



        //region OnClickListeners

        bucketTimeTo.setOnClickListener(this);
        bucketTimeFrom.setOnClickListener(this);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        bucketWordSwitch.setOnClickListener(v -> { //if the switch was clicked start action

            //if the switch is checked hide seekbar and the Textview of the progress and disable it,
            // show and unable the layout holding the bucket word elements, set bucket flag to true.
            if (bucketWordSwitch.isChecked()){
                seekBarPriority.setVisibility(View.GONE);
                seekBarPriority.setEnabled(false);
                priorityOnSeekBar.setVisibility(View.GONE);
                bucketTimeSetLayout.setVisibility(View.VISIBLE);
                isBucket=true;
            }
            else{ //if the switch is check was turned back hide the layout holding the bucket word elements, and show the seekbar and Textview of the priority word, set flag to false.
                seekBarPriority.setVisibility(View.VISIBLE);
                seekBarPriority.setEnabled(true);
                priorityOnSeekBar.setVisibility(View.VISIBLE);
                bucketTimeSetLayout.setVisibility(View.GONE);
                isBucket=false;
            }
        });

        seekBarPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                priorityOnSeekBar.setText("priority: " + progress + "/10"); //if seekbar was moved, updated what his progress is now at the TextView
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //endregion


        return view;
    }


}