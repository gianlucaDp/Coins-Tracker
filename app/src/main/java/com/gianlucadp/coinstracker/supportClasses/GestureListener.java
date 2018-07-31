package com.gianlucadp.coinstracker.supportClasses;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.gianlucadp.coinstracker.model.TransactionGroup;


public abstract class GestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public abstract boolean onDoubleTap(MotionEvent e) ;



}
