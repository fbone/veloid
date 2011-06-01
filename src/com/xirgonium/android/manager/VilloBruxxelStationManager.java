package com.xirgonium.android.manager;

import android.content.Context;

public class VilloBruxxelStationManager extends ConstructorJCDecaultVelibLikeStationManager {

  // public final static String WS_VELIB_ALL_STATIONS_URL = "http://xirgonium.free.fr/testxml/carto.xml";
  private final static String WS_ALL_STATIONS_URL   = "http://www.villo.be/service/carto";

  // ---- Add the station ID at the end of the URL to get data from Velib'
  private final static String WS_STATION_DETAIL_URL = "http://www.villo.be/service/stationdetails/";

  public VilloBruxxelStationManager() {
  }

  public VilloBruxxelStationManager(Context launched) {
    super(launched);
  }

  public String[] getCities() {
    return new String[] { "Bruxelles" };
  }

  @Override
  protected String getAllStationURL() {
    return WS_ALL_STATIONS_URL;
  }

  @Override
  protected String getOneStationInfoURL() {
    // TODO Auto-generated method stub
    return WS_STATION_DETAIL_URL;
  }

  @Override
  boolean useOpenTag() {
    return false;
  }
}