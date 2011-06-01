package com.xirgonium.android.manager;

import android.content.Context;

public class SeviciSevillaStationManager extends ConstructorJCDecaultVelibLikeStationManager {

    private final static String  WS_ALL_STATIONS_URL                = "http://www.sevici.es/service/carto";
    
    // ---- Add the station ID at the end of the URL to get data from sevici'
    private final static String  WS_STATION_DETAIL_URL              = "http://www.sevici.es/service/stationdetails/";

    public SeviciSevillaStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public SeviciSevillaStationManager(Context launched) {
       super(launched);
    }
    
    @Override
    protected String getAllStationURL() {
        return WS_ALL_STATIONS_URL;
    }

    @Override
    protected String getOneStationInfoURL() {
        return WS_STATION_DETAIL_URL;
    }

    @Override
    public String[] getCities() {
         return new String[]{"Sevilla"};
    }
    
    @Override
    boolean useOpenTag() {
      return false;
    }

}
