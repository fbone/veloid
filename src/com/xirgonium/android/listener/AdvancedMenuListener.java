package com.xirgonium.android.listener;

import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.DeleteStationActivity;
import com.xirgonium.android.veloid.DialogSetFilterActivity;
import com.xirgonium.android.veloid.NewStationActivityStep1;
import com.xirgonium.android.veloid.R;
import com.xirgonium.android.veloid.TimerSetActivity;
import com.xirgonium.android.veloid.Veloid;

public class AdvancedMenuListener implements OnClickListener {

    private Veloid activity;

    public AdvancedMenuListener(Veloid act, View advancedMenu) {
        this.activity = act;
    }

    public void onClick(View clicked) {
        Intent intent = null;
        switch (clicked.getId()) {
            case R.id.adv_hide_btn:
                activity.startHideAnimationForMenu(Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS);
                //ViewGroup vg = (ViewGroup) activity.findViewById(R.id.global_main_container);
                //vg.removeView(advancedMenu);
                break;
            case R.id.adv_add_station_btn:
                intent = new Intent(activity, NewStationActivityStep1.class);
                activity.startActivityForResult(intent, Constant.ACTIVITY_NEW_STATION_S1_START);
                break;
            case R.id.adv_timer_btn:
                intent = new Intent(activity, TimerSetActivity.class);
                activity.startActivityForResult(intent, Constant.ACTIVITY_TIMER_SET_START);
                break;
            case R.id.adv_nearest_station_btn:
                intent = new Intent(activity, DialogSetFilterActivity.class);
                activity.startActivityForResult(intent, Constant.ACTIVITY_NEAREST_STATIONS);
                break;
            case R.id.adv_delete_station_btn:
                intent = new Intent(activity, DeleteStationActivity.class);
                Bundle b = new Bundle();
                for (Iterator<Station> iterator = activity.getSignets().iterator(); iterator.hasNext();) {
                    Station station = (Station) iterator.next();
                    b.putString(station.getId(), station.getName());
                }
                intent.putExtras(b);
                activity.startActivityForResult(intent, Constant.ACTIVITY_DEL_STATION_START);
                break;
        }

    }

}
