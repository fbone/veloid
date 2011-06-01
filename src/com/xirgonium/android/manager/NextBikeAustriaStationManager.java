package com.xirgonium.android.manager;

import android.content.Context;

public class NextBikeAustriaStationManager extends ConstructorNextBikeStationManager {

	final String	COUNTRY_NAME	= "Austria";
	final String	NETWORK_NAME	= "Next Bike [AT]";

	public NextBikeAustriaStationManager(){}
	
	public NextBikeAustriaStationManager(Context launched) {
		super(launched);
	}

	@Override
	protected String getCountry() {
		return COUNTRY_NAME;
	}

	@Override
	public String[] getCities() {
		return new String[] { "Neusiedler See" };
	}
}
