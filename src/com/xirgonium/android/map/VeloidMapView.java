package com.xirgonium.android.map;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.maps.MapView;

public class VeloidMapView extends MapView {
    public VeloidMapView(Context context, String apiKey) {
        super(context, apiKey);
    }

    public VeloidMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VeloidMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return false;
    }

}
