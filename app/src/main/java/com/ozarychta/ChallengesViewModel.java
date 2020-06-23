package com.ozarychta;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ozarychta.model.Challenge;

import java.util.ArrayList;

public class ChallengesViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Challenge>> createdLiveData;
    private MutableLiveData<ArrayList<Challenge>> joinedLiveData;
    private MutableLiveData<ArrayList<Challenge>> allLiveData;

    public ChallengesViewModel() {
        createdLiveData = new MutableLiveData<>();
        joinedLiveData = new MutableLiveData<>();
        allLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Challenge>> getCreatedLiveData() {
        return createdLiveData;
    }

    public void setCreatedLiveData(ArrayList<Challenge> createdChallenges){
        createdLiveData.setValue(createdChallenges);
    }

    public MutableLiveData<ArrayList<Challenge>> getJoinedLiveData() {
        return joinedLiveData;
    }

    public void setJoinedLiveData(ArrayList<Challenge> joinedChallenges) {
        joinedLiveData.setValue(joinedChallenges);
    }

    public MutableLiveData<ArrayList<Challenge>> getAllLiveData() {
        return allLiveData;
    }

    public void setAllLiveData(ArrayList<Challenge> allChallenges) {
        allLiveData.setValue(allChallenges);
    }
}
