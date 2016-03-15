package com.ifalot.tripzor.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.utils.FastProgressDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import java.util.HashMap;
import java.util.List;

public class PasswordChange extends AppCompatActivity implements ResultListener{

	protected MaterialDialog progressDialog;
	protected String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_change);
		
		Button confirm = (Button) findViewById(R.id.confirm_password);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				password = ((EditText) findViewById(R.id.new_password))
						.getText().toString();
				String passwordConfirm = ((EditText) findViewById(R.id.new_password_confirm))
						.getText().toString();
				String oldPassword = ((EditText) findViewById(R.id.old_password)).getText().toString();
				if(password.length() == 0 || passwordConfirm.length() == 0 || oldPassword.length() == 0){
					FastDialog.simpleErrorDialog(PasswordChange.this, "You left some fields empty");
				}else{
					if(password.equals(passwordConfirm)){
						progressDialog = FastProgressDialog.buildProgressDialog(PasswordChange.this);
						progressDialog.show();
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("action", "ChangePassword");
						data.put("oldPassword", oldPassword);
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
			FastDialog.simpleErrorDialog(this, "Old password is probably wrong");
		}else if(result.equals(Codes.DONE)){
			DataManager.updateValue("password", password);
			FastDialog.simpleDialog(this, "Password Changed", "You successfully changed your password", "CLOSE", new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
					PasswordChange.this.finish();
				}
			});
		}
		
	}
}