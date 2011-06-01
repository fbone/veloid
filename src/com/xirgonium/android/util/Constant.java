package com.xirgonium.android.util;

import com.google.android.maps.GeoPoint;
import com.xirgonium.android.veloid.R;

public class Constant {

	// ---- Configuration file name
	public final static String		CONFIG_FILE_NAME									= "veloid.config";
	public final static String		CONFIG_KEY_FIRST_LOCATION_PROVIDER					= "first.location.provider";
	public final static String		CONFIG_KEY_SECOND_LOCATION_PROVIDER					= "second.location.provider";
	public final static String		CONFIG_KEY_STATION_RETURNED							= "station.returned";															// OK
	public final static String		CONFIG_KEY_STATION_FILTERED							= "station.filtered";															// OK
	public final static String		CONFIG_KEY_TIMEOUT									= "timeout";
	public final static String		CONFIG_KEY_FILTER_MIN_AV_BIKE						= "filter.av.bike";
	public final static String		CONFIG_KEY_FILTER_MIN_FR_SLOT						= "filter.fr.slot";
	public final static String		CONFIG_KEY_TIMER_DURATION							= "timer";
	public final static String		CONFIG_KEY_GEOLOC_AT_TIMER_END						= "timer.geoloc.at.end";
	public final static String		CONFIG_KEY_NOTIFY_LED								= "notif.led";
	public final static String		CONFIG_KEY_NOTIFY_VIBRA								= "notif.vibra";
	public final static String		CONFIG_KEY_NOTIFY_SOUND								= "notif.sound";
	public final static String		CONFIG_KEY_NETWORK									= "biking.network";
	public final static String		CONFIG_KEY_LAST_ENTERRED_ADDRESS					= "last.enteredf.address";
	public final static String		CONFIG_KEY_EXPRESS_BIKE_SEARCH_NEAREST				= "express.search.bike.filter";
	public final static String		CONFIG_KEY_EXPRESS_SLOT_SEARCH_NEAREST				= "express.search.slots.filter";
	public final static String		CONFIG_KEY_ACCEPTED_EULA							= "eula.accepted";
	public final static String		CONFIG_KEY_SEARCH_UNITS								= "search.units";
	public final static String		CONFIG_KEY_STARTUP_TAB								= "startup.tab";
	public final static String		CONFIG_KEY_SORT_FAVORITE_BY_COLORS					= "favorite.sorted.by.colors";

	public final static String		CONFIG_KEY_DEFAULT_LOCATION_PROVIDER				= "n/a";
	public final static int			CONFIG_KEY_DEFAULT_STATION_RETURNED					= 3;
	public final static int			CONFIG_KEY_DEFAULT_STATION_PROCESSED				= 15;
	public final static int			CONFIG_KEY_DEFAULT_TIMEOUT							= 15000;
	public final static int			CONFIG_KEY_DEFAULT_FILTER_MIN_AV_BIKE				= 0;
	public final static boolean		CONFIG_KEY_DEFAULT_GEOLOC_AT_TIMER_END				= true;
	public final static int			CONFIG_KEY_DEFAULT_FILTER_MIN_FR_SLOT				= 0;
	public final static int			CONFIG_KEY_DEFAULT_TIMER_DURATION					= 25;
	public final static boolean		CONFIG_KEY_DEFAULT_NOTIFY_LED						= false;
	public final static boolean		CONFIG_KEY_DEFAULT_NOTIFY_VIBRA						= false;
	public final static boolean		CONFIG_KEY_DEFAULT_NOTIFY_SOUND						= false;
	public final static String		CONFIG_KEY_DEFAULT_NETWORK							= "velib";
	public final static int			CONFIG_KEY_DEFAULT_EXPRESS_BIKE_SEARCH_NEAREST		= 1;
	public final static int			CONFIG_KEY_DEFAULT_EXPRESS_SLOT_SEARCH_NEAREST		= 1;
	public final static String		CONFIG_KEY_DEFAULT_STARTUP_TAB						= "signet";
	public final static boolean		CONFIG_KEY_DEFAULT_SORT_FAVORITE_BY_COLORS			= false;

	public final static int			MAX_SEARCHED_UNITS									= 5;

	public final static int			SEARCH_TYPE_BIKES									= 0;
	public final static int			SEARCH_TYPE_SLOTS									= 1;
	public final static int			SEARCH_TYPE_BIKES_AND_SLOTS							= 2;
	public final static int			SEARCH_TYPE_NONE									= -1;

