package com.xirgonium.android.manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
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

public class VelovLyonStationManager extends CommonStationManager {

	String	urlAllStationsList	= "http://www.velov.grandlyon.com/velovmap/zhp/inc/StationsParCoord.php?lat=45.75099555813836&long=4.8470306396484375&nombreStation=2000";
	String	urlForStationInfo	= "http://www.velov.grandlyon.com/velovmap/zhp/inc/DispoStationsParId.php?id=";

	public VelovLyonStationManager() {
		// TODO Auto-generated constructor stub
	}

	public VelovLyonStationManager(Context launched) {
		super(launched);
	}

	@Override
	public void clearListOfStationFromDatabase() {
		super.clearListOfStationFromDatabaseImpl(getNetworkId());
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
	public String[] getCities() {
		return new String[] { "Lyon", "Villeurbanne", "Caluire", "Vaulx en Velin" };
	}

	@Override
	public Vector<Station> restoreAllStationWithminimumInfoFromDataBase() {
		return super.restoreAllStationWithminimumInfoFromDataBaseImpl(getNetworkId());
	}

	@Override
	public Vector<Station> restoreFavoriteFromDataBase() {
		return super.restoreFavoriteFromDataBaseImpl(getNetworkId());
	}

	@Override
	public Vector<Station> restoreFavoriteFromDataBaseAndWeb() throws NoInternetConnection {
		return super.restoreFavoriteFromDataBaseAndWebImpl(getNetworkId());
	}

	@Override
	public void updateStationListDynamicaly() throws NoInternetConnection {
		try {

			Vector<String> favoritesID = new Vector<String>();
			Vector<Station> allFavorites = restoreFavoriteFromDataBase();
			
			for (Iterator<Station> iterator = allFavorites.iterator(); iterator.hasNext();) {
				Station station = (Station) iterator.next();
				favoritesID.add(station.getId());
			}
			
			allFavorites=null;

			clearListOfStationFromDatabase();

			URL url = new URL(urlAllStationsList);
			URLConnection urlconn = url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
			// Log.d("MGR", "open url " + urlAllStationsList);

			// isolate the kml part
			String jsonPage = FormatUtility.slurp(urlconn.getInputStream(), Constant.CHARSET_ISO_8859_1);

			JSONObject page = new JSONObject(new JSONTokener(jsonPage));
			JSONArray markers = page.getJSONArray("markers");

			for (int i = 0; i < markers.length(); i++) {
				JSONObject aStationInJSON = markers.getJSONObject(i);
				int id = aStationInJSON.getInt("numStation");
				String name = aStationInJSON.getString("nomStation");
				if (name.indexOf("-") != -1) {
					name = name.substring(name.indexOf("-") + 1).trim();
				}
				double lat = aStationInJSON.getDouble("x");
				double lon = aStationInJSON.getDouble("y");
				String address = aStationInJSON.getString("infoStation");

				Station aStation = new Station();
				aStation.setAddress(address);
				aStation.setNetwork(getNetworkId());
				aStation.setId(String.valueOf(id));
				aStation.setName(name);
				aStation.setLatitude(lat);
				aStation.setLongitude(lon);
				
				if(favoritesID.contains(aStation.getId())){
					aStation.setFavorite(1);
				}
				
				saveStationIntoDB(aStation);

				// Log.d("MGR-CCTY", aStationInJSON.toString());
			}

		} catch (SocketTimeoutException ste) {
			Log.e("MGR", "Timeout !");
			throw new NoInternetConnection(urlAllStationsList);
		} catch (UnknownHostException uhe) {
			Log.e("MGR", "Unknown host !");
			throw new NoInternetConnection(urlAllStationsList);
		} catch (JSONException e) {
			Log.e("MGR-CCTY", "JSON error " + e.getMessage());
			e.printStackTrace();
		} catch (MalformedURLException e) {
			Log.e("MGR-CCTY", "URL malformed " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("MGR-CCTY", "IO Exception " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public Station fillDynamicInformationForAStation(Station aStation) throws NoInternetConnection {
		aStation.setUpdateSuccess(1);
		try {
			URL url = new URL(urlForStationInfo + aStation.getId());
			Log.d("MGR", "Connect to " + url);
			URLConnection urlconn = url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
			Document doc = null;

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(urlconn.getInputStream());

				Element station = (Element) doc.getElementsByTagName(Constant.TAG_SD_STATION).item(0);

				NodeList station_children = station.getChildNodes();
				for (int i = 0; i < station_children.getLength(); i++) {
					Node aNode = station_children.item(i);
					if (aNode.getNodeName() != null) {
						if (aNode.getNodeName().equals(Constant.TAG_SD_AVAILABLE)) {
							try {
								aStation.setAvailableBikes(Integer.parseInt(aNode.getFirstChild().getNodeValue()));
							} catch (Exception e) {
								Log.e("MGR", "Unable to parse information from velib - Tag : " + Constant.TAG_SD_AVAILABLE + " - CAUSE : " + e.getMessage());
								aStation.setUpdateSuccess(Constant.ERR_PARSING);
							}
						}
						if (aNode.getNodeName().equals(Constant.TAG_SD_FREE)) {
							try {
								aStation.setFreeSlot(Integer.parseInt(aNode.getFirstChild().getNodeValue()));
							} catch (Exception e) {
								Log.e("MGR", "Unable to parse information from velib - Tag : " + Constant.TAG_SD_FREE + " - CAUSE : " + e.getMessage());
								aStation.setUpdateSuccess(Constant.ERR_PARSING);
							}
						}
					}
				}
			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				throw new NoInternetConnection(urlAllStationsList);
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				throw new NoInternetConnection(urlAllStationsList);
			} catch (Exception e) {
				Log.e("MGR", "Unable to parse information from velib  - CAUSE : " + e.getMessage());
				aStation.setUpdateSuccess(Constant.ERR_PARSING);
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.e("MGR", "Unable to connect and get information from velib  - CAUSE : " + e.getMessage());
			aStation.setUpdateSuccess(Constant.ERR_CONNECT);
		}

		return aStation;
	}

}
