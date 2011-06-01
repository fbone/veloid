package com.xirgonium.android.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorClearChannelSpanishType2 extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;
	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	public ConstructorClearChannelSpanishType2() {
	}

	public ConstructorClearChannelSpanishType2(Context launched) {
		super(launched);
	}

	/**
	 * 
	 * Get on the bip APIs the informations related to all Stations.
	 * 
	 * @param aStation
	 * @return The station given as parameter but completed with bip information
	 * 
	 */
	public void updateStationListDynamicaly() throws NoInternetConnection {
		// Log.d("MGR", "Get the list of station from web");

		// Log.d("MGR", "All stations grabbed from web, delete all the table");
		this.clearListOfStationFromDatabase();

		Vector<Station> favorite = restoreFavoriteFromDataBase();
		Hashtable<String, Station> favorites = new Hashtable<String, Station>();

		// TODO see if this part can be optimized - restore favorite
		for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();
			// Log.d("MGR", "Found favorite station id : " + station.getId());
			favorites.put(station.getId(), station);
		}
		try {
			URL url = new URL(getStationListURL());
			URLConnection urlconn = url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

			// Log.d("MGR", "open url " + getURLToUpdate());


			try {
				InputStreamReader read = new InputStreamReader(urlconn.getInputStream(), "ISO-8859-1");

				BufferedReader inReader = new BufferedReader(read);

				LineNumberReader reader = new LineNumberReader(inReader);

				String line = null;
				boolean endOfProcess = false;

				

				String beginNewPoint = "point";
				String beginStationId = "data:";
				String recordStation = "map.addOverlay";

				/*
				 * point = new GLatLng(41.67235600000000000,-0.89798600000000000); marker[1]= new GMarker(point, icon); GEvent.addListener(marker[1],'click',function(){ $.ajax({ async:true, type:
				 * "POST", dataType: "html", data:"idStation="+2+"&addressnew=RVhQTy4gUEFCTE8gUlVJWiBQSUNBU1NPIChQdGEuIE5vcnRlKQ=="+"&s_id_idioma="+"en", contentType:
				 * "application/x-www-form-urlencoded", url: "/callwebservice/StationBussinesStatus.php", beforeSend:function(){ marker[1].openInfoWindowHtml("cargando.. ");
				 * 
				 * },success: function(datos){ marker[1].openInfoWindowHtml(datos); } });
				 * 
				 * }); map.addOverlay(marker[1]);
				 * 
				 * 
				 * point = new GLatLng(41.67046400000000000,-0.90466700000000000); marker[0]= new GMarker(point, icon); GEvent.addListener(marker[0],'click',function(){ $.ajax({ async:true, type:
				 * "POST", dataType: "html", data:"idStation="+1+"&addressnew=RVhQTy4gVE9SUkUgREVMIEFHVUE="+"&s_id_idioma="+"es", contentType: "application/x-www-form-urlencoded", url:
				 * "/callwebservice/StationBussinesStatus.php", beforeSend:function(){ marker[0].openInfoWindowHtml("");
				 * 
				 * },success: function(datos){ marker[0].openInfoWindowHtml(datos); } });
				 * 
				 * }); map.addOverlay(marker[0]);
				 */

				Station toAdd = null;
				while ((line = reader.readLine()) != null && !endOfProcess) {
					
					

					if (line.trim().startsWith(beginNewPoint)) {
						toAdd = new Station();
						toAdd.setNetwork(getNetworkId());
						String latlon = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
						StringTokenizer token = new StringTokenizer(latlon, ",");
						int index = 0;
						while (token.hasMoreElements()) {
							String latOrLon = (String) token.nextElement();
							Double lolasnum = Double.parseDouble(latOrLon.trim());
							if (index == 0) {
								toAdd.setLongitude(lolasnum);
								index++;
							} else if (index == 1) {
								toAdd.setLatitude(lolasnum);
							}
						}
					} else if (line.trim().startsWith(beginStationId)) {
						String idPrefix = "idStation=";
						int indexIdStation = line.indexOf(idPrefix);
						String idInThat = line.substring(indexIdStation + idPrefix.length() + 2);
						idInThat = idInThat.substring(0, idInThat.indexOf("+"));
						//Long id = Long.parseLong(idInThat);
						toAdd.setId(idInThat);
					} else if (line.trim().startsWith(recordStation)) {
						if (favorites.containsKey(toAdd.getId())) {
							toAdd.setFavorite(1);
							toAdd.setComment(favorites.get(toAdd.getId()).getComment());
						} else {
							toAdd.setFavorite(0);
							toAdd.setComment(toAdd.getName());
						}

						lastUpdatedFavorites.put(toAdd.getId(), toAdd);
						Log.d("MGR", "Station " + toAdd.getId() + " added");

					}
					
						endOfProcess = line.trim().startsWith("<div id=\"estaciones\">");
				}

			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				throw new NoInternetConnection(getStationListURL());
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				throw new NoInternetConnection(getStationListURL());
			} catch (Exception e) {
				Log.e("MGR", "Unable to parse information  - CAUSE : " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			throw new NoInternetConnection(getStationListURL());
		}

		// Log.d("MGR", "Restore all information");

		for (Enumeration<String> enumStation = lastUpdatedFavorites.keys(); enumStation.hasMoreElements();) {
			Station station = lastUpdatedFavorites.get(enumStation.nextElement());
			saveStationIntoDB(station);
		}
		// Log.d("MGR", "List successfully restored");
	}

	/**
	 * 
	 * Get the informations related to a Station. Here all the information are gathered the first time. Then theyr are used if they are not older than 30 secondes
	 * 
	 * @param aStation
	 * @return The station given as parameter but completed with bip information
	 * 
	 */
	public Station fillDynamicInformationForAStation(Station aStation) throws NoInternetConnection {

		try {
			
			String data = "addressnew=&idStation="+aStation.getId()+"&s_id_idioma=en";

			URL url = new URL(getStationDetailURL());
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; fr; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5");
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	        wr.write(data);
	        
	        wr.flush();

			
			
//			URL url = new URL(getStationDetailURL() + aStation.getId());
//			URLConnection urlconn = url.openConnection();
			conn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

			Log.d("MGR", "open url " + getStationDetailURL() + aStation.getId());


			InputStreamReader read = new InputStreamReader(conn.getInputStream(), "ISO-8859-1");

			BufferedReader inReader = new BufferedReader(read);

			LineNumberReader reader = new LineNumberReader(inReader);

			String line = null;
			boolean endOfProcess = false;

			String bicyclePrefix = "bicycle : ";
			String slotsPrefix = "Slots : ";
			
			while ((line = reader.readLine()) != null && !endOfProcess) {
				int posBic = line.indexOf(bicyclePrefix);
				int posSlots = line.indexOf(slotsPrefix);
				if (posBic != -1) {
					String bicyclesInside = line.substring(posBic);
					bicyclesInside = bicyclesInside.substring(bicyclePrefix.length()-1, bicyclesInside.indexOf("<")).trim();
					int bikes = Integer.parseInt(bicyclesInside);
					aStation.setAvailableBikes(bikes);
				}
				
				if (posSlots != -1) {
					String slotsInside = line.substring(posSlots);
					slotsInside = slotsInside.substring(slotsPrefix.length()-1, slotsInside.indexOf("<")).trim();
					int slots = Integer.parseInt(slotsInside);
					aStation.setFreeSlot(slots);
				}
			}

		} catch (UnsupportedEncodingException e) {
			Log.e(this.toString(), "Unsupported encoding " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(this.toString(), "IOException " + e.getMessage());
			e.printStackTrace();
		}

		return aStation;
	}

	abstract String getStationDetailURL();

	abstract String getStationListURL();

	@Override
	public Vector<Station> restoreFavoriteFromDataBaseAndWeb() throws NoInternetConnection {
		return super.restoreFavoriteFromDataBaseAndWebImpl(getNetworkId());
	}

	@Override
	public void clearListOfStationFromDatabase() {
		super.clearListOfStationFromDatabaseImpl(getNetworkId());
	}

	@Override
	public Vector<Station> restoreAllStationWithminimumInfoFromDataBase() {
		return super.restoreAllStationWithminimumInfoFromDataBaseImpl(getNetworkId());
	}

	@Override
	public Vector<Station> fillInformationFromDB(Vector<Station> stations) {
		return super.fillInformationFromDBImpl(getNetworkId(), stations);
	}

	@Override
	public Vector<Station> fillInformationFromDBAndWeb(Vector<Station> stations) throws NoInternetConnection {
		return super.fillInformationFromDBAndWebImpl(getNetworkId(), stations);
	}

	@Override
	public Vector<Station> restoreFavoriteFromDataBase() {
		return super.restoreFavoriteFromDataBaseImpl(getNetworkId());
	}
}
