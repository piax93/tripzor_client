package com.ifalot.tripzor.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.utils.FastProgressDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Login extends AppCompatActivity implements ResultListener{

	private String email;
	private String password;
	private boolean freshLogin = false;
	private MaterialDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewById(R.id.login_form_subcontainer).setVisibility(View.INVISIBLE);

		if (DataManager.isPresent("user")) {
			progressDialog = FastProgressDialog.buildProgressDialog(this);
			progressDialog.show();
			email = DataManager.selectData("user");
			password = DataManager.selectData("password");
			login(email, password);
		} else {
			findViewById(R.id.login_form_subcontainer).setVisibility(View.VISIBLE);
			final EditText emailEditText = (EditText) findViewById(R.id.email);
			final EditText passwordEditText = (EditText) findViewById(R.id.password);
			Button loginButton = (Button) findViewById(R.id.email_sign_in_button);
			Button registerButton = (Button) findViewById(R.id.register_button);
			TextView forgotPassword = (TextView) findViewById(R.id.forgot_password_button);

			loginButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					email = emailEditText.getText().toString();
					password = passwordEditText.getText().toString();
					if (email.length() == 0 || password.length() == 0) {
						FastDialog.simpleDialog(Login.this, "Error", "You left some fields empty", "CLOSE");
					} else {
						progressDialog = FastProgressDialog.buildProgressDialog(Login.this);
						progressDialog.show();
						freshLogin = true;
						login(email, password);
					}
				}
			});

			registerButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), Register.class);
					intent.putExtra("email", emailEditText.getText().toString());
					intent.putExtra("password", passwordEditText.getText().toString());
					startActivity(intent);
				}
			});

			forgotPassword.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), PasswordRecovery.class);
					intent.putExtra("email", emailEditText.getText().toString());
					startActivity(intent);
				}
			});

		}
		findViewById(R.id.forgot_password_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Login.this, Loader.class));
			}
		});
	}
	
	protected void login(String email, String password) {
		HashMap<String, String> loginData = new HashMap<String, String>();
		loginData.put("action", "UserLogin");
		loginData.put("email", email);
		loginData.put("password", password);
		PostSender.sendPost(loginData, this);
	}
	
	@Override
	public void onResultsSucceeded(JSONObject res) throws JSONException {
		String result = res.getString("result");
		if(result.equals(Codes.DONE)){
			if(freshLogin){
				DataManager.insertData("user", email);
				DataManager.insertData("password", password);
			}
			Intent intent = new Intent(Login.this, TripList.class);
			startActivity(intent);
			finish();
		}else {
			if(result.equals(Codes.USER_NOT_FOUND)){
				FastDialog.simpleDialog(Login.this, "Not registered", "You'd better press that register button, you smartass", "OK");
				DataManager.deleteData("user");
			}else if(result.equals(Codes.ERROR)){
				FastDialog.yesNoDialog(Login.this, "Error", "Wrong password!\nDo you want to recover it?",
						new MaterialDialog.SingleButtonCallback() {
							@Override
							public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
								Intent intent = new Intent(Login.this, PasswordRecovery.class);
								intent.putExtra("email", email);
								startActivity(intent);
								finish();
							}
						}, new MaterialDialog.SingleButtonCallback() {
							@Override
							public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
								materialDialog.dismiss();
							}
						});
				DataManager.deleteData("user");
			}else{
				FastDialog.simpleDialog(Login.this, "Error", "Something really bad happened", "CLOSE");
				DataManager.deleteData("user");
			}
			if(progressDialog != null) progressDialog.dismiss();
			findViewById(R.id.login_form_subcontainer).setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(progressDialog != null) progressDialog.dismiss();
	}

}
