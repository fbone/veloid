package com.xirgonium.android.manager;

import android.content.Context;

public class OyBikeLondonStationManager extends ConstructorOyBikeType {

    public OyBikeLondonStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public OyBikeLondonStationManager(Context ctx) {
        super(ctx);
    }
    
    @Override
    protected String getCountry() {
        return "United Kingdom";
    }

    @Override
    protected String getUrlOfInformationPage() {
        return "http://oybike.com/OYBIKE/obhome.nsf/locations.html";
    }

    @Override
    public String[] getCities() {
        return new String[]{"London", "Cheltenham", "Cambridge", "Reading", "Farnborough"};
    }

}
