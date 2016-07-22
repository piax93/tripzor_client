package com.ifalot.tripzor.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.ifalot.tripzor.main.R;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import java.util.HashMap;
import java.util.List;

public class SearchParticipants extends AppCompatActivity implements ResultListener {

    private ListView lv;
    private SwipeRefreshLayout srl;
    private int tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_participants);

        srl = (SwipeRefreshLayout) findViewById(R.id.search_refresh);
        lv = (ListView) findViewById(R.id.part_search_list);
        EditText search_box = (EditText) findViewById(R.id.search_box);
        tripId = getIntent().getIntExtra("TripId", -1);

        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("action", "SearchUsers");
                postData.put("tripId", String.valueOf(tripId));
                postData.put("key", editable.toString());
                srl.setRefreshing(true);
                PostSender.sendPost(postData, SearchParticipants.this);
            }
        });

    }

    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {
        srl.setRefreshing(false);
    }
}
