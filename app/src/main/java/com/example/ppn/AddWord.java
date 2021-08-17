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
 * A simple {@link Fragment} subclass.
 * Use the {@link AddWord#newInstance} factory method to
 * create an instance of this fragment.
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddWord() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddWord.
     */
    // TODO: Rename and change types and number of parameters
    public static AddWord newInstance(String param1, String param2) {
        AddWord fragment = new AddWord();
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

            case R.id.Btn_save_word:
                //region save
                if(!wordTxt.getText().toString().matches("[a-zA-Z]+")) {
                    Toast.makeText(getActivity(), "has to be one word and contain only letters", Toast.LENGTH_SHORT).show();
                    break;
                }

                if(isBucket)
                {
                    if(!bucketTimeFrom.getText().toString().matches("[0-9:]+") || !bucketTimeTo.getText().toString().matches("[0-9:]+"))
                    {
                        Toast.makeText(getActivity(), "choose time", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    String timeCheckerFrom="";
                    String timeCheckerTo="";

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

                    Repository.createBucketWord(wordTxt.getText().toString(),time);
                    getParentFragmentManager().beginTransaction().remove(this).commit();
                }
                else
                {
                    Repository.createPriorityWord(wordTxt.getText().toString(),seekBarPriority.getProgress());
                    getParentFragmentManager().beginTransaction().remove(this).commit();
                }

                //endregion
                break;
            case R.id.Btn_cancel_word:
                //region cancel

                getParentFragmentManager().beginTransaction().remove(this).commit();

                //endregion
                break;

            case R.id.TextView_from_time_word:
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

                        bucketTimeFrom.setText(timeChecker);
                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();

                //endregion
                break;
            case R.id.TextView_to_time_word:
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

        seekBarPriority.setProgress(0);



        //region OnClickListeners

        bucketTimeTo.setOnClickListener(this);
        bucketTimeFrom.setOnClickListener(this);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        bucketWordSwitch.setOnClickListener(v -> {
            if (bucketWordSwitch.isChecked()){
                seekBarPriority.setVisibility(View.GONE);
                seekBarPriority.setEnabled(false);
                priorityOnSeekBar.setVisibility(View.GONE);
                bucketTimeSetLayout.setVisibility(View.VISIBLE);
                isBucket=true;
            }
            else{
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
                priorityOnSeekBar.setText("priority: " + progress + "/10");//every time something changed, update the text of seekbar
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