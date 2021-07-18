package com.example.ppn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

    ViewPager2 viewPager;
    TabLayout tabLayout;
    TabLayoutMediator tabLayoutMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.TabLayout);
        viewPager = findViewById(R.id.viewpager);

        Repository.init();


        Repository.setUserName("matan");

        //region test insert and get word priority, TODO:NON CANON CODE DELETE!!!!

        Task t;

        Map<String,Integer> m = new HashMap<>();
        Map<String,TimePack> bw = new HashMap<>();
        //ArrayList<ActivityTask> at;
        ArrayList<LocalDateTime> LDT = new ArrayList<>();
        ArrayList<LocalDateTime> relaventDates = new ArrayList<>();

        for (int i = 1; i <= LocalDate.now().withMonth(7).lengthOfMonth(); i++)
            relaventDates.add(LocalDateTime.now().withDayOfMonth(i));
        /*for (int i = 1; i <= LocalDate.now().withMonth(7).lengthOfMonth(); i++)
            monthRange.put(LocalDate.now().withDayOfMonth(i),true);
        for (int i = 1; i <= LocalDate.now().withMonth(7).lengthOfMonth(); i++)
            overlapdMonth.put(LocalDateTime.now().withDayOfMonth(i),true);*/

        //region priority word test  ****C R U D  is working, word priority is working as intended.*****
        /*//Repository.createPriorityWord("XXXXX",1);
        //Repository.createPriorityWord("1111111",50);
        //Repository.createPriorityWord("2222",42);
        //Repository.updatePriorityWord("XXXXX",17);
        //Repository.deletePriorityWord("XXXXX");

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

        //region bucket word test **** C is working *****

        /*LDT.add(LocalDateTime.now().withHour(22).withMinute(23));
        LDT.add(LocalDateTime.now().withHour(23).withMinute(50));
        TimePack T=new TimePack(LDT,7,Repetition.No_repeting,relaventDates);
        Repository.createBucketWord("Bucket1", T);*/

        /*LDT.clear();
        LDT.add(LocalDateTime.now().withHour(19).withMinute(00));
        LDT.add(LocalDateTime.now().withHour(23).withMinute(55));
        TimePack T=new TimePack(LDT,7,Repetition.No_repeting,relaventDates);
        Repository.createBucketWord("buCKEt222", T);*/

        //Repository.updateBucketWord()

        t=Repository.getBucketWords();
        t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            if(task.isSuccessful())
            {
                for (Map.Entry<String ,Object> entry:
                        task.getResult().getData().entrySet()) {
                    bw.put(entry.getKey(),new TimePack((HashMap<String, Object>) entry.getValue()));
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
        });/**/
        //endregion bucket word test

        //region activity task test
         //TODO <<<<< activity task test 1 start
        /*LDT.put(LocalDateTime.now().withHour(22).withMinute(23),LocalDateTime.now().withHour(23).withMinute(50));
        TimePack T=new TimePack(LDT,7,monthRange,overlapdMonth);
        Repository.createActivityTask(1,MasloCategory.Esteem,"testing activity task",null,T); //TODO:crash at the constructor of the ActivityTask class
         //TODO <<<<< activity task test 1 end
        try {
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


        viewPager.setAdapter(new ViewpagerAdapter(getSupportFragmentManager(),getLifecycle()));

        tabLayoutMediator = new TabLayoutMediator(tabLayout,viewPager,true,
                (tab,position)->{
                    switch (position) {
                        case 0:
                            tab.setText("Home");
                            break;
                        case 1:
                            tab.setText("Word Management");
                            break;
                        case 2:
                            tab.setText("Group And Points");
                            break;
                        default:
                            return;
                    }
                });
        tabLayoutMediator.attach();


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