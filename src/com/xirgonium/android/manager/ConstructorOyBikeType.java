package com.xirgonium.android.manager;

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

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorOyBikeType extends CommonStationManager {

    protected long                       lastUpdateTimeStamp  = 0;
    protected Hashtable<String, Station> lastUpdatedFavorites = new Hashtable<String, Station>();

    public ConstructorOyBikeType() {}
    
    public ConstructorOyBikeType(Context ctx) {
        super(ctx);
    }

    /*
     * ------------------------------- 
     *         ABSTRACT METHODS
     * -------------------------------
     */
    protected abstract String getCountry();

    protected abstract String getUrlOfInformationPage();

    /*
     * ------------------------------- 
     *         SPECIFIC METHODS
     * -------------------------------
     */

    @Override
    public void updateStationListDynamicaly()
        throws NoInternetConnection {
        //        Log.d("MGR-OYB", "Get the list of station from web");
        getStationInfoFromPage();

        //        Log.d("MGR-OYB", "All stations grabbed from web, delete all the table");
        this.clearListOfStationFromDatabase();

        //        Log.d("MGR-OYB", "Restore all information");

        for (Enumeration<String> enumStation = lastUpdatedFavorites.keys(); enumStation.hasMoreElements();) {
            Station station = lastUpdatedFavorites.get(enumStation.nextElement());
            saveStationIntoDB(station);
        }
        //        Log.d("MGR-OYB", "List successfully restored");
    }

    @Override
    public Station fillDynamicInformationForAStation(Station aStation)
        throws NoInternetConnection {
        long now = new Date().getTime();
        if ((now - lastUpdateTimeStamp < 30000) && lastUpdatedFavorites.containsKey(aStation.getId())) {
            //            Log.d("MGR-OYB", "The last update is smaller than X sec and station exist");
            Station toRet = lastUpdatedFavorites.get(aStation.getId());

            return toRet;
        } else {
            //            Log.d("MGR-OYB", "The last update is older than X sec");
            lastUpdateTimeStamp = now;
            getStationInfoFromPage();

            Station toRet = lastUpdatedFavorites.get(aStation.getId());

            return toRet;
        }
    }

    protected void getStationInfoFromPage()
        throws NoInternetConnection {
        Vector<Station> favorite = restoreFavoriteFromDataBase();
        Hashtable<String, Station> favorites = new Hashtable<String, Station>();

        // TODO see if this part can be optimized - restore favorite
        for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
            Station station = (Station) iterator.next();
            //            Log.d("MGR-OYB", "Found favorite station id : " + station.getId());
            favorites.put(station.getId(), station);
        }
        try {
            URL url = new URL(getUrlOfInformationPage());

            URLConnection urlconn = (URLConnection) url.openConnection();
            urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

            //            Log.d("MGR-OYB", "open url " + getUrlOfInformationPage());            

            String page = FormatUtility.slurp(urlconn.getInputStream(), Constant.CHARSET_ISO_8859_1);

            String varpointPref = "var point = new GLatLng(";
            String addMarkerPref = "map.addOverlay(marker);";
            String oyBikeDiv = "<div class=\"oybtext\">";
            String endDiv = "</div";
            String oyBikeAvailable = "OYBikes available";
            String parkingAvailable = "Parking spaces";

            //Tout sur une ligne
            //var point = new GLatLng(51.4908362518725,-0.225009577761604);
            //var marker = createMarker(point,'Apollo Theatre', '<div class="oybtext">Apollo Theatre<br><br>Fulham Palace Road<br> East side 20 meters<br><br>3 OYBikes available for hire<br>0 Parking spaces available<br>Last updated at 07/10/2008 05:36:41 PM<br></div>',noParkingIcon);
            //map.addOverlay(marker);

            Station toAdd = new Station();

            int beginStationInfo = page.indexOf(varpointPref);
            int endStationInfo = page.lastIndexOf(addMarkerPref) + addMarkerPref.length();

            String stationInformation = page.substring(beginStationInfo, endStationInfo);

            String aStation = stationInformation.substring(0, stationInformation.indexOf(addMarkerPref) + addMarkerPref.length());

            while (true) {
                toAdd.setNetwork(getNetworkId());

                String latlon = aStation.substring(aStation.indexOf("(") + 1, aStation.indexOf(");"));
                //latlon = latlon.replace(");", "");
                StringTokenizer token = new StringTokenizer(latlon, ",");
                int index = 0;
                while (token.hasMoreElements()) {
                    String latOrLon = (String) token.nextElement();
                    Double lolasnum = Double.parseDouble(latOrLon.trim());
                    if (index == 0) {
                        toAdd.setLatitude(lolasnum);
                        index++;
                    } else if (index == 1) {
                        toAdd.setLongitude(lolasnum);
                    }
                }

                int indexOfDetailedInfoBegin = aStation.indexOf(oyBikeDiv);
                int endOfDetailedInfoBegin = aStation.indexOf(endDiv);

                String detailedInfo = aStation.substring(indexOfDetailedInfoBegin + oyBikeDiv.length(), endOfDetailedInfoBegin);
                //Apollo Theatre<br><br>Fulham Palace Road<br> East side 20 meters<br><br>3 OYBikes available for hire<br>0 Parking spaces available<br>Last updated at 07/10/2008 05:36:41 PM<br>
                String name = detailedInfo.substring(0, detailedInfo.indexOf("<br>"));
                if (name.indexOf("<font") != -1) {
                    name = name.substring(0, name.indexOf("<font"));
                }
                toAdd.setName(name);

                String fulladdress = detailedInfo.substring(detailedInfo.indexOf("<br><br>") + "<br><br>".length(), detailedInfo.lastIndexOf("<br><br>"));

                detailedInfo = detailedInfo.substring(fulladdress.length());
                fulladdress = fulladdress.replace("<br>", " - ");
                toAdd.setAddress(fulladdress);

                //--- Availabilities
                String availableBike = detailedInfo.substring(detailedInfo.indexOf("<br><br>") + "<br><br>".length(), detailedInfo.indexOf(oyBikeAvailable));

                String freeSlots = detailedInfo.substring(detailedInfo.indexOf(oyBikeAvailable) + oyBikeAvailable.length(), detailedInfo.indexOf(parkingAvailable));

                StringBuffer numberToBuild = new StringBuffer();
                for (int i = 0; i < availableBike.length(); i++) {
                    if (Character.isDigit(availableBike.charAt(i))) {
                        numberToBuild.append(availableBike.charAt(i));
                    }
                }
                int availableBikes = Integer.parseInt(numberToBuild.toString());
                toAdd.setAvailableBikes(availableBikes);

                numberToBuild = new StringBuffer();
                for (int i = 0; i < freeSlots.length(); i++) {
                    if (Character.isDigit(freeSlots.charAt(i))) {
                        numberToBuild.append(freeSlots.charAt(i));
                    }
                }
                int availableSlot = Integer.parseInt(numberToBuild.toString());
                toAdd.setFreeSlot(availableSlot);

                toAdd.setId(String.valueOf(FormatUtility.intFromString(name)));
                //                Log.d("MGR-OYBB", indexId++ + "Add station " + toAdd.getName());
                if (lastUpdatedFavorites.containsKey(toAdd.getId())) {
                    //                	 Log.d("MGR-OYBB", "Station " + toAdd.getName() + "Already in list");

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

                stationInformation = stationInformation.substring(aStation.length());

                if (stationInformation.length() < 10) {
                    break;
                }

                aStation = stationInformation.substring(0, stationInformation.indexOf(addMarkerPref) + addMarkerPref.length());
                toAdd = new Station();
            }

        } catch (SocketTimeoutException ste) {
            Log.e("MGR-OYB", "Timeout !");
            throw new NoInternetConnection(getUrlOfInformationPage());
        } catch (UnknownHostException uhe) {
            Log.e("MGR-OYB", "Unknown host !");
            throw new NoInternetConnection(getUrlOfInformationPage());
        } catch (Exception e) {
            throw new NoInternetConnection(getUrlOfInformationPage());
        }
    }

    /*
     * ------------------------------- SIMPLE OVERRIDEN METHOD
     * -------------------------------
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
    public Vector<Station> fillInformationFromDBAndWeb(Vector<Station> stations)
        throws NoInternetConnection {
        return super.fillInformationFromDBAndWebImpl(getNetworkId(), stations);
    }

    @Override
    public Vector<Station> restoreFavoriteFromDataBase() {
        return super.restoreFavoriteFromDataBaseImpl(getNetworkId());
    }

    @Override
    public Vector<Station> restoreFavoriteFromDataBaseAndWeb()
        throws NoInternetConnection {
        return super.restoreFavoriteFromDataBaseAndWebImpl(getNetworkId());
    }

    public class Verifier implements HostnameVerifier {

        public Verifier() {}

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

    }

}
