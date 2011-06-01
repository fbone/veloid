package com.xirgonium.android.manager;

import android.content.Context;

public class VeolCaenStationManager extends ConstructorClearChannelSpanishTypeStationManager {

	String	WS_ALL_STATIONS_AND_DETAIL_URL	= "http://www.veol.caen.fr/localizaciones/localizaciones.php";

	public VeolCaenStationManager() {
    // TODO Auto-generated constructor stub
    }
	
	public VeolCaenStationManager(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "Caen" };
	}
	
	@Override
    String getURLToUpdate() {
       return WS_ALL_STATIONS_AND_DETAIL_URL;
    }
}
