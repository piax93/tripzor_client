package com.ifalot.tripzor.ui;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ifalot.tripzor.main.R;
import com.ifalot.tripzor.model.Trip;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class TripListAdapter extends ArrayAdapter<Trip> implements Animation.AnimationListener {

    private List<Trip> trips;
    private List<Boolean> checked;
    private Queue<ImageView> flips;
    private Animation animation1;
    private Animation animation2;

    public TripListAdapter(Context context, List<Trip> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        trips = objects;
        checked = new LinkedList<Boolean>();
        flips = new LinkedList<ImageView>();
        animation1 = AnimationUtils.loadAnimation(context, R.anim.to_middle);
        animation1.setAnimationListener(this);
        animation2 = AnimationUtils.loadAnimation(context, R.anim.from_middle);
        animation2.setAnimationListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.triplist_item, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.trip_item_icon);
        TextView title = (TextView) view.findViewById(R.id.trip_item_title);

        TextDrawable td = TextDrawable.builder()
                .buildRound(trips.get(position).toString().trim(),
                        ColorGenerator.DEFAULT.getRandomColor(), position);
        icon.setImageDrawable(td);
        title.setText(trips.get(position).toString());
        checked.add(false);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flips.add((ImageView) v);
                v.clearAnimation();
                v.setAnimation(animation1);
                v.startAnimation(animation1);
            }
        });

        return view;
    }

    @Override
    public synchronized void onAnimationEnd(Animation animation) {
        ImageView currentFlip = flips.remove();
        int position = ((TextDrawable)currentFlip.getDrawable()).getElementId();
        if (animation == animation1) {
            if (checked.get(position)) {
                TextDrawable td = TextDrawable.builder()
                        .buildRound(trips.get(position).toString().trim(),
                                ColorGenerator.DEFAULT.getRandomColor(), position);
                LinearLayout back = (LinearLayout) currentFlip.getParent();
                ((TransitionDrawable) back.getBackground()).reverseTransition(300);
                currentFlip.setImageDrawable(td);
            } else {
                TextDrawable check = TextDrawable.builder().buildRound(">", 0xfff44336, position);
                LinearLayout back = (LinearLayout) currentFlip.getParent();
                ((TransitionDrawable) back.getBackground()).startTransition(500);
                currentFlip.setImageDrawable(check);
            }
            currentFlip.clearAnimation();
            currentFlip.setAnimation(animation2);
            flips.add(currentFlip);
            currentFlip.startAnimation(animation2);
        } else {
            boolean tmp = checked.get(position);
            checked.set(position, !tmp);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationRepeat(Animation animation) {}

}
