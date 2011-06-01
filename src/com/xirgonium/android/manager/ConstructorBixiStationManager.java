package com.xirgonium.android.manager;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorBixiStationManager extends CommonStationManager {

	protected long							lastUpdateTimeStamp		= 0;
	protected Hashtable<String, Station>	lastUpdatedFavorites	= new Hashtable<String, Station>();

	/*
	 * ------------------------------- CONSTRUCTOR -------------------------------
	 */

	public ConstructorBixiStationManager() {
	}

	public ConstructorBixiStationManager(Context launched) {
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
			Log.d("BIXI", "CASE1" +this.toString());
			return toRet;
		} else {
			Log.d("BIXI", "CASE2" +this.toString());
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
			// URLConnection urlconn = url.openConnection();
			//
			// urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

			// HttpsURLConnection urlconn = (HttpsURLConnection) url.openConnection();
			// urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
			//      
			// urlconn.setHostnameVerifier(new Verifier());
			// urlconn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 Firefox/3.0");
			// urlconn.setRequestProperty("Host", "profil.bixi.ca");
			// urlconn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			// urlconn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			// urlconn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			// urlconn.setRequestProperty("Keep-Alive", "300");
			// urlconn.setRequestProperty("Connection", "keep-alive");
			// urlconn.setRequestProperty("Referer", "https://profil.bixi.ca/iframe/bikeStations.php?lang=fr");
			//      
			// urlconn.connect();

			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}
			} };

			// URLConnection urlconn = url.openConnection();
			// urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
			HttpsURLConnection urlconn = (HttpsURLConnection) url.openConnection();
			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
			urlconn.setHostnameVerifier(new Verifier());
			// Install the all-trusting trust manager
			try {
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				urlconn.setSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
				e.printStackTrace();
			}

			urlconn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 Firefox/3.0");
			urlconn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlconn.connect();

			Document doc = null;

			final String STATION = "station";
			final String NAME = "name";
			final String LAT = "lat";
			final String LON = "long";
			final String BIKES = "nbBikes";
			final String SLOTS = "nbEmptyDocks";

			final String INSTALLED = "installed";

			final String LOCKED = "locked";

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(urlconn.getInputStream());

				NodeList stations = doc.getElementsByTagName(STATION);

				for (int i = 0; i < stations.getLength(); i++) {
					Node station = stations.item(i);
					Station stationToAdd = new Station();
					stationToAdd.setNetwork(this.getNetworkId());
					NodeList stationProps = station.getChildNodes();
					boolean installed = false;
					for (int j = 0; j < stationProps.getLength(); j++) {
						Node aChild = stationProps.item(j);
						if (aChild.getNodeName() != null) {
							if (aChild.getNodeName().equals(NAME)) {
								// Log.d("BIXI", "Set name to " + aChild.getFirstChild().getNodeValue());
								stationToAdd.setName(aChild.getFirstChild().getNodeValue());
								stationToAdd.setId(String.valueOf(FormatUtility.intFromString(aChild.getFirstChild().getNodeValue())));
							}
							if (aChild.getNodeName().equals(INSTALLED)) {
								try {
									installed = Boolean.parseBoolean(aChild.getFirstChild().getNodeValue());
								} catch (Exception e) {
									Log.w("BIXI", "Error while getting the longitude");

								}

							}

							if (aChild.getNodeName().equals(LAT)) {
								try {
									stationToAdd.setLatitude(Double.parseDouble(aChild.getFirstChild().getNodeValue()));
								} catch (Exception e) {
									Log.w("BIXI", "Error while getting the latitude");
								}
							}
							if (aChild.getNodeName().equals(LON)) {
								try {
									stationToAdd.setLongitude(Double.parseDouble(aChild.getFirstChild().getNodeValue()));
								} catch (Exception e) {
									Log.w("BIXI", "Error while getting the longitude");

								}

							}
							if (aChild.getNodeName().equals(BIKES)) {
								try {
									stationToAdd.setAvailableBikes(Integer.parseInt(aChild.getFirstChild().getNodeValue()));
								} catch (Exception e) {
									Log.w("BIXI", "Error while getting the bikes");

								}
							}
							if (aChild.getNodeName().equals(SLOTS)) {
								try {
									stationToAdd.setFreeSlot(Integer.parseInt(aChild.getFirstChild().getNodeValue()));
								} catch (Exception e) {
									Log.w("BIXI", "Error while getting the slots");

								}
							}

						}
					}

					if (stationToAdd.getId() != null) {

						if (favorites.containsKey(stationToAdd.getId())) {
							stationToAdd.setFavorite(1);
							stationToAdd.setComment(favorites.get(stationToAdd.getId()).getComment());
							stationToAdd.setFavoriteColor(favorites.get(stationToAdd.getId()).getFavoriteColor());
						} else {
							stationToAdd.setFavorite(0);
							stationToAdd.setComment(stationToAdd.getName());
							stationToAdd.setFavoriteColor(-1);
						}
						if (installed) {
							lastUpdatedFavorites.put(stationToAdd.getId(), stationToAdd);
						}
					}
				}

			} catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				throw new NoInternetConnection(getUrlOfInformationPage());
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				throw new NoInternetConnection(getUrlOfInformationPage());
			} catch (Exception e) {
				Log.e("MGR-NXT", "Unable to parse information from next bike - CAUSE : " + e.getMessage());
				e.printStackTrace();
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
