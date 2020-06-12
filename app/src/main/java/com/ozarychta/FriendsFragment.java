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

import com.ozarychta.enums.FriendType;
import com.ozarychta.model.FriendAdapter;
import com.ozarychta.model.User;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    private static final String ARG_TYPE = "friendType";

    private FriendType friendType;
    private ArrayList<User> friends;

    private FriendsViewModel friendsViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private TextView noResultsLabel;

    public FriendsFragment() {
    }

    public static FriendsFragment newInstance(FriendType friendType) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, friendType);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friendType = (FriendType) getArguments().getSerializable(ARG_TYPE);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.friends_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        noResultsLabel = view.findViewById(R.id.noResultsLabel);
        noResultsLabel.setVisibility(View.GONE);

        friendsViewModel = new ViewModelProvider(requireActivity()).get(FriendsViewModel.class);
        if(friendType==FriendType.FOLLOWING){
            friendsViewModel.getFollowingLiveData().observe(getViewLifecycleOwner(), friendsListUpdateObserver);
        } else {
            friendsViewModel.getFollowersLiveData().observe(getViewLifecycleOwner(), friendsListUpdateObserver);
        }

    }

    Observer<ArrayList<User>> friendsListUpdateObserver = new Observer<ArrayList<User>>() {
        @Override
        public void onChanged(ArrayList<User> userArrayList) {
            adapter = new FriendAdapter(userArrayList);
            recyclerView.setAdapter(adapter);
            if(userArrayList.isEmpty()){
                noResultsLabel.setVisibility(View.VISIBLE);
            } else {
                noResultsLabel.setVisibility(View.GONE);
            }
        }
    };
}
