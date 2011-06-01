package com.xirgonium.android.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorBincinettaV2 extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;

	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	private final String					PREFIX_LATITUDE			= "var sita_x =";
	private final String					PREFIX_LONGITUDE		= "var sita_y =";
	private final String					PREFIX_NAME				= "var sita_n =";
	private final String					PREFIX_AVAILABILITIES	= "var sita_b =";
	private final String					PREFIX_ID				= "var sita_p =";

	public ConstructorBincinettaV2() {
	}

	/*
	 * ------------------------------- ABSTRACT METHODS
	 * -------------------------------
	 */
	protected abstract String getCountry();

	protected abstract String getUrlOfInformationPage();

	/*
	 * ------------------------------- SPECIFIC METHODS
	 * -------------------------------
	 */

	public ConstructorBincinettaV2(Context launched) {
		super(launched);
	}

	@Override
	public Station fillDynamicInformationForAStation(Station aStation) throws NoInternetConnection {
		long now = new Date().getTime();
		if ((now - lastUpdateTimeStamp < 30000)
				&& lastUpdatedFavorites.containsKey(aStation.getId())) {
			Station toRet = lastUpdatedFavorites.get(aStation.getId());
			return toRet;
		} else {
			lastUpdateTimeStamp = now;
			getStationInfoFromPage();
			Station toRet = lastUpdatedFavorites.get(aStation.getId());
			return toRet;
		}
	}

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
	public Vector<Station> fillInformationFromDBAndWeb(Vector<Station> stations)
			throws NoInternetConnection {
		return super.fillInformationFromDBAndWebImpl(getNetworkId(), stations);
	}

	@Override
	public Vector<Station> restoreFavoriteFromDataBase() {
		return super.restoreFavoriteFromDataBaseImpl(getNetworkId());
	}

	@Override
	public void updateStationListDynamicaly() throws NoInternetConnection {
		getStationInfoFromPage();

		this.clearListOfStationFromDatabase();

		for (Enumeration<String> enumStation = lastUpdatedFavorites.keys(); enumStation
				.hasMoreElements();) {
			Station station = lastUpdatedFavorites.get(enumStation.nextElement());
			saveStationIntoDB(station);
		}
	}

	protected void getStationInfoFromPage() throws NoInternetConnection {
		Vector<Station> favorite = restoreFavoriteFromDataBase();
		Hashtable<String, Station> favorites = new Hashtable<String, Station>();
		Hashtable<String, Station> buildingStations = new Hashtable<String, Station>();

		// TODO see if this part can be optimized - restore favorite
		for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();
			favorites.put(station.getId(), station);
		}
		try {
			URL url = new URL(getUrlOfInformationPage());
			URLConnection urlconn = url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

			try {
				// isolate the information part
				InputStreamReader reader = new InputStreamReader(urlconn.getInputStream(),
						"ISO-8859-1");

				BufferedReader inReader = new BufferedReader(reader);
				String line = null;

				boolean endOfProcess = false;

				Station toAdd = new Station();
				toAdd.setNetwork(getNetworkId());

				while ((line = inReader.readLine()) != null && !endOfProcess) {
					int index = 1;
					line = line.trim();
					line = line.replace("\"", "");
					line = line.replace("+", "");
					line = line.replace(";", "");
					int processedData = 0;

					if (line.startsWith(PREFIX_LATITUDE)) {

						// Log.d("ROMA", line);
						String values = line.substring(PREFIX_LATITUDE.length());
						StringTokenizer lats = new StringTokenizer(values, "_");
						while (lats.hasMoreTokens()) {
							String aLat = lats.nextToken();
							double lat = Double.parseDouble(aLat);
							Log.d("BINCICITTA", "latitude trouvée : " + aLat);
							Station aStation = new Station();
							if (buildingStations.containsKey(String.valueOf(index))) {
								aStation = buildingStations.get(String.valueOf(index));
							}
							aStation.setLatitude(lat);
							buildingStations.put(String.valueOf(index++), aStation);

						}
						if (++processedData == 5)
							break;
					}
				
					index = 1;
					if (line.startsWith(PREFIX_LONGITUDE)) {
						// Log.d("ROMA", line);
						String values = line.substring(PREFIX_LONGITUDE.length());
						StringTokenizer longs = new StringTokenizer(values, "_");
						while (longs.hasMoreTokens()) {
							String aLong = longs.nextToken();
							double lng = Double.parseDouble(aLong);
							Log.d("BINCICITTA", "longitude trouvée : " + aLong);
							Station aStation = new Station();
							if (buildingStations.containsKey(String.valueOf(index))) {
								aStation = buildingStations.get(String.valueOf(index));
							}
							aStation.setLongitude(lng);
							buildingStations.put(String.valueOf(index++), aStation);
						}
						if (++processedData == 5)
							break;
					}
		//			Log.d("BINCICITTA", "Nb Stations  2 : " + buildingStations.size() + " : "
			//				+ buildingStations);

					index = 1;
					if (line.startsWith(PREFIX_NAME)) {
						String values = line.substring(PREFIX_NAME.length());
						StringTokenizer names = new StringTokenizer(values, "_");
						while (names.hasMoreTokens()) {
							String aName = names.nextToken();

							Station aStation = new Station();
							if (buildingStations.containsKey(String.valueOf(index))) {
								aStation = buildingStations.get(String.valueOf(index));
							}
							aStation.setName(aName);
							buildingStations.put(String.valueOf(index++), aStation);
						}
						if (++processedData == 5)
							break;
					}
			//		Log.d("BINCICITTA", "Nb Stations  3 : " + buildingStations.size() + " : "
				//			+ buildingStations);

					index = 1;
					if (line.startsWith(PREFIX_ID)) {
//						Log.d("Bicincitta", line);
						String values = line.substring(PREFIX_ID.length());
						StringTokenizer ids = new StringTokenizer(values, "_");
						while (ids.hasMoreTokens()) {
							String anID = ids.nextToken();
							Station aStation = new Station();
							if (buildingStations.containsKey(String.valueOf(index))) {
								aStation = buildingStations.get(String.valueOf(index));
							}
							int supposedId = FormatUtility.intFromString(anID);
							if(supposedId != 0){
							aStation.setId(String.valueOf(supposedId));
							buildingStations.put(String.valueOf(index++), aStation);
							}
							
						}
						

						// Patch pour suisse
						if (index == 1) {
							Enumeration<String> keys = buildingStations.keys();
							while (keys.hasMoreElements()) {
								String key = (String) keys.nextElement();
								Station current = buildingStations.get(key);
								current.setId(key);
								buildingStations.put(key, current);
								//Log.d("BICI", "ID SET TO " + key);
							}
						}
						
						if (++processedData == 5)
							break;
					}
					//Log.d("BINCICITTA", "Nb Stations  4 : " + buildingStations.size() + " : "
						//	+ buildingStations);
					index = 1;
					if (line.startsWith(PREFIX_AVAILABILITIES)) {
						// Log.d("ROMA", line);
						String values = line.substring(PREFIX_AVAILABILITIES.length());
						StringTokenizer availabilities = new StringTokenizer(values, "_");
						while (availabilities.hasMoreTokens()) {
							String anAv = availabilities.nextToken();
							Station aStation = new Station();
							if (buildingStations.containsKey(String.valueOf(index))) {
								aStation = buildingStations.get(String.valueOf(index));
							}
							int[] parsed = transformAvailabilitiesForString(anAv);
							aStation.setAvailableBikes(parsed[0]);
							aStation.setFreeSlot(parsed[1]);
							buildingStations.put(String.valueOf(index++), aStation);
						}
						if (++processedData == 5)
							break;
					}
				}

				//Log.d("BINCICITTA", "Nb Stations  5 : " + buildingStations.size() + " : "
					//	+ buildingStations);

				Collection<Station> stations = buildingStations.values();
				for (Iterator<Station> iterator = stations.iterator(); iterator.hasNext();) {
				//	Log.d("BICINCITTA ", "++ stations");
					Station station = (Station) iterator.next();
					station.setNetwork(getNetworkId());
					if (station.getId() != null) {
						if (favorites.containsKey(station.getId())) {
							station.setFavorite(1);
							station.setComment(favorites.get(station.getId()).getComment());
							station.setFavoriteColor(favorites.get(station.getId()).getFavoriteColor());
						} else {
							station.setFavorite(0);
							station.setComment(station.getName());
							station.setFavoriteColor(-1);
						}
						lastUpdatedFavorites.put(station.getId(), station);
					}
				}

			//	Log.d("BINCICITTA", "Nb Stations avant Sauvegarde: " + lastUpdatedFavorites.size()
				//		+ " : " + lastUpdatedFavorites);

			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				throw new NoInternetConnection(getUrlOfInformationPage());
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				throw new NoInternetConnection(getUrlOfInformationPage());
			} catch (Exception e) {
				Log.e("MGR", "Unable to parse information  - CAUSE : " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			throw new NoInternetConnection(getUrlOfInformationPage());
		}
	}

	private int[] transformAvailabilitiesForString(String values) {
		int bikes = 0;
		int total = 0;

		StringReader reader = new StringReader(values);

		try {
			while (reader.ready()) {
				int aCharAsInt = reader.read();
				if (aCharAsInt == -1)
					break;
				char aChar = (char) aCharAsInt;
				if (aChar != 'x')
					total++;
				if (aChar == '4')
					bikes++;
			}
		} catch (IOException e) {

		}

		int[] toret = { bikes, total - bikes };
		return toret;
	}

}
