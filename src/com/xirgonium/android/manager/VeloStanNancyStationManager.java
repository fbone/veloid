package com.xirgonium.android.manager;

import android.content.Context;

public class VeloStanNancyStationManager extends ConstructorJCDecaultVelibLikeStationManager {
    
	public final static String  WS_ALL_STATIONS_URL                = "http://www.velostanlib.fr/service/carto";
	
	public final static String  WS_STATION_DETAIL_URL              = "http://www.velostanlib.fr/service/stationdetails/";

	public VeloStanNancyStationManager() {
    // TODO Auto-generated constructor stub
    }
	
    public VeloStanNancyStationManager(Context launched) {
       super(launched);
    }
    
    public String[] getCities(){
    	return new String[]{"Nancy"};
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
      return true;
    }
}
