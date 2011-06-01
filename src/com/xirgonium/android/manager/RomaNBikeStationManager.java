package com.xirgonium.android.manager;

import android.content.Context;

public class RomaNBikeStationManager extends ConstructorBincinettaV2 {

    public RomaNBikeStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public RomaNBikeStationManager(Context launched) {
        super(launched);
        // TODO Auto-generated constructor stub
    }

    private String url = "http://www.roma-n-bike.com/citta_v2.asp?id=18&pag=2";
    private String country = "Italia";
    
    @Override
    protected String getCountry() {      
        return country;
    }

    @Override
    protected String getUrlOfInformationPage() {      
        return url;
    }

    @Override
    public String[] getCities() {       
        return new String[]{"Roma"};
    }
}
