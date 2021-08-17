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
 * A simple {@link Fragment} subclass.
 * Use the {@link KeyWords#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyWords extends Fragment implements View.OnClickListener{

    private Button addWord;
    private RecyclerView wordRecyclerView;
    private RecyclerView bucketRecyclerView;
    private WordRecycleAdapter adapter;

    private Map<String,Integer> priorityWordMap;
    private Map<String,String> bucketWordMap;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public KeyWords() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment keyWords.
     */
    // TODO: Rename and change types and number of parameters
    public static KeyWords newInstance(String param1, String param2) {
        KeyWords fragment = new KeyWords();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//recognizing what button was pushed
            case R.id.Btn_add_word:
                //region add reminder

                AddWord addWord = new AddWord();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(((ViewGroup)(getView().getParent())).getId(), addWord)
                        .setReorderingAllowed(true)
                        .addToBackStack("addWord") // name can be null
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




        Repository.getPriorityWordsRef().addSnapshotListener((value, error) -> {

            Task t=Repository.getAllPriorityWords();
            t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                if(task.isSuccessful())
                {
                    for (Map.Entry<String ,Object> entry:
                            task.getResult().getData().entrySet()) {
                        priorityWordMap.put(entry.getKey(),((Long) entry.getValue()).intValue());
                    }
                    adapter = new WordRecycleAdapter(priorityWordMap,null,false);
                    wordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    wordRecyclerView.setAdapter(adapter);
                }
            });

        });

        Repository.getBucketWordsRef().addSnapshotListener((value, error) -> {

            Task t=Repository.getBucketWords();
            t.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                if(task.isSuccessful())
                {
                    for (Map.Entry<String ,Object> entry:
                            task.getResult().getData().entrySet()) {

                        TimePack timePack = new TimePack((HashMap<String, Object>) entry.getValue());
                        String time = "" + timePack.getStartingTime().split(" ")[1] + " - " + timePack.getEndingTime().split(" ")[1];
                        bucketWordMap.put(entry.getKey(),time);
                    }


                    adapter = new WordRecycleAdapter(null,bucketWordMap,true);
                    bucketRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    bucketRecyclerView.setAdapter(adapter);
                }
            });

        });

        //recyclerView.setAdapter(adapter);



        //region OnClickListeners

        addWord.setOnClickListener(this);

        //endregion

        return view;
    }


}