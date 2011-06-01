package com.xirgonium.android.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.xirgonium.android.location.WhereAmI;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.NetworkSkeletonParameters;

public class ConfigurationContext {

	private static int					connectionTimeout					= Constant.CONFIG_KEY_DEFAULT_TIMEOUT;
	private static int					filterMinSlot						= 0;
	private static int					filterMinAvailableBike				= 0;
	private static int					timerInitialMinutes					= 1;
	private static int					lastSearchedUnits					= 1;
	private static int					lastSearchedType					= -1;
	private static boolean				timerServiceRunning					= false;
	private static Bundle				timerBundle							= null;
	private static boolean				geolocAtTimerEnd					= false;
	private static boolean				timerWaitsForCustomerEndValidation	= false;
	private static String				tabOnStartup						= Constant.CONFIG_KEY_DEFAULT_STARTUP_TAB;

	private static boolean				acceptedEULA						= false;

	// null = silent, Constant.DEFAULT_SOUND_PATH or customised path
	private static String				soundPath							= Constant.DEFAULT_SOUND_PATH;

	private static String				currentNetwork						= null;
	private static String				lastAddr							= "";
	private static int					expressBikeSearchFilter				= Constant.CONFIG_KEY_DEFAULT_EXPRESS_BIKE_SEARCH_NEAREST;
	private static int					expressSlotSearchFilter				= Constant.CONFIG_KEY_DEFAULT_EXPRESS_SLOT_SEARCH_NEAREST;
	private static MediaPlayer			player								= null;

	private static CommonStationManager	mgr;

	private static Station				usedStation							= null;

	// NEW
	private static SharedPreferences	pref								= null;

	private static WhereAmI				mylocation							= null;

	private static boolean				sortFavoriteByColor					= false;

	public static WhereAmI getWhereAmIInstance(Context ctx) {
		mylocation = (mylocation == null ? new WhereAmI(ctx) : mylocation);
		return mylocation;
	}

	public static CommonStationManager getCurrentStationManager(Context ctx) {
		currentNetwork = pref.getString(Constant.CONFIG_KEY_NETWORK, Constant.CONFIG_KEY_DEFAULT_NETWORK);
		if (mgr == null || !currentNetwork.equals(mgr.getNetworkId())) {
			mgr = NetworkSkeletonParameters.buildRequiredNetwork(currentNetwork);
		}
		return mgr;

	}

	public static CommonStationManager getStationManagerFromName(Context ctx, String name) {
		return NetworkSkeletonParameters.buildRequiredNetwork(name);
	}

	public static String getNetwork() {
		return currentNetwork;
	}

	public static void setNetwork(String currentNetwork) {
		ConfigurationContext.currentNetwork = currentNetwork;
	}

	public static boolean isAcceptedEULA() {
		return acceptedEULA;
	}

	public static void setAcceptedEULA(boolean acceptedEULA) {
		ConfigurationContext.acceptedEULA = acceptedEULA;
	}

