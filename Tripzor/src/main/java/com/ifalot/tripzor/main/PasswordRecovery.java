package com.ifalot.tripzor.main;

import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;

import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class PasswordRecovery extends AppCompatActivity implements ResultListener{

	protected ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_recovery);
		
		String email = getIntent().getStringExtra("email");
		final EditText et = (EditText) findViewById(R.id.email_recovery);
		et.setText(email);
		
		AppCompatButton b = (AppCompatButton) findViewById(R.id.recover_password_button);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("email", et.getText().toString());
				data.put("action", "ForgottenPassword");
				PostSender.sendPost(data, PasswordRecovery.this);
				dialog = ProgressDialog.show(PasswordRecovery.this, null, null);
			}
		});
		
	}

	@Override
	public void onResultsSucceeded(String result, List<String> listResult) {
		dialog.dismiss();
		if(result.equals(Codes.ERROR) || result.equals(Codes.USER_NOT_FOUND)){
			FastDialog.simpleDialog(this, "Error", "Something really strange happened, try again later", "CLOSE");
		}else if (result.equals(Codes.MAIL_NOT_SENT)){
			FastDialog.simpleDialog(this, "Error", "Our server was not able to send you an email, try again later", null);
		}else if (result.equals(Codes.DONE)){
			FastDialog.simpleDialog(this, "Success", "An email was sent to your account", 
					"CLOSE", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DataManager.deleteData("user");
					Intent intent = new Intent(PasswordRecovery.this, Login.class);
					dialog.dismiss();
					startActivity(intent);
					finish();			
				}
			});
		}	
		
	}
	
	

}
