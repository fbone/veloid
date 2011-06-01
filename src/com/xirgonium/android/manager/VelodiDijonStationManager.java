package com.xirgonium.android.manager;

import android.content.Context;

public class VelodiDijonStationManager extends ConstructorClearChannelSpanishTypeStationManager {

	String	WS_ALL_STATIONS_AND_DETAIL_URL	= "http://www.velodi.net/localizaciones/localizaciones.php";

	public VelodiDijonStationManager() {
    // TODO Auto-generated constructor stub
    }
	
	public VelodiDijonStationManager(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "Dijon" };
	}

	@Override
    String getURLToUpdate() {
       return WS_ALL_STATIONS_AND_DETAIL_URL;
    }
}
