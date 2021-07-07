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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        if(textView==null)
            Toast.makeText(getApplication(), "null", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplication(), "ZZZZZZ", Toast.LENGTH_SHORT).show();
    */
        Repository.init();

        Repository.setUserName("matan");

        //region test insert and get word priority, TODO:NON CANON CODE DELETE!!!!

        Repository.createPriorityWord("test1",6);
        Task t=Repository.getAllPriorityWords();

        Map<String,Integer> m = new HashMap<>();
        t.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        m.putAll((Map<? extends String, ? extends Integer>) document.getData().entrySet());
                    }
                }
            }
        });
        TextView textView= new TextView(getApplication());
        LinearLayout linearLayout = findViewById(R.id.linearlayout);
        textView.setText("test");
        linearLayout.addView(textView);

        Toast.makeText(getApplication(), "", Toast.LENGTH_SHORT).show();
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