package com.xirgonium.android.manager;

import android.content.Context;

public class BikeMiMilanoStationManager extends ConstructorClearChannelSpanishTypeStationManager {

	String	WS_ALL_STATIONS_AND_DETAIL_URL	= "http://www.bikemi.com/localizaciones/localizaciones.php";

	public BikeMiMilanoStationManager() {}
	
	public BikeMiMilanoStationManager(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "Milano" };
	}
	
	
	@Override
    String getURLToUpdate() {
       return WS_ALL_STATIONS_AND_DETAIL_URL;
    }
}
