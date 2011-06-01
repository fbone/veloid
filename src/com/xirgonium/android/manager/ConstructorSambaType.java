package com.xirgonium.android.manager;

import java.io.LineNumberReader;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import android.content.Context;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorSambaType extends CommonStationManager {

    protected long                       lastUpdateTimeStamp  = 0;
    protected Hashtable<String, Station> lastUpdatedFavorites = new Hashtable<String, Station>();

    public ConstructorSambaType() {}

    public ConstructorSambaType(Context ctx) {
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
        //        Log.d("MGR-SMB", "Get the list of station from web");
        getStationInfoFromPage();

        //        Log.d("MGR-SMB", "All stations grabbed from web, delete all the table");
        this.clearListOfStationFromDatabase();

        //        Log.d("MGR-SMB", "Restore all information");

        for (Enumeration<String> enumStation = lastUpdatedFavorites.keys(); enumStation.hasMoreElements();) {
            Station station = lastUpdatedFavorites.get(enumStation.nextElement());
            saveStationIntoDB(station);
        }
        //        Log.d("MGR-SMB", "List successfully restored");
    }

    @Override
    public Station fillDynamicInformationForAStation(Station aStation)
        throws NoInternetConnection {
        long now = new Date().getTime();
        if ((now - lastUpdateTimeStamp < 30000) && lastUpdatedFavorites.containsKey(aStation.getId())) {
            //            Log.d("MGR-SMB", "The last update is smaller than X sec and station exist");
            Station toRet = lastUpdatedFavorites.get(aStation.getId());

            return toRet;
        } else {
            //            Log.d("MGR-SMB", "The last update is older than X sec");
            lastUpdateTimeStamp = now;
            getStationInfoFromPage();

            Station toRet = lastUpdatedFavorites.get(aStation.getId());

            return toRet;
        }
    }
    
   // criaPonto(point,12,'Posto 6','Av. AtlÃ¢ntica, NÂº 4230','em frente ao NÂº 4230 - Posto 6','12x0',1,'A','EO',12,7,269,58,33) )

    protected void getStationInfoFromPage()
        throws NoInternetConnection {
        Vector<Station> favorite = restoreFavoriteFromDataBase();
        Hashtable<String, Station> favorites = new Hashtable<String, Station>();

        // TODO see if this part can be optimized - restore favorite
        for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
            Station station = (Station) iterator.next();
            Log.d("MGR-SMB", "Found favorite station id : " + station.getId());
            favorites.put(station.getId(), station);
        }
        try {
            URL url = new URL(getUrlOfInformationPage());

            URLConnection urlconn = (URLConnection) url.openConnection();
            urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

            Log.d("MGR-SMB", "open url " + getUrlOfInformationPage());

            String page = FormatUtility.slurp(urlconn.getInputStream(), Constant.CHARSET_ISO_8859_1);

            String varpointPref = "var point = new GLatLng(";
            String addMarkerPref = "map.addOverlay( criaPonto(";
            String endStationInfo = "function criaPonto(";
            
            int startStationIndex = page.indexOf(varpointPref);
            int endStationIndex = page.indexOf(endStationInfo);
            
            page = page.substring(startStationIndex, endStationIndex);
            
            Log.d("MGR-SMB", "page : " + page);

            
            //var point = new GLatLng(-22.972815,-43.185925);
            //map.addOverlay( criaPonto(point,15,'Santa Clara','Av. Atlântica - Copacabana - em frente a Rua Santa Clara','em frente a Rua Santa Clara','8x6',1,'A','EO',14,7,713,50) );


            Station temporaryStation = null;

                
            StringTokenizer tokenizeByLine = new StringTokenizer(page, ";");
            while(tokenizeByLine.hasMoreTokens()){
                String line = tokenizeByLine.nextToken();
                Log.d("MGR-SMB", "line : " + line);

                if (line.indexOf(varpointPref) != -1) {
                    Log.d("MGR-SMB", "create a new station");
                    
                    temporaryStation = new Station();
                    temporaryStation.setNetwork(getNetworkId());

                    String latlon = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    //latlon = latlon.replace(");", "");
                    Log.d("MGR-SMB", "LAT LON" + latlon);
                    StringTokenizer token = new StringTokenizer(latlon, ",");
                    int index = 0;
                    while (token.hasMoreElements()) {
                        String latOrLon = (String) token.nextElement();
                        Double lolasnum = Double.parseDouble(latOrLon.trim());
                        if (index == 0) {
                            temporaryStation.setLatitude(lolasnum);
                            index++;
                        } else if (index == 1) {
                            temporaryStation.setLongitude(lolasnum);
                        }
                    }
                }
                //PATCH
                line = line.replace("tica, Nº", "tica - Nº");
                
                if (line.startsWith(addMarkerPref)) {
                    String tmp = line.substring(addMarkerPref.length(), line.indexOf(")"));
                    StringTokenizer parseStationData = new StringTokenizer(tmp, ",");
                    int index = 0;
                    int totalSlot = 0;

                    while (parseStationData.hasMoreTokens()) {
                        String tok = parseStationData.nextToken();
                        //point,<1>15,<2>'Santa Clara',<3>'Av. Atlântica - Copacabana - em frente a Rua Santa Clara','em frente a Rua Santa Clara','8x6',1,'A','EO',<9>14,<10>7,713,50
                        Log.d("MGR-SMB", "token " + index + " : " + tok);

                        switch (index++) {
                            case 1:
                                //id
                                temporaryStation.setId(tok);
                                break;
                            case 2:
                                //name           
                                temporaryStation.setName(tok.replace("'", ""));
                                break;
                            case 3:
                                //address
                                temporaryStation.setAddress(tok);
                                break;
                            case 9:
                                //total
                                try {
                              totalSlot = Integer.parseInt(tok.trim());
                            } catch (Exception e) {
                             
                              e.printStackTrace();
                            }
                                break;
                            case 10:
                                //bikes
                                try {
                              int bikes = Integer.parseInt(tok.trim());
                              temporaryStation.setAvailableBikes(bikes);
                              temporaryStation.setFreeSlot(totalSlot - bikes);
                            } catch (Exception e) {
                             
                              e.printStackTrace();
                            }
                                break;
                            case 12:
                                //validate
                                if (favorites.containsKey(temporaryStation.getId())) {
                                    temporaryStation.setFavorite(1);
                                    temporaryStation.setComment(favorites.get(temporaryStation.getId()).getComment());
                                    temporaryStation.setFavoriteColor(favorites.get(temporaryStation.getId()).getFavoriteColor());
                                } else {
                                    temporaryStation.setFavorite(0);
                                    temporaryStation.setComment(temporaryStation.getName());
                                    temporaryStation.setFavoriteColor(-1);
                                }
                                
                                //optim : faire un clone
                                Station toAdd = new Station();
                                toAdd.setNetwork(temporaryStation.getNetwork());
                                toAdd.setLatitude(temporaryStation.getLatitude());                                
                                toAdd.setLongitude(temporaryStation.getLongitude());
                                toAdd.setName(temporaryStation.getName());
                                toAdd.setId(temporaryStation.getId());
                                toAdd.setAvailableBikes(temporaryStation.getAvailableBikes());
                                toAdd.setFreeSlot(temporaryStation.getFreeSlot());
                                toAdd.setFavorite(temporaryStation.getFavorite());
                                toAdd.setComment(temporaryStation.getComment());
                                toAdd.setAddress(temporaryStation.getAddress());
                                
                                lastUpdatedFavorites.put(temporaryStation.getId(), toAdd);
                    
                                Log.d("MGR-SMB", "add the station");
                  
                                break;
                            default:
                                break;
                        }
                    }
                }

            }
            Log.e("MGR-SMB", "FIN");

            //            int beginStationInfo = page.indexOf(varpointPref);
            //            String stationInformation = page.substring(beginStationInfo);
            //            int endStationInfo = 0;
            //
            //            stationInformation = page.substring(beginStationInfo, endStationInfo);
            //
            //            String aStation = stationInformation.substring(0, stationInformation.indexOf(addMarkerPref) + addMarkerPref.length());

            //            while (true) {
            //                toAdd.setNetwork(getNetworkId());
            //
            //                String latlon = aStation.substring(aStation.indexOf("(") + 1, aStation.indexOf(");"));
            //                //latlon = latlon.replace(");", "");
            //                StringTokenizer token = new StringTokenizer(latlon, ",");
            //                int index = 0;
            //                while (token.hasMoreElements()) {
            //                    String latOrLon = (String) token.nextElement();
            //                    Double lolasnum = Double.parseDouble(latOrLon.trim());
            //                    if (index == 0) {
            //                        toAdd.setLatitude(lolasnum);
            //                        index++;
            //                    } else if (index == 1) {
            //                        toAdd.setLongitude(lolasnum);
            //                    }
            //                }
            //
            //                int indexOfDetailedInfoBegin = aStation.indexOf(oyBikeDiv);
            //                int endOfDetailedInfoBegin = aStation.indexOf(endDiv);
            //
            //                String detailedInfo = aStation.substring(indexOfDetailedInfoBegin + oyBikeDiv.length(), endOfDetailedInfoBegin);
            //                //Apollo Theatre<br><br>Fulham Palace Road<br> East side 20 meters<br><br>3 OYBikes available for hire<br>0 Parking spaces available<br>Last updated at 07/10/2008 05:36:41 PM<br>
            //                String name = detailedInfo.substring(0, detailedInfo.indexOf("<br>"));
            //                if (name.indexOf("<font") != -1) {
            //                    name = name.substring(0, name.indexOf("<font"));
            //                }
            //                toAdd.setName(name);
            //
            //                String fulladdress = detailedInfo.substring(detailedInfo.indexOf("<br><br>") + "<br><br>".length(), detailedInfo.lastIndexOf("<br><br>"));
            //
            //                detailedInfo = detailedInfo.substring(fulladdress.length());
            //                fulladdress = fulladdress.replace("<br>", " - ");
            //                toAdd.setAddress(fulladdress);
            //
            //                //--- Availabilities
            //                String availableBike = detailedInfo.substring(detailedInfo.indexOf("<br><br>") + "<br><br>".length(), detailedInfo.indexOf(oyBikeAvailable));
            //
            //                String freeSlots = detailedInfo.substring(detailedInfo.indexOf(oyBikeAvailable) + oyBikeAvailable.length(), detailedInfo.indexOf(parkingAvailable));
            //
            //                StringBuffer numberToBuild = new StringBuffer();
            //                for (int i = 0; i < availableBike.length(); i++) {
            //                    if (Character.isDigit(availableBike.charAt(i))) {
            //                        numberToBuild.append(availableBike.charAt(i));
            //                    }
            //                }
            //                int availableBikes = Integer.parseInt(numberToBuild.toString());
            //                toAdd.setAvailableBikes(availableBikes);
            //
            //                numberToBuild = new StringBuffer();
            //                for (int i = 0; i < freeSlots.length(); i++) {
            //                    if (Character.isDigit(freeSlots.charAt(i))) {
            //                        numberToBuild.append(freeSlots.charAt(i));
            //                    }
            //                }
            //                int availableSlot = Integer.parseInt(numberToBuild.toString());
            //                toAdd.setFreeSlot(availableSlot);
            //
            //                toAdd.setId(String.valueOf(FormatUtility.intFromString(name)));
            //                //                Log.d("MGR-SMBB", indexId++ + "Add station " + toAdd.getName());
            //                if (lastUpdatedFavorites.containsKey(toAdd.getId())) {
            //                    //                	 Log.d("MGR-SMBB", "Station " + toAdd.getName() + "Already in list");
            //
            //                }
            //
            //                if (favorites.containsKey(toAdd.getId())) {
            //                    toAdd.setFavorite(1);
            //                    toAdd.setComment(favorites.get(toAdd.getId()).getComment());
            //                } else {
            //                    toAdd.setFavorite(0);
            //                    toAdd.setComment(toAdd.getName());
            //                }
            //                lastUpdatedFavorites.put(toAdd.getId(), toAdd);
            //
            //                stationInformation = stationInformation.substring(aStation.length());
            //
            //                if (stationInformation.length() < 10) {
            //                    break;
            //                }
            //
            //                aStation = stationInformation.substring(0, stationInformation.indexOf(addMarkerPref) + addMarkerPref.length());
            //                
            //            }

        } catch (SocketTimeoutException ste) {
            Log.e("MGR-SMB", "Timeout !");
            throw new NoInternetConnection(getUrlOfInformationPage());
        } catch (UnknownHostException uhe) {
            Log.e("MGR-SMB", "Unknown host !");
            throw new NoInternetConnection(getUrlOfInformationPage());
        } catch (Exception e) {
            //Log.e("MGR-SMB", e.getMessage());
            e.printStackTrace();
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
