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
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorVeloStar extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;
	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	public ConstructorVeloStar() {
	}

	public ConstructorVeloStar(Context ctx) {
		super(ctx);
	}

	/*
	 * ------------------------------- ABSTRACT METHODS -------------------------------
	 */
	protected abstract String getCountry();

	/*
	 * ------------------------------- SPECIFIC METHODS -------------------------------
	 */

	@Override
	public void updateStationListDynamicaly() throws NoInternetConnection {

		// Log.d("MGR", "Restore the list of station");
		Vector<Station> favorite = restoreFavoriteFromDataBase();

		Hashtable<String, Station> keysOfIds = new Hashtable<String, Station>();

		for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();
			// Log.d("MGR", "Found favorite station id : " + station.getId());
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

				NodeList markers = doc.getElementsByTagName("marker");

				this.clearListOfStationFromDatabase();

				for (int i = 0; i < markers.getLength(); i++) {
					Node marker = markers.item(i);

					Station aStation = new Station();

					/*
					 * 
					 * <marker cat="1" uid="2" lng="-1.677976" lat="48.109955" img="img_station_01.png" table="tt_address" test="" > <t><![CDATA[ REPUBLIQUE ]]></t> </marker>
					 */

					String id = ((Element) marker).getAttribute("uid");

					String lat = ((Element) marker).getAttribute("lat");
					String lng = ((Element) marker).getAttribute("lng");
					String name = "";
					NodeList markerChildren = marker.getChildNodes();
					for (int j = 0; j < markerChildren.getLength(); j++) {
						Node aNode = markerChildren.item(j);
						if ("t".equals(aNode.getNodeName())) {
							name = aNode.getFirstChild().getNodeValue();
							if (name != null) {
								name = name.replace("<![CDATA[  ", "");
								name = name.replace("]]>", "");
							} else {
								name = id;
							}

						}
					}
					boolean toAdd = "img_station_01.png".equals(((Element) marker).getAttribute("img"));

					aStation.setName(name);
					aStation.setId(id);
					try {
						aStation.setLatitude(Float.parseFloat(lat));
						aStation.setLongitude(Float.parseFloat(lng));
					} catch (Exception e) {
						toAdd = false;
					}

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

					if (toAdd) {
						saveStationIntoDB(aStation);
					}
				}

			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				throw new NoInternetConnection(getAllStationURL());
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				throw new NoInternetConnection(getAllStationURL());
			} catch (Exception e) {
				Log.e("MGR", "Unable to parse information from velostar  - CAUSE : " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.e("MGR", "Unable to connect and get information from velostar  - CAUSE : " + e.getMessage());
		}
	}

	@Override
	public Station fillDynamicInformationForAStation(Station aStation) throws NoInternetConnection {

		try {
			URL url = new URL(getOneStationInfoURL(aStation.getId()));
			URLConnection urlconn = url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

			// Log.d("MGR", "open url " + getURLToUpdate());

			Document doc = null;

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(urlconn.getInputStream());

				NodeList divs = doc.getElementsByTagName("div");

				for (int i = 0; i < divs.getLength(); i++) {

					Node div = divs.item(i);

					String divClass = ((Element) div).getAttribute("class");

					if ("velo_dispo".equals(divClass)) {
						NodeList divChildren = div.getChildNodes();
						for (int j = 0; j < divChildren.getLength(); j++) {
							Node aNode = divChildren.item(j);
							if ("div".equals(aNode.getNodeValue())) {
								try {
									String bikeStr = aNode.getFirstChild().getNodeValue();
									aStation.setAvailableBikes(Integer.parseInt(bikeStr));
								} catch (Exception e) {
									aStation.setAvailableBikes(-1);
								}
							}
						}
					}

					if ("place_dispo".equals(divClass)) {
						NodeList divChildren = div.getChildNodes();
						for (int j = 0; j < divChildren.getLength(); j++) {
							Node aNode = divChildren.item(j);
							if ("div".equals(aNode.getNodeName())) {
								try {
									String bikeStr = aNode.getFirstChild().getNodeValue();
									aStation.setFreeSlot(Integer.parseInt(bikeStr));
								} catch (Exception e) {
									aStation.setAvailableBikes(-1);
								}
							}
						}
					}

				}

				/*
				 * 
				 * <?xml version='1.0'?>
				 * 
				 * <div id="poi" style="width:220px;height: auto;"> <div class="poi-content"> <div class="infos1"> <div class="titre_googlemap"> CHEQUES POSTAUX</div> <div class="adresse"> Mail
				 * Fran̤ois Mitterand <br/> </div> </div>
				 * 
				 * <div class="velo_dispo"> <div class="number">4</div> V̩los disponibles </div>
				 * 
				 * <div class="place_dispo"> <div class="number">10</div> Places disponibles </div>
				 * 
				 * <div class="maj">Mise � jour il y a <b>0 min et 45 sec</b> .</div> <span class="latitude">Latitude : <i>48.108852</i> <br/>Longitude : <i>-1.69339</i></span>
				 * 
				 * </div>
				 * 
				 * </div>
				 */

				return aStation;

			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				throw new NoInternetConnection(getOneStationInfoURL(aStation.getId()));
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				throw new NoInternetConnection(getOneStationInfoURL(aStation.getId()));
			} catch (Exception e) {
				Log.e("MGR", "Unable to parse information  - CAUSE : " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			throw new NoInternetConnection(getOneStationInfoURL(aStation.getId()));
		}
		return aStation;

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

	// http://www.levelostar.fr/index.php?id=43&type=500&tx_rggooglemap_pi1[cat]=&tx_rggooglemap_pi1[area]=48.09499292725062%2C%20-1.721334457397461%2C%2048.12857505474668%2C%20-1.6392803192138672&tx_rggooglemap_pi1[zoom]=14&r=0.3605429250865563
	protected abstract String getAllStationURL();

	// http://www.levelostar.fr/index.php?id=43&type=500&no_cache=1&tx_rggooglemap_pi1[detail]=31&tx_rggooglemap_pi1[table]=tt_address
	protected abstract String getOneStationInfoURL(String stationId);
}
