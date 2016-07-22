package com.ifalot.tripzor.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.ifalot.tripzor.main.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParticipantsAdapter extends ArrayAdapter<String> {

    private JSONArray participants;
    private int owner;

    public ParticipantsAdapter(Context context, JSONArray objects, int owner) throws JSONException {
        super(context, android.R.layout.simple_list_item_1, new String[objects.length()]);
        participants = objects;
        this.owner = owner;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;
        if(convertView == null) view = inflater.inflate(R.layout.participant_item, parent, false);
        else view = convertView;

        TextView title = (TextView) view.findViewById(R.id.part_id);

        try {
            JSONObject tmp = participants.getJSONObject(position);
            StringBuilder sb = new StringBuilder(tmp.getString("name")).append(' ')
                    .append(tmp.getString("surname")).append(" (@")
                    .append(tmp.getString("nickname")).append(')');
            title.setText(sb);
            if(tmp.getInt("userId") == this.owner) title.setTypeface(null, Typeface.BOLD);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;

    }

}
