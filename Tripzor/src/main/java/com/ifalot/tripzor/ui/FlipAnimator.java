package com.ifalot.tripzor.ui;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ifalot.tripzor.main.R;
import com.ifalot.tripzor.model.Trip;

/**
 * Created by mat on 3/28/16.
 */
public class FlipAnimator implements Animation.AnimationListener {

    private Animation animation1;
    private Animation animation2;
    private boolean checked;
    private ImageView currentFlip;
    private Trip trip;

    public FlipAnimator(ImageView toFlip, boolean checked, Trip trip, Context context){
        this.checked = checked;
        this.currentFlip = toFlip;
        this.trip = trip;
        animation1 = AnimationUtils.loadAnimation(context, R.anim.to_middle);
        animation1.setAnimationListener(this);
        animation2 = AnimationUtils.loadAnimation(context, R.anim.from_middle);
        animation2.setAnimationListener(this);
    }

    public void startAnimation(){
        currentFlip.setAnimation(animation1);
        currentFlip.startAnimation(animation1);
    }

    @Override
    public synchronized void onAnimationEnd(Animation animation) {
        int position = ((TextDrawable)currentFlip.getDrawable()).getElementId();
        if (animation == animation1) {
            if (checked) {
                TextDrawable td = TextDrawable.builder()
                        .buildRound(trip.toString().trim(), ColorGenerator.DEFAULT.getRandomColor(), position);
                LinearLayout back = (LinearLayout) currentFlip.getParent();
                ((TransitionDrawable) back.getBackground()).reverseTransition(300);
                currentFlip.setImageDrawable(td);
            } else {
                TextDrawable check = TextDrawable.builder().buildRound(">", 0xfff44336, position);
                LinearLayout back = (LinearLayout) currentFlip.getParent();
                ((TransitionDrawable) back.getBackground()).startTransition(500);
                currentFlip.setImageDrawable(check);
            }
            currentFlip.setAnimation(animation2);
            currentFlip.startAnimation(animation2);
        } else {
            currentFlip.setClickable(true);
        }

    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationRepeat(Animation animation) {}

}
