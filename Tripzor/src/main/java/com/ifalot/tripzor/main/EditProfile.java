package com.ifalot.tripzor.main;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.utils.FastProgressDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import java.io.FileDescriptor;
import java.util.HashMap;
import java.util.List;

public class EditProfile extends AppCompatActivity implements ResultListener{

    final private int PICK_IMAGE = 1;
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        progressDialog = FastProgressDialog.buildProgressDialog(this);

        Button b = (Button) findViewById(R.id.update_info_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> postdata = new HashMap<String, String>();
                EditText nick = (EditText) findViewById(R.id.nickname);
                EditText name = (EditText) findViewById(R.id.name);
                EditText surname = (EditText) findViewById(R.id.surname);
                EditText phone = (EditText) findViewById(R.id.phone_number);
                postdata.put("action", "UserInfo");
                postdata.put("update", "true");
                postdata.put("nick", nick.getText().toString());
                postdata.put("name", name.getText().toString());
                postdata.put("surname", surname.getText().toString());
                postdata.put("phone", phone.getText().toString());
                PostSender.sendPost(postdata, EditProfile.this);
                progressDialog.show();
            }
        });

        RelativeLayout profilepic = (RelativeLayout) findViewById(R.id.profile_picture_container);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a profile picture"), PICK_IMAGE);
            }
        });

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("action", "UserInfo");
        PostSender.sendPostML(data, this);
        progressDialog.show();
    }


    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {
        progressDialog.dismiss();
        if(result.equals(Codes.USER_NOT_FOUND) || result.equals(Codes.ERROR)){
            String verb = result.equals(Codes.ERROR) ? "updating" : "retrieving";
            FastDialog.simpleErrorDialog(this, "Error " + verb + " info", new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    EditProfile.this.finish();
                }
            });
        } else if(result.equals(Codes.DONE)){
            finish();
        } else {
            if(listResult.size() >= 4) {
                EditText nick = (EditText) findViewById(R.id.nickname);
                EditText name = (EditText) findViewById(R.id.name);
                EditText surname = (EditText) findViewById(R.id.surname);
                EditText phone = (EditText) findViewById(R.id.phone_number);
                nick.setText(listResult.get(0));
                name.setText(listResult.get(1));
                surname.setText(listResult.get(2));
                phone.setText(listResult.get(3));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Uri mediaUri = data.getData();
                    ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(mediaUri, "r");
                    FileDescriptor fd = pfd.getFileDescriptor();
                    Bitmap bm = BitmapFactory.decodeFileDescriptor(fd);
                    pfd.close();

                    if(bm != null) {
                        ImageView img = (ImageView) findViewById(R.id.profile_picture);
                        img.setImageBitmap(bm);
                    }

                }catch (Exception e){}
            } else {
                FastDialog.simpleErrorDialog(this, "No image loaded");
            }
        }
    }
}
