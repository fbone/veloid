package com.xirgonium.android.manager;

import android.content.Context;

public class OyBikeCardiffStationManager extends ConstructorOyBike2 {

    public OyBikeCardiffStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public OyBikeCardiffStationManager(Context ctx) {
        super(ctx);
    }
    
    @Override
    protected String getCountry() {
        return "United Kingdom";
    }

    @Override
    protected String getURLToUpdate() {
        return "http://www.oybike.com/oybike/stands.nsf/getsite?openagent&site=cardiff&format=xml&key=diolev";
    }

    @Override
    public String[] getCities() {
        return new String[]{"Cardiff"};
    }

}
