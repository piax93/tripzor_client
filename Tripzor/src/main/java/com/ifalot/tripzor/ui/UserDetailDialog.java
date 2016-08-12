package com.ifalot.tripzor.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class UserDetailDialog implements MediaListener {

    private MaterialDialog.Builder builder;
    private String userId;

    private UserDetailDialog(Context context, String userId){
        this.builder = new MaterialDialog.Builder(context);
        this.userId = userId;
    }

    public static void showProfile(Context context, JSONObject user, @Nullable ViewGroup parentView) throws JSONException {
        UserDetailDialog udd = new UserDetailDialog(context, user.getString("userId"));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View main = inflater.inflate(R.layout.user_profile_dialog, parentView);
        TextView name = (TextView) main.findViewById(R.id.profile_name_tv);
        TextView surname = (TextView) main.findViewById(R.id.profile_surname_tv);
        TextView nickname = (TextView) main.findViewById(R.id.profile_nickname_tv);
        name.setText(Stuff.ucfirst(user.getString("name")));
        surname.setText(Stuff.ucfirst(user.getString("surname")));
        nickname.setText("@");
        nickname.append(user.getString("nickname"));
        udd.builder.customView(main, true);

        if(new File(Media.getImagePath(context, Media.PROFILE_PICTURE_DIR + "/" + udd.userId, "png")).exists()){
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("action", "GetProfilePictureTime");
            postData.put("userId", udd.userId);
            PostSender.sendPost(postData, udd);
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
        if(!result.equals(Codes.ERROR) && !result.equals(Codes.USER_NOT_FOUND)){
            File f = new File(Media.getImagePath(builder.getContext(), Media.PROFILE_PICTURE_DIR + "/" + userId, "png"));
            if(Long.parseLong(result)*1000 > f.lastModified()){
                PostSender.getProfilePicture(userId, builder.getContext().getFilesDir().getAbsolutePath(), this);
                return;
            }
        }
        ImageView iv = (ImageView) builder.show().findViewById(R.id.profile_picture);
        iv.setImageDrawable(Media.getRoundedImage(iv.getContext(), Media.PROFILE_PICTURE_DIR + "/" + userId, "png"));
    }

}
