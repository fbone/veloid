package com.xirgonium.android.manager;

import android.content.Context;

//public class VelomaggMontpellier extends ConstructorSmoove {
//
//  String url = "http://www.velomagg.com/10003.html";
//  
//  @Override
//  protected String getUrlOfInformationPage() {
//    return url;
//  }
//
//  
//  
//}

public class MetroVeloGrenoble extends ConstructorJCDecaultVelibLikeStationManager {//ConstructorSmoove {

	//String url = "http://www.velomagg.com/10003.html";

	// public final static String WS_VELIB_ALL_STATIONS_URL = "http://xirgonium.free.fr/testxml/carto.xml";
	private final static String	WS_ALL_STATIONS_URL		= " http://vms.metrovelo.fr/service/carto";

	// ---- Add the station ID at the end of the URL to get data from Velib'
	private final static String	WS_STATION_DETAIL_URL	= " http://vms.metrovelo.fr/service/stationdetails/";

	public MetroVeloGrenoble() {
	}

	public MetroVeloGrenoble(Context launched) {
		super(launched);
	}

	@Override
	protected String getAllStationURL() {
		return WS_ALL_STATIONS_URL;
	}

	@Override
	protected String getOneStationInfoURL() {
		return WS_STATION_DETAIL_URL;
	}

	@Override
	public String[] getCities() {
		return new String[] { "Grenoble" };
	}

	@Override
	boolean useOpenTag() {
		return true;
	}

	@Override
	public boolean isSupportedbyMolib() {
		return false;
	}

}
