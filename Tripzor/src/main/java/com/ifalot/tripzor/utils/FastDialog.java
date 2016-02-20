package com.ifalot.tripzor.utils;


import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class FastDialog {

	public static void simpleErrorDialog(Context context, String message){
		simpleDialog(context, "Error", message, null);
	}
	
	public static void simpleDialog(Context context, String title, String message, String button){
		simpleDialog(context, title, message, button, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});
	}
	
	public static void simpleDialog(Context context, String title, String message, String button, DialogInterface.OnClickListener action){
		Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setNeutralButton(button != null ? button : "CLOSE", action);
		builder.show();
	}
	
	public static void yesNoDialog(Context context, String title, 
			String message, OnClickListener yes, OnClickListener no){
		Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("YES", yes);
		builder.setNegativeButton("NO", no);
		builder.show();
	}
	
}
