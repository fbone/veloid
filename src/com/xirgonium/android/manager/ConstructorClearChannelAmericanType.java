package com.xirgonium.android.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorClearChannelAmericanType extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;
	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	public ConstructorClearChannelAmericanType() {
	}

	public ConstructorClearChannelAmericanType(Context ctx) {
		super(ctx);
	}

	/*
	 * ------------------------------- ABSTRACT METHODS -------------------------------
	 */
	protected abstract String getCountry();

	protected abstract String getUrlOfInformationPage();

	/*
	 * ------------------------------- SPECIFIC METHODS -------------------------------
	 */

	@Override
	public void updateStationListDynamicaly() throws NoInternetConnection {
		// Log.d("MGR-NXT", "Get the list of station from web");
		getStationInfoFromPage();

		// Log.d("MGR-NXT", "All stations grabbed from web, delete all the table");
		this.clearListOfStationFromDatabase();

		// Log.d("MGR-NXT", "Restore all information");

		for (Enumeration<String> enumStation = lastUpdatedFavorites.keys(); enumStation.hasMoreElements();) {
			Station station = lastUpdatedFavorites.get(enumStation.nextElement());
			saveStationIntoDB(station);
		}
		// Log.d("MGR-NXT", "List successfully restored");
	}

	@Override
	public Station fillDynamicInformationForAStation(Station aStation) throws NoInternetConnection {
		long now = new Date().getTime();
		if ((now - lastUpdateTimeStamp < 30000) && lastUpdatedFavorites.containsKey(aStation.getId())) {
			// Log.d("MGR-NXT", "The last update is smaller than X sec and station exist");
			Station toRet = lastUpdatedFavorites.get(aStation.getId());

			return toRet;
		} else {
			// Log.d("MGR-NXT", "The last update is older than X sec");
			lastUpdateTimeStamp = now;
			getStationInfoFromPage();

			Station toRet = lastUpdatedFavorites.get(aStation.getId());

			return toRet;
		}
	}

	protected void getStationInfoFromPage() throws NoInternetConnection {
		Vector<Station> favorite = restoreFavoriteFromDataBase();
		Hashtable<String, Station> favorites = new Hashtable<String, Station>();
		int stationId = 1;

		// TODO see if this part can be optimized - restore favorite
		for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();
			// Log.d("MGR-CCUS", "Found favorite station id : " + station.getId());
			favorites.put(station.getId(), station);
		}
		try {
			URL url = new URL(getUrlOfInformationPage());

			HttpsURLConnection urlconn = (HttpsURLConnection) url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

			urlconn.setHostnameVerifier(new Verifier());
			urlconn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 Firefox/3.0");
			urlconn.setRequestProperty("Host", "www.smartbikedc.com");
			urlconn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlconn.connect();

			// Log.d("MGR-CCUS", "open url " + getUrlOfInformationPage());

			InputStreamReader reader = new InputStreamReader(urlconn.getInputStream(), "ISO-8859-1");

			BufferedReader inReader = new BufferedReader(reader);
			String line = null;

			String varpointPref = "var point = new GPoint(";
			String addressPref = "address = ";
			String addMarkerPref = "addMarker(";
			String htmlPref = "html = ";

			boolean endOfProcess = false;

			Station toAdd = new Station();
			toAdd.setNetwork(getNetworkId());

			while ((line = inReader.readLine()) != null && !endOfProcess) {

				if (line.startsWith(varpointPref)) {

					String latlon = line.substring(line.indexOf("(") + 1);
					latlon = latlon.replace(");", "");
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
				} else if (line.startsWith(addressPref)) {
					// address = 'Reeves Center<br />14th St & U St, NW<br />Washington, DC 20005';
					String addressVal = line.substring(addressPref.length());
					addressVal = addressVal.replace('\'', ' ').trim();
					addressVal = addressVal.replace('"', ' ').trim();
					int endOfName = addressVal.indexOf("<");
					String name = addressVal.substring(0, endOfName);
					toAdd.setName(name);
					addressVal = addressVal.replace("<br />", ", ");

					toAdd.setAddress(addressVal);
				} else if (line.startsWith(htmlPref)) {

					// html = 'Reeves Center' + '<br><br>Available SmartBikes: <font color=red>8</font><br> Open Return Slots: 4<br><br><img src="imgs/bikes.jpg" width=200 height=100>'
					int indexAvlb = line.indexOf("Available ");
					int indexSlot = line.indexOf("Open ");
					int indexImg = line.indexOf("<img");

					String available = line.substring(indexAvlb, indexSlot);
					StringBuffer numberToBuild = new StringBuffer();
					for (int i = 0; i < available.length(); i++) {
						if (Character.isDigit(available.charAt(i))) {
							numberToBuild.append(available.charAt(i));
						}
					}
					int availableBikes = Integer.parseInt(numberToBuild.toString());
					toAdd.setAvailableBikes(availableBikes);

					String slots = line.substring(indexSlot, indexImg);
					numberToBuild = new StringBuffer();
					for (int i = 0; i < slots.length(); i++) {
						if (Character.isDigit(slots.charAt(i))) {
							numberToBuild.append(slots.charAt(i));
						}
					}
					int availableSlot = Integer.parseInt(numberToBuild.toString());
					toAdd.setFreeSlot(availableSlot);

				} else if (line.startsWith(addMarkerPref)) {
					// all attributes found, add the station to the list of favorite
					toAdd.setId(String.valueOf(stationId++));
					if (favorites.containsKey(toAdd.getId())) {
						toAdd.setFavorite(1);
						toAdd.setComment(favorites.get(toAdd.getId()).getComment());
						toAdd.setFavoriteColor(favorites.get(toAdd.getId()).getFavoriteColor());
					} else {
						toAdd.setFavorite(0);
						toAdd.setComment(toAdd.getName());
						toAdd.setFavoriteColor(-1);
					}
					lastUpdatedFavorites.put(toAdd.getId(), toAdd);

					// reset the station for the next step
					toAdd = new Station();
					toAdd.setNetwork(getNetworkId());
				} else if (line.contains("//]]>")) {
					// end of process reached
					endOfProcess = true;
				}
			}

		} catch (SocketTimeoutException ste) {
			Log.e("MGR", "Timeout !");
			throw new NoInternetConnection(getUrlOfInformationPage());
		} catch (UnknownHostException uhe) {
			Log.e("MGR", "Unknown host !");
			throw new NoInternetConnection(getUrlOfInformationPage());
		} catch (Exception e) {
			throw new NoInternetConnection(getUrlOfInformationPage());
		}
	}

	/*
	 * ------------------------------- SIMPLE OVERRIDEN METHOD -------------------------------
	 */

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

	@Override
	public Vector<Station> restoreFavoriteFromDataBaseAndWeb() throws NoInternetConnection {
		return super.restoreFavoriteFromDataBaseAndWebImpl(getNetworkId());
	}

	public class Verifier implements HostnameVerifier {

		public Verifier() {
		}

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}

	}

}
