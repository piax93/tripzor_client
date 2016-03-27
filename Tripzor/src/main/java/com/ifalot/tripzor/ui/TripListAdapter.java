package com.ifalot.tripzor.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ifalot.tripzor.main.R;
import com.ifalot.tripzor.model.Trip;
import com.ifalot.tripzor.utils.FastDialog;

import java.util.List;


public class TripListAdapter extends ArrayAdapter<Trip> {

    private List<Trip> trips;

    public TripListAdapter(Context context, List<Trip> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        trips = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        char first = trips.get(position).toString().trim().charAt(0);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.triplist_item, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.trip_item_icon);
        TextView title = (TextView) view.findViewById(R.id.trip_item_title);

        TextDrawable td = TextDrawable.builder()
                .buildRound(String.valueOf(first).toUpperCase(), ColorGenerator.DEFAULT.getRandomColor());
        icon.setImageDrawable(td);
        title.setText(trips.get(position).toString());

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}
