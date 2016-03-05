package com.ifalot.tripzor.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;

public class FastDialog {

	public static void simpleErrorDialog(Context context, String message){
		simpleDialog(context, "Error", message, null);
	}
	
	public static void simpleDialog(Context context, String title, String message, String button){
		simpleDialog(context, title, message, button, new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
				materialDialog.dismiss();
			}
		});
	}
	
	public static void simpleDialog(Context context, String title, String message, String button, MaterialDialog.SingleButtonCallback action){
		Builder builder = new MaterialDialog.Builder(context);
		builder.content(message)
				.title(title)
				.neutralText(button != null ? button : "CLOSE")
				.onNeutral(action)
				.show();
	}
	
	public static void yesNoDialog(Context context, String title, String message,
								   MaterialDialog.SingleButtonCallback yes, MaterialDialog.SingleButtonCallback no){
		Builder builder = new MaterialDialog.Builder(context);
		builder.content(message)
				.title(title)
				.positiveText("YES")
				.onPositive(yes)
				.negativeText("NO")
				.onNegative(no)
				.show();
	}
	
}
