package com.example.ppn;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.time.MonthDay;

/**
 *
 * A simple {@link Fragment} subclass used to show the home page of the UI.<br><br>
 *
 * The home page is used to be the first UI element interacting with the user after he logged in with his Google account to the app,
 * the class is called automatically by the {@link ViewpagerAdapter} class (which is in the {@link MainActivity}) when the app and login process been completed.<br><br>
 *
 * It will show the user a {@link RecyclerView} showing the reminders in under his user in the FireBase,
 * the reminders are  {@link ActivityTask} objects and stored in the database (see the {@link Repository} class for info on the backend).<br><br>
 *
 * In addition a button to add new reminder will be presented at the bottom of the RecyclerView, at click on the button the {@link HomePage} fragment will be
 * replaced with {@link AddReminder} fragment.
 *
 */
public class HomePage extends Fragment implements View.OnClickListener{

    //The Button and RecyclerView variables
    private Button addReminder;
    private RecyclerView recyclerView;
    //options is a variable to dynamically fetch data from the firebase and show it in the RecyclerView.
    //The adapter used to set the elements of the holder class the necessary data and click actions.
    private FirestoreRecyclerOptions<ActivityTask> options = new FirestoreRecyclerOptions.Builder<ActivityTask>()
            .setQuery(Repository.getActivityTaskCollection().orderBy("activityTaskID"), ActivityTask.class)
            .build();
    private FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<ActivityTask,RecycleHolder>(options) {

        @SuppressLint("ResourceType")
        @Override
        protected void onBindViewHolder(@NonNull RecycleHolder holder, int position, @NonNull ActivityTask model) {

            if(model.getTimePack().getRelaventDatesNumbered().contains(MonthDay.now().getDayOfMonth())) { //If the ActivityTask is for today show it, else hide all and disable it.
                //Set the text of the reminder and the time range.
                holder.textTask.setText(model.getContent());
                holder.textTime.setText("" + model.getTimePack().getStartingTime() + " - " + model.getTimePack().getEndingTime());

                if(model.getComplete()) //If the reminder was done today so disable the CheckBox and mark it as checked, else make sure it's enabled and marked unchecked.
                {
                    holder.checkBox.setChecked(true);
                    holder.checkBox.setEnabled(false);
                }
                else
                {
                    holder.checkBox.setChecked(false);
                    holder.checkBox.setEnabled(true);
                }

                //If the CheckBox was clicked and it now it marked as checked then make sure it's checked and disable it, call the Repository method to mark it as checked in the DB.
                holder.checkBox.setOnClickListener(v -> {
                    if(holder.checkBox.isChecked())
                    {
                        holder.checkBox.setChecked(true);
                        holder.checkBox.setEnabled(false);

                        Repository.completeActivityTask(model.getActivityTaskID());

                    }
                });

                //If the delete button was clicked call the Repository method to delete the reminder,
                //when completed deleting the reminder call the Repository method to update the notifications.
                holder.btnDelete.setOnClickListener(v -> {
                    Repository.deleteActivityTask(model.getActivityTaskID()).addOnCompleteListener(task -> {
                        Repository.refreshNotifications();
                    });

                    Toast.makeText(getContext(), model.getContent() + " was deleted", Toast.LENGTH_SHORT).show();
                });

                //if the edit button was clicked create a Bundle to hold the reminder ID and an edit flag,
                //send it to the AddReminder fragment and replace HomePage fragment to AddReminder Fragment.
                holder.btnEdit.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("activityTaskID", model.getActivityTaskID());
                    bundle.putBoolean("isEdit", true);
                    AddReminder addReminder = new AddReminder();
                    addReminder.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(((ViewGroup) (getView().getParent())).getId(), addReminder)
                            .setReorderingAllowed(true)
                            .addToBackStack("addReminder") // name can be null
                            .commit();

                });
                //If the layout holding the reminder content and time was clicked create a Bundle to hold the reminder ID and an dataShow flag,
                //send it to the AddReminder fragment and replace HomePage fragment to AddReminder Fragment.
                holder.dataHolder.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("activityTaskID", model.getActivityTaskID());
                    bundle.putBoolean("isDataShow", true);
                    //create the fragment that we will replace to and then replace the current fragment to it
                    AddReminder addReminder = new AddReminder();
                    addReminder.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(((ViewGroup) (getView().getParent())).getId(), addReminder)
                            .setReorderingAllowed(true)
                            .addToBackStack("addReminder") // name can be null
                            .commit();

                });
            }

            else //If the reminders is not for today make it inviable and disable all the clickiness of the elements.
            {
                holder.textTask.setVisibility(View.GONE);
                holder.textTask.setEnabled(false);
                holder.textTime.setVisibility(View.GONE);
                holder.textTime.setEnabled(false);
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnDelete.setEnabled(false);
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnEdit.setEnabled(false);
                holder.checkBox.setVisibility(View.GONE);
                holder.checkBox.setEnabled(false);
            }

        }

        //an inner older class to inflate the layout of the RecyclerView
        @NonNull
        @Override
        public RecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activitytask_recycleview, parent, false);

            return new RecycleHolder(view);
        }

    };

    public HomePage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {//recognizing what button was pushed
            case R.id.Btn_add_reminder: //when the add reminder button was clicked this will be the chosen case
                //region add reminder

                //created a bundle to pass that edit flag is false
                Bundle bundle= new Bundle();
                bundle.putBoolean("isEdit",false);
                //create the fragment that we will replace to and then replace the current fragment to it
                AddReminder addReminder = new AddReminder();
                addReminder.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(((ViewGroup)(getView().getParent())).getId(), addReminder)
                        .setReorderingAllowed(true)
                        .addToBackStack("addReminder")
                        .commit();

                //endregion
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        //connect the elements in the layout to the values
        recyclerView = view.findViewById(R.id.recycleView_home);
        addReminder = view.findViewById(R.id.Btn_add_reminder);

        //set the RecyclerView element to his adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        RecyclerView recyclerView = new RecyclerView(getContext());
        adapter.startListening();
        recyclerView.setAdapter(adapter);

        //region OnClickListeners
        addReminder.setOnClickListener(this);

        //endregion

        return view;
    }


}