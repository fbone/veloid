package com.xirgonium.android.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.map.VeloidMapCenter;
import com.xirgonium.android.object.Station;

public class MapUtility {

  /**
   * Retrieve coordinates from address
   * 
   * @param location
   * @return
   * @throws IOException
   */
  public static List<Address> getAddressList(String location, Context ctx) throws IOException {
    Geocoder geocoder = new Geocoder(ctx);
    List<Address> addresses = geocoder.getFromLocationName(location, 10);
    for (Iterator<Address> iterator = addresses.iterator(); iterator.hasNext();) {
      Address address = (Address) iterator.next();
      System.out.println("Address  : lat " + address.getLatitude());
    }
    return addresses;
  }

  /**
   * Retrieve coordinates from address
   * 
   * @param location
   * @return
   * @throws IOException
   */
  public static GeoPoint getGeoCode(String location, Context ctx) throws IOException {

    int latitude = 0;
    int longitude = 0;

    Geocoder geocoder = new Geocoder(ctx);
    List<Address> addresses = geocoder.getFromLocationName(location, 10);

    if (addresses.size() > 0) {
      latitude = (int) (addresses.get(0).getLatitude() * 1000000);
      longitude = (int) (addresses.get(0).getLongitude() * 1000000);
      Log.d("MAP_UTIL", "From " + location + "=> lat " + latitude + ", lng " + longitude);
      GeoPoint p = new GeoPoint(latitude, longitude);
      return p;
    } else {
      return null;
    }
  }

  public static LocationProvider getLocationProviderToUse(Context act) {
    LocationManager locMgr = MapUtility.getBuiltLocationManager(act);

    String name = locMgr.getBestProvider(new Criteria(), true);

    Log.d("MAP", "The best location provider is " + name);

    return locMgr.getProvider(name);
  }

  public static VeloidMapCenter geoLocalize(Activity act) throws SecurityException {

    LocationProvider toUse = getLocationProviderToUse(act);

    LocationManager locMgr = MapUtility.getBuiltLocationManager(act);

    // LocationListener listener = new LocationListener() {
    // public void onLocationChanged(Location location) {
    // }
    // public void onProviderDisabled(String provider) {
    // }
    // public void onProviderEnabled(String provider) {
    // }
    // public void onStatusChanged(String provider, int status,
    // Bundle extras) {
    // }
    // };

    // locMgr.requestLocationUpdates(toUse.getName(), 0, 0, listener);

    Location current = locMgr.getLastKnownLocation(toUse.getName());

    // Location current = ConfigurationContext.getMyLocationInstance(act).getLastLocation();

    if (current != null) {
      Log.d("MAP", "Location : " + current.getLatitude() + ", " + current.getLongitude());
      VeloidMapCenter p = new VeloidMapCenter((int) (current.getLatitude() * 1000000), (int) (current.getLongitude() * 1000000));
      return p;
    } else {
      Log.e("MAP", "Unable to get the last known position");
      return null;
    }
  }

  /**
   * Among all the stations given as parameter, return the X nearest stations of the point p. X is the limit
   * 
   * @param p
   *          : reference
   * @param limit
   *          : maximum number of station to return
   * @param stations
   *          : list of station to check
   * @return
   */
  public static Vector<Station> getNearestStations(GeoPoint p, int limit, Vector<Station> stations) {

    // Fill the station with the distance from point p
    for (Iterator<Station> iterator = stations.iterator(); iterator.hasNext();) {
      Station aStation = (Station) iterator.next();
      aStation.setDistance(getDistanceBetween(p, new GeoPoint((int) aStation.getMicroDegreeLatitude(), aStation.getMicroDegreeLongitude())));
    }

    // sort regarding the distance
    Collections.sort(stations, new Comparator<Station>() {

      public int compare(Station station1, Station station2) {
        return (int) (station1.getDistance() - station2.getDistance());
      }
    });

    Vector<Station> toRet = new Vector<Station>();
    if (stations.size() > 0 && limit < stations.size()) {
      toRet = new Vector<Station>(stations.subList(0, limit));
    } else {
      toRet = stations;
    }
    return toRet;
  }

  public static double getDistanceBetween(GeoPoint from, GeoPoint to) {

    if (from == null || to == null)
      return Double.MAX_VALUE;

    double distanceX = Math.abs(to.getLatitudeE6() - from.getLatitudeE6());
    double distanceY = Math.abs(to.getLongitudeE6() - from.getLongitudeE6());

    double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
return distance;
    
    //return (distance == 0 ? Double.MAX_VALUE : distance);
  }

  // public static void addMyMockLocationProvider(LocationManager locmgr) {
  //
  // String mocLocationProvider = "gps";
  //
  // if (locmgr.getProvider(mocLocationProvider) == null ||
  // !locmgr.isProviderEnabled(mocLocationProvider)) {
  // locmgr.addTestProvider(mocLocationProvider, false, false, false, false,
  // false, false, false, 0, 5);
  // locmgr.setTestProviderEnabled(mocLocationProvider, true);
  // locmgr.setTestProviderStatus(mocLocationProvider,
  // LocationProvider.AVAILABLE, null, (new Date()).getTime());
  // }
  //
  // }

  public static LocationManager getBuiltLocationManager(Context ctx) {
    LocationManager mgr = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

    return mgr;
  }

}
