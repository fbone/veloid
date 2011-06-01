package com.xirgonium.android.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.xirgonium.android.place.FavoritePlace;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.StationNearActivity;

public class FavoritePlaceItemListener implements OnClickListener {

	private Context		activity;
	private FavoritePlace	favoritePlace;

	public FavoritePlaceItemListener(Context act, FavoritePlace fav) {
		this.activity = act;
		this.favoritePlace = fav;
	}

	public void onClick(View clicked) {
		Intent i = new Intent(activity, StationNearActivity.class);
		Bundle b = new Bundle();
		double latitude = favoritePlace.getLatitude();
		double longitude = favoritePlace.getLongitude();
		String name = favoritePlace.getName();

		b.putString(Constant.MAP_FAVORITE_NAME_KEY, name);
		b.putDouble(Constant.MAP_FAVORITE_LAT_KEY, latitude);
		b.putDouble(Constant.MAP_FAVORITE_LON_KEY, longitude);

		b.putBoolean(Constant.MAP_LOCATE_FAVORITE_KEY, true);

		i.putExtras(b);
		activity.startActivity(i);

	}

}
