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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.ifalot.tripzor.main.R;
import com.ifalot.tripzor.ui.ParticipantsAdapter;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SearchParticipants extends AppCompatActivity implements ResultListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView lv;
    private SwipeRefreshLayout srl;
    private JSONArray users;
    private int tripId;
    private boolean adding;
    private static Semaphore mutex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_participants);

        mutex = new Semaphore(1);
        adding = false;
        srl = (SwipeRefreshLayout) findViewById(R.id.search_refresh);
        srl.setOnRefreshListener(this);
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
                getUsers(editable);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mutex.acquire();
                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("action", "AddParticipant");
                    postData.put("tripId", String.valueOf(tripId));
                    postData.put("participant", users.getJSONObject(position).getString("email"));
                    adding = true;
                    PostSender.sendPost(postData, SearchParticipants.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {
        srl.setRefreshing(false);
        if(result.equals(Codes.USER_NOT_FOUND) || result.equals(Codes.ERROR) || result.equals(Codes.TRIP_NOT_FOUND)){
            if(adding) FastDialog.simpleErrorDialog(this, "Error adding participant");
            else Log.d("SearchParticipants", "Error retrieving data");
        } else {
            if(adding){
                finish();
            } else {
                try {
                    users = new JSONArray(result);
                    lv.setAdapter(new ParticipantsAdapter(this, users, -1));
                } catch (JSONException e) {
                    ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
                    aa.add("No users found");
                    lv.setAdapter(aa);
                }
            }
        }
        mutex.release();
        lv.setClickable(true);
    }

    @Override
    public void onRefresh() {
        EditText et = (EditText) findViewById(R.id.search_box);
        getUsers(et.getText());
    }

    private void getUsers(Editable editable){
        try {
            if(mutex.tryAcquire(500, TimeUnit.MILLISECONDS)) {
                if(editable.length() == 0){
                    lv.setAdapter(new ArrayAdapter<String>(SearchParticipants.this, android.R.layout.simple_list_item_1));
                    mutex.release();
                    srl.setRefreshing(false);
                } else {
                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("action", "SearchUsers");
                    postData.put("tripId", String.valueOf(tripId));
                    postData.put("key", editable.toString());
                    srl.setRefreshing(true);
                    lv.setClickable(false);
                    PostSender.sendPost(postData, SearchParticipants.this);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
