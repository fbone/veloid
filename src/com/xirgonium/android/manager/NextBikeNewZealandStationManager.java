package com.xirgonium.android.manager;

import android.content.Context;

public class NextBikeNewZealandStationManager extends ConstructorNextBikeStationManager {

	final String	COUNTRY_NAME	= "New Zealand";
	final String	NETWORK_NAME	= "Next Bike [NZ]";

	public NextBikeNewZealandStationManager() {
    // TODO Auto-generated constructor stub
    }
	
	public NextBikeNewZealandStationManager(Context launched) {
		super(launched);
	}

	@Override
	protected String getCountry() {
		return COUNTRY_NAME;
	}

	@Override
	public String[] getCities() {
		return new String[] { "Auckland" };
	}

}
