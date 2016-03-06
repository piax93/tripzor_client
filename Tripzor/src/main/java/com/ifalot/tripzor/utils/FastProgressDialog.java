package com.ifalot.tripzor.utils;

import com.afollestad.materialdialogs.MaterialDialog;
import android.content.Context;
import com.ifalot.tripzor.main.R;

/**
 * Created by mat on 3/6/16.
 */
public class FastProgressDialog {

    public static MaterialDialog buildProgressDialog(Context context){
        MaterialDialog.Builder b = new MaterialDialog.Builder(context);
        b.customView(R.layout.custom_progress_dialog, false);
        return b.build();
    }

}
