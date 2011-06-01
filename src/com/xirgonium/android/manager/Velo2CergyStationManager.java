package com.xirgonium.android.manager;

import android.content.Context;

public class Velo2CergyStationManager extends ConstructorJCDecaultVelibLikeStationManager {


  private final static String WS_ALL_STATIONS_URL   = "http://www.velo2.cergypontoise.fr/service/carto";

  private final static String WS_STATION_DETAIL_URL = "http://www.velo2.cergypontoise.fr/service/stationdetails/";

  @Override
  boolean useOpenTag() {
    return true;
  }
  
  public Velo2CergyStationManager() {
  }

  public Velo2CergyStationManager(Context launched) {
    super(launched);
  }

  public String[] getCities() {
    return new String[] { "Cergy" };
  }

  @Override
  protected String getAllStationURL() {
    return WS_ALL_STATIONS_URL;
  }

  @Override
  protected String getOneStationInfoURL() {
    return WS_STATION_DETAIL_URL;
  }
}
