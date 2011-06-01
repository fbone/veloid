package com.xirgonium.android.manager;

import android.content.Context;

public class BipPerpignanStationManager extends ConstructorClearChannelSpanishTypeStationManager {

	String	WS_ALL_STATIONS_AND_DETAIL_URL	= "http://www.bip-perpignan.fr/localizaciones/localizaciones.php";

	public BipPerpignanStationManager() {}
	
	public BipPerpignanStationManager(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "Perpignan" };
	}


    @Override
    String getURLToUpdate() {
       return WS_ALL_STATIONS_AND_DETAIL_URL;
    }
}
