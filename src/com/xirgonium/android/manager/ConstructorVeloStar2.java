package com.xirgonium.android.manager;

import java.io.StringReader;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorVeloStar2 extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;
	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	public ConstructorVeloStar2() {
	}

	public ConstructorVeloStar2(Context launched) {
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
		getStationInfoFromPage();

		// Log.d("MGR", "All stations grabbed from web, delete all the table");
		this.clearListOfStationFromDatabase();

		// Log.d("MGR", "Restore all information");

		for (Enumeration<String> enumStation = lastUpdatedFavorites.keys(); enumStation.hasMoreElements();) {
			Station station = lastUpdatedFavorites.get(enumStation.nextElement());
			saveStationIntoDB(station);
		}
		// Log.d("MGR", "List successfully restored");
	}

	protected void getStationInfoFromPage() throws NoInternetConnection {
		Vector<Station> favorite = restoreFavoriteFromDataBase();
		Hashtable<String, Station> favorites = new Hashtable<String, Station>();

		for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();
			// Log.d("MGR", "Found favorite station id : " + station.getId());
			favorites.put(station.getId(), station);
		}
		try {
			URL url = new URL(getURLToUpdate());
			URLConnection urlconn = url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
			urlconn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 Firefox/3.0");
			urlconn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlconn.connect();


//			Log.d("MGR", "open url " + getURLToUpdate());

			Document doc = null;

			try {
				String page = FormatUtility.slurp(urlconn.getInputStream(), Constant.CHARSET_UTF8);

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				//
				InputSource inStream = new InputSource();
				inStream.setCharacterStream(new StringReader(page));

				doc = db.parse(inStream);

				NodeList stations = doc.getElementsByTagName("station");

				for (int i = 0; i < stations.getLength(); i++) {
					// for each placemark
					Node viewEntry = stations.item(i);
					NodeList stationProperties = viewEntry.getChildNodes();

					Station aStation = new Station();
					aStation.setNetwork(getNetworkId());
					aStation.setAvailableBikes(-1);
					aStation.setFreeSlot(-1);

					boolean notRecord = false;

					// now parse between Description and Point
//					for (int j = 0; j < stationProperties.getLength(); j++) {
//						Node entryData = stationProperties.item(j);

						//name
						String name = getTextNodeValue(viewEntry, "name");
						aStation.setName(name);

						//id
						String value = getTextNodeValue(viewEntry, "number");
						aStation.setId(value);

						//lat
						String lat = getTextNodeValue(viewEntry, "latitude");
						aStation.setLatitude(Double.parseDouble(lat));

						//id
						String lng = getTextNodeValue(viewEntry, "longitude");
						aStation.setLongitude(Double.parseDouble(lng));

						//						// capacity
						//						if (name != null && "Capacity".equalsIgnoreCase(name)) {
						//							String value = getTextNodeValue(entryData, "number");
						//							notRecord = ("0".equals(value));
						//						}

						// slots
						String slots = getTextNodeValue(viewEntry, "slotsavailable");
						aStation.setFreeSlot(Integer.parseInt(slots));

						// bikes
						String bikes = getTextNodeValue(viewEntry, "bikesavailable");
						aStation.setAvailableBikes(Integer.parseInt(bikes));

						String addr = getTextNodeValue(viewEntry, "district");
						aStation.setAddress(addr);

//					}

					if (favorites.containsKey(aStation.getId())) {
						aStation.setFavorite(1);
						aStation.setComment(favorites.get(aStation.getId()).getComment());
						aStation.setFavoriteColor(favorites.get(aStation.getId()).getFavoriteColor());
					} else {
						aStation.setFavorite(0);
						aStation.setComment(aStation.getName());
						aStation.setFavoriteColor(-1);
					}
					if (notRecord == false) {
						lastUpdatedFavorites.put(aStation.getId(), aStation);
					}
					// Log.d("MGR", "Station " + aStation.getId() + " added");
				}

			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				throw new NoInternetConnection(getURLToUpdate());
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				throw new NoInternetConnection(getURLToUpdate());
			} catch (Exception e) {
				Log.e("MGR", "Unable to parse information  - CAUSE : " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			throw new NoInternetConnection(getURLToUpdate());
		}
	}

	public String getTextNodeValue(Node node, String subTagName) {
		String toRet = null;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node aChild = children.item(i);
			if (aChild.getNodeName().equals(subTagName)) {
				toRet = aChild.getFirstChild().getNodeValue();
			}
		}
		return toRet;
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
		long now = new Date().getTime();
		if ((now - lastUpdateTimeStamp < 60000) && lastUpdatedFavorites.containsKey(aStation.getId())) {
			// Log.d("MGR",
			// "The last update is smaller than X sec and station exist");
			Station toRet = lastUpdatedFavorites.get(aStation.getId());

			return toRet;
		} else {
			// Log.d("MGR", "The last update is older than 60 sec now = " + now
			// + " - last update = " + lastUpdateTimeStamp);
			lastUpdateTimeStamp = now;
			getStationInfoFromPage();

			Station toRet = lastUpdatedFavorites.get(aStation.getId());

			return toRet;
		}
	}

	abstract String getURLToUpdate();

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
