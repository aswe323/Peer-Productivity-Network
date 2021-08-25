package com.example.ppn;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * A simple {@link Fragment} subclass used to show the priority and bucket words of the UI.<br><br>
 *
 * The word management UI page is open when the user is clicking the <b><i>word management</i></b> tab in the {@link android.widget.TableLayout}.<br><br>
 *
 * It will show the user two {@link RecyclerView} elements, one for priority words, other for bucket words that are in the DB.<br><br>
 *
 * At the bottom of the page there's an the <b><i>Add new word</i></b> button that when clicked replacing the page to {@link AddWord} page.
 *
 */
public class KeyWords extends Fragment implements View.OnClickListener{

    private Button addWord;
    private RecyclerView wordRecyclerView;
    private RecyclerView bucketRecyclerView;
    private WordRecycleAdapter adapter;

    private Map<String,Integer> priorityWordMap; //Holds the priority words from the DB
    private Map<String,String> bucketWordMap; //Holds the bucket words from the DB


    public KeyWords() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//recognizing what button was pushed
            case R.id.Btn_add_word: //when the Add new word button was clicked this will be the chosen case
                //region add reminder

                //create the fragment that we will replace to and then replace the current fragment to it
                AddWord addWord = new AddWord();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(((ViewGroup)(getView().getParent())).getId(), addWord)
                        .setReorderingAllowed(true)
                        .addToBackStack("addWord")
                        .commit();

                //endregion
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_key_words, container, false);

        priorityWordMap = new HashMap<>();
        bucketWordMap = new HashMap<>();
        addWord = view.findViewById(R.id.Btn_add_word);
        wordRecyclerView = view.findViewById(R.id.priorityWordRecycler);
        bucketRecyclerView = view.findViewById(R.id.bucketWordRecycler);



        //on and data manipulation the database of the words will recall this section and get the priority words from the DB
        Repository.getPriorityWordsRef().addSnapshotListener((value, error) -> {

            Task t=Repository.getAllPriorityWords();
            t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                if(task.isSuccessful())
                {
                    //add the word to the map
                    for (Map.Entry<String ,Object> entry:
                            task.getResult().getData().entrySet()) {
                        priorityWordMap.put(entry.getKey(),((Long) entry.getValue()).intValue());
                    }
                    //creating the RecyclerView for the priority words and their adapter and connecting between them
                    adapter = new WordRecycleAdapter(priorityWordMap,null,false);
                    wordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    wordRecyclerView.setAdapter(adapter);
                }
            });

        });

        //on and data manipulation the database of the words will recall this section and get the bucket words from the DB
        Repository.getBucketWordsRef().addSnapshotListener((value, error) -> {

            Task t=Repository.getBucketWords();
            t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                if(task.isSuccessful())
                {
                    //add the word to the map
                    for (Map.Entry<String ,Object> entry:
                            task.getResult().getData().entrySet()) {

                        TimePack timePack = new TimePack((HashMap<String, Object>) entry.getValue());
                        String time = "" + timePack.getStartingTime().split(" ")[1] + " - " + timePack.getEndingTime().split(" ")[1];
                        bucketWordMap.put(entry.getKey(),time);
                    }
                    //creating the RecyclerView for the bucket words and their adapter and connecting between them
                    adapter = new WordRecycleAdapter(null,bucketWordMap,true);
                    bucketRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    bucketRecyclerView.setAdapter(adapter);
                }
            });

        });

        //region OnClickListeners

        addWord.setOnClickListener(this);

        //endregion

        return view;
    }


}