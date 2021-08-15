package com.example.ppn;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

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

    private FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter(options) {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

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

        RecyclerView recyclerView = new RecyclerView(getContext());
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {//recognizing what button was pushed
            case R.id.Btn_add_reminder:
                //region add reminder

                AddReminder addReminder = new AddReminder();
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

        /*RecycleAdapter recycleAdapter = new RecycleAdapter(getActivity());
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));*/



        /*FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<ActivityTask,ActivityTaskHolder>(options) {
            @Override
            public void onBindViewHolder(ActivityTaskHolder holder, int position, ActivityTask model) {
                // Bind the Chat object to the ChatHolder
                // ...
            }

            @NonNull
            @Override
            public ActivityTaskHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.activitytask_recycleview, group, false);

                return new ActivityTaskHolder(view);
            }

        };*/

        addReminder.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
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