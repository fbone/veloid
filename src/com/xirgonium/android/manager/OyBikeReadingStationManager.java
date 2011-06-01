package com.xirgonium.android.manager;

import android.content.Context;

public class OyBikeReadingStationManager extends ConstructorOyBike2 {

    public OyBikeReadingStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public OyBikeReadingStationManager(Context ctx) {
        super(ctx);
    }
    
    @Override
    protected String getCountry() {
        return "United Kingdom";
    }

    @Override
    protected String getURLToUpdate() {
        return "http://www.oybike.com/oybike/stands.nsf/getsite?openagent&site=reading&format=xml&key=diolev";
    }

    @Override
    public String[] getCities() {
        return new String[]{"Reading"};
    }

}
