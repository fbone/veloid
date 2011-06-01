package com.xirgonium.android.manager;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
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
import com.xirgonium.android.util.Constant;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorNextBikeStationManager extends CommonStationManager {

  String                               urlNextBikeInfo      = "http://nextbike.de/m/maps.php";

  protected long                       lastUpdateTimeStamp  = 0;
  protected Hashtable<String, Station> lastUpdatedFavorites = new Hashtable<String, Station>();

  /*
   * ------------------------------- CONSTRUCTOR -------------------------------
   */

  public ConstructorNextBikeStationManager() {
  }

  public ConstructorNextBikeStationManager(Context launched) {
    super(launched);
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
    if ((now - lastUpdateTimeStamp < 30000) && lastUpdatedFavorites.containsKey(aStation.getId())) {
      // Log.d("MGR-NXT", "The last update is smaller than X sec and station exist");
      Station toRet = lastUpdatedFavorites.get(aStation.getId());

      return toRet;
    } else {
      // Log.d("MGR-NXT", "The last update is older than X sec");
      lastUpdateTimeStamp = now;
      getStationInfoFromPage();

      Station toRet = lastUpdatedFavorites.get(aStation.getId());

      return toRet;
    }
  }

  protected void getStationInfoFromPage() throws NoInternetConnection {
    Vector<Station> favorite = restoreFavoriteFromDataBase();
    Hashtable<String, Station> favorites = new Hashtable<String, Station>();

    // TODO see if this part can be optimized - restore favorite
    for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
      Station station = (Station) iterator.next();
      // Log.d("MGR-NXT", "Found favorite station id : " + station.getId());
      favorites.put(station.getId(), station);
    }
    try {
      URL url = new URL(urlNextBikeInfo);
      URLConnection urlconn = url.openConnection();

      urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

      // Log.d("MGR-NXT", "open url " + urlNextBikeInfo);

      Document doc = null;

      try {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(urlconn.getInputStream());

        NodeList countries = doc.getElementsByTagName(Constant.TAG_NEXTCITY_COUNTRY);

        for (int i = 0; i < countries.getLength(); i++) {
          // for each placemark
          Node aCountry = countries.item(i);

          // check if this is the right country
          if (getCountry().equals(((Element) aCountry).getAttribute(Constant.ATTR_NEXTCITY_NAME))) {

            // yes, now list all the stations
            // NodeList cities = ((Element) aCountry).getChildNodes();
            NodeList cities = ((Element) aCountry).getElementsByTagName(Constant.TAG_NEXTCITY_CITY);

            for (int j = 0; j < cities.getLength(); j++) {
              Node aCity = cities.item(j);

              // only for "CITY" tag
              // if (aCity.getNodeName().equals(Constant.TAG_NEXTCITY_CITY)) {

              String city = ((Element) aCity).getAttribute(Constant.ATTR_NEXTCITY_NAME);

              NodeList stations = ((Element) aCity).getElementsByTagName(Constant.TAG_NEXTCITY_STATION);

              for (int k = 0; k < stations.getLength(); k++) {
                Node aStation = stations.item(k);
                // --- Now we have a station, need to get data
                // from it
                String id = ((Element) aStation).getAttribute(Constant.ATTR_NEXTCITY_ID);
                String name = ((Element) aStation).getAttribute(Constant.ATTR_NEXTCITY_NAME);
                String lat = ((Element) aStation).getAttribute(Constant.ATTR_NEXTCITY_LAT);
                String lng = ((Element) aStation).getAttribute(Constant.ATTR_NEXTCITY_LONG);
                String bikes = ((Element) aStation).getAttribute(Constant.ATTR_NEXTCITY_BIKES);
                String spot = ((Element) aStation).getAttribute(Constant.ATTR_NEXTCITY_SPOTS);

                // Common data
                Station stationToAdd = new Station();
                stationToAdd.setId(id);
                stationToAdd.setName(name);
                stationToAdd.setAddress(city);
                stationToAdd.setLatitude(Double.parseDouble(lat));
                stationToAdd.setLongitude(Double.parseDouble(lng));
                stationToAdd.setNetwork(getNetworkId());

                // Dynamic data
                if (bikes.indexOf("+") != -1) {
                  bikes = bikes.substring(0, bikes.indexOf("+"));
                }
                stationToAdd.setAvailableBikes(Integer.parseInt(bikes));
                stationToAdd.setFreeSlot(0);

                if (favorites.containsKey(stationToAdd.getId())) {
                  stationToAdd.setFavorite(1);
                  stationToAdd.setComment(favorites.get(stationToAdd.getId()).getComment());
                  stationToAdd.setFavoriteColor(favorites.get(stationToAdd.getId()).getFavoriteColor());
                } else {
                  stationToAdd.setFavorite(0);
                  stationToAdd.setComment(name);
                  stationToAdd.setFavoriteColor(-1);
                }
                if (spot != null && spot.equals("1")) {
                  lastUpdatedFavorites.put(stationToAdd.getId(), stationToAdd);
                  // Log.d("MGR-NXT", "Station " + stationToAdd.getId() + " added");
                }
              }
              // }
            }

          }
        }

      } catch (SocketTimeoutException ste) {
        Log.e("MGR", "Timeout !");
        throw new NoInternetConnection(urlNextBikeInfo);
      } catch (UnknownHostException uhe) {
        Log.e("MGR", "Unknown host !");
        throw new NoInternetConnection(urlNextBikeInfo);
      } catch (Exception e) {
        Log.e("MGR-NXT", "Unable to parse information from next bike - CAUSE : " + e.getMessage());
        e.printStackTrace();
      }
    } catch (Exception e) {
      throw new NoInternetConnection(urlNextBikeInfo);
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

}
