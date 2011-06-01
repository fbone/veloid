package com.xirgonium.android.manager;

import android.content.Context;

public class YelloLaRochelle extends ConstructorRTCLaRochelle {

	String	WS_ALL_STATIONS_AND_DETAIL_URL	= "http://127.0.0.1/larochelle.html";

	public YelloLaRochelle() {
	}

	public YelloLaRochelle(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "La Rochelle" };
	}

	@Override
	protected String getCountry() {
		return "France";
	}

	@Override
	protected String getInfoURL() {
		return WS_ALL_STATIONS_AND_DETAIL_URL;
	}

}
