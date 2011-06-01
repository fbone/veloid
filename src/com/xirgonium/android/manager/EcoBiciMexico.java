package com.xirgonium.android.manager;

import android.content.Context;

public class EcoBiciMexico extends ConstructorClearChannelSpanishType2 {

	String	STATION_LIST_URL	= "http://www.ecobici.df.gob.mx/localizaciones/localizaciones.php";
	String	STATION_DETAIL_URL	= "http://www.ecobici.df.gob.mx/localizaciones/localizaciones_body.php";

	public EcoBiciMexico() {
	}

	public EcoBiciMexico(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "Mexico" };
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
