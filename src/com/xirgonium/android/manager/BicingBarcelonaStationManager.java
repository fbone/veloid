package com.xirgonium.android.manager;

import android.content.Context;

public class BicingBarcelonaStationManager extends ConstructorClearChannelSpanishTypeStationManager {

	String	WS_ALL_STATIONS_AND_DETAIL_URL	= "http://www.bicing.cat/localizaciones/localizaciones.php?TU5fTE9DQUxJWkFDSU9ORVM%3D&MQ%3D%3D";

	public BicingBarcelonaStationManager() {}
	
	public BicingBarcelonaStationManager(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "Barcelona" };
	}
	
	
	@Override
    String getURLToUpdate() {
       return WS_ALL_STATIONS_AND_DETAIL_URL;
    }
}
