package com.xirgonium.android.veloid;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AbsoluteLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView.ReticleDrawMode;
import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.location.WhereAmI;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.map.FilterInformationView;
import com.xirgonium.android.map.ToolbarActionView;
import com.xirgonium.android.map.VeloidMapCenter;
import com.xirgonium.android.map.VeloidMapView;
import com.xirgonium.android.map.VeloidStationsOverlay;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.util.MapUtility;
import com.xirgonium.android.util.StationFilter;
import com.xirgonium.exception.NoInternetConnection;

/**
 * This activity display a map with the stations near a map point. The map point
 * is determined when the activity is started with the information contained in
 * the bundle given as parameter.
 * <ul>
 * <li>From an address</li>
 * <li>From the current position</li>
 * </ul>
 * 
 * @author Cyril Gervais
 * 
 */
public class StationNearActivity extends MapActivity implements OnClickListener, Runnable {

	boolean					foundDirections						= false;
	Vector<Station>			nearestStations						= null;
	Vector<Station>			nearestFullStations					= null;
	VeloidMapView			mapViewFromXML						= null;
	StationNearActivity		thisInstance						= null;
	VeloidMapCenter			mapCenter							= null;
	VeloidStationsOverlay	veloidMapOverlay					= null;
	MyLocationOverlay		locOverlay							= null;
	// public OverlayController myOC = null;
	// ImageView compass;

	CommonStationManager	mgr									= null;
	StationFilter			filter								= null;

	ProgressDialog			pgd									= null;
	Dialog					noAddrDlg							= null;
	Dialog					unableToLocateDlg					= null;
	Dialog					severalAddresses					= null;
	Dialog					noInternetDlg						= null;
	DisplayMetrics			dm									= new DisplayMetrics();

	MapController			mc									= null;

	SensorManager			sensorManager;
	float					totalForcePrev;

	String					address								= null;

	 WhereAmI gpsManager;
	
	
	
	int						screenHeight;
	int						screenWidth;
	/*float					compassOrientation					= -9999;
	final static int		COMPASS_WIDTH						= 50;
	int						compassX							= 0;
	int						compassY							= 10;
	double					hReference							= COMPASS_WIDTH / 2;*/

	int						findCenterType						= 0;

	private final int		UI_PROGRESS_DISMISS					= 0;
	private final int		UI_ERROR_UNABLE_TO_FIND_ADDR_SHOW	= 1;
	private final int		UI_ERROR_SEVERAL_ADDR_SHOW			= 2;
	private final int		UI_ERROR_UNABLE_TO_LOCATE_SHOW		= 3;

	private final int		UI_ERROR_NO_INTERNET				= 4;

	private Handler			handler								= new Handler() {

																	@Override
																	public void handleMessage(
																			Message msg) {
																		switch (msg.what) {
																			case UI_PROGRESS_DISMISS:
																				pgd.dismiss();
																				break;

																			case UI_ERROR_UNABLE_TO_FIND_ADDR_SHOW:
																				noAddrDlg.show();
																				break;

																			case UI_ERROR_SEVERAL_ADDR_SHOW:
																				severalAddresses
																						.show();
																				break;
																			case UI_ERROR_UNABLE_TO_LOCATE_SHOW:
																				unableToLocateDlg
																						.show();
																				break;

																			case UI_ERROR_NO_INTERNET:
																				noInternetDlg
																						.show();
																				break;

																			default:
																				break;
																		}

																	}
																};

	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		thisInstance = this;
		filter = null;
		// --- Progress dialog
		pgd = ProgressDialog.show(this, null,
				getString(R.string.nearest_station_progress_dialog_wait));

