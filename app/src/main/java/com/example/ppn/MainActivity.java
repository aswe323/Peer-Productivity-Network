package com.example.ppn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Repository.init();


        Repository.setUserName("matan");

        //region test insert and get word priority, TODO:NON CANON CODE DELETE!!!!

        Task t;

        Map<String,Integer> m = new HashMap<>();
        Map<String,TimePack> bw = new HashMap<>();
        //ArrayList<ActivityTask> at;
        Map<LocalDateTime,LocalDateTime> LDT= new HashMap<>();
        Map<LocalDate, Boolean> monthRange = new HashMap<>();
        Map<LocalDateTime, Boolean> overlapdMonth = new HashMap<>();
        for (int i = 1; i <= LocalDate.now().withMonth(7).lengthOfMonth(); i++)
            monthRange.put(LocalDate.now().withDayOfMonth(i),true);
        for (int i = 1; i <= LocalDate.now().withMonth(7).lengthOfMonth(); i++)
            overlapdMonth.put(LocalDateTime.now().withDayOfMonth(i),true);/**/

        //region priority word test
        //TODO: adding word is working, no do anything else plz
        /*Repository.createPriorityWord("XXXXX",1);
        Repository.createPriorityWord("1111111",50);
        //Repository.createPriorityWord("2222",42);


        t=Repository.getAllPriorityWords();
        t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            if(task.isSuccessful())
            {
                for (Map.Entry<String ,Object> entry:
                     task.getResult().getData().entrySet()) {
                    m.put(entry.getKey(),((Long) entry.getValue()).intValue());
                }
            }
            for( Map.Entry<String,Integer> entry:m.entrySet())
            {
                TextView textView= new TextView(getApplication());
                LinearLayout linearLayout = findViewById(R.id.linearlayout);
                textView.setTextSize(25);
                textView.setText(entry.getKey()+" priority:"+entry.getValue());
                linearLayout.addView(textView);
            }
        });*/
        //endregion

        //region bucket word test
        
        /*//TODO <<<<< bucket word test 1 start
        LDT.put(LocalDateTime.now().withHour(22).withMinute(23),LocalDateTime.now().withHour(23).withMinute(50));
        TimePack T=new TimePack(LDT,7,monthRange,overlapdMonth);
        Repository.createBucketWord("Bucket1", T); //TODO: crash when trying to set.
        *///TODO <<<<< bucket word test 1 end

        /*LDT.clear(); //TODO: second bucket word **take out of commenting only if the first one worked**
        LDT.put(LocalDateTime.now().withHour(18).withMinute(00),LocalDateTime.now().withHour(18).withMinute(05));
        T=new TimePack(LDT,7,monthRange,overlapdMonth);
        Repository.createBucketWord("buCKEt222", T);*/

        /*t=Repository.getBucketWords();
        t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            if(task.isSuccessful())
            {
                for (Map.Entry<String ,Object> entry:
                        task.getResult().getData().entrySet()) {
                    bw.put(entry.getKey(),((TimePack) entry.getValue()));
                }
            }
            for( Map.Entry<String,TimePack> entry:bw.entrySet())
            {
                TextView textView= new TextView(getApplication());
                LinearLayout linearLayout = findViewById(R.id.linearlayout);
                textView.setTextSize(25);
                textView.setText(entry.getKey()+" range: "+entry.getValue());
                linearLayout.addView(textView);
            }
        });*/
        //endregion bucket word test

        //region activity task test
        /* //TODO <<<<< activity task test 1 start
        LDT.put(LocalDateTime.now().withHour(22).withMinute(23),LocalDateTime.now().withHour(23).withMinute(50));
        TimePack T=new TimePack(LDT,7,monthRange,overlapdMonth);
        Repository.createActivityTask(1,MasloCategory.Esteem,"testing activity task",null,T); //TODO:crash at the constructor of the ActivityTask class
        */ //TODO <<<<< activity task test 1 end
        /*try {
            t=Repository.getAllUserActivityTasks();
            t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                if(task.isSuccessful())
                {
                    ArrayList<ActivityTask> at = ((ArrayList<ActivityTask>) task.getResult().get("matan" + "ActivityTasks")); //idk if this will crash, should get arraylist
                }

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }*/


        //endregion activity task test

        Toast.makeText(getApplication(), ""+m.isEmpty(), Toast.LENGTH_SHORT).show();
        //endregion

        final ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewpagerAdapter(getSupportFragmentManager(),getLifecycle()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}