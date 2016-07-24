package com.ifalot.tripzor.utils;

import com.afollestad.materialdialogs.MaterialDialog;
import android.content.Context;
import com.ifalot.tripzor.main.R;

public class FastProgressDialog {

    public static MaterialDialog buildProgressDialog(Context context){
        MaterialDialog.Builder b = new MaterialDialog.Builder(context);
        b.customView(R.layout.custom_progress_dialog, false);
        b.cancelable(false);
        return b.build();
    }

}
