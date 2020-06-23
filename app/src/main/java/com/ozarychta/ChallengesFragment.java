package com.ozarychta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ozarychta.enums.ChallengeType;
import com.ozarychta.model.ChallengeAdapter;
import com.ozarychta.model.Challenge;

import java.util.ArrayList;

public class ChallengesFragment extends Fragment {
    private static final String ARG_TYPE = "challengeType";

    private ChallengeType challengeType;
    private ArrayList<Challenge> challenges;

    private ChallengesViewModel challengesViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private TextView noResultsLabel;

    public ChallengesFragment() {
    }

    public static ChallengesFragment newInstance(ChallengeType challengeType) {
        ChallengesFragment fragment = new ChallengesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, challengeType);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeType = (ChallengeType) getArguments().getSerializable(ARG_TYPE);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        noResultsLabel = view.findViewById(R.id.noResultsLabel);
        noResultsLabel.setVisibility(View.GONE);

        challengesViewModel = new ViewModelProvider(requireActivity()).get(ChallengesViewModel.class);
        if(challengeType == ChallengeType.CREATED){
            challengesViewModel.getCreatedLiveData().observe(getViewLifecycleOwner(), challengesListUpdateObserver);
        } else if(challengeType == ChallengeType.JOINED){
            challengesViewModel.getJoinedLiveData().observe(getViewLifecycleOwner(), challengesListUpdateObserver);
        } else {
            challengesViewModel.getAllLiveData().observe(getViewLifecycleOwner(), challengesListUpdateObserver);
        }

    }

    Observer<ArrayList<Challenge>> challengesListUpdateObserver = new Observer<ArrayList<Challenge>>() {
        @Override
        public void onChanged(ArrayList<Challenge> challengeArrayList) {
            adapter = new ChallengeAdapter(challengeArrayList);
            recyclerView.setAdapter(adapter);
            if(challengeArrayList.isEmpty()){
                noResultsLabel.setVisibility(View.VISIBLE);
            } else {
                noResultsLabel.setVisibility(View.GONE);
            }
        }
    };
}