	// ---- STATIONS ----
	public final static String		S_LABEL_KEY											= "station_label";
	public final static String		S_FREE_SLOTS_KEY									= "station_free_slot";
	public final static String		S_AVAILABLE_BIKES_KEY								= "station_available_bikes";
	public final static String		S_ID_KEY											= "station_id";

	// ---- Keys for bundles ----
	public final static String		NEW_STATION_IN_BUNDLE_ID_KEY						= "new.station.id.key";
	public final static String		NEW_STATION_IN_BUNDLE_SEARCH_PATTERN_KEY			= "new.station.search.pattern.key";
	public final static String		NEW_STATION_IN_BUNDLE_SEARCH_ADDRESS_KEY			= "new.station.search.address.key";
	public final static String		DEL_STATION_IN_BUNDLE_ID_KEY						= "del.station.id.key";
	public final static String		LAT_IN_BUNDLE_ID_KEY								= "latitude.id.key";
	public final static String		LNG_IN_BUNDLE_ID_KEY								= "longitude.id.key";

	// ---- Return codes
	public final static int			RETURN_CODE_VALID									= 1;
	public final static int			RETURN_CODE_CANCEL									= 0;

	// ---- Activity & service codes
	public final static int			ACTIVITY_NEW_STATION_S1_START						= 27011977;
	public final static int			ACTIVITY_NEW_STATION_S2_DATABASE_START				= 29011977;
	public final static int			ACTIVITY_NEW_STATION_S2_GOOGLEMAPS_START			= 30011977;
	public final static int			ACTIVITY_GOOGLEMAPS_FROM_FAVORITE_START				= 31011977;
	public final static int			ACTIVITY_DEL_STATION_START							= 010177;
	public final static int			ACTIVITY_TIMER_SET_START							= 020177;
	public final static int			ACTIVITY_TIMER_RUN_START							= 030177;
	public final static int			ACTIVITY_SET_FILTER_START							= 040177;
	public final static int			ACTIVITY_NEAREST_STATIONS							= 050177;
	public final static int			SERVICE_TIMER										= 900986;
	public final static int			ACTIVITY_PREFERENCE									= 90057;

	// ------------------ Charset ----------------------
	public final static String		CHARSET_UTF8										= "UTF-8";
	public final static String		CHARSET_ISO_8859_1									= "ISO-8859-1";

	// ------------------ GUI constants ----------------

	// Width of the information on a station, in favortie page. This have to be
	// the sum of the width attributes in station_info.xml, for all the text
	// related to the av bikes and slts
	public final static int			GUI_WIDTH_INFO_STATION								= 119;

	// constant to display secondary menu on screen, used to reach advanced
	// functions
	public final static int			MENU_ADVANCED_MARGIN								= 6;
	public final static int			MENU_ADVANCED_HEIGHT								= 300;
	public final static int			MENU_ADVANCED_Y_OFFSET								= -280;
	public final static int			MENU_ADVANCED_X_OFFSET								= -3;
	public final static int			MENU_ADVANCED_Y_FINAL_OFFSET						= 10;
	public final static int			MENU_ADVANCED_EXPRESS_S_Y_FINAL_OFFSET				= 5;

	// Used by the handlers
	public final static int			HANDLER_VELOID_MSG_SHOW_ADV_MENU					= 1224553355;
	public final static int			HANDLER_VELOID_MSG_HIDE_ADV_MENU					= 1224553356;
	public final static int			HANDLER_VELOID_MSG_SHOW_EXPRESS_SEARCH_BIKE_MENU	= 1224574555;
	public final static int			HANDLER_VELOID_MSG_HIDE_EXPRESS_SEARCH_BIKE_MENU	= 1224593356;
	public final static int			HANDLER_VELOID_MSG_SHOW_EXPRESS_SEARCH_SLOT_MENU	= 1224553357;
	public final static int			HANDLER_VELOID_MSG_HIDE_EXPRESS_SEARCH_SLOT_MENU	= 1224553358;

	// Animation duration
	public final static int			MENU_ANIMATION_DURATION_MS							= 300;

	// type for the animation
	public final static int			MENU_ANIMATED_IS_ADVANCED_ACTIONS					= 0;
	public final static int			MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE				= 1;
	public final static int			MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT				= 2;

	// ID of the menus animated
	public final static int			MENU_ADVANCED_ACTION_ID								= 189091874;
	public final static int			MENU_EXPRESS_SEARCH_BIKE_ID							= 1890896474;
	public final static int			MENU_EXPRESS_SEARCH_SLOT_ID							= 1874589674;

