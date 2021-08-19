package com.example.ppn;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TabLayoutMediator tabLayoutMediator;
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );


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
        //region testing
        /*Repository.addCommentToAnotherUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"1Avi");
        Repository.addCommentToAnotherUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"2test");
        Repository.addCommentToAnotherUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"3Comment");
        Repository.addCommentToAnotherUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"44444");
        Repository.addCommentToAnotherUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"5this");
        Repository.addCommentToAnotherUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"6that");
        Repository.addCommentToAnotherUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"7yeet");
        Repository.addCommentToAnotherUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"8ququq");*/
       /*FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Repository.createPriorityWord("hello",10);
        LocalDateTime startingTime = LocalDateTime.now().withDayOfMonth(25).withHour(19).withMinute(15);
        LocalDateTime endingTime = LocalDateTime.now().withDayOfMonth(25).withHour(19).withMinute(20);
        ArrayList<LocalDateTime> localDateTimes = new ArrayList<>();
        localDateTimes.add(startingTime);
        localDateTimes.add(endingTime);
        TimePack t= new TimePack(startingTime,endingTime,8,Repetition.every_monday,null);
         ArrayList<String > nonStirigifiedRelaventDates = new ArrayList<>();
        for (int i = 1; i <= LocalDate.now().withMonth(7).lengthOfMonth(); i++)
            nonStirigifiedRelaventDates.add(LocalDateTime.now().withDayOfMonth(i).format(TimePack.getFormatter()));




        Repository.createActivityTask(69,MasloCategory.Esteem,"bleep bloop",null,new TimePack(localDateTimes, YearMonth.now().getYear(),Repetition.every_satuday,nonStirigifiedRelaventDates));
        Repository.deleteActivivtyTask(69);
        Repository.createActivityTask(70,MasloCategory.Esteem,"bleep bloop",null,new TimePack(localDateTimes, YearMonth.now().getYear(),Repetition.every_satuday,nonStirigifiedRelaventDates));



        Repository.getAllUserActivityTasks().addOnCompleteListener(task -> {
            task.getResult().getDocuments().forEach(documentSnapshot -> Log.d(TAG, "onSignInResult: found -> " + documentSnapshot.toObject(ActivityTask.class).getActivityTaskID()));

        });*/

        //Repository.addUserToMyGroup(firebaseUser.getDisplayName());
/*        Repository.addCommentToAnotherUser(firebaseUser.getDisplayName(), "this is a comment");
        Repository.addCommentToAnotherUser(firebaseUser.getDisplayName(), "this is another comment");

        Repository.completeActivityTask(70);
        Repository.createActivityTask(70,MasloCategory.Esteem,"bleep bloop",null,new TimePack(localDateTimes, YearMonth.now().getYear(),Repetition.every_satuday,nonStirigifiedRelaventDates));
        Repository.createActivityTask(69,MasloCategory.Esteem,"bleep bloop",null,new TimePack(localDateTimes, YearMonth.now().getYear(),Repetition.every_satuday,nonStirigifiedRelaventDates));
        Repository.completeActivityTask(69);
        Repository.completeActivityTask(69);
        Repository.completeActivityTask(69);
        Repository.completeActivityTask(69);
        Repository.completeActivityTask(69);
       Repository.completeActivityTask(69); */

       Repository.getAllUserActivityTasks().addOnSuccessListener(queryDocumentSnapshots -> {
           List<DocumentSnapshot> activityTasks = queryDocumentSnapshots.getDocuments();
           activityTasks.forEach(documentSnapshot -> {
                ActivityTask    activityTask = documentSnapshot.toObject(ActivityTask.class);
               Log.d(TAG, "onCreate: found" + activityTask.getActivityTaskID());
           });
       });


        //endregion

        //Toast.makeText(this, ""+ FirebaseAuth.getInstance().getCurrentUser(), Toast.LENGTH_LONG).show(); used to show if logged in or not


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

    public void startSignInFlow() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());


        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d(TAG, "onSignInResult: signed in as " + user.getDisplayName());



        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu: //if clicked the search icon it will move the user into the search fragment
                /*FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                Search erf = new Search();
                ft.replace(getView().getId(), erf).commit();*/

                //getChildFragmentManager().beginTransaction().add(R.id.Fragment_Search,)
                return true;
            case R.id.user_menu: //TODO:this will allow the user to login\logout of the google user
                signOut();
                startActivity(new Intent(this,loading.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}