package com.xirgonium.android.manager;

import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

public class VeloPlusOrleansStationManager extends CommonStationManager {

    public final static String WS_ALL_STATIONS_URL   = "https://www.agglo-veloplus.fr/component/data_1.xml";

    // ---- Add the station ID at the end of the URL to get data from Velo+'
    public final static String WS_STATION_DETAIL_URL = "http://www.agglo-veloplus.fr/getStatusBorne?idBorne=";

    public VeloPlusOrleansStationManager() {
    // TODO Auto-generated constructor stub
    }
    
    public VeloPlusOrleansStationManager(Context launched) {
        super(launched);
    }

    public String[] getCities() {
        return new String[] { "Orléans" };
    }

    /**
     * 
     * Get on the Velib APIs the informations related to all Stations.
     * 
     * @param aStation
     * @return The station given as parameter but completed with Velo+
     *         information
     * 
     */
    public void updateStationListDynamicaly() throws NoInternetConnection{

//        Log.d("MGR", "Restore the list of station");
        Vector<Station> favorite = restoreFavoriteFromDataBase();
        Vector<Station> stationsLst = new Vector<Station>();

        Hashtable<String, Station> keysOfIds = new Hashtable<String, Station>();

        // @TODO see if this part can be optimized - restore favorite
        for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
            Station station = (Station) iterator.next();
//            Log.d("MGR", "Found favorite station id : " + station.getId());
            keysOfIds.put(station.getId(), station);
        }

//        Log.d("MGR", "Get the list of station from web");

        try {
            URL url = new URL(WS_ALL_STATIONS_URL);

            HttpsURLConnection urlconn = (HttpsURLConnection) url.openConnection();

			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
            
            urlconn.setHostnameVerifier(new Verifier());
            urlconn.setRequestProperty("User-Agent",
                                       "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 Firefox/3.0");
            urlconn.setRequestProperty("Host", "www.agglo-veloplus.fr");
            urlconn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            urlconn.connect();

            String page = FormatUtility.slurp(urlconn.getInputStream(), Constant.CHARSET_UTF8);
            page = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + page;

            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(page));

//            Log.d("MGR", "open url " + WS_ALL_STATIONS_URL);

            Document doc = null;

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(inStream);

