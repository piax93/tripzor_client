package com.ifalot.tripzor.main;
 
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import java.util.List;

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
			FastDialog.simpleDialog(this, "Oops", "Server not available at the moment :-(\nTry again later", "CLOSE",
					new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
							materialDialog.dismiss();
							Loader.this.finish();
							System.exit(RESULT_CANCELED);
						}
					});
		}		
	}

}
