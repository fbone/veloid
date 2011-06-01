package com.xirgonium.android.manager;

import android.content.Context;

public class LeVeloMarseilleStationManager extends ConstructorJCDecaultVelibLikeStationManager {
    
	public final static String  WS_ALL_STATIONS_URL                = "http://www.levelo-mpm.fr/service/carto";
	
	public final static String  WS_STATION_DETAIL_URL              = "http://www.levelo-mpm.fr/service/stationdetails/";

	public LeVeloMarseilleStationManager() {}
	
    public LeVeloMarseilleStationManager(Context launched) {
       super(launched);
    }
    
    public String[] getCities(){
    	return new String[]{"Marseille"};
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
