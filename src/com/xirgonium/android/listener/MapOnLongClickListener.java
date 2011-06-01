package com.xirgonium.android.listener;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;

import com.google.android.maps.MapView;

public class MapOnLongClickListener implements OnLongClickListener {

    private MapView map;
    
    public MapOnLongClickListener(MapView map){
        this.map = map;
    }
    
    public boolean onLongClick(View arg0) {
      //  Log.d("LSR", "Long click on map");
//        LinearLayout zoomView = (LinearLayout) map.getZoomControls(); 
//        zoomView.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT ) );
//        zoomView.setGravity(Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL);
//        map.addView(zoomView);
//        map.displayZoomControls(true); 
        return true;
    }

}