	// ---- Databases ----
	// Database
	public final static String		DB_DATABASE_NAME									= "veloid.db";
	public final static int			DB_VERSION											= 2;

	// tables
	public final static String		DB_TABLE_STATIONS									= "stations";
	public final static String		DB_TABLE_PLACE										= "place";

	// fields
	public final static String		DB_FIELD_S_ID										= "id";
	public final static String		DB_FIELD_S_NAME										= "name";
	public final static String		DB_FIELD_S_ADDRESS									= "address";
	public final static String		DB_FIELD_S_FULL_ADRESS								= "full_address";
	public final static String		DB_FIELD_S_LATITUDE									= "latitude";
	public final static String		DB_FIELD_S_LONGITUDE								= "longitude";
	public final static String		DB_FIELD_S_OPEN										= "open";
	public final static String		DB_FIELD_S_COMMENT									= "comment";
	public final static String		DB_FIELD_S_SIGNET									= "signet";
	public final static String		DB_FIELD_S_NETWORK									= "network";
	public final static String		DB_FIELD_S_COLOR									= "color";

	// ---- Tags name, for the Velib API
	public final static String		TAG_SD_STATION										= "station";
	public final static String		TAG_SD_AVAILABLE									= "available";
	public final static String		TAG_SD_FREE											= "free";
	public final static String		TAG_SD_TOTAL										= "total";
	public final static String		TAG_SD_TICKET										= "ticket";

	// all station list
	public final static String		TAG_AS_CARTO										= "carto";
	public final static String		TAG_AS_MARKERS										= "markers";
	public final static String		TAG_AS_MARKER										= "marker";
	public final static String		ATTR_AS_NAME										= "name";
	public final static String		ATTR_AS_NUMBER										= "number";
	public final static String		ATTR_AS_ADDRESS										= "address";
	public final static String		ATTR_AS_FUL_ADDRESS									= "fullAddress";
	public final static String		ATTR_AS_LATITUDE									= "lat";
	public final static String		ATTR_AS_LONGITUDE									= "lng";
	public final static String		ATTR_AS_OPEN										= "open";

	// --- Tags used to identify the information from Spanish Clear Channel
	// systems
	public final static String		TAG_KML_START										= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml";
	public final static String		TAG_KML_STOP										= "</kml>";
	public final static String		TAG_CC_DESCRIPTION									= "description";
	// --- Tags used by Eiffage
	public final static String		TAG_EIFFAGE_AS_MARKERS								= "markers";
	public final static String		TAG_EIFFAGE_AS_MARKER								= "marker";
	public final static String		ATTR_EIFFAGE_AS_ID									= "id";
	public final static String		ATTR_EIFFAGE_AS_LAT									= "lat";
	public final static String		ATTR_EIFFAGE_AS_LONG								= "lng";
	public final static String		ATTR_EIFFAGE_AS_NAME								= "name";

	public final static String		TAG_EIFFAGE_SD_STATION								= "station";
	public final static String		TAG_EIFFAGE_SD_STATUS								= "status";
	public final static String		TAG_EIFFAGE_SD_BIKE									= "bikes";
	public final static String		TAG_EIFFAGE_SD_ATTACH								= "attachs";

	public final static String		TAG_CICLOCITY_SD_INFO								= "Infos";
	public final static String		TAG_CICLOCITY_SD_ID									= "id";
	public final static String		TAG_CICLOCITY_SD_LABEL								= "label";
	public final static String		TAG_CICLOCITY_SD_STATE								= "state";
	public final static String		TAG_CICLOCITY_SD_TOTAL								= "totalBikeBase";
	public final static String		TAG_CICLOCITY_SD_BIKE								= "availableBike";
	public final static String		TAG_CICLOCITY_SD_SLOT								= "freeBikeBase";

	// --- Log ID
	public static final String		LOG_NS2_MAP											= "NS2MAP";

	// --- Tags used to identify the information from NextCity Germany systems
	public final static String		TAG_NEXTCITY_MARKERS								= "markers";
	public final static String		TAG_NEXTCITY_COUNTRY								= "country";
	public final static String		TAG_NEXTCITY_CITY									= "city";
	public final static String		TAG_NEXTCITY_STATION								= "place";
	public final static String		ATTR_NEXTCITY_ID									= "uid";
	public final static String		ATTR_NEXTCITY_NAME									= "name";
	public final static String		ATTR_NEXTCITY_LAT									= "lat";
	public final static String		ATTR_NEXTCITY_LONG									= "lng";
	public final static String		ATTR_NEXTCITY_BIKES									= "bikes";
	public final static String		ATTR_NEXTCITY_SPOTS									= "spot";

