package com.xirgonium.android.manager;

import android.content.Context;

public class BiziZaragozaStationManager extends ConstructorClearChannelSpanishType2 {

	String	STATION_LIST_URL	= "http://www.bizizaragoza.com/localizaciones/localizaciones.php";
	String	STATION_DETAIL_URL	= "http://www.bizizaragoza.com/callwebservice/StationBussinesStatus.php";

	public BiziZaragozaStationManager() {
	}

	public BiziZaragozaStationManager(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "Zaragoza" };
	}

	@Override
	String getStationDetailURL() {
		return STATION_DETAIL_URL;
	}

	@Override
	String getStationListURL() {
		return STATION_LIST_URL;
	}

}