	public static void restoreConfig(Activity act) {

		// --- NEW
		pref = PreferenceManager.getDefaultSharedPreferences(act);

		// /data/data/com.xirgonium.android/shared_prefs/com.xirgonium.android_preferences.xml

		// --- OLD
		SharedPreferences settings = pref;

		setConnectionTimeout(settings.getInt(Constant.CONFIG_KEY_TIMEOUT, Constant.CONFIG_KEY_DEFAULT_TIMEOUT));
		setFilterMinAvailableBike(settings.getInt(Constant.CONFIG_KEY_FILTER_MIN_AV_BIKE, Constant.CONFIG_KEY_DEFAULT_FILTER_MIN_AV_BIKE));
		setFilterMinSlot(settings.getInt(Constant.CONFIG_KEY_FILTER_MIN_FR_SLOT, Constant.CONFIG_KEY_DEFAULT_FILTER_MIN_FR_SLOT));
		setTimerMinutes(settings.getInt(Constant.CONFIG_KEY_TIMER_DURATION, Constant.CONFIG_KEY_DEFAULT_TIMER_DURATION));

		setSoundPath(settings.getString(Constant.CONFIG_KEY_NOTIFY_SOUND, Constant.DEFAULT_SOUND_PATH));

		setGeolocAtTimerEnd(settings.getBoolean(Constant.CONFIG_KEY_GEOLOC_AT_TIMER_END, Constant.CONFIG_KEY_DEFAULT_GEOLOC_AT_TIMER_END));

		setNetwork(settings.getString(Constant.CONFIG_KEY_NETWORK, Constant.CONFIG_KEY_DEFAULT_NETWORK));
		setLastAddr(settings.getString(Constant.CONFIG_KEY_LAST_ENTERRED_ADDRESS, ""));
		setExpressBikeSearchFilter(settings.getInt(Constant.CONFIG_KEY_EXPRESS_BIKE_SEARCH_NEAREST, Constant.CONFIG_KEY_DEFAULT_EXPRESS_BIKE_SEARCH_NEAREST));
		setExpressSlotSearchFilter(settings.getInt(Constant.CONFIG_KEY_EXPRESS_SLOT_SEARCH_NEAREST, Constant.CONFIG_KEY_DEFAULT_EXPRESS_SLOT_SEARCH_NEAREST));
		setAcceptedEULA(settings.getBoolean(Constant.CONFIG_KEY_ACCEPTED_EULA, false));
		setLastSearchedUnits(settings.getInt(Constant.CONFIG_KEY_SEARCH_UNITS, 1));
		setTabOnStartup(settings.getString(Constant.CONFIG_KEY_STARTUP_TAB, Constant.CONFIG_KEY_DEFAULT_STARTUP_TAB));
		setSortFavoriteByColor(settings.getBoolean(Constant.CONFIG_KEY_SORT_FAVORITE_BY_COLORS, Constant.CONFIG_KEY_DEFAULT_SORT_FAVORITE_BY_COLORS));
	}

