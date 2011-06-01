package com.xirgonium.android.manager;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorRTCLaRochelle extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;
	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	/*
	 * ------------------------------- CONSTRUCTOR -------------------------------
	 */

	public ConstructorRTCLaRochelle() {
	}

	public ConstructorRTCLaRochelle(Context launched) {
		super(launched);
	}

	/*
	 * ------------------------------- ABSTRACT METHODS -------------------------------
	 */
	protected abstract String getCountry();

	protected abstract String getInfoURL();

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
		if ((now - lastUpdateTimeStamp < 60000) && lastUpdatedFavorites.containsKey(aStation.getId())) {
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

		// TODO see if this part can be optimized - restore favorite
		for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();
			// Log.d("MGR-NXT", "Found favorite station id : " + station.getId());
			favorites.put(station.getId(), station);
		}
		try {
			URL url = new URL(getInfoURL());
			//URLConnection urlconn = url.openConnection();

			//urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

			String page = "<script src=\"mapiconmaker.js\" type=\"text/javascript\"></script><script type=\"text/javascript\">"+
"var markers = [{num: '1', lat: '46.15971375', lon: '-1.1518102', name: 'H̫tel de Ville', color: 'vert', bikeCount: '2 v&eacute;los', freeLockCount: '6', lockCount: '8 places'},{num: '2', lat: '46.15738769', lon: '-1.1506086', name: 'Square Valin', color: 'noir', bikeCount: '0 v&eacute;lo', freeLockCount: '8', lockCount: '8 places'},{num: '3', lat: '46.15992554', lon: '-1.1486184', name: 'Arsenal', color: 'vert', bikeCount: '4 v&eacute;los', freeLockCount: '4', lockCount: '8 places'},{num: '4', lat: '46.16140435', lon: '-1.1489564', name: 'March̩', color: 'vert', bikeCount: '4 v&eacute;los', freeLockCount: '4', lockCount: '8 places'},{num: '5', lat: '46.16271964', lon: '-1.1525398', name: 'H̫tel de Police', color: 'vert', bikeCount: '2 v&eacute;los', freeLockCount: '6', lockCount: '8 places'},{num: '6', lat: '46.16235552', lon: '-1.1535269', name: 'Gare routi̬re', color: 'vert', bikeCount: '4 v&eacute;los', freeLockCount: '4', lockCount: '8 places'},{num: '7', lat: '46.15697152', lon: '-1.1568474', name: 'Pr̩fecture', color: 'vert', bikeCount: '6 v&eacute;los', freeLockCount: '2', lockCount: '8 places'},{num: '8', lat: '46.15693807', lon: '-1.1537361', name: 'Vieux Port', color: 'noir', bikeCount: '0 v&eacute;lo', freeLockCount: '8', lockCount: '8 places'},{num: '9', lat: '46.15590877', lon: '-1.1488974', name: 'Motte Rouge', color: 'gris', bikeCount: '0 v&eacute;lo', freeLockCount: '0', lockCount: '0 place'},{num: '10', lat: '46.15831292', lon: '-1.1450940', name: 'H̫pital', color: 'vert', bikeCount: '1 v&eacute;lo', freeLockCount: '7', lockCount: '8 places'},{num: '11', lat: '46.16784302', lon: '-1.1504477', name: 'Porte Dauphine', color: 'vert', bikeCount: '4 v&eacute;los', freeLockCount: '4', lockCount: '8 places'},{num: '12', lat: '46.16986030', lon: '-1.1523467', name: 'Piscine', color: 'vert', bikeCount: '4 v&eacute;los', freeLockCount: '4', lockCount: '8 places'},{num: '13', lat: '46.15341533', lon: '-1.1532318', name: 'M̩diath̬que', color: 'gris', bikeCount: '0 v&eacute;lo', freeLockCount: '0', lockCount: '0 place'},{num: '14', lat: '46.15235252', lon: '-1.1535966', name: 'Biblioth̬que Universitaire', color: 'vert', bikeCount: '7 v&eacute;los', freeLockCount: '9', lockCount: '16 places'},{num: '15', lat: '46.15503552', lon: '-1.1499166', name: 'Office de Tourisme', color: 'noir', bikeCount: '0 v&eacute;lo', freeLockCount: '8', lockCount: '8 places'},{num: '16', lat: '46.15279846', lon: '-1.1458128', name: 'Gare 1', color: 'vert', bikeCount: '3 v&eacute;los', freeLockCount: '5', lockCount: '8 places'},{num: '17', lat: '46.15292480', lon: '-1.1455875', name: 'Gare 2', color: 'rouge', bikeCount: '8 v&eacute;los', freeLockCount: '0', lockCount: '8 places'},{num: '18', lat: '46.15280589', lon: '-1.1406040', name: 'Parking Jean Moulin 1', color: 'noir', bikeCount: '0 v&eacute;lo', freeLockCount: '8', lockCount: '8 places'},{num: '19', lat: '46.15297683', lon: '-1.1413013', name: 'Parking Jean Moulin 2', color: 'gris', bikeCount: '0 v&eacute;lo', freeLockCount: '0', lockCount: '0 place'},{num: '20', lat: '46.14665906', lon: '-1.1571693', name: 'Facult̩ de Sciences', color: 'vert', bikeCount: '1 v&eacute;lo', freeLockCount: '15', lockCount: '16 places'},{num: '21', lat: '46.14825715', lon: '-1.1535644', name: 'Cin̩ma', color: 'vert', bikeCount: '5 v&eacute;los', freeLockCount: '3', lockCount: '8 places'},{num: '22', lat: '46.16297972', lon: '-1.1458665', name: 'Place des Cordeliers', color: 'vert', bikeCount: '6 v&eacute;los', freeLockCount: '2', lockCount: '8 places'},{num: '23', lat: '46.16375253', lon: '-1.1472129', name: 'Place Cacaud', color: 'gris', bikeCount: '0 v&eacute;lo', freeLockCount: '0', lockCount: '0 place'},{num: '24', lat: '46.14029596', lon: '-1.1538219', name: 'EIGSI', color: 'vert', bikeCount: '3 v&eacute;los', freeLockCount: '5', lockCount: '8 places'},{num: '25', lat: '46.14205778', lon: '-1.1525452', name: 'IUT', color: 'vert', bikeCount: '3 v&eacute;los', freeLockCount: '13', lockCount: '16 places'},{num: '26', lat: '46.13819583', lon: '-1.1517244', name: 'Maison du D̩partement', color: 'vert', bikeCount: '5 v&eacute;los', freeLockCount: '3', lockCount: '8 places'},]</script><script src=\"affichePoints.js\" type=\"text/javascript\"></script><div id='tableauListe'><table summary='Liste des stations de v&eacute;los avec leur disponibilit&eacute;es'><caption>Liste des stations</caption><thead> <tr>"+
 "          <th>Numero</th>"+
 "          <th>Nom</th>"; 
				
				
				//FormatUtility.slurp(urlconn.getInputStream(), Constant.CHARSET_UTF8);
			// Log.d("MGR-NXT", "open url " + urlNextBikeInfo);

			String starmarkers = "[";
			String endmarkers = "]";
			int beginStationInfo = page.indexOf(starmarkers);
			int endStationInfo = page.lastIndexOf(endmarkers);

			String jsObj = page.substring(beginStationInfo, endStationInfo + 1);

			JSONArray markers = new JSONArray(new JSONTokener(jsObj));

			for (int i = 0; i < markers.length(); i++) {
				try {
					JSONObject aStationInJSON = markers.getJSONObject(i);
					int id = aStationInJSON.getInt("num");
					String name = aStationInJSON.getString("name");
					double lat = aStationInJSON.getDouble("lat");
					double lon = aStationInJSON.getDouble("lon");

					Station aStation = new Station();
					aStation.setNetwork(getNetworkId());
					aStation.setId(String.valueOf(id));
					aStation.setName(name);
					aStation.setLatitude(lat);
					aStation.setLongitude(lon);

					String bikesStr = aStationInJSON.getString("bikeCount");
					bikesStr = bikesStr.substring(0, bikesStr.indexOf(" "));
					int bike = Integer.parseInt(bikesStr);
					aStation.setAvailableBikes(bike);
					
					int slots = aStationInJSON.getInt("freeLockCount");
					aStation.setFreeSlot(slots);
					
					if (favorites.contains(aStation.getId())) {
						aStation.setFavorite(1);
					}

					saveStationIntoDB(aStation);
					lastUpdatedFavorites.put(aStation.getId(), aStation);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				// Log.d("MGR-CCTY", aStationInJSON.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new NoInternetConnection(getInfoURL());
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

}
