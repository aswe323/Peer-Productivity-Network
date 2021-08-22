package com.example.ppn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.time.MonthDay;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage extends Fragment implements View.OnClickListener{

    private FirestoreRecyclerOptions<ActivityTask> options = new FirestoreRecyclerOptions.Builder<ActivityTask>()
            .setQuery(Repository.getActivityTaskCollection().orderBy("activityTaskID"), ActivityTask.class)
            .build();


    private Button addReminder;
    private RecyclerView recyclerView;
    private AlertDialog.Builder reminderDataDialog;

    private FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<ActivityTask,RecycleHolder>(options) {

        @SuppressLint("ResourceType")
        @Override
        protected void onBindViewHolder(@NonNull RecycleHolder holder, int position, @NonNull ActivityTask model) {

            if(model.getTimePack().getRelaventDatesNumbered().contains(MonthDay.now().getDayOfMonth())) { //if the activity is for today show it, else hide all and disable it

                holder.textTask.setText(model.getContent());
                holder.textTime.setText("" + model.getTimePack().getStartingTime() + " - " + model.getTimePack().getEndingTime());

                if(model.getComplete())
                {
                    holder.checkBox.setChecked(true);
                    holder.checkBox.setEnabled(false);
                }
                else
                {
                    holder.checkBox.setChecked(false);
                    holder.checkBox.setEnabled(true);
                }
                holder.checkBox.setOnClickListener(v -> {
                    if(holder.checkBox.isChecked())
                    {
                        holder.checkBox.setChecked(true);
                        holder.checkBox.setEnabled(false);

                        Repository.completeActivityTask(model.getActivityTaskID());

                    }
                });

                holder.btnDelete.setOnClickListener(v -> {
                    Repository.deleteActivivtyTask(model.getActivityTaskID()).addOnCompleteListener(task -> {
                        Repository.refreshNotifications();
                    });

                    Toast.makeText(getContext(), model.getContent() + " was deleted", Toast.LENGTH_SHORT).show();
                });

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

                holder.dataHolder.setOnClickListener(v -> {
                    /*DialogFragment dialogFragment...... //TODO:*************************/
                    Bundle bundle = new Bundle();
                    bundle.putInt("activityTaskID", model.getActivityTaskID());
                    bundle.putBoolean("isDataShow", true);
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
            else
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

        @NonNull
        @Override
        public RecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activitytask_recycleview, parent, false);

            return new RecycleHolder(view);
        }

    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homePage.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePage newInstance(String param1, String param2) {
        HomePage fragment = new HomePage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //unable the menu onOptionsItemSelected method to work in the fragment.

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {//recognizing what button was pushed
            case R.id.Btn_add_reminder:
                //region add reminder

                Bundle bundle= new Bundle();
                bundle.putBoolean("isEdit",false);
                AddReminder addReminder = new AddReminder();
                addReminder.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(((ViewGroup)(getView().getParent())).getId(), addReminder)
                        .setReorderingAllowed(true)
                        .addToBackStack("addReminder") // name can be null
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
        recyclerView = view.findViewById(R.id.recycleView_home);
        addReminder = view.findViewById(R.id.Btn_add_reminder);

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
                //Toast.makeText(this.getContext(), "this is not a bug... it's a feature, read comment to learn more", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), ""+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}