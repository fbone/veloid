package com.xirgonium.android.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UTFDataFormatException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorVCubStationManager extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;
	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	/*
	 * ------------------------------- CONSTRUCTOR -------------------------------
	 */

	public ConstructorVCubStationManager() {
	}

	public ConstructorVCubStationManager(Context launched) {
		super(launched);
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
		if ((now - lastUpdateTimeStamp < 60000) && lastUpdatedFavorites.containsKey(aStation.getId())) {
			Station toRet = lastUpdatedFavorites.get(aStation.getId());
			Log.d("BIXI", "CASE1" + this.toString());
			return toRet;
		} else {
			Log.d("BIXI", "CASE2" + this.toString());
			lastUpdateTimeStamp = now;
			getStationInfoFromPage();

			Station toRet = lastUpdatedFavorites.get(aStation.getId());
			if (toRet == null)
				return aStation;
			return toRet;
		}
	}

	protected void getStationInfoFromPage() throws NoInternetConnection {
		Vector<Station> favorite = restoreFavoriteFromDataBase();
		Hashtable<String, Station> favorites = new Hashtable<String, Station>();

		// TODO see if this part can be optimized - restore favorite
		for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();
			favorites.put(station.getId(), station);
		}
		try {
			URL url = new URL(getUrlOfInformationPage());

			URLConnection urlconn = url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

//			urlconn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 Firefox/3.0");
//			urlconn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlconn.connect();

			InputStreamReader reader = new InputStreamReader(urlconn.getInputStream(), "utf-8");

			//			String page = FormatUtility.slurp(urlconn.getInputStream(), Constant.CHARSET_UTF8);

			//		LineNumberReader inReader = new LineNumberReader(new StringReader(page));
			BufferedReader inReader = new BufferedReader(reader);
			String line = null;

			boolean endOfProcess = false;

			Station toAdd = new Station();
			toAdd.setNetwork(getNetworkId());

			//jQuery.extend(true, Drupal, { settings: { "gmap": { "stations-map": { "width": "750px", "height": "600px", "zoom": 12, "maxzoom": "14", "controltype": "3D", "align": "None", "maptype": "Map", "mtc": "standard", "baselayers": { "Map": 1, "Satellite": 1, "Hybrid": 1, "Physical": 1 }, "styles": { "line_default": [ "0000ff", "5", "45", "", "" ], "poly_default": [ "000000", "3", "25", "ff0000", "45" ] }, "line_colors": [ "#00cc00", "#ff0000", "#0000ff" ], "behavior": { "locpick": false, "nodrag": 0, "nokeyboard": 1, "nomousezoom": 0, "nocontzoom": 0, "autozoom": 0, "dynmarkers": 0, "overview": 0, "collapsehack": 0, "scale": 0, "extramarkerevents": false, "clickableshapes": false }, "markermode": "0", "id": "stations-map", "latitude": "44.835178", "longitude": "-0.577126", "markers": [ 
			/*
			 * { "latitude": "44.8380506328798", "longitude": "-0.584174631829883", "text": "\x3cdiv class=\"gmap-popup\"\x3e\x3cdiv class=\"gmap-infobulle\"\x3e\n \x3cdiv
			 * class=\"gmap-titre\"\x3eMériadeck\x3c/div\x3e\n \x3cdiv class=\"gmap-adresse\"\x3eRue Claude Bonnier (face à la station Tram et à l’entrée du Centre) \x3c/div\x3e\x3cdiv
			 * class=\"gmap-velos\"\x3e\n \x3ctable\x3e\x3ctr\x3e\x3ctd class=\"alert\"\x3e\x3cstrong\x3e0\x3c/strong\x3e vélos disponibles\x3c/td\x3e\n \x3ctd
			 * class=\"ok\"\x3e\x3cstrong\x3e20\x3c/strong\x3e places disponibles\x3c/td\x3e\x3c/tr\x3e\x3c/table\x3e\x3c/div\x3e\x3cdiv class=\"gmap-datemaj\"\x3edernière mise à jour il y a
			 * \x3cstrong\x3e39 min\x3c/strong\x3e \x3c/div\x3e\n \x3c/div\x3e\x3c/div\x3e", "markername": "green" }, { "latitude": "44.8378455555646", "longitude": "-0.590256714652825", "text":
			 * "\x3cdiv class=\"gmap-popup\"\x3e\x3cdiv class=\"gmap-infobulle\"\x3e\n \x3cdiv class=\"gmap-titre\"\x3eSt Bruno\x3c/div\x3e\n \x3cdiv class=\"gmap-adresse\"\x3ePlace du XI novembre
			 * (face au cimetière de la Chartreuse)\x3c/div\x3e\x3cdiv class=\"gmap-velos\"\x3e\n \x3ctable\x3e\x3ctr\x3e\x3ctd class=\"alert\"\x3e\x3cstrong\x3e0\x3c/strong\x3e vélos
			 * disponibles\x3c/td\x3e\n \x3ctd class=\"ok\"\x3e\x3cstrong\x3e20\x3c/strong\x3e places disponibles\x3c/td\x3e\x3c/tr\x3e\x3c/table\x3e\x3c/div\x3e\x3cdiv
			 * class=\"gmap-datemaj\"\x3edernière mise à jour il y a \x3cstrong\x3e39 min\x3c/strong\x3e \x3c/div\x3e\n \x3c/div\x3e\x3c/div\x3e", "markername": "green" }, { "latitude":
			 * "44.840898543866", "longitude": "-0.590986847416734", "text": "\x3cdiv class=\"gmap-popup\"\x3e\x3cdiv class=\"gmap-infobulle\"\x3e\n \x3cdiv class=\"gmap-titre\"\x3ePlace
			 * Tartas\x3c/div\x3e\n \x3cdiv class=\"gmap-adresse\"\x3e167, Rue Judaïque\x3c/div\x3e\x3cdiv class=\"gmap-velos\"\x3e\n \x3ctable\x3e\x3ctr\x3e\x3ctd
			 * class=\"alert\"\x3e\x3cstrong\x3e0\x3c/strong\x3e vélos disponibles\x3c/td\x3e\n \x3ctd class=\"ok\"\x3e\x3cstrong\x3e18\x3c/strong\x3e places
			 * disponibles\x3c/td\x3e\x3c/tr\x3e\x3c/table\x3e\x3c/div\x3e\x3cdiv class=\"gmap-datemaj\"\x3edernière mise à jour il y a \x3cstrong\x3e39 min\x3c/strong\x3e \x3c/div\x3e\n
			 * \x3c/div\x3e\x3c/div\x3e", "markername": "green" }
			 */
			String latLbl = "{ \"latitude\":";
			String separator = "\\x3c";
			String separator2 = "\\x3e";

			//			while ((line = inReader.readLine()) != null && !endOfProcess) {
			while ((line = inReader.readLine()) != null && !endOfProcess) {
				if (line.contains("jQuery.extend(true")) {
					int firstNextStation = line.indexOf(latLbl);
					while (firstNextStation != -1) {
						String tmpLine = line.substring(firstNextStation + 1 + latLbl.length());
						line = tmpLine;
						int nextStation = tmpLine.indexOf(latLbl);
						if (nextStation != -1) {
							tmpLine = tmpLine.substring(0, nextStation);
							tmpLine = tmpLine.replace(separator, ";").replace(separator2, ";").replace("\\", "").replace("div class=", "").replace("/div", "").replace("/strong", "").replace("strong",
									"").replace("/td", "").replace("td", "");

							int i = 0;
//							System.out.println("--------------");
							StringTokenizer tokenLine = new StringTokenizer(tmpLine, ";");
							while (tokenLine.hasMoreTokens()) {
								String next = (String) tokenLine.nextToken();

//								System.out.println(i + " | " + next);

								switch (i++) {
								case 0:
									String latLng = next;
									latLng = latLng.replace("\"", "").replace("longitude:", ";").replace("text:", "").replace(",", "");
									StringTokenizer latOrLong = new StringTokenizer(latLng, ";");
									String lat = latOrLong.nextToken().trim();
									String lng = latOrLong.nextToken().trim();

									toAdd.setLatitude(Double.parseDouble(lat));
									toAdd.setLongitude(Double.parseDouble(lng));

//									System.out.println("lat/lng = " + lat + "/" + lng);
									//System.out.println("latlon | " + next);
									break;
								case 5:
									String name = next.replace("Ã©", "é").replace("Ã¨", "è");
//									System.out.println("NAME = " + name);
									toAdd.setName(name);
									toAdd.setId(String.valueOf(FormatUtility.intFromString(name)));
									break;
								case 8:
									String addr = next.replace("Ã©", "é").replace("Ã¨", "è");
									toAdd.setAddress(addr);
//									System.out.println("Addr = " + addr);
									break;
								case 15:
									String bikes = next;
									try {
										toAdd.setAvailableBikes(Integer.parseInt(bikes));
									} catch (Exception e) {
										toAdd.setAvailableBikes(0);
									}
//									System.out.println("Bikes = " + next);
									break;

								case 19:
									String slots = next;
									try {
										toAdd.setFreeSlot(Integer.parseInt(slots));
									} catch (Exception e) {
										toAdd.setFreeSlot(0);									}
//									System.out.println("SLots = " + next);
									break;

								default:
									break;
								}

							}

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

							toAdd = new Station();
							toAdd.setNetwork(getNetworkId());
						}

						System.out.println("VCubStation : " + tmpLine);

						firstNextStation = nextStation;
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
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
