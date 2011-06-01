package com.xirgonium.android.listener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.R;
import com.xirgonium.android.veloid.StationNearActivity;
import com.xirgonium.android.veloid.Veloid;

public class ExpressSeachSlotsMenuListener implements OnClickListener {

	private Veloid activity;

	public ExpressSeachSlotsMenuListener(Veloid act) {
		this.activity = act;
	}

	public void onClick(View clicked) {

		switch (clicked.getId()) {
		case R.id.adv_hide_btn:
			activity.startHideAnimationForMenu(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT);
			break;
		case R.id.express_search_1_btn:
			actionForOneExpressSearch(1);
			break;
		case R.id.express_search_2_btn:
			actionForOneExpressSearch(2);
			break;
		case R.id.express_search_3_btn:
			actionForOneExpressSearch(3);
			break;
		case R.id.express_search_4_btn:
			actionForOneExpressSearch(4);
			break;
		}

	}

	private void actionForOneExpressSearch(int filter) {
		Intent i = new Intent(activity, StationNearActivity.class);
		Bundle b = new Bundle();
		b.putInt(Constant.FILTER_MIN_FREE_SLOTS, filter);
		b.putBoolean(Constant.MAP_FIND_GEOLOC_KEY, true);
		i.putExtras(b);
		activity.startActivityForResult(i, Constant.ACTIVITY_NEW_STATION_S2_GOOGLEMAPS_START);

	}
}
