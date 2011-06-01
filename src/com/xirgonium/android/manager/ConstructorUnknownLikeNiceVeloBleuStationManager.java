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

public abstract class ConstructorUnknownLikeNiceVeloBleuStationManager extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;
	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	public ConstructorUnknownLikeNiceVeloBleuStationManager() {
	}

	public ConstructorUnknownLikeNiceVeloBleuStationManager(Context launched) {
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

			// Log.d("MGR", "open url " + getURLToUpdate());

			Document doc = null;

			try {
				// isolate the kml part
				String page = FormatUtility.slurp(urlconn.getInputStream(), Constant.CHARSET_UTF8);

				// index of KML start and stop
				// int indexStartKML = page.indexOf(Constant.TAG_KML_START);
				// int indexStopKML = page.indexOf(Constant.TAG_KML_STOP);
				//
				// String kml = page.substring(indexStartKML, indexStopKML + 6);
				//
				// // Remove the CDATA information
				// kml = kml.replace("<![CDATA[", "");
				// kml = kml.replace("]]>", "");
				// kml = kml.replace("<i>", "");
				// kml = kml.replace("</i>", "");
				//
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				//
				InputSource inStream = new InputSource();
				inStream.setCharacterStream(new StringReader(page));

				doc = db.parse(inStream);

				NodeList viewentries = doc.getElementsByTagName("viewentry");

				for (int i = 0; i < viewentries.getLength(); i++) {
					// for each placemark
					Node viewEntry = viewentries.item(i);
					NodeList entriesData = viewEntry.getChildNodes();

					Station aStation = new Station();
					aStation.setNetwork(getNetworkId());
					aStation.setAvailableBikes(-1);
					aStation.setFreeSlot(-1);

					boolean notRecord = false;

					// now parse between Description and Point
					for (int j = 0; j < entriesData.getLength(); j++) {
						Node entryData = entriesData.item(j);

						String name = entryData.getAttributes().getNamedItem("name").getNodeValue();

						// ID
						if (name != null && "standId".equalsIgnoreCase(name)) {
							String value = getTextNodeValue(entryData, "number");
							aStation.setId(value);
						}

						// Name
						if (name != null && "standName".equalsIgnoreCase(name)) {
							String value = getTextNodeValue(entryData, "text");
							aStation.setName(value);
						}

						// Lat lon
						// Name
						if (name != null && "$1".equalsIgnoreCase(name)) {
							String latLon = getTextNodeValue(entryData, "text");

							StringTokenizer tokenLatLon = new StringTokenizer(latLon, ",");
							int index = 0;
							while (tokenLatLon.hasMoreElements()) {
								String latOrLon = (String) tokenLatLon.nextElement();
								if (index++ == 0) {
									aStation.setLatitude(Double.parseDouble(latOrLon));
								} else {
									aStation.setLongitude(Double.parseDouble(latOrLon));

								}

							}
						}

						// capacity
						if (name != null && "Capacity".equalsIgnoreCase(name)) {
							String value = getTextNodeValue(entryData, "number");
							notRecord = ("0".equals(value));
						}

						// slots
						if (name != null && "portsFree".equalsIgnoreCase(name)) {
							String value = getTextNodeValue(entryData, "number");
							aStation.setFreeSlot(Integer.parseInt(value));
						}

						// bikes
						if (name != null && "BikesAvailable".equalsIgnoreCase(name)) {
							String value = getTextNodeValue(entryData, "number");
							aStation.setAvailableBikes(Integer.parseInt(value));
						}

						// name
						// if description
						// if
						// (Constant.TAG_CC_DESCRIPTION.equals(entryData.getNodeName()))
						// {
						// Node mainDiv = entryData.getFirstChild();
						// NodeList descriptionContent =
						// mainDiv.getChildNodes();
						// for (int k = 0; k < descriptionContent.getLength();
						// k++) {
						// Node aDiv = descriptionContent.item(k);
						//
						// switch (k) {
						// // 0 : Nom
						// case 0:
						// String name =
						// aDiv.getFirstChild().getNodeValue().replace(",", "");
						//
						// aStation.setId(String.valueOf(FormatUtility.intFromString(name)));
						// int endOfName = name.indexOf(" - ");
						// if (endOfName != -1) {
						// aStation.setName(name.substring(0, endOfName));
						// aStation.setAddress(name.substring(endOfName + 3));
						// } else {
						// aStation.setName(name);
						// aStation.setAddress(name);
						// }
						// break;
						// // 1 : A ne pas prendre en compte
						// // 2 : velo<br/>emplacement
						// case 2:
						// NodeList bikesAndSlot = aDiv.getChildNodes();
						// for (int l = 0; l < bikesAndSlot.getLength(); l++) {
						// Node bikesOrSlot = bikesAndSlot.item(l);
						// if (bikesOrSlot.getNodeType() == Node.TEXT_NODE) {
						// if (aStation.getAvailableBikes() < 0) {
						// aStation.setAvailableBikes(Integer.parseInt(bikesOrSlot.getNodeValue()));
						// } else {
						// aStation.setFreeSlot(Integer.parseInt(bikesOrSlot.getNodeValue()));
						// }
						// }
						// }
						// break;
						// }
						// }
						// }
						//
						// if ("Point".equals(entryData.getNodeName())) {
						// String latLong =
						// entryData.getFirstChild().getFirstChild().getNodeValue();
						// StringTokenizer tokeniseLatLong = new
						// StringTokenizer(latLong, ",");
						// int index = 0;
						// while (tokeniseLatLong.hasMoreElements()) {
						//
						// String latOrLong = (String)
						// tokeniseLatLong.nextElement();
						// if (latOrLong.indexOf("?") != -1) {
						// latOrLong = latOrLong.replace("?", "");
						// }
						// try {
						// switch (index++) {
						// case 0:
						// aStation.setLongitude(Double.parseDouble(latOrLong));
						// break;
						// case 1:
						// aStation.setLatitude(Double.parseDouble(latOrLong));
						// break;
						// }
						// } catch (Exception e) {
						// Log.d("BICING", latOrLong);
						// e.printStackTrace();
						// }
						// }
						// }
					}

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
