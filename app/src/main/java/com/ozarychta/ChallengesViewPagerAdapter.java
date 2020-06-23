package com.ozarychta;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ozarychta.enums.ChallengeType;
import com.ozarychta.enums.FriendType;

public class ChallengesViewPagerAdapter extends FragmentPagerAdapter {
    private static final int ITEM_COUNT = 3;
    private Context ctx;

    public ChallengesViewPagerAdapter(FragmentManager fm, Context ctx) {
        super(fm);
        this.ctx = ctx;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return ChallengesFragment.newInstance(ChallengeType.CREATED);
        } else if(position == 1){
            return ChallengesFragment.newInstance(ChallengeType.JOINED);
        } else {
            return ChallengesFragment.newInstance(ChallengeType.ALL);
        }
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return ctx.getString(R.string.created);
        } else if(position == 1){
            return ctx.getString(R.string.joined);
        } else {
            return ctx.getString(R.string.all);
        }
    }
}
