package com.xirgonium.android.manager;

import android.content.Context;

public class VeloBleuNice2 extends ConstructorOyBike2 {

    public VeloBleuNice2() {
    // TODO Auto-generated constructor stub
    }
    
    public VeloBleuNice2(Context ctx) {
        super(ctx);
    }
    
    @Override
    protected String getCountry() {
        return "France";
    }

    @Override
    protected String getURLToUpdate() {
        return "http://www.velobleu.org/oybike/stands.nsf/getsite?openagent&site=nice&format=xml&key=diolev";
    }

    @Override
    public String[] getCities() {
        return new String[]{"Nice"};
    }

}
