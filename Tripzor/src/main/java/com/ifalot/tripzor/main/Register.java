package com.ifalot.tripzor.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Register extends AppCompatActivity implements ResultListener {

	private HashMap<String, String> form = new HashMap<String, String>();
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
	
	private void register(){
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
	public void onResultsSucceeded(JSONObject res) throws JSONException {
		dialog.dismiss();
		String result = res.getString("result");
		if(result.equals(Codes.DONE)){
			DataManager.insertData("user", form.get("email"));
			DataManager.insertData("password", form.get("password"));
			finish();
		}else if(result.equals(Codes.USER_ALREADY_PRESENT)){
			FastDialog.yesNoDialog(this, "User already present", "This email address is already registered on our server, would you like to recover you password?",
					new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
							Intent intent = new Intent(Register.this, PasswordRecovery.class);
							intent.putExtra("email", form.get("email"));
							startActivity(intent);
							Register.this.finish();
						}
					},
					new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
							materialDialog.dismiss();
						}
					});
		}else if(result.equals(Codes.ERROR)){
			FastDialog.simpleDialog(this, "Error", "Something bad happened on our server, try again later", "CLOSE");
		}
	}

}