		// --- Error dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.nearest_station_gm__popup_no_address_found_title);
		builder.setIcon(R.drawable.warning);
		builder.setMessage(R.string.nearest_station_gm__popup_no_address_found_label);
		android.content.DialogInterface.OnClickListener dlglsnr = new android.content.DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				thisInstance.finish();
			}
		};
		builder.setPositiveButton(R.string.dialog_btn_ok, dlglsnr);
		noAddrDlg = builder.create();

		AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
		builder2.setTitle(R.string.nearest_station_gm__popup_no_address_found_title);
		builder2.setIcon(R.drawable.warning);
		builder2.setMessage(R.string.nearest_station_location_providers_broken);
		android.content.DialogInterface.OnClickListener dlglsnr2 = new android.content.DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				thisInstance.finish();
			}
		};
		builder2.setPositiveButton(R.string.dialog_btn_ok, dlglsnr2);
		unableToLocateDlg = builder2.create();

		noInternetDlg = new AlertDialog.Builder(this).setMessage(
				this.getString(R.string.error_no_internet_connection)).setPositiveButton(
				R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						thisInstance.finish();
					}
				}).setIcon(R.drawable.warning).create();

		// Hide the title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Log.d("MAP", "start");
		setContentView(R.layout.map_stations_near_main);
		ViewGroup main = (ViewGroup) findViewById(R.id.my_map_main_frame);

		// Information on screen
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels;
		screenWidth = dm.widthPixels;

		// center the map on the address requested
		mapViewFromXML = (VeloidMapView) this.findViewById(R.id.my_map);
		LayoutParams paramMap = new AbsoluteLayout.LayoutParams(screenWidth, screenHeight
				- Constant.MAP_TOOLBAR_EXPECTED_HEIGHT, 0, 0);
		mapViewFromXML.setLayoutParams(paramMap);
		mapViewFromXML.setReticleDrawMode(ReticleDrawMode.DRAW_RETICLE_UNDER);

		// add a long click listener
		// mapViewFromXML.setLongClickable(true);
		// mapViewFromXML.setOnLongClickListener(new
		// MapOnLongClickListener(mapViewFromXML));

		mc = mapViewFromXML.getController();
		mc.animateTo(Constant.MAP_INITIAL_CENTER);

		LinearLayout zoomView = (LinearLayout) mapViewFromXML.getZoomControls();
		zoomView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		zoomView.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.BOTTOM);
		mapViewFromXML.addView(zoomView);
		mapViewFromXML.setClickable(true);
		mapViewFromXML.setEnabled(true);

		// --- get the data from the bundle and build objects
		Bundle b = this.getIntent().getExtras();

		// Get the way to center the map
		if (b != null)
			address = b.getString(Constant.NEW_STATION_IN_BUNDLE_SEARCH_ADDRESS_KEY);
		if (address != null) {

			findCenterType = Constant.MAP_FIND_CENTER_FROM_ADDR;

			// ---- check the entered address
			// Log.d("MAP", "geocode");
			try {
				final List<Address> proposedAddress = MapUtility.getAddressList(address, this);
				// mapCenter = MapUtility.getGeoCode(address, this);
				switch (proposedAddress.size()) {
					case 0:
						// --- No address found
						handler.sendEmptyMessage(UI_PROGRESS_DISMISS);
						handler.sendEmptyMessage(UI_ERROR_UNABLE_TO_FIND_ADDR_SHOW);
						break;

					case 1:
						int lat = (int) (proposedAddress.get(0).getLatitude() * 1000000);
						int lon = (int) (proposedAddress.get(0).getLongitude() * 1000000);
						mapCenter = new VeloidMapCenter(lat, lon);
						break;

					default:
						// --- several address
						String[] addressesAsStr = new String[proposedAddress.size()];
						int i = 0;
						for (Iterator<Address> iterator = proposedAddress.iterator(); iterator
								.hasNext();) {
							Address address = (Address) iterator.next();
							addressesAsStr[i++] = FormatUtility.formatAdressForList(address);
						}

						severalAddresses = new AlertDialog.Builder(StationNearActivity.this)
								.setTitle(R.string.nearest_station_warning_dialog_several_addresses)
								.setItems(addressesAsStr, new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {

										int lat = (int) (proposedAddress.get(which).getLatitude() * 1000000);
										int lon = (int) (proposedAddress.get(which).getLongitude() * 1000000);
										mapCenter = new VeloidMapCenter(lat, lon);

										nearestFullStations = findStations(mapCenter, filter);
										veloidMapOverlay.setStationToDisplay(nearestFullStations);

										dialog.dismiss();
									}
								}).create();

						handler.sendEmptyMessage(UI_PROGRESS_DISMISS);
						handler.sendEmptyMessage(UI_ERROR_SEVERAL_ADDR_SHOW);

						break;
				}
			} catch (IOException ioe) {
				Log.e("MAP", ioe.getMessage());
			}

		} else if (b.get(Constant.MAP_FIND_GEOLOC_KEY) != null) {
			findCenterType = Constant.MAP_FIND_CENTER_FROM_GEOLOC;
		} else if (b.get(Constant.MAP_LOCATE_FAVORITE_KEY) != null) {
			findCenterType = Constant.MAP_FIND_CENTER_FROM_FAVORITE;
		}

		int minFreeSlotFilter = b.getInt(Constant.FILTER_MIN_FREE_SLOTS);
		int minAvBikesFilter = b.getInt(Constant.FILTER_MIN_AVAILABLE_BIKES);
		if (minFreeSlotFilter > 0 || minAvBikesFilter > 0) {
			filter = new StationFilter();
			if (minFreeSlotFilter != 0) {
				filter.set(Constant.FILTER_MIN_FREE_SLOTS, minFreeSlotFilter);
			}
			if (minAvBikesFilter != 0) {
				filter.set(Constant.FILTER_MIN_AVAILABLE_BIKES, minAvBikesFilter);
			}

			// display the filter on the map
			LayoutInflater vi = getLayoutInflater();
			FilterInformationView filterInfo = (FilterInformationView) vi.inflate(
					R.layout.map_filter_info, null);
			filterInfo.setFilter(filter);

			LayoutParams absLayoutparams = new LayoutParams(screenWidth, 25, 0, 0);
			main.addView(filterInfo, absLayoutparams);
		}

		// --- Create the bar button
		LayoutInflater vi = getLayoutInflater();
		ToolbarActionView bar = (ToolbarActionView) vi.inflate(R.layout.map_toolbar_action, null);

		int expectedHeight = Constant.MAP_TOOLBAR_EXPECTED_HEIGHT;
		int offsety = Constant.MAP_TOOLBAR_OFFSET_Y;

		LayoutParams absLayoutparams = new LayoutParams(screenWidth, expectedHeight, 0,
				screenHeight - offsety);

		main.addView(bar, absLayoutparams);

		// Listen if shaked

		// Before calling any of the Simulator data,
		// the Content resolver has to be set !!
		// Hardware.mContentResolver = getContentResolver();

		// BEGIN DELETE
		// sensorManager = (SensorManager) new
		// SensorManagerSimulator((SensorManager)
		// getSystemService(SENSOR_SERVICE));
		// Intent intent = new Intent(Intent.ACTION_VIEW,
		// Hardware.Preferences.CONTENT_URI);
		// startActivity(intent);
		// sensorManager.unregisterListener(this);
		// SensorManagerSimulator.connectSimulator();
		// --- END DELETE
		// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// sensorManager.registerListener(this,
		// SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_GAME);

		// ----- Behaviour of the buttons in the bar
		ImageButton refreshMap = (ImageButton) bar.findViewById(R.id.toolbar_btn_refresh_current);
		LinearLayout resfreshMapLayout = (LinearLayout) bar
				.findViewById(R.id.map_toolbar_refresh_label);
		refreshMap.setOnClickListener(this);
		resfreshMapLayout.setOnClickListener(this);

		ImageButton recenterAndRefreshMap = (ImageButton) bar
				.findViewById(R.id.toolbar_btn_center_of_map);
		LinearLayout recenterMapLayout = (LinearLayout) bar
				.findViewById(R.id.map_toolbar_recenter_label);
		recenterAndRefreshMap.setOnClickListener(this);
		recenterMapLayout.setOnClickListener(this);

		ImageButton geoloc = (ImageButton) bar.findViewById(R.id.toolbar_btn_geolocalization);
		LinearLayout geolocMapLayout = (LinearLayout) bar
				.findViewById(R.id.map_toolbar_btn_geoloc_label);
		geoloc.setOnClickListener(this);
		geolocMapLayout.setOnClickListener(this);

		Thread threadOfMap = new Thread(this);
		threadOfMap.start();

	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// sensorManager.registerListener(this, SensorManager.SENSOR_ORIENTATION |
	// SensorManager.SENSOR_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_NORMAL);
	// }
	//
	// @Override
	// protected void onStop() {
	// sensorManager.unregisterListener(this);
	// super.onStop();
	// }

	public void onClick(View clicked) {
		switch (clicked.getId()) {
			case R.id.toolbar_btn_center_of_map:
				mapCenter = new VeloidMapCenter(mapViewFromXML.getMapCenter().getLatitudeE6(), mapViewFromXML.getMapCenter().getLongitudeE6());
				nearestFullStations = findStations(mapCenter, filter);
				veloidMapOverlay.setStationToDisplay(nearestFullStations);
				break;
			case R.id.map_toolbar_recenter_label:
				mapCenter = new VeloidMapCenter(mapViewFromXML.getMapCenter().getLatitudeE6(), mapViewFromXML.getMapCenter().getLongitudeE6());
				nearestFullStations = findStations(mapCenter, filter);
				veloidMapOverlay.setStationToDisplay(nearestFullStations);
				break;

			case R.id.toolbar_btn_refresh_current:
				refreshNearestStationInfo(filter);
				veloidMapOverlay.setStationToDisplay(nearestFullStations);
				break;
			case R.id.map_toolbar_refresh_label:
				refreshNearestStationInfo(filter);
				veloidMapOverlay.setStationToDisplay(nearestFullStations);
				break;

			case R.id.toolbar_btn_geolocalization:

				// geolocalize
				findCenterType = Constant.MAP_FIND_CENTER_FROM_GEOLOC;
				setMapCenter();

				nearestFullStations = findStations(mapCenter, filter);
				veloidMapOverlay.setStationToDisplay(nearestFullStations);

				break;
			case R.id.map_toolbar_btn_geoloc_label:
				// geolocalize
				findCenterType = Constant.MAP_FIND_CENTER_FROM_GEOLOC;
				setMapCenter();
				nearestFullStations = findStations(mapCenter, filter);
				veloidMapOverlay.setStationToDisplay(nearestFullStations);
				break;
			default:
				break;
		}
	}

	public Vector<Station> findStations(GeoPoint center, StationFilter filter) {

		// Log.d("MAP", "restore minimum");
		Vector<Station> stations = mgr.restoreAllStationWithminimumInfoFromDataBase();

		// Log.d("MAP", "find nearest");
		if (filter != null) {
			nearestStations = MapUtility.getNearestStations(mapCenter, ConfigurationContext
					.getMaxStationFiltered(), stations);
		} else {
			nearestStations = MapUtility.getNearestStations(mapCenter, ConfigurationContext
					.getMaxStationReturned(), stations);
		}
		return refreshNearestStationInfo(filter);
	}

	public Vector<Station> refreshNearestStationInfo(StationFilter filter) {
		try {
			if (filter != null) {

				Vector<Station> filteredStations = filter.filterStationsWithRealTimeChecking(
						nearestStations, mgr);

				/*
				 * Vector<Station> tobefiltered =
				 * mgr.fillInformationFromWeb(nearestStations); Vector<Station>
				 * filteredStations = filter.filterStations(tobefiltered);
				 */
				nearestFullStations = mgr.fillInformationFromDB(filteredStations);
			} else {
				nearestFullStations = mgr.fillInformationFromDBAndWeb(nearestStations);
			}
		} catch (NoInternetConnection e) {

			handler.sendEmptyMessage(UI_PROGRESS_DISMISS);
			handler.sendEmptyMessage(UI_ERROR_NO_INTERNET);
		}

		return nearestFullStations;
	}

	public void run() {
		Looper.prepare();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// --- Fill the map
		try {
			setMapCenter();

			// --- Find the nearest stations from the center of the mark
			mgr = ConfigurationContext.getCurrentStationManager(this);

			nearestFullStations = findStations(mapCenter, filter);

			// Log.d("MAP", "add overlay");
			veloidMapOverlay = new VeloidStationsOverlay(this, nearestFullStations);
			mapViewFromXML.getOverlays().add(veloidMapOverlay);
			mapViewFromXML.postInvalidate();

		} catch (Exception e) {
			Log.e(Constant.LOG_NS2_MAP, e.toString(), e);
		} finally {
			pgd.dismiss();
		}
	}

	/**
	 * Define the map center regarding the find center type attribut. Values are
	 * for the address center (need geocoding) and for the geolocalization.
	 * 
	 */
	private void setMapCenter() {

		switch (findCenterType) {
			case Constant.MAP_FIND_CENTER_FROM_ADDR:
				// --- map center already defined
				break;

			case Constant.MAP_FIND_CENTER_FROM_GEOLOC:
				mapCenter = MapUtility.geoLocalize(this);
				if (mapCenter == null) {
					handler.sendEmptyMessage(UI_PROGRESS_DISMISS);
					handler.sendEmptyMessage(UI_ERROR_UNABLE_TO_LOCATE_SHOW);
				}
				break;

			case Constant.MAP_FIND_CENTER_FROM_FAVORITE:
				Bundle b = this.getIntent().getExtras();
				int lat = (int) (b.getDouble(Constant.MAP_FAVORITE_LAT_KEY) * 1000000);
				int lon = (int) (b.getDouble(Constant.MAP_FAVORITE_LON_KEY) * 1000000);

				mapCenter = new VeloidMapCenter(lat, lon);
				break;
		}
	}

	public void addSignet(int stationid) {
		Toast.makeText(this, R.string.nearest_station_popup_favorite_added, Toast.LENGTH_SHORT)
				.show();
		Station station = new Station();
		station.setId(String.valueOf(stationid));
		station.setNetwork(mgr.getNetworkId());
		mgr.setStationAsSignet(station);
	}

	public VeloidMapCenter getMapCenter() {
		return mapCenter;
	}

	public void setMapCenter(VeloidMapCenter mapCenter) {
		this.mapCenter = mapCenter;
	}

	public MapView getMapView() {
		return mapViewFromXML;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onStart() {
		gpsManager = ConfigurationContext.getWhereAmIInstance(this);
		gpsManager.resumeUpdate();
		super.onStart();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		//Log.d("Veloid", "----------- ON STOP ");
		gpsManager.pauseUpdates();
		
	}

	// public void onAccuracyChanged(int arg0, int arg1) {
	// }
	//
	// public void onSensorChanged(int sensor, float[] values) {
	// /*
	// * if (sensor == SensorManager.SENSOR_ACCELEROMETER) { double
	// forceThreshHold = 1.5f;
	// *
	// * float totalForce = 0.0f; totalForce +=
	// Math.pow(values[SensorManager.DATA_X] / SensorManager.GRAVITY_EARTH,
	// 2.0); totalForce += Math.pow(values[SensorManager.DATA_Y] /
	// * SensorManager.GRAVITY_EARTH, 2.0); totalForce +=
	// Math.pow(values[SensorManager.DATA_Z] / SensorManager.GRAVITY_EARTH,
	// 2.0); totalForce = (float) Math.sqrt(totalForce);
	// *
	// * if ((totalForce < forceThreshHold) && (totalForcePrev >
	// forceThreshHold)) { displayCompass(); }
	// *
	// * totalForcePrev = totalForce; } else
	// */
	// if (sensor == SensorManager.SENSOR_ORIENTATION) {
	// float x = values[SensorManager.DATA_X];
	// compassOrientation = x;
	// displayCompass();
	//
	// }
	// }
	//
	// public BitmapDrawable getOrientedCompassImage() {
	//
	// Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
	// R.drawable.compass);
	//
	// int width = bitmapOrg.getWidth();
	// int height = bitmapOrg.getHeight();
	//
	// // Log.d("STATION NEAR.setOrientation", "height = " + height +
	// " | width = " + width);
	//
	// Matrix matrix = new Matrix();
	//
	// matrix.postRotate(compassOrientation);

	// if (compassOrientation != -9999) {
	// double newH = COMPASS_WIDTH / 2 *
	// Math.tan(Math.toRadians(compassOrientation));
	// double scaleRatio = newH/hReference;
	//
	// double newWidth = Math.abs(width * scaleRatio);
	//
	// Log.d("dededede", String.valueOf(newWidth) + "/" +
	// String.valueOf(compassOrientation));
	//
	// if ((int) newWidth != 0 && newWidth < 300) {
	// width = (int) newWidth;
	// height = (int) newWidth;
	// }
	// }
	// Log.d("STATION NEAR.setOrientation", "height = " + height + " | width = "
	// + width);

	// recreate the new Bitmap
	// Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
	// height, matrix, true);
	// BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
	//
	// return bmd;
	// }
	//
	// private void displayCompass() {
	// ViewGroup main = (ViewGroup) findViewById(R.id.my_map_main_frame);
	//
	// main.removeView(compass);
	// compass = new ImageView(this);
	// compass.setBackgroundDrawable(getOrientedCompassImage());
	//
	// compassX = screenWidth - COMPASS_WIDTH;
	// compassY = 25;
	//
	// LayoutParams absCompassLayoutparams = new LayoutParams(COMPASS_WIDTH,
	// COMPASS_WIDTH, compassX, compassY);
	// main.addView(compass, absCompassLayoutparams);
	// }

}