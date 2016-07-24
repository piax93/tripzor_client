package com.ifalot.tripzor.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.main.R;
import com.ifalot.tripzor.utils.Media;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.MediaListener;
import com.ifalot.tripzor.web.PostSender;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
        name.setText(user.getString("name"));
        surname.setText(user.getString("surname"));
        nickname.setText("@" + user.getString("nickname"));
        udd.builder.customView(main, true);
        PostSender.getProfilePicture(udd.userId, context.getFilesDir().getAbsolutePath(), udd);
    }

    @Override
    public void onMediaReceived(String result) {
        if(result.equals(Codes.DONE)){
            ImageView iv = (ImageView) builder.show().getView().findViewById(R.id.profile_picture);
            iv.setImageDrawable(Media.getRoundedImage(iv.getContext(), Media.PROFILE_PICTURE_DIR + "/" + userId, null));
        }
    }

    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {

    }

}
