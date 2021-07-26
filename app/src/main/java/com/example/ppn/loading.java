package com.example.ppn;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class loading extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Thread startingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("logging", "running log in thread");
                if (FirebaseAuth.getInstance().getCurrentUser()==null) {
                    Log.d("logging", "required to log in ");
                    startSignInFlow();
                }
                else{
                    Log.d("logging", "logged");
                }

            }
        });
        startingThread.start();
        try {
            startActivity(new Intent(loading.this,MainActivity.class));
            finish();
            startingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

            /*/ ...
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            Repository.createPriorityWord("hello",10);
            LocalDateTime startingTime = LocalDateTime.now().withDayOfMonth(25).withHour(19).withMinute(15);
            LocalDateTime endingTime = LocalDateTime.now().withDayOfMonth(25).withHour(19).withMinute(20);
            ArrayList<LocalDateTime> localDateTimes = new ArrayList<>();
            localDateTimes.add(startingTime);
            localDateTimes.add(endingTime);
            ArrayList<String > nonStirigifiedRelaventDates = new ArrayList<>();
            for (int i = 1; i <= LocalDate.now().withMonth(7).lengthOfMonth(); i++)
                nonStirigifiedRelaventDates.add(LocalDateTime.now().withDayOfMonth(i).format(TimePack.getFormatter()));
            Repository.createBucketWord("hello",new TimePack(localDateTimes, YearMonth.now().getYear(),Repetition.Every_24_hours,nonStirigifiedRelaventDates));
            Repository.deletePriorityWord("hello");
            Repository.deleteBucketWord("hello");

            Repository.createActivityTask(69,MasloCategory.Esteem,"bleep bloop",null,new TimePack(localDateTimes, YearMonth.now().getYear(),Repetition.every_satuday,nonStirigifiedRelaventDates));
            Repository.deleteActivivtyTask(69);
            Repository.createActivityTask(70,MasloCategory.Esteem,"bleep bloop",null,new TimePack(localDateTimes, YearMonth.now().getYear(),Repetition.every_satuday,nonStirigifiedRelaventDates));

            Repository.getAllUserActivityTasks().addOnCompleteListener(task -> {
                task.getResult().getDocuments().forEach(documentSnapshot -> Log.d(TAG, "onSignInResult: found -> " + documentSnapshot.toObject(ActivityTask.class).getActivityTaskID()));

            });*/

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
}