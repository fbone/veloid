package com.xirgonium.android.manager;

import android.content.Context;

public class OyBikeFarnboroughStationManager extends ConstructorOyBike2 {

    public OyBikeFarnboroughStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public OyBikeFarnboroughStationManager(Context ctx) {
        super(ctx);
    }
    
    @Override
    protected String getCountry() {
        return "United Kingdom";
    }

    @Override
    protected String getURLToUpdate() {
        return "http://www.oybike.com/oybike/stands.nsf/getsite?openagent&site=farnborough&format=xml&key=diolev";
    }

    @Override
    public String[] getCities() {
        return new String[]{"Farnborough"};
    }

}
