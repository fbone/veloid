package com.xirgonium.android.manager;

import android.content.Context;

public class VeloBleuNice extends ConstructorUnknownLikeNiceVeloBleuStationManager {

	String	WS_ALL_STATIONS_AND_DETAIL_URL	= "http://www.velobleu.org/nice/nicecms.nsf/vStands?readviewentries&count=999&ms=1247997198725";

	public VeloBleuNice() {}
	
	public VeloBleuNice(Context launched) {
		super(launched);
	}

	public String[] getCities() {
		return new String[] { "Nice" };
	}
	
	
	@Override
    String getURLToUpdate() {
       return WS_ALL_STATIONS_AND_DETAIL_URL;
    }
}
