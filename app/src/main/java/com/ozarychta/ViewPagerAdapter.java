package com.ozarychta;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ozarychta.enums.FriendType;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int ITEM_COUNT = 2;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return FriendsFragment.newInstance(FriendType.FOLLOWING);
        } else {
            return FriendsFragment.newInstance(FriendType.FOLLOWER);
        }
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
}
