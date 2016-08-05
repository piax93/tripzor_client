package com.ifalot.tripzor.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.main.EditProfile;
import com.ifalot.tripzor.main.R;
import com.ifalot.tripzor.utils.Media;
import com.ifalot.tripzor.utils.Stuff;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.MediaListener;
import com.ifalot.tripzor.web.PostSender;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class UserDetailDialog implements MediaListener {

    private MaterialDialog.Builder builder;
    private String userId;

    private UserDetailDialog(Context context, String userId){
        this.builder = new MaterialDialog.Builder(context);
        this.userId = userId;
    }

    public static void showProfile(Context context, JSONObject user) throws JSONException {
        UserDetailDialog udd = new UserDetailDialog(context, user.getString("userId"));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View main = inflater.inflate(R.layout.user_profile_dialog, null);
        TextView name = (TextView) main.findViewById(R.id.profile_name_tv);
        TextView surname = (TextView) main.findViewById(R.id.profile_surname_tv);
        TextView nickname = (TextView) main.findViewById(R.id.profile_nickname_tv);
        name.setText(Stuff.ucfirst(user.getString("name")));
        surname.setText(Stuff.ucfirst(user.getString("surname")));
        nickname.setText("@" + user.getString("nickname"));
        udd.builder.customView(main, true);
        if(!EditProfile.newProfilePic && new File(Media.getImagePath(context, Media.PROFILE_PICTURE_DIR + "/" + udd.userId, "png")).exists()){
            ImageView iv = (ImageView) main.findViewById(R.id.profile_picture);
            iv.setImageDrawable(Media.getRoundedImage(iv.getContext(), Media.PROFILE_PICTURE_DIR + "/" + udd.userId, "png"));
            udd.builder.show();
        } else {
            EditProfile.newProfilePic = false;
            PostSender.getProfilePicture(udd.userId, context.getFilesDir().getAbsolutePath(), udd);
        }
    }

    @Override
    public void onMediaReceived(String result) {
        if(result.equals(Codes.DONE)){
            ImageView iv = (ImageView) builder.show().getView().findViewById(R.id.profile_picture);
            iv.setImageDrawable(Media.getRoundedImage(iv.getContext(), Media.PROFILE_PICTURE_DIR + "/" + userId, "png"));
        }else{
            builder.show();
        }
    }

    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {

    }

}