	public static void saveConfig(Context act) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(act);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(Constant.CONFIG_KEY_FILTER_MIN_AV_BIKE);
		editor.putInt(Constant.CONFIG_KEY_FILTER_MIN_AV_BIKE, getFilterMinAvailableBike());
		editor.remove(Constant.CONFIG_KEY_FILTER_MIN_FR_SLOT);
		editor.putInt(Constant.CONFIG_KEY_FILTER_MIN_FR_SLOT, getFilterMinSlot());
		editor.remove(Constant.CONFIG_KEY_TIMER_DURATION);
		editor.putInt(Constant.CONFIG_KEY_TIMER_DURATION, getTimerMinutes());
		editor.remove(Constant.CONFIG_KEY_GEOLOC_AT_TIMER_END);
		editor.putBoolean(Constant.CONFIG_KEY_GEOLOC_AT_TIMER_END, isGeolocAtTimerEnd());
		editor.putString(Constant.CONFIG_KEY_NOTIFY_SOUND, soundPath);
		editor.putString(Constant.CONFIG_KEY_NETWORK, getNetwork());
		editor.putString(Constant.CONFIG_KEY_LAST_ENTERRED_ADDRESS, getLastAddr());
		editor.putBoolean(Constant.CONFIG_KEY_ACCEPTED_EULA, isAcceptedEULA());
		editor.putInt(Constant.CONFIG_KEY_SEARCH_UNITS, getLastSearchedUnits());
		editor.remove(Constant.CONFIG_KEY_STARTUP_TAB);
		editor.putString(Constant.CONFIG_KEY_STARTUP_TAB, getTabOnStartup());
		editor.putBoolean(Constant.CONFIG_KEY_SORT_FAVORITE_BY_COLORS, isSortFavoriteByColor());
		editor.commit();

	}

	public static int getMaxStationFiltered() {
		return Integer.parseInt(pref.getString(Constant.CONFIG_KEY_STATION_FILTERED, String.valueOf(Constant.CONFIG_KEY_DEFAULT_STATION_PROCESSED)));
	}

	public static int getMaxStationReturned() {
		return Integer.parseInt(pref.getString(Constant.CONFIG_KEY_STATION_RETURNED, String.valueOf(Constant.CONFIG_KEY_DEFAULT_STATION_RETURNED)));

	}

	public static int getConnectionTimeout() {
		return connectionTimeout;
	}

	public static void setConnectionTimeout(int connectionTimeout) {
		ConfigurationContext.connectionTimeout = connectionTimeout;
	}

	public static int getFilterMinSlot() {
		return filterMinSlot;
	}

	public static void setFilterMinSlot(int filterMinSlot) {
		ConfigurationContext.filterMinSlot = filterMinSlot;
	}

	public static int getFilterMinAvailableBike() {
		return filterMinAvailableBike;
	}

	public static void setFilterMinAvailableBike(int filterMinAvailableBike) {
		ConfigurationContext.filterMinAvailableBike = filterMinAvailableBike;
	}

	public static int getTimerMinutes() {
		return timerInitialMinutes;
	}

	public static void setTimerMinutes(int timerMinutes) {
		if (timerMinutes > 0)
			ConfigurationContext.timerInitialMinutes = timerMinutes;
	}

	public static boolean isTimerServiceRunning() {
		return timerServiceRunning;
	}

	public static void setTimerServiceRunning(boolean timerServiceRunning) {
		ConfigurationContext.timerServiceRunning = timerServiceRunning;
	}

	public static Bundle getTimerBundle() {
		return timerBundle;
	}

	public static void setTimerBundle(Bundle timerBundle) {
		ConfigurationContext.timerBundle = timerBundle;
	}

	public static boolean isGeolocAtTimerEnd() {
		return geolocAtTimerEnd;
	}

	public static void setGeolocAtTimerEnd(boolean geolocAtTimerEnd) {
		ConfigurationContext.geolocAtTimerEnd = geolocAtTimerEnd;
	}

	public static boolean isNotifyLED() {
		return pref.getBoolean(Constant.CONFIG_KEY_NOTIFY_LED, Constant.CONFIG_KEY_DEFAULT_NOTIFY_LED);
	}

	public static boolean isNotifyVibra() {
		return pref.getBoolean(Constant.CONFIG_KEY_NOTIFY_VIBRA, Constant.CONFIG_KEY_DEFAULT_NOTIFY_VIBRA);
	}

	public static boolean isNotifySound() {
		return !pref.getString(Constant.CONFIG_KEY_NOTIFY_SOUND, "").equals("");
	}

	public static String getSoundPath() {
		return soundPath;
	}

	public static void setSoundPath(String soundPath) {
		ConfigurationContext.soundPath = soundPath;
	}

	public static String getLastAddr() {
		return lastAddr;
	}

	public static void setLastAddr(String lastAddr) {
		ConfigurationContext.lastAddr = lastAddr;
	}

	public static int getExpressBikeSearchFilter() {
		return expressBikeSearchFilter;
	}

	public static void setExpressBikeSearchFilter(int expressBikeSearchFilter) {
		ConfigurationContext.expressBikeSearchFilter = expressBikeSearchFilter;
	}

	public static int getExpressSlotSearchFilter() {
		return expressSlotSearchFilter;
	}

	public static void setExpressSlotSearchFilter(int expressSlotSearchFilter) {
		ConfigurationContext.expressSlotSearchFilter = expressSlotSearchFilter;
	}

	public static MediaPlayer getPlayer() {
		return player;
	}

	public static void setPlayer(MediaPlayer player) {
		ConfigurationContext.player = player;
	}

	public static int getLastSearchedUnits() {
		return lastSearchedUnits;
	}

	public static void setLastSearchedUnits(int lastSearchedUnits) {
		if (lastSearchedUnits > 0 && lastSearchedUnits <= Constant.MAX_SEARCHED_UNITS) {
			ConfigurationContext.lastSearchedUnits = lastSearchedUnits;
		}
	}

	public static int getLastSearchedType() {
		return lastSearchedType;
	}

	public static void setLastSearchedType(int lastSearchedType) {
		ConfigurationContext.lastSearchedType = lastSearchedType;
	}

	public static boolean isTimerWaitsForCustomerEndValidation() {
		return timerWaitsForCustomerEndValidation;
	}

	public static void setTimerWaitsForCustomerEndValidation(boolean timerWaitsForCustomerEndValidation) {
		ConfigurationContext.timerWaitsForCustomerEndValidation = timerWaitsForCustomerEndValidation;
	}

	public static String getTabOnStartup() {
		return tabOnStartup;
	}

	public static void setTabOnStartup(String tabOnStartup) {
		ConfigurationContext.tabOnStartup = tabOnStartup;
	}

	public static Station getUsedStation() {
		return usedStation;
	}

	public static void setUsedStation(Station usedStation) {
		ConfigurationContext.usedStation = usedStation;
	}

	public static boolean isSortFavoriteByColor() {
		return sortFavoriteByColor;
	}

	public static void setSortFavoriteByColor(boolean sortFavoriteByColor) {
		ConfigurationContext.sortFavoriteByColor = sortFavoriteByColor;
	}

}
