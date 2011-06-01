package com.xirgonium.android.util;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.object.Station;
import com.xirgonium.exception.NoInternetConnection;

public class StationFilter {

    private Hashtable<String, Object> filtervalues = null;

    public StationFilter() {
        filtervalues = new Hashtable<String, Object>();
    }

    public void set(String key, Object value) {
        filtervalues.put(key, value);
    }

    /**
     * return the stations corresponding to the filter defined, in the vector of Stations 
     * provided.
     * 
     * @param stations - a vector of sorted station, nearest first.
     * @return
     */
    public Vector<Station> filterStations(Vector<Station> stations) {

        Vector<Station> returnedStations = new Vector<Station>();

        int maxStation = ConfigurationContext.getMaxStationReturned();

        for (Iterator<Station> iterator = stations.iterator(); iterator.hasNext();) {
            Station station = (Station) iterator.next();

            if (matchFilter(station)) {
                returnedStations.add(station);
            }

            if (returnedStations.size() >= maxStation) {
                return returnedStations;
            }
        }

        return returnedStations;
    }
    
    /**
     * return the stations corresponding to the filter defined, in the vector of Stations 
     * provided.
     * 
     * @param stations - a vector of sorted station, nearest first.
     * @return
     */
    public Vector<Station> filterStationsWithRealTimeChecking(Vector<Station> stations, CommonStationManager mgr) throws NoInternetConnection {

        Vector<Station> returnedStations = new Vector<Station>();

        int maxStation = ConfigurationContext.getMaxStationReturned();
        
        
        for (Iterator<Station> iterator = stations.iterator(); iterator.hasNext();) {
            Station station = (Station) iterator.next();
            
            station = mgr.fillDynamicInformationForAStation(station);

            if (matchFilter(station)) {
                returnedStations.add(station);
            }

            if (returnedStations.size() >= maxStation) {
                return returnedStations;
            }
        }

        return returnedStations;
    }

    private boolean matchFilter(Station station) {
        int minFreeSlot = 0;
        int minAvailableBike = 0;
        boolean checkUpdate = false;

        if (filtervalues.get(Constant.FILTER_MIN_FREE_SLOTS) != null) {
            minFreeSlot = ((Integer) filtervalues.get(Constant.FILTER_MIN_FREE_SLOTS)).intValue();
            if (station.getFreeSlot() < minFreeSlot) {
                return false;
            }
        }

        if (filtervalues.get(Constant.FILTER_MIN_AVAILABLE_BIKES) != null) {
            minAvailableBike = ((Integer) filtervalues.get(Constant.FILTER_MIN_AVAILABLE_BIKES)).intValue();
            if (station.getAvailableBikes() < minAvailableBike) {
                return false;
            }
        }

//        if (filtervalues.get(Constant.FILTER_UPDATED_STATIONS) != null) {
//            checkUpdate = (((BigDecimal) filtervalues.get(Constant.FILTER_UPDATED_STATIONS)).intValue() == 1);
//            if (checkUpdate & station.getUpdateStatus() < 0) {
//                return false;
//            }
//        }
        return true;

    }

    public Object getFiltervalues(String key) {
        return filtervalues.get(key);
    }

}
