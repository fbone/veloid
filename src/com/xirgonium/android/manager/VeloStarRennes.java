package com.xirgonium.android.manager;

import android.content.Context;

public class VeloStarRennes extends ConstructorVeloStar2 {
	
	public VeloStarRennes() {
	}

	public VeloStarRennes(Context ctx) {
		super(ctx);
	}

	@Override
	public String[] getCities() {
		return new String[] { "Rennes" };
	}

	@Override
	String getURLToUpdate() {
		return "http://data.keolis-rennes.com/xml/?version=1.0&key=...&cmd=getstation";
//		return "http://127.0.0.1/~xirgonium/r.html";
	}

}
