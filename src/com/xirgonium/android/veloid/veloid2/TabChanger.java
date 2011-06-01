package com.xirgonium.android.veloid.veloid2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xirgonium.android.veloid.veloid2.map.VeloidMap;

public class TabChanger extends BroadcastReceiver {

	VeloidMain	tabInstance;

	public VeloidMain getTabInstance() {
		return tabInstance;
	}

	public void setTabInstance(VeloidMain tabInstance) {
		this.tabInstance = tabInstance;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String tag = intent.getStringExtra("tab");

		if (tag.equals("map")) {
			Intent mapintent = new Intent(context, VeloidMap.class);

			mapintent.putExtras(intent.getExtras());
			//tabInstance.initTabs(mapintent);
			tabInstance.getTabHost().setCurrentTabByTag(tag);
		} else {
			tabInstance.getTabHost().setCurrentTabByTag(tag);
		}
	}

	public static IntentFilter getTabChangingIntentFilter() {
		return new IntentFilter("changetab");
	}
}
