package com.xirgonium.android.manager;

import android.content.Context;

public class BixiMontrealStationManager extends ConstructorBixiStationManager {

    String WS_ALL_STATIONS_AND_DETAIL_URL = "https://profil.bixi.ca/data/bikeStations.xml";

    public BixiMontrealStationManager() {
    }

    public BixiMontrealStationManager(Context launched) {
        super(launched);
    }

    public String[] getCities() {
        return new String[] { "Montreal" };
    }

    @Override
    protected String getCountry() {
        return "Quebec";
    }

    @Override
    protected String getUrlOfInformationPage() {
        return WS_ALL_STATIONS_AND_DETAIL_URL;
    }

}
