package com.xirgonium.android.manager;

import android.content.Context;

public class VeloceaVannes extends ConstructorOyBike2 {

    public VeloceaVannes() {
    // TODO Auto-generated constructor stub
    }
    
    public VeloceaVannes(Context ctx) {
        super(ctx);
    }
    
    @Override
    protected String getCountry() {
        return "France";
    }

    @Override
    protected String getURLToUpdate() {
        return "http://www.velocea.fr/oybike/stands.nsf/getsite?openagent&site=vannes&format=xml&key=diolev";
    }

    @Override
    public String[] getCities() {
        return new String[]{"Vannes"};
    }

}
