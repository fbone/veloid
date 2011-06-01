package com.xirgonium.android.manager;

import android.content.Context;

public class VCubBordeaux extends ConstructorVCubStationManager {

    String WS_ALL_STATIONS_AND_DETAIL_URL = "http://www.vcub.fr/stations/plan";

    public VCubBordeaux() {
    }

    public VCubBordeaux(Context launched) {
        super(launched);
    }

    public String[] getCities() {
        return new String[] { "Bordeaux" };
    }

    @Override
    protected String getCountry() {
        return "France";
    }

    @Override
    protected String getUrlOfInformationPage() {
        return WS_ALL_STATIONS_AND_DETAIL_URL;
    }

}
