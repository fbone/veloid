package com.xirgonium.android.manager;

import android.content.Context;

public class VelcomPlaineCommune extends ConstructorJCDecaultVelibLikeStationManager {

  private final static String WS_ALL_STATIONS_URL   = "http://www.velcom.fr/service/carto";

  private final static String WS_STATION_DETAIL_URL = "http://www.velcom.fr/service/stationdetails/";

  public VelcomPlaineCommune() {
  }

  public VelcomPlaineCommune(Context launched) {
    super(launched);
  }

  public String[] getCities() {
    return new String[] { "Aubervilliers", "Saint-Denis", "La Courneuve", "LÕIle-Saint-Denis" };
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
    return true;
  }
}
