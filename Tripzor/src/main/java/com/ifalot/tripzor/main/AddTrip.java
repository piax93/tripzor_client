package com.ifalot.tripzor.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.utils.FastProgressDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import java.util.*;

@SuppressWarnings("deprecation")
public class AddTrip extends AppCompatActivity implements ResultListener, DatePickerDialog.OnDateSetListener {

    private static int DIALOG_ID = 13;
    private static int currentDateView;
    private MaterialDialog progressDialog;

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
                if(compareDate ( ((EditText)ets.get(2)).getText().toString(), ((EditText)ets.get(3)).getText().toString()) ) {
                    postData.put("action", "AddTrip");
                    progressDialog = FastProgressDialog.buildProgressDialog(AddTrip.this);
                    progressDialog.show();
                    PostSender.sendPost(postData, AddTrip.this);
                }else{
                    FastDialog.simpleErrorDialog(AddTrip.this, "Start date must be before end date");
                }
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

        EditText startDate = (EditText) findViewById(R.id.start_date);
        EditText endDate = (EditText) findViewById(R.id.end_date);
        startDate.setInputType(InputType.TYPE_NULL);
        endDate.setInputType(InputType.TYPE_NULL);
        startDate.setOnClickListener(dateOCL);
        startDate.setOnFocusChangeListener(dateOFCL);
        endDate.setOnClickListener(dateOCL);
        endDate.setOnFocusChangeListener(dateOFCL);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID){
            EditText et = (EditText) findViewById(currentDateView);
            int day, month, year;
            if(et.getText().length() == 0){
                EditText sd = (EditText) findViewById(R.id.start_date);
                if(currentDateView == R.id.end_date && sd.getText().length() > 4) {
                    String[] tmp = sd.getText().toString().split("-");
                    year = Integer.parseInt(tmp[0]);
                    month = Integer.parseInt(tmp[1]) - 1;
                    day = Integer.parseInt(tmp[2]);
                } else {
                    Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                }
            }else{
                String[] tmp = et.getText().toString().split("-");
                year = Integer.parseInt(tmp[0]);
                month = Integer.parseInt(tmp[1]) - 1;
                day = Integer.parseInt(tmp[2]);
            }
            DatePickerDialog dpd = DatePickerDialog.newInstance(AddTrip.this, year, month, day);
            dpd.setVibrate(false);
            dpd.show(AddTrip.this.getSupportFragmentManager(), et.getHint().toString().split(" ")[0] + " Date");
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {
        progressDialog.dismiss();
        if(result.equals(Codes.DONE)){
            DataManager.insertData("new_trip", "true");
            finish();
        }else{
            FastDialog.simpleDialog(this, "ERROR", "An Error occurred during trip creation", "CLOSE");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        if(compareDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                year, monthOfYear, dayOfMonth)) {
            EditText et = (EditText) findViewById(currentDateView);
            String b = String.valueOf(year) + '-' + (monthOfYear + 1) + '-' + dayOfMonth;
            et.setText(b);
            if(currentDateView == R.id.start_date) findViewById(R.id.end_date).performClick();
        }else{
            FastDialog.simpleErrorDialog(this, "You cannot select dates in the past");
        }
    }

    private boolean compareDate(String start, String end){
        Scanner startScn = new Scanner(start);
        Scanner endScn = new Scanner(end);
        startScn.useDelimiter("-");
        endScn.useDelimiter("-");
        return compareDate(startScn.nextInt(), startScn.nextInt(), startScn.nextInt(),
                endScn.nextInt(), endScn.nextInt(), endScn.nextInt());
    }

    private boolean compareDate(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay){
        return endYear > startYear
                || (endYear == startYear && endMonth > startMonth)
                || (endYear == startYear && endMonth == startMonth && endDay >= startDay);
    }
}