	// --- mapping information
	public static final String		APPID												= "A0x9J0fV34H6uXGoEosE9.w1dvwLLLZ6jvjkr5WaDimUY7TC4js6zltojSCqDj_1lJNO_A--";
	// public static final String YAHOO_GEO_API_URL =
	// "http://local.yahooapis.com/MapsService/V1/geocode?appid=";
	public static final GeoPoint	MAP_INITIAL_CENTER									= new GeoPoint((int) 48.856578 * 1000000, (int) 2.351828 * 1000000);
	public final static String		MAP_FIND_GEOLOC_KEY									= "map.geoloc.center";
	public final static String		MAP_LOCATE_FAVORITE_KEY								= "map.loc.favorite";

	public final static String		MAP_FAVORITE_LAT_KEY								= "map.loc.favorite.latitude";
	public final static String		MAP_FAVORITE_LON_KEY								= "map.loc.favorite.longitude";
	public final static String		MAP_FAVORITE_NAME_KEY								= "map.loc.favorite.name";

	public final static int			MAP_FIND_CENTER_FROM_ADDR							= 1;
	public final static int			MAP_FIND_CENTER_FROM_GEOLOC							= 2;
	public final static int			MAP_FIND_CENTER_FROM_FAVORITE						= 3;

	public final static String		MAP_FIND_GEOLOC_AFTER_WARNING_KEY					= "map.geoloc.center.after.alarm";

	// --- filter information
	public static final String		FILTER_MIN_FREE_SLOTS								= "filter.min.free.slot";
	public static final String		FILTER_MIN_AVAILABLE_BIKES							= "filter.min.av.bike";
	public static final String		FILTER_MAX_STATIONS									= "filter.max.station";
	// public static final String FILTER_UPDATED_STATIONS =
	// "filter.updated.station";

	// --- Error code
	public static final int			ERR_PARSING											= -1;
	public static final int			ERR_CONNECT											= -2;
	public static final int			ERR_UNAVAILABLE_SITE								= -3;

	// --- Color theme
	public static final int			COLOR_THEME_ITEM_EVEN								= 1;
	public static final int			COLOR_THEME_ITEM_ODD								= 2;
	public static final int			COLOR_THEME_ITEM_ALPHA								= 3;

	// --- map displayed information
	public static final int			MAP_INFO_STATION_MIN_WIDTH							= 100;
	public static final int			MAP_INFO_STATION_HEIGHT								= 47;
	public static final int			MAP_INFO_STATION_OFFSET_Y							= 45;																			// should be the height of the
	// displayed image
	public static final int			MAP_INFO_STATION_OFFSET_X							= 30;

	public static final int			MAP_TOOLBAR_EXPECTED_HEIGHT							= 98;
	public static final int			MAP_TOOLBAR_OFFSET_Y								= 100;

	public static final int			MAP_FILTER_INFO_EXPECTED_HEIGHT						= 15;
	public static final int			MAP_FILTER_INFO_OFFSET_Y							= 0;

	// --- Timer information
	public static final String		TIMER_MINUTE_BUNDLE_KEY								= "timer.value";
	public static final String		TIMER_GELOCALIZE_OPT_BUNDLE_KEY						= "geolocalisation.required";
	public static final String		TITLE_FONT											= "font/Clubland.ttf";

	public static final int			NOTIFY_LED_OFF_DURATION								= 100;
	public static final int			NOTIFY_LED_ON_DURATION								= 300;
	public static final long[]		NOTIFY_VIBRA_PATTERN								= { 300, 100 };
	public static final String		PACKAGE_NAME										= "com.xirgonium.android.veloid";
	public static final String		NOTIFY_SOUND_PATH_URI								= "android.resource://com.xirgonium.android/" + R.raw.beep;					// "android.resource://"
	public static final String		CALLED_FROM_NOTIFICATION							= "called.from.notification";
	// +
	// PACKAGE_NAME
	// +
	// "/"
	// +
	// R.raw.beep;
	public static String			DEFAULT_SOUND_PATH									= "sound/beep.wav";

	// public static final GradientDrawable getGradient(Activity act) {
	// GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new
	// int[] { act.getResources().getColor(R.color.list_top_color),
	// act.getResources().getColor(R.color.list_bottom_color) });
	// return grad;
	// }
}
