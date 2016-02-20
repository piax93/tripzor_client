package com.ifalot.tripzor.main;

import java.util.HashMap;
import java.util.List;

import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class PasswordChange extends AppCompatActivity implements ResultListener{

	protected ProgressDialog progressDialog;
	protected String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_change);
		
		AppCompatButton confirm = (AppCompatButton) findViewById(R.id.confirm_password);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				password = ((EditText) findViewById(R.id.new_password))
						.getText().toString();
				String passwordConfirm = ((EditText) findViewById(R.id.new_password_confirm))
						.getText().toString();
				if(password.length() == 0 || passwordConfirm.length() == 0){
					FastDialog.simpleErrorDialog(PasswordChange.this, "You left some fields empty");
				}else{
					if(password.equals(passwordConfirm)){
						progressDialog = ProgressDialog.show(PasswordChange.this, "", "");
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("action", "ChangePassword");
						data.put("oldPassword", DataManager.selectData("password"));
						data.put("newPassword", password);
						PostSender.sendPost(data, PasswordChange.this);
					}else{
						FastDialog.simpleErrorDialog(PasswordChange.this, "Passwords do not match");
					}
				}
			}
		});
	}

	@Override
	public void onResultsSucceeded(String result, List<String> listResult) {
		progressDialog.dismiss();
		if(result.equals(Codes.USER_NOT_FOUND)){
			FastDialog.simpleErrorDialog(this, "Server Error");
		}else if(result.equals(Codes.ERROR)){
			FastDialog.simpleErrorDialog(this, "Bad things");
		}else if(result.equals(Codes.DONE)){
			DataManager.updateValue("password", password);
			finish();
		}
		
	}
}
