package com.xirgonium.android.manager;

import android.content.Context;

public class SmartBikeDCWashingtonStationManager extends ConstructorClearChannelAmericanType {

    public SmartBikeDCWashingtonStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public SmartBikeDCWashingtonStationManager(Context ctx) {
        super(ctx);
    }
    
    @Override
    protected String getCountry() {
        return "USA";
    }

    @Override
    protected String getUrlOfInformationPage() {
        return "https://www.smartbikedc.com/smartbike_locations.asp";
    }

    @Override
    public String[] getCities() {
        return new String[]{"Washington"};
    }
}
