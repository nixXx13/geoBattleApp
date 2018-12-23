package com.example.nir.geobattle;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class UIHandler {

    private Handler mHandler;

    UIHandler(){
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void setViewVisability(final View view , final int visability){
        mHandler.post(new Runnable() {
            public void run() {
                view.setVisibility(visability);
            }
        });
    }

    public void updateTextView (final TextView tv , final String s ) {
        mHandler.post(new Runnable() {
            public void run() {
                tv.setText(s);
            }
        });
    }

    public void makeToast(final Context context,final String msg){
        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setViewBackground(final Context context,final View view ,final int drawableId){
        mHandler.post(new Runnable() {
            public void run() {
                view.setBackground(ContextCompat.getDrawable(context, drawableId));
            }
        });
    }

    public void alpha(final View v , final float startAlpha , final float finishAlpha ){

        mHandler.post(new Runnable() {
            public void run() {
                AlphaAnimation animation = new AlphaAnimation(startAlpha, finishAlpha);
                animation.setDuration(250);
                animation.setFillAfter(true);
                v.startAnimation(animation);
            }
        });


    }

}
