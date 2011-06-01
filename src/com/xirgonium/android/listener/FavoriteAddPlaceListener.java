package com.xirgonium.android.listener;

import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.GeoPoint;
import com.xirgonium.android.place.DialogToNamePlace;
import com.xirgonium.android.place.FavoritePlace;

public class FavoriteAddPlaceListener implements OnClickListener {

	private Context		activity;
	private GeoPoint	favCoord;

	public FavoriteAddPlaceListener(Context act) {
		this.activity = act;
	}

	public void onClick(View clicked) {
		FavoritePlace newPlace = new FavoritePlace();
		newPlace.setLatitude(favCoord.getLatitudeE6()*1000000);
		newPlace.setLongitude(favCoord.getLongitudeE6()*1000000);
		Long id = (new Date()).getTime();
		Log.d(this.toString(), "ID TO BE SET " + id);
		newPlace.setId(id);

		DialogToNamePlace dialog = new DialogToNamePlace(activity, newPlace);
		dialog.show();
		
		Log.d(this.getClass().getName(), "CENTER CLICK on " + newPlace.getId());

	}

	public void setFavCoord(GeoPoint favCoord) {
		this.favCoord = favCoord;
	}

	public GeoPoint getFavCoord() {
		return favCoord;
	}

}
