package com.xirgonium.android.map;

import com.google.android.maps.GeoPoint;

public class VeloidMapCenter extends GeoPoint {

	public static final int	TYPE_GEOLOC		= 0;
	public static final int	TYPE_ADDR		= 1;
	public static final int	TYPE_FAVORITE	= 2;

	String				name;
	int					type			= -1;

	public VeloidMapCenter(int lat, int lon) {
		super(lat, lon);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
