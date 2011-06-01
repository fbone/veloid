package com.xirgonium.android.manager;

import android.content.Context;

public class BiclooNantesStationManager extends ConstructorJCDecaultVelibLikeStationManager {
    
	private final static String  WS_ALL_STATIONS_URL                = "http://www.bicloo.nantesmetropole.fr/service/carto";

	private final static String  WS_STATION_DETAIL_URL              = "http://www.bicloo.nantesmetropole.fr/service/stationdetails/";

	public BiclooNantesStationManager() {}
	
	@Override
	boolean useOpenTag() {
	return false;
	}
	
    public BiclooNantesStationManager(Context launched) {
       super(launched);
    }
    
    public String[] getCities(){
    	return new String[]{"Nantes"};
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
}
