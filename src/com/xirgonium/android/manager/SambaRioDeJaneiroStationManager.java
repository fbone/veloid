package com.xirgonium.android.manager;

import android.content.Context;

public class SambaRioDeJaneiroStationManager extends ConstructorSambaType {

    public SambaRioDeJaneiroStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public SambaRioDeJaneiroStationManager(Context ctx) {
        super(ctx);
    }
    
    @Override
    protected String getCountry() {
        return "Brazil";
    }

    @Override
    protected String getUrlOfInformationPage() {
        return "http://www.zae.com.br/zaerio/mapaestacao.asp";
    }

    @Override
    public String[] getCities() {
        return new String[]{"Rio de Janeiro"};
    }

}
