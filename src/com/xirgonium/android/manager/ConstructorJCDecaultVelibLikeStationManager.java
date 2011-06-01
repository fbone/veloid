package com.xirgonium.android.manager;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.test.IsolatedContext;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorJCDecaultVelibLikeStationManager extends CommonStationManager {

	abstract boolean useOpenTag();
	
	@Override
	public boolean isSupportedbyMolib() {
		return true;
	}

	public ConstructorJCDecaultVelibLikeStationManager(Context launched) {
		super(launched);
	}

	public ConstructorJCDecaultVelibLikeStationManager() {

	}

	@Override
	public void clearListOfStationFromDatabase() {
		super.clearListOfStationFromDatabaseImpl(getNetworkId());
	}

	@Override
	public Vector<Station> fillInformationFromDB(Vector<Station> stations) {
		return fillInformationFromDBImpl(getNetworkId(), stations);
	}

	@Override
	public Vector<Station> fillInformationFromDBAndWeb(Vector<Station> stations) throws NoInternetConnection {
		return fillInformationFromDBAndWebImpl(getNetworkId(), stations);
	}

	@Override
	public abstract String[] getCities();

	@Override
	public Vector<Station> restoreAllStationWithminimumInfoFromDataBase() {
		return restoreAllStationWithminimumInfoFromDataBaseImpl(getNetworkId());
	}

	@Override
	public Vector<Station> restoreFavoriteFromDataBase() {
		return restoreFavoriteFromDataBaseImpl(getNetworkId());
	}

	@Override
	public Vector<Station> restoreFavoriteFromDataBaseAndWeb() throws NoInternetConnection {
		return restoreFavoriteFromDataBaseAndWebImpl(getNetworkId());
	}

	/**
	 * 
	 * Get on the Velib APIs the informations related to all Stations.
	 * 
	 * @param aStation
	 * @return The station given as parameter but completed with Velib information
	 * 
	 */
	@Override
	public void updateStationListDynamicaly() throws NoInternetConnection {

		// Log.d("MGR", "Restore the list of station");
		Vector<Station> favorite = restoreFavoriteFromDataBase();
		// Vector<Station> stationsLst = new Vector<Station>();

		Hashtable<String, Station> keysOfIds = new Hashtable<String, Station>();

		for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();
			Log.d("MGR", "Found favorite station id : " + station.getId());
			keysOfIds.put(station.getId(), station);
		}

		// Log.d("MGR", "Get the list of station from web");

		try {
			URL url = new URL(getAllStationURL());
			// Log.d("MGR", "open url");
			URLConnection urlconn = url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
			Document doc = null;

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(urlconn.getInputStream());

				Element stations = (Element) doc.getElementsByTagName(Constant.TAG_AS_MARKERS).item(0);
				NodeList station_children = stations.getChildNodes();

				this.clearListOfStationFromDatabase();

				for (int i = 0; i < station_children.getLength(); i++) {
					Node aNode = station_children.item(i);

					// Log.d("MGR", "Node name " + aNode.getNodeName());

					Station aStation = new Station();

					if (aNode.getNodeName() != null) {
						if (aNode.getNodeName().equals(Constant.TAG_AS_MARKER)) {
							// Log.d("MGR", "one station found");
							try {
								// if (((Element) aNode).getAttribute(Constant.ATTR_AS_OPEN).equals("1")) {
								String name = ((Element) aNode).getAttribute(Constant.ATTR_AS_NAME);
								if (name.indexOf("-") != -1) {
									name = name.substring(name.indexOf("-") + 1);
								}
								aStation.setName(name);
								aStation.setId(((Element) aNode).getAttribute(Constant.ATTR_AS_NUMBER));
								aStation.setAddress(((Element) aNode).getAttribute(Constant.ATTR_AS_ADDRESS));
								aStation.setFullAddress(((Element) aNode).getAttribute(Constant.ATTR_AS_FUL_ADDRESS));
								aStation.setLatitude(Float.parseFloat(((Element) aNode).getAttribute(Constant.ATTR_AS_LATITUDE)));
								aStation.setLongitude(Float.parseFloat(((Element) aNode).getAttribute(Constant.ATTR_AS_LONGITUDE)));
								aStation.setOpen(((Element) aNode).getAttribute(Constant.ATTR_AS_OPEN));
								aStation.setNetwork(getNetworkId());
								if (keysOfIds.containsKey(aStation.getId())) {
									aStation.setFavorite(1);
									aStation.setComment(keysOfIds.get(aStation.getId()).getComment());
									aStation.setFavoriteColor(keysOfIds.get(aStation.getId()).getFavoriteColor());
								} else {
									aStation.setFavorite(0);
									aStation.setComment(name);
									aStation.setFavoriteColor(-1);
								}
								if (useOpenTag() && aStation.getOpen().equals("1")) {
									// stationsLst.add(aStation);
									saveStationIntoDB(aStation);
								} else if (!useOpenTag()) {
									// stationsLst.add(aStation);
									saveStationIntoDB(aStation);
								}
								// Log.d("MGR", "Station " + aStation.getId() + " added");
								// } else {
								// Log.d("MGR", "Station closed");
								// }
							} catch (Exception e) {
								Log.e("MGR", "Unable to parse information from velib - Tag : " + "marker" + " - CAUSE : " + e.getMessage());
							}
						}
					}
				}
			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				throw new NoInternetConnection(getOneStationInfoURL());
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				throw new NoInternetConnection(getOneStationInfoURL());
			} catch (Exception e) {
				Log.e("MGR", "Unable to parse information from velib  - CAUSE : " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.e("MGR", "Unable to connect and get information from velib  - CAUSE : " + e.getMessage());
		}

		// Log.d("MGR", "Restore all information");
		// for (Iterator<Station> iterator = stationsLst.iterator(); iterator.hasNext();) {
		// Station station = (Station) iterator.next();
		// saveStationIntoDB(station);
		// }
		// Log.d("MGR", "List successfully restored");
	}

	/**
	 * 
	 * Get on the Velib APIs the informations related to a Station.
	 * 
	 * @param aStation
	 * @return The station given as parameter but completed with Velib information
	 * 
	 */
	@Override
	public Station fillDynamicInformationForAStation(Station aStation) throws NoInternetConnection {
		aStation.setUpdateSuccess(1);
		try {
			URL url = new URL(getOneStationInfoURL() + aStation.getId());
			// Log.d("MGR", "Connect to " + url);
			URLConnection urlconn = url.openConnection();

			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
			urlconn.setReadTimeout(ConfigurationContext.getConnectionTimeout());

			Document doc = null;

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(urlconn.getInputStream());

				Element station = (Element) doc.getElementsByTagName("station").item(0);
				if (station != null) {
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
				}
			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				aStation.setUpdateSuccess(Constant.ERR_CONNECT);
				throw new NoInternetConnection(getOneStationInfoURL());

			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				aStation.setUpdateSuccess(Constant.ERR_CONNECT);
				throw new NoInternetConnection(getOneStationInfoURL());

			} catch (Exception e) {
				Log.e("MGR", "Unable to parse information from velib  - CAUSE : " + e.getMessage());
				aStation.setUpdateSuccess(Constant.ERR_PARSING);
				e.printStackTrace();
			}
		} catch (NoInternetConnection nie) {
			throw nie;
		} catch (Exception e) {
			Log.e("MGR", "Unable to connect and get information from velib  - CAUSE : " + e.getMessage());
			aStation.setUpdateSuccess(Constant.ERR_CONNECT);
			throw new NoInternetConnection(getOneStationInfoURL());
		}

		return aStation;
	}

	protected abstract String getAllStationURL();

	protected abstract String getOneStationInfoURL();
}
