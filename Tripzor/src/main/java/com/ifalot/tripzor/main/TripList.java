package com.ifalot.tripzor.main;

import java.util.HashMap;
import java.util.List;

import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TripList extends AppCompatActivity implements ResultListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_list);	
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("action", "ListTrips");
		PostSender.sendPostML(data, this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.trip_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.password_change:
			Intent intent = new Intent(this, PasswordChange.class);
			startActivity(intent);
			break;
		case R.id.logout_item:
			DataManager.deleteData("user");
			startActivity(new Intent(this, Login.class));
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResultsSucceeded(String result, List<String> listResult) {
		if(result.equals(Codes.USER_NOT_FOUND)){
			FastDialog.simpleDialog(this, "ERROR", "An error occurred...",
					"CLOSE", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					System.exit(RESULT_CANCELED);
				}
			});
		}else{
			ListView lv = (ListView) findViewById(R.id.trip_list);
			if(listResult.size() == 0) {
				listResult.add("No trips linked to your account");
			}else{
				
			}
			lv.setAdapter(new ArrayAdapter<String>(this, 
					android.R.layout.simple_list_item_1, listResult));
		}
	}
}
