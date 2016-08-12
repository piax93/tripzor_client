package com.ifalot.tripzor.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ifalot.tripzor.main.TripDetailCostFragment;
import com.ifalot.tripzor.main.TripDetailParticipantFragment;

public class TripDetailPageAdapter extends FragmentPagerAdapter {

    private int tripId;
    private boolean owned;
    private String[] tabs;

    public TripDetailPageAdapter(FragmentManager fm, int tripId, boolean owned, String[] tabs) {
        super(fm);
        this.tripId = tripId;
        this.owned = owned;
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int index) {
        if(index == 1){
            TripDetailCostFragment f = new TripDetailCostFragment();
            return f;
        }else {
            TripDetailParticipantFragment f = new TripDetailParticipantFragment();
            f.setOwned(owned);
            f.setTripId(tripId);
            return f;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

}

