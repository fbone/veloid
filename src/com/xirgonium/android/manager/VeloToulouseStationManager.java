package com.xirgonium.android.manager;

import android.content.Context;

public class VeloToulouseStationManager extends ConstructorJCDecaultVelibLikeStationManager {
    
	public final static String  WS_ALL_STATIONS_URL                = "http://www.velo.toulouse.fr/service/carto";
	
	public final static String  WS_STATION_DETAIL_URL              = "http://www.velo.toulouse.fr/service/stationdetails/";

	public VeloToulouseStationManager() {
    // TODO Auto-generated constructor stub
    }
	
    public VeloToulouseStationManager(Context launched) {
       super(launched);
    }
    
    public String[] getCities(){
    	return new String[]{"Toulouse"};
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
