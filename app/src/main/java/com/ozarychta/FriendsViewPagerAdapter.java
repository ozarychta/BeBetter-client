package com.ozarychta;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ozarychta.enums.FriendType;

public class FriendsViewPagerAdapter extends FragmentPagerAdapter {
    private static final int ITEM_COUNT = 2;
    private Context ctx;

    public FriendsViewPagerAdapter(FragmentManager fm, Context ctx) {
        super(fm);
        this.ctx = ctx;
    }


    @NonNull @Override
    public Fragment getItem(int position) {
        if(position==0){
            return FriendsFragment.newInstance(FriendType.FOLLOWING);
        } else {
            return FriendsFragment.newInstance(FriendType.FOLLOWER);
        }
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return ctx.getString(R.string.following);
        } else {
            return ctx.getString(R.string.follower);
        }
    }
}
