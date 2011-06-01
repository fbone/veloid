package com.xirgonium.android.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.object.Station;
import com.xirgonium.exception.NoInternetConnection;

public abstract class ConstructorSmoove extends CommonStationManager {

  protected long                       lastUpdateTimeStamp  = 0;
  protected Hashtable<String, Station> lastUpdatedFavorites = new Hashtable<String, Station>();

  protected abstract String getUrlOfInformationPage();

  @Override
  public String[] getCities() {
    return new String[] { "Montpellier" };
  }

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
    int stationId = 1;

    // TODO see if this part can be optimized - restore favorite
    for (Iterator<Station> iterator = favorite.iterator(); iterator.hasNext();) {
      Station station = (Station) iterator.next();
      // Log.d("MGR-CCUS", "Found favorite station id : " + station.getId());
      favorites.put(station.getId(), station);
    }
    try {
      URL url = new URL(getUrlOfInformationPage());

      HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
      urlconn.setConnectTimeout(ConfigurationContext.getConnectionTimeout());

      // urlconn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; fr; rv:1.9) Gecko/2008052906 Firefox/3.0");
      // urlconn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
      urlconn.connect();

      // Log.d("VELOMAGG", "open url " + getUrlOfInformationPage());

      InputStreamReader reader = new InputStreamReader(urlconn.getInputStream(), "ISO-8859-1");

      BufferedReader inReader = new BufferedReader(reader);
      String line = null;

      boolean endOfProcess = false;

      Station toAdd;

      String newStationPoint = "map.addOverlay";
      String beforeDisp = "</table>";
      String beginTab = "<table";

      while ((line = inReader.readLine()) != null && !endOfProcess) {
        if (line.trim().startsWith(newStationPoint)) {
         // //Log.d("VELOMAGG", line);

          toAdd = new Station();
          toAdd.setNetwork(getNetworkId());

          int beginLatLon = line.indexOf("(4");
          int endlatLon = line.indexOf("\"");
          String latLon = line.substring(beginLatLon, endlatLon).trim();
          latLon = latLon.replace("(", "");
          //Log.d("VELOMAGG", latLon);

          StringTokenizer tokLatLon = new StringTokenizer(latLon, ",");
          int indexTok = 0;
          while (tokLatLon.hasMoreTokens()) {
            String latOrlon = tokLatLon.nextToken();
            if (indexTok++ == 0) {
              double lat = Double.parseDouble(latOrlon);
              toAdd.setLatitude(lat);
              //Log.d("VELOMAGG", "lat set to " + lat);
            } else {
              double lon = Double.parseDouble(latOrlon);
              //Log.d("VELOMAGG", "lon set to " + lon);
              toAdd.setLongitude(lon);
            }
          }

          String rest = line.substring(endlatLon);
          ////Log.d("VELOMAGG", "Parse the rest " + rest);

          int beginName = rest.indexOf(" ");
          String id = rest.substring(0, beginName);
          id = id.replace("\"", "");
         // //Log.d("VELOMAGG", "Set id " + String.valueOf(Integer.parseInt(id.trim())));
          toAdd.setId(String.valueOf(Integer.parseInt(id.trim())));
          if(favorites.containsKey(toAdd.getId())){
            toAdd.setFavorite(1);
          }

          int bTable = rest.indexOf(beginTab);
          String name = rest.substring(beginName, bTable);
          name = name.replace("<br>", "");
          //Log.d("VELOMAGG", "Set name " + name.trim());
          toAdd.setName(name.trim());

          int endTable = rest.lastIndexOf(beforeDisp);
          String disp = rest.substring(endTable + beforeDisp.length());
          disp = disp.replace("\"));", "");
          //Log.d("VELOMAGG", "disp " + disp);

          StringTokenizer dispTok = new StringTokenizer(disp, "/");
          indexTok = 0;
          int bikes = 0;
          int tot = 0;
          while (dispTok.hasMoreTokens()) {
            String bikeOrTot = dispTok.nextToken();
            if(indexTok++ ==0){
              
              bikes = Integer.parseInt(bikeOrTot);
              //Log.d("VELOMAGG", "bikes " + bikes);
            }else{
              tot = Integer.parseInt(bikeOrTot);
              //Log.d("VELOMAGG", "tot " + tot);
            }
          }
          toAdd.setAvailableBikes(bikes);
          toAdd.setFreeSlot(tot-bikes);
          
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
          
        }else if(line.trim().startsWith("//]]")){
          //Log.d("VELOMAGG", "FIN DE PROCESS");
          endOfProcess = true;
        }
      }

    } catch (SocketTimeoutException ste) {
      Log.e("MGR", "Timeout !");
      throw new NoInternetConnection(getUrlOfInformationPage());
    } catch (UnknownHostException uhe) {
      Log.e("MGR", "Unknown host !");
      throw new NoInternetConnection(getUrlOfInformationPage());
    } catch (Exception e) {
      e.printStackTrace();
      throw new NoInternetConnection(getUrlOfInformationPage());
    }
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
  public void clearListOfStationFromDatabase() {
    super.clearListOfStationFromDatabaseImpl(getNetworkId());
  }

}
