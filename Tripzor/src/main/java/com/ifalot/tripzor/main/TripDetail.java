package com.ifalot.tripzor.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.ui.ParticipantsAdapter;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.utils.FastProgressDialog;
import com.ifalot.tripzor.utils.SwipeBack;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class TripDetail extends AppCompatActivity implements ResultListener, View.OnTouchListener {

    private GestureDetectorCompat mDetector;
    private MaterialDialog progressDialog;
    private ListView part_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        mDetector = new GestureDetectorCompat(this, new SwipeBack(this));
        progressDialog = FastProgressDialog.buildProgressDialog(this);
        part_list = (ListView) findViewById(R.id.participant_list);
        part_list.setOnTouchListener(this);

        Intent prev = getIntent();
        setTitle(prev.getStringExtra("TripName"));
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("action", "TripDetail");
        postData.put("tripid", String.valueOf(prev.getIntExtra("TripId", -1)));
        PostSender.sendPost(postData, this);
        progressDialog.show();
    }

    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {
        progressDialog.dismiss();
        if(result.equals(Codes.USER_NOT_FOUND) || result.equals(Codes.TRIP_NOT_FOUND)){
            error();
        } else {
            try {
                TextView place = (TextView) findViewById(R.id.location_tv);
                TextView start = (TextView) findViewById(R.id.startdate_tv);
                TextView end = (TextView) findViewById(R.id.enddate_tv);
                JSONObject jo = new JSONObject(result); // fields: tripid, name, place, start, end
                place.setText(jo.getString("place"));
                start.setText(jo.getString("start"));
                end.setText(jo.getString("end"));

                JSONArray participants = jo.getJSONArray("participants");
                String[] v = new String[participants.length()];
                for(int i = 0; i < v.length; i++) v[i] = participants.getString(i);
                // part_list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, v));
                part_list.setAdapter(new ParticipantsAdapter(this, participants));

            } catch (JSONException e) { error(); }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.mDetector.onTouchEvent(motionEvent);
        return view.onTouchEvent(motionEvent);
    }

    private void error(){
        FastDialog.simpleErrorDialog(this, "Error retrieving data", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                TripDetail.this.onBackPressed();
            }
        });
    }
}
