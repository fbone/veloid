package com.xirgonium.android.manager;

import android.content.Context;

public class LausanneRoule extends ConstructorBincinettaV2 {

    public LausanneRoule() {}
    
    public LausanneRoule(Context launched) {
        super(launched);
        // TODO Auto-generated constructor stub
    }

    private String url = "http://www.bicincitta.com/citta_ch.asp";
    private String country = "Suisse";
    
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
        return new String[]{"Lausanne"};
    }

}
