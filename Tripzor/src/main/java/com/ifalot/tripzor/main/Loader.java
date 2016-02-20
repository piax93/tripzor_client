package com.ifalot.tripzor.main;
 
import java.util.List;

import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class Loader extends AppCompatActivity implements ResultListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loader);
		
		PostSender.initCookieStore();
		DataManager.init(this.getApplicationContext());
		PostSender.checkServer(this);
	}

	@Override
	public void onResultsSucceeded(String result, List<String> listResult) {
		if(result.equals("SERVER UP AND RUNNING")){
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
		}else{
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Server not available at the moment :-(\nTry again later");
			builder.setTitle("Oops");
			builder.setNeutralButton("CLOSE", new OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Loader.this.finish();
					System.exit(RESULT_CANCELED);
				}
			});
			builder.show();
		}		
	}

}
