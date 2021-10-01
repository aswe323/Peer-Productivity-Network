package com.example.ppn;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 *
 * This is the main {{@link android.app.Activity}} class that holds our {@link ViewPager2} and present us the fragments of the UI
 *
 * In this class we set up out {@link TabLayout}, our {@link ViewPager2}, initialize the {@link Repository} and the {@link Menu} logout option
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TabLayoutMediator tabLayoutMediator;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.TabLayout);
        viewPager = findViewById(R.id.viewpager);

        Repository.setDefaultContext(this);
        Repository.setDefaultActivity(this);

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


        Repository.init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //infilating our menu
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //if the logout was selected it will log us out and relaunching the loading Activity
        switch (item.getItemId()) {
            case R.id.user_menu:
                signOut();
                startActivity(new Intent(this,loading.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}