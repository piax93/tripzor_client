package com.ifalot.tripzor.ui;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ifalot.tripzor.main.R;
import com.ifalot.tripzor.main.TripList;
import com.ifalot.tripzor.model.Trip;

import java.util.LinkedList;
import java.util.List;


public class TripListAdapter extends ArrayAdapter<Trip> implements View.OnClickListener {

    private List<Trip> trips;
    private Boolean[] checked;
    private Integer[] colors;
    private TripList parent;
    private int nSelected;

    public TripListAdapter(TripList parent, List<Trip> objects) {
        super(parent, android.R.layout.simple_list_item_1, objects);
        trips = objects;
        this.parent = parent;
        checked = new Boolean[objects.size()];
        for(int i = 0; i < objects.size(); i++) checked[i] = false;
        colors = new Integer[objects.size()];
        this.nSelected = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if(convertView == null) view = inflater.inflate(R.layout.triplist_item, parent, false);
        else view = convertView;

        ImageView icon = (ImageView) view.findViewById(R.id.trip_item_icon);
        TextView title = (TextView) view.findViewById(R.id.trip_item_title);

        TextDrawable td;
        if(checked[position]){
            td = TextDrawable.builder().buildRound(">", 0xfff44336, position);
            ((TransitionDrawable)((LinearLayout)icon.getParent()).getBackground()).startTransition(0);
        } else {
            if(colors[position] == null) colors[position] = ColorGenerator.DEFAULT.getRandomColor();
            td = TextDrawable.builder().buildRound(trips.get(position).toString().trim(),
                   colors[position], position);
            ((TransitionDrawable)((LinearLayout)icon.getParent()).getBackground()).resetTransition();
        }

        icon.setImageDrawable(td);
        title.setText(trips.get(position).toString());

        icon.setOnClickListener(this);

        return view;
    }

    @Override
    public synchronized void onClick(View v) {
        v.setClickable(false);
        if(nSelected == 0) parent.itemsAreSelected();
        int position = ((TextDrawable)((ImageView)v).getDrawable()).getElementId();
        boolean tmp = checked[position];
        checked[position] = !tmp;
        if(tmp) nSelected--;
        else nSelected++;
        new FlipAnimator((ImageView) v, tmp, trips.get(position), getContext()).startAnimation();
        if(nSelected == 0) parent.noItemsAreSelected();
    }

    public void deselectAll(ListView lv){
        if(nSelected == 0) return;
        for (int i = 0; i < checked.length; i++) {
            if(checked[i]){
                LinearLayout ll = (LinearLayout) lv.getChildAt(i);
                if(ll != null) ll.getChildAt(0).performClick();
                else{
                    checked[i] = false;
                    nSelected--;
                }
            }
        }
    }

    public List<Trip> getTrips(){
        return this.trips;
    }

    public List<Trip> getSelected(){
        List<Trip> selected = new LinkedList<Trip>();
        for (int i = 0; i < trips.size(); i++) {
            if(checked[i]) selected.add(trips.get(i));
        }
        return selected;
    }

}
