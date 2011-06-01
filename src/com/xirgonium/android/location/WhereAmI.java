package com.xirgonium.android.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.xirgonium.android.veloid.veloid2.map.VeloidMap;

public class WhereAmI {

	Location					lastUpdated	= null;
	LocationManager				manager;
	WhereamiLocationListener	listener;
	VeloidMap					map			= null;

	public class WhereamiLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			if (location != null) {
				lastUpdated = location;
//				if (map != null) {
//					map.updateStationOnMapBackground();
//				}
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	public WhereAmI(Context ctx) {
		listener = new WhereamiLocationListener();
		manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void registerMap(VeloidMap map){
		this.map=map;
	}
	
	public void unregisterMap(){
		this.map=null;
	}

	public void pauseUpdates() {
		manager.removeUpdates(listener);
	}

	public void resumeUpdate() {
		 long updateTimeMsec = 5000L;
		//long updateTimeMsec = 0L;
		long updateMinDistanceMeters = 0L;
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateTimeMsec, updateMinDistanceMeters, listener);
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateTimeMsec, updateMinDistanceMeters, listener);
		lastUpdated = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastUpdated == null) {
			lastUpdated = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
	}

	public Location getLastLocation() {
		return lastUpdated;
	}
}
