package com.ifalot.tripzor.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.astuetz.PagerSlidingTabStrip;
import com.ifalot.tripzor.ui.TripDetailPageAdapter;

public class TripDetail extends  FragmentActivity {

    private ViewPager viewPager;
    private TripDetailPageAdapter mAdapter;
    private PagerSlidingTabStrip slidingTabStrip;
    private Toolbar actionBar;
    private String[] tabs = { "Summary", "Costs" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        final Intent prev = getIntent();
        int tripId = prev.getIntExtra("TripId", -1);
        boolean owned = prev.getBooleanExtra("Owned", false);

        mAdapter = new TripDetailPageAdapter(getSupportFragmentManager(), tripId, owned, tabs);
        viewPager = (ViewPager) findViewById(R.id.trip_detail_pager);
        viewPager.setAdapter(mAdapter);
        slidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.detail_pager_title_strip);
        slidingTabStrip.setViewPager(viewPager);
        actionBar = (Toolbar) findViewById(R.id.trip_detail_toolbar);
        actionBar.setTitle(prev.getStringExtra("TripName"));
        actionBar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_back, null));
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TripDetail.this.onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
    }

}
