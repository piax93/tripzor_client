package com.ifalot.tripzor.main;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class AddTrip extends AppCompatActivity implements ResultListener, DatePickerDialog.OnDateSetListener {

    private static int DIALOG_ID = 13;
    private static int currentDateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        Button create = (Button) findViewById(R.id.create_trip_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<View> ets = findViewById(R.id.new_trip_form).getFocusables(View.FOCUS_DOWN);
                String[] fields = {"name", "place", "startDate", "endDate"};
                HashMap<String, String> postData = new HashMap<String, String>();
                for (int i = 0; i < ets.size(); i++) {
                    EditText et = (EditText) ets.get(i);
                    if(et.getText().length() == 0){
                        FastDialog.simpleErrorDialog(AddTrip.this, "You left some fields empty !");
                        return;
                    }
                    postData.put(fields[i], et.getText().toString());
                }
                postData.put("action", "AddTrip");
                PostSender.sendPost(postData, AddTrip.this);
            }
        });

        View.OnClickListener dateOCL = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                currentDateView = v.getId();
                showDialog(DIALOG_ID);
            }
        };

        View.OnFocusChangeListener dateOFCL = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    currentDateView = v.getId();
                    showDialog(DIALOG_ID);
                }
            }
        };

        findViewById(R.id.start_date).setOnClickListener(dateOCL);
        findViewById(R.id.start_date).setOnFocusChangeListener(dateOFCL);
        findViewById(R.id.end_date).setOnClickListener(dateOCL);
        findViewById(R.id.end_date).setOnFocusChangeListener(dateOFCL);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID){
            EditText et = (EditText) findViewById(currentDateView);
            int day, month, year;
            if(et.getText().length() == 0){
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }else{
                String[] tmp = et.getText().toString().split("-");
                year = Integer.parseInt(tmp[0]);
                month = Integer.parseInt(tmp[1]);
                day = Integer.parseInt(tmp[2]);
            }
            return new DatePickerDialog(this, this, year, month, day);
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {
        if(result.equals(Codes.DONE)){
            DataManager.insertData("new_trip", "true");
            finish();
        }else{
            FastDialog.simpleDialog(this, "ERROR", "An Error occurred during trip creation", "Cancel");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        EditText et = (EditText) findViewById(currentDateView);
        String b = String.valueOf(year) + '-' + (monthOfYear+1) + '-' + dayOfMonth;
        et.setText(b);
    }
}
