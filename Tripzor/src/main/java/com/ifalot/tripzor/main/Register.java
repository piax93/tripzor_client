package com.ifalot.tripzor.main;

import java.util.HashMap;
import java.util.List;

import android.widget.Button;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Register extends AppCompatActivity implements ResultListener {

	protected HashMap<String, String> form = new HashMap<String, String>();
	protected ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		final LinearLayout layout = (LinearLayout) findViewById(R.id.email_register_form);
		Intent intent = getIntent();
		((EditText) layout.getChildAt(0)).setText(intent.getStringExtra("email"));
		((EditText) layout.getChildAt(1)).setText(intent.getStringExtra("password"));
		
		Button b = (Button) findViewById(R.id.register_button);
		b.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String[] labels = {"email", "password", "password_re", "name", "surname", "cellPhone"};
				for (int i = 0; i < labels.length; i++) {
					form.put(labels[i], ((EditText)layout.getChildAt(i))
							.getText().toString());
				}
				register();
			}
		});
		
	}
	
	protected void register(){
		if(form.get("email").length() == 0 || form.get("password").length() == 0){
			FastDialog.simpleDialog(this, "Error", "Email and password are required!", "CLOSE");
		}else{
			if(form.get("password").equals(form.get("password_re"))){
				form.remove("password_re");
				form.put("action", "UserRegistration");
				PostSender.sendPost(form, this);
				dialog = ProgressDialog.show(this, null, null);
			}else{
				FastDialog.simpleDialog(this, "Error", "Passwords do not match", "CLOSE");
			}
		}
	}

	@Override
	public void onResultsSucceeded(String result, List<String> listResult) {
		dialog.dismiss();
		if(result.equals(Codes.DONE)){
			DataManager.insertData("user", form.get("email"));
			DataManager.insertData("password", form.get("password"));
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
		}else if(result.equals(Codes.USER_ALREADY_PRESENT)){
			FastDialog.yesNoDialog(this, "User already present", "This email address is already registered on our server, would you like to recover you password?",
					new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Register.this, null);
							intent.putExtra("email", form.get("email"));
							startActivity(intent);
						}
					},
					new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();							
						}
					});
		}else if(result.equals(Codes.ERROR)){
			FastDialog.simpleDialog(this, "Error", "Something bad happened on our server, try again later", "CLOSE");
		}
	}

}
