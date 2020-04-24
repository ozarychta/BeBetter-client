package com.ozarychta;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ozarychta.model.User;

import java.util.ArrayList;

public class FriendsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<User>> followingLiveData;
    private MutableLiveData<ArrayList<User>> followersLiveData;

    public FriendsViewModel() {
        followingLiveData = new MutableLiveData<>();
        followersLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<User>> getFollowingLiveData() {
        return followingLiveData;
    }

    public void setFollowingLiveData(ArrayList<User> following){
        followingLiveData.setValue(following);
    }

    public MutableLiveData<ArrayList<User>> getFollowersLiveData() {
        return followersLiveData;
    }

    public void setFollowersLiveData(ArrayList<User> followers) {
        followersLiveData.setValue(followers);
    }
}