                Element stations = (Element) doc.getElementsByTagName(Constant.TAG_EIFFAGE_AS_MARKERS).item(0);
                // Node station = (Node)doc.getFirstChild();
                NodeList station_children = stations.getChildNodes();
                for (int i = 0; i < station_children.getLength(); i++) {
                    Node aNode = station_children.item(i);

                    Station aStation = new Station();

                    if (aNode.getNodeName() != null) {
                        if (aNode.getNodeName().equals(Constant.TAG_EIFFAGE_AS_MARKER)) {
//                            Log.d("MGR", "one station found");
                            try {
                                String name = ((Element) aNode).getAttribute(Constant.ATTR_EIFFAGE_AS_NAME);
                                aStation.setName(name);
                                aStation.setAddress(name);
                                aStation.setId(((Element) aNode).getAttribute(Constant.ATTR_EIFFAGE_AS_ID));
                                aStation.setLatitude(Float.parseFloat(((Element) aNode).getAttribute(Constant.ATTR_EIFFAGE_AS_LAT)));
                                aStation.setLongitude(Float.parseFloat(((Element) aNode).getAttribute(Constant.ATTR_EIFFAGE_AS_LONG)));
                                aStation.setNetwork(getNetworkId());
                                if (keysOfIds.containsKey(aStation.getId())) {
                                    aStation.setFavorite(1);
                                    aStation.setComment(keysOfIds.get(aStation.getId()).getComment());
                                } else {
                                    aStation.setFavorite(0);
                                    aStation.setComment("");
                                }
                                stationsLst.add(aStation);
//                                Log.d("MGR", "Station " + aStation.getId() + " added");
                            } catch (Exception e) {
                                Log.e("MGR", "Unable to parse information from velo+" + " - CAUSE : " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("MGR", "Unable to parse information from velib  - CAUSE : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SocketTimeoutException ste) {
			Log.e("MGR", "Timeout !");
			throw new NoInternetConnection(WS_ALL_STATIONS_URL );
		} catch (UnknownHostException uhe) {
			Log.e("MGR", "Unknown host !");
			throw new NoInternetConnection(WS_ALL_STATIONS_URL);
		} catch (Exception e) {
           throw new NoInternetConnection(WS_ALL_STATIONS_URL);
        }

//        Log.d("MGR", "All stations grabbed from web, delete all the table");
        this.clearListOfStationFromDatabase();
//        Log.d("MGR", "Restore all information");
        for (Iterator<Station> iterator = stationsLst.iterator(); iterator.hasNext();) {
            Station station = (Station) iterator.next();
            saveStationIntoDB(station);
        }
//        Log.d("MGR", "List successfully restored");
    }

    /**
     * 
     * Get on the Velib APIs the informations related to a Station.
     * 
     * @param aStation
     * @return The station given as parameter but completed with Velib
     *         information
     * 
     */
    public Station fillDynamicInformationForAStation(Station aStation) throws NoInternetConnection{
        aStation.setUpdateSuccess(1);
        try {
            URL url = new URL(WS_STATION_DETAIL_URL + aStation.getId());
//            Log.d("MGR", "Connect to " + url);
            URLConnection urlconn = url.openConnection();

			urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());
            Document doc = null;

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(urlconn.getInputStream());

                Element station = (Element) doc.getElementsByTagName(Constant.TAG_EIFFAGE_SD_STATION).item(0);
                NodeList station_children = station.getChildNodes();
                for (int i = 0; i < station_children.getLength(); i++) {
                    Node aNode = station_children.item(i);
                    if (aNode.getNodeName() != null) {
                        if (aNode.getNodeName().equals(Constant.TAG_EIFFAGE_SD_BIKE)) {
                            try {
                                aStation.setAvailableBikes(Integer.parseInt(aNode.getFirstChild().getNodeValue()));
                            } catch (Exception e) {
                                Log.e("MGR", "Unable to parse information from velo+ - Tag : "
                                    + Constant.TAG_SD_AVAILABLE + " - CAUSE : " + e.getMessage());
                                aStation.setUpdateSuccess(Constant.ERR_PARSING);
                            }
                        }
                        if (aNode.getNodeName().equals(Constant.TAG_EIFFAGE_SD_ATTACH)) {
                            try {
                                aStation.setFreeSlot(Integer.parseInt(aNode.getFirstChild().getNodeValue()));
                            } catch (Exception e) {
                                Log.e("MGR", "Unable to parse information from velo+ - Tag : " + Constant.TAG_SD_FREE
                                    + " - CAUSE : " + e.getMessage());
                                aStation.setUpdateSuccess(Constant.ERR_PARSING);
                            }
                        }
                    }
                }
            } catch (SocketTimeoutException ste) {
				Log.e("MGR", "Timeout !");
				aStation.setUpdateSuccess(Constant.ERR_CONNECT);
				throw new NoInternetConnection(WS_STATION_DETAIL_URL + aStation.getId());
			} catch (UnknownHostException uhe) {
				Log.e("MGR", "Unknown host !");
				aStation.setUpdateSuccess(Constant.ERR_CONNECT);
				throw new NoInternetConnection(WS_STATION_DETAIL_URL + aStation.getId());
			} catch (Exception e) {
                Log.e("MGR", "Unable to parse information from velib  - CAUSE : " + e.getMessage());
                aStation.setUpdateSuccess(Constant.ERR_PARSING);
                e.printStackTrace();
            }
        } catch (Exception e) {            
            aStation.setUpdateSuccess(Constant.ERR_CONNECT);
            throw new NoInternetConnection(WS_STATION_DETAIL_URL);
        }

        return aStation;

    }

    @Override
    public void clearListOfStationFromDatabase() {
        super.clearListOfStationFromDatabaseImpl(getNetworkId());

    }

    @Override
    public Vector<Station> restoreFavoriteFromDataBaseAndWeb() throws NoInternetConnection {
        return super.restoreFavoriteFromDataBaseAndWebImpl(getNetworkId());
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

    public class Verifier implements HostnameVerifier {

        public Verifier() {}

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

    }
}
