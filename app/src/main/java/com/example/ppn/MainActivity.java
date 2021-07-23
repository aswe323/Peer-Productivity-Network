package com.example.ppn;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "MainActivity";
    ViewPager2 viewPager;
    TabLayout tabLayout;
    TabLayoutMediator tabLayoutMediator;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        final ArrayList<ActivityTask> at = new ArrayList<>();
        ArrayList<LocalDateTime> LDT = new ArrayList<>();
        ArrayList<String> relaventDates = new ArrayList<>();

        for (int i = 1; i <= LocalDate.now().withMonth(7).lengthOfMonth(); i++)
            relaventDates.add(LocalDateTime.now().withDayOfMonth(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
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

        //region bucket word test **** C R D is working *****

        /*LDT.add(LocalDateTime.now().withHour(22).withMinute(23));
        LDT.add(LocalDateTime.now().withHour(23).withMinute(50));
        TimePack T=new TimePack(LDT,7,Repetition.No_repeting,relaventDates);
        Repository.createBucketWord("Bucket1", T);*/

        /*LDT.clear();
        LDT.add(LocalDateTime.now().withHour(19).withMinute(00));
        LDT.add(LocalDateTime.now().withHour(23).withMinute(55));
        TimePack T=new TimePack(LDT,7,Repetition.No_repeting,relaventDates);
        Repository.createBucketWord("buCKEt222", T);

        //Repository.deleteBucketWord("Bucket1");
        //Repository.deleteBucketWord("buCKEt222");

        LDT.clear();
        LDT.add(LocalDateTime.now().withHour(20).withMinute(00));
        LDT.add(LocalDateTime.now().withHour(21).withMinute(30));
        TimePack T=new TimePack(LDT,7,Repetition.No_repeting,relaventDates);
        Repository.createBucketWord("TestBucket333", T);*/

        /*LDT.clear();
        LDT.add(LocalDateTime.now().withHour(10).withMinute(00));
        LDT.add(LocalDateTime.now().withHour(16).withMinute(30));
        TimePack T=new TimePack(LDT,7,Repetition.No_repeting,relaventDates);
        Repository.updateBucketWord("TestBucket333",T);*/

        /*Repository.deleteBucketWord("TestBucket333");

       t=Repository.getBucketWords();
        t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            if(task.isSuccessful())
            {
                for (Map.Entry<String ,Object> entry:
                        task.getResult().getData().entrySet()) {
                    Log.d(TAG, "onCreate: " + entry.getKey());
                    bw.put(entry.getKey(), new TimePack((HashMap<String, Object>) entry.getValue()));

                }
            }
            for( Map.Entry<String,TimePack> entry:bw.entrySet())
            {
                TextView textView= new TextView(getApplication());
                LinearLayout linearLayout = findViewById(R.id.linearlayout);
                textView.setTextSize(25);
                textView.setText(entry.getKey()+" range: "+entry.getValue().getStartingTime());
                linearLayout.addView(textView);
            }
        });*/
        //endregion bucket word test

        //region activity task test **** C R U D working ****

        Repository.deleteActivivtyTask(2);

        /*LDT.add(LocalDateTime.now().withHour(18).withMinute(03));
        LDT.add(LocalDateTime.now().withHour(18).withMinute(06));
        TimePack T=new TimePack(LDT,7,Repetition.No_repeting,relaventDates);
        SubActivity subActivity=new SubActivity("sub1AT1",1);
        ArrayList<SubActivity> sa=new ArrayList<>();
        sa.add(subActivity);
        Repository.createActivityTask(1,MasloCategory.Esteem,"testing activity task",sa,T);*/

        LDT.add(LocalDateTime.now().withHour(14).withMinute(24));
        LDT.add(LocalDateTime.now().withHour(14).withMinute(26));
        TimePack T=new TimePack(LDT,7,Repetition.every_friday,relaventDates);
        SubActivity subActivity=new SubActivity("subdog",1);
        ArrayList<SubActivity> sa=new ArrayList<>();
        sa.add(subActivity);
        Repository.createActivityTask(2,MasloCategory.Esteem,"thisActivityTaskTest",sa,T);

        //Repository.updateActivityTask(1,"content","WWW");
        //Repository.deleteActivivtyTask(1);

        /*try {
            //t=Repository.getAllUserActivityTasks();
            t=Repository.getThisDayActivityTasks();
            t.addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot entry:
                            task.getResult().getDocuments())
                        at.add(entry.toObject(ActivityTask.class));

                    for( ActivityTask activity:at)
                    {
                        TextView textView= new TextView(getApplication());
                        LinearLayout linearLayout = findViewById(R.id.linearlayout);
                        textView.setTextSize(25);
                        textView.setText(activity.getContent()+" sub: "+activity.getSubActivitys().get(0).getContent());
                        linearLayout.addView(textView);
                    }
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }*/


        //endregion activity task test

        //region notification test

        try {
            t=Repository.getAllUserActivityTasks();
            t.addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot entry:
                            task.getResult().getDocuments())
                        at.add(entry.toObject(ActivityTask.class));
                }
                Repository.setNotification(getApplication(),at.get(1),this);
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }/**/

        //endregion

        //Toast.makeText(getApplication(), ""+at.isEmpty(), Toast.LENGTH_SHORT).show();
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