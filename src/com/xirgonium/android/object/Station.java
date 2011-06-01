package com.xirgonium.android.object;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.R;

public class Station extends HashMap<String, String> implements Serializable, Comparable<Station> {

	private static final long	serialVersionUID	= 70100L;

	private String				name;
	private String				comment;
	private String				id;
	private int					freeSlot;
	private int					availableBikes;
	private String				address;
	private String				fullAddress;
	private double				latitude;
	private double				longitude;
	private String				open;
	private int					favorite;
	private double				distance;
	private int					updateStatus		= 0;
	private String				network;
	private int					favoriteColor  = -1;

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getComment() {
	    if(comment!= null){
	        return comment;
	    }else{
	        return name;
	    }
	}

	public void setComment(String comment) {
		this.comment = comment;
		this.put(Constant.S_LABEL_KEY, comment);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		this.put(Constant.S_ID_KEY, id);
	}

	public int getFreeSlot() {
		return freeSlot;
	}

	public void setFreeSlot(int freeSlot) {
		this.freeSlot = freeSlot;
		this.put(Constant.S_FREE_SLOTS_KEY, String.valueOf(freeSlot));
	}

	public int getAvalableBikes() {
		return availableBikes;
	}

	public void setAvailableBikes(int availableBikes) {
		this.availableBikes = availableBikes;
		this
				.put(Constant.S_AVAILABLE_BIKES_KEY, String
						.valueOf(availableBikes));
	}

	/**
	 * Get the available jey in an ordonnanced way : ID (not yet) LABEL FREE
	 * SLOTS AVAILABLE BIKES
	 * 
	 * @see Constant
	 * @return the ordonanced key
	 */
	public static String[] getOrderedKeys() {
		String[] keys = { Constant.S_LABEL_KEY, Constant.S_FREE_SLOTS_KEY,
				Constant.S_AVAILABLE_BIKES_KEY };
		return keys;
	}

	/**
	 * Get the available values of the layout linked to labels: ID (not yet)
	 * LABEL FREE SLOTS AVAILABLE BIKES
	 * 
	 * @see Constant
	 * @return the ordonnanced IDs
	 */
	public static int[] getOrderedIds() {
		int[] ids = { R.id.si_station_title, R.id.si_free_slots_val,
				R.id.si_available_bikes_val };
		return ids;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public double getLatitude() {
		return latitude;
	}

	public int getMicroDegreeLatitude() {
		return (int) (latitude * 1000000);
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getMicroDegreeLongitude() {
		return (int) (longitude * 1000000);
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public int getAvailableBikes() {
		return availableBikes;
	}

	public int getFavorite() {
		return favorite;
	}

	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		id = (String) ois.readObject();
	}

	public void writeObject(ObjectOutputStream oos) throws IOException {
//		Log.d("STATION", "Save object");
		oos.writeObject(id);
	}

	public int getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateSuccess(int updateStatus) {
		this.updateStatus = updateStatus;
	}

	public int getFavoriteColor() {
		return favoriteColor;
	}

	public void setFavoriteColor(int favoriteColor) {
		this.favoriteColor = favoriteColor;
	}

	public int compareTo(Station another) {
		if(getFavoriteColor() == another.getFavoriteColor()){
			return getId().compareTo(another.getId());
		} else {
			return getFavoriteColor()-another.getFavoriteColor();
		}
	}
}