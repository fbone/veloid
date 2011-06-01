package com.xirgonium.android.manager;

import android.content.Context;

public class CyclicRouen extends ConstructorJCDecaultVelibLikeStationManager {
    
	public final static String  WS_ALL_STATIONS_URL                = "http://cyclic.rouen.fr/service/carto";
	
	public final static String  WS_STATION_DETAIL_URL              = "http://cyclic.rouen.fr/service/stationdetails/";

	public CyclicRouen() {
    // TODO Auto-generated constructor stub
    }
	
    public CyclicRouen(Context launched) {
       super(launched);
    }
    
    public String[] getCities(){
    	return new String[]{"Rouen"};
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
    boolean useOpenTag() {
      return false;
    }
    
}
