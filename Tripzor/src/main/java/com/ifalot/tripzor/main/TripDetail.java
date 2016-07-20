package com.ifalot.tripzor.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.utils.FastProgressDialog;
import com.ifalot.tripzor.utils.SwipeBack;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class TripDetail extends AppCompatActivity implements ResultListener {

    private GestureDetectorCompat mDetector;
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        mDetector = new GestureDetectorCompat(this, new SwipeBack(this));
        progressDialog = FastProgressDialog.buildProgressDialog(this);

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

    private void error(){
        FastDialog.simpleErrorDialog(this, "Error retrieving data", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                TripDetail.this.onBackPressed();
            }
        });
    }

}
