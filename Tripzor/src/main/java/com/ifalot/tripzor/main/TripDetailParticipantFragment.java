package com.ifalot.tripzor.main;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.ui.ParticipantsAdapter;
import com.ifalot.tripzor.ui.UserDetailDialog;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.utils.FastProgressDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class TripDetailParticipantFragment extends Fragment implements ResultListener {

    private MaterialDialog progressDialog;
    private ListView part_list;
    private JSONArray participants;
    private int tripId;
    private boolean owned;
    private int owner;
    private boolean removing;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_participant_detail, container, false);

        progressDialog = FastProgressDialog.buildProgressDialog(this.getContext());
        part_list = (ListView) rootView.findViewById(R.id.participant_list);
        removing = false;

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.button_add_participant);
        if(owned) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(TripDetailParticipantFragment.this.getContext(), SearchParticipants.class);
                    i.putExtra("TripId", tripId);
                    startActivity(i);
                }
            });

            part_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    try {
                        final JSONObject tmp = participants.getJSONObject(position);
                        if(tmp.getInt("userId") == owner){
                            FastDialog.simpleErrorDialog(TripDetailParticipantFragment.this.getContext(), "You can't remove yourself, you are the owner, just delete the entire trip");
                        } else {
                            final String email = tmp.getString("email");
                            FastDialog.yesNoDialog(TripDetailParticipantFragment.this.getContext(), "Remove Participant",
                                    "Are you sure you want to remove user @" + tmp.getString("nickname") + " from participants?",
                                    new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            HashMap<String, String> postData = new HashMap<String, String>();
                                            postData.put("action", "RemoveParticipant");
                                            postData.put("tripId", String.valueOf(tripId));
                                            postData.put("participant", email);
                                            progressDialog.show();
                                            removing = true;
                                            PostSender.sendPost(postData, TripDetailParticipantFragment.this);
                                        }
                                    }, new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    });
                        }
                        return true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

        } else {
            fab.hide();
        }

        part_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    UserDetailDialog.showProfile(TripDetailParticipantFragment.this.getContext(),
                            participants.getJSONObject(position), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;

    }

    @Override
    public void onResultsSucceeded(String result, List<String> listResult) {
        progressDialog.dismiss();
        if(result.equals(Codes.USER_NOT_FOUND) || result.equals(Codes.TRIP_NOT_FOUND)){
            error();
        } else {
            if(removing){
                removing = false;
                refresh();
            }else {
                try {
                    TextView place = (TextView) rootView.findViewById(R.id.location_tv);
                    TextView start = (TextView) rootView.findViewById(R.id.startdate_tv);
                    TextView end = (TextView) rootView.findViewById(R.id.enddate_tv);
                    JSONObject jo = new JSONObject(result); // fields: tripid, name, place, start, end
                    place.setText(jo.getString("place"));
                    place.setPaintFlags(place.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    start.setText(jo.getString("start"));
                    end.setText(jo.getString("end"));

                    participants = jo.getJSONArray("participants");
                    owner = jo.getInt("owner");
                    String[] v = new String[participants.length()];
                    for (int i = 0; i < v.length; i++) v[i] = participants.getString(i);
                    part_list.setAdapter(new ParticipantsAdapter(this.getContext(), participants, owner));

                } catch (JSONException e) {
                    error();
                }
            }
        }
    }

    @Override
    public void onResume() {
        refresh();
        super.onResume();
    }

    private void error(){
        FastDialog.simpleErrorDialog(this.getContext(), "Error retrieving data", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                TripDetailParticipantFragment.this.getActivity().onBackPressed();
            }
        });
    }

    private void refresh(){
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("action", "TripDetail");
        postData.put("tripid", String.valueOf(tripId));
        PostSender.sendPost(postData, this);
        progressDialog.show();
    }

    public void setTripId(int tripId){
        this.tripId = tripId;
    }

    public void setOwned(boolean owned){
        this.owned = owned;
    }

}
