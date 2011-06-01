package com.xirgonium.android.veloid.veloid2.map;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.location.WhereAmI;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.map.VeloidMapCenter;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.util.MapUtility;
import com.xirgonium.android.util.StationFilter;
import com.xirgonium.android.veloid.R;
import com.xirgonium.exception.NoInternetConnection;

public class VeloidMap extends MapActivity {

	//MyLocationOverlay		myLoc;

	MapController			mapController;
	Vector<Station>			nearestFullStations					= null;
	VeloidMapCenter			mapCenter							= null;
	VeloidStationsOverlay	veloidMapOverlay					= null;
	StationFilter			filter								= null;
	CommonStationManager	mgr									= null;
	WhereAmI				where								= null;
	MapView					map;
	VeloidMap				thisInstance						= null;
	boolean					displayNothing						= false;
//	boolean					zoomToSpan							= true;

	ProgressDialog			pgd									= null;
	Dialog					noAddrDlg							= null;
	Dialog					unableToLocateDlg					= null;
	Dialog					severalAddresses					= null;
	Dialog					noInternetDlg						= null;

	private final int		OPEN_FOR_ADDRESS					= 0;
	private final int		OPEN_FOR_GEOLOC						= 1;
	private final int		OPEN_FROM_LAT_LON					= 2;
	private int				openMapFor							= -1;

	private final int		UI_PROGRESS_DISMISS_AND_NO_GEOLOC	= 0;
	private final int		UI_PROGRESS_DISMISS_AND_GEOLOC		= 10;
	private final int		UI_ERROR_UNABLE_TO_FIND_ADDR_SHOW	= 1;
	private final int		UI_ERROR_SEVERAL_ADDR_SHOW			= 2;
	private final int		UI_ERROR_UNABLE_TO_LOCATE_SHOW		= 3;
	private final int		UI_ERROR_NO_INTERNET				= 4;

	private Menu			mMenu;

	private Handler			handler								= new Handler() {

																	@Override
																	public void handleMessage(Message msg) {
																		switch (msg.what) {
																		case UI_PROGRESS_DISMISS_AND_NO_GEOLOC:
																			pgd.dismiss();
																			setUpMapOverlays(true);
																			break;

																		case UI_PROGRESS_DISMISS_AND_GEOLOC:
																			pgd.dismiss();
																			setUpMapOverlays(true);
																			break;

																		case UI_ERROR_UNABLE_TO_FIND_ADDR_SHOW:
																			noAddrDlg.show();
																			break;

																		case UI_ERROR_SEVERAL_ADDR_SHOW:
																			severalAddresses.show();
																			break;
																		case UI_ERROR_UNABLE_TO_LOCATE_SHOW:
																			unableToLocateDlg.show();
																			break;

																		case UI_ERROR_NO_INTERNET:
																			noInternetDlg.show();
																			break;

																		default:
																			break;
																		}

																	}
																};

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// -- Set the view
		setContentView(R.layout.amap);

		map = (MapView) findViewById(R.id.mapview);
		mapController = map.getController();
		map.setOnTouchListener(new MapTouchListener());

		map.setBuiltInZoomControls(true);

		registerForContextMenu(map);
	}

	@Override
	protected void onResume() {
		mgr = ConfigurationContext.getCurrentStationManager(this);
//		zoomToSpan = true;
		thisInstance = this;
		initDialogs();

		runOnFirstTime();

		super.onResume();
		// Log.d(this.getLocalClassName(), " ---- ON RESUME CALLED -----");
		//		if (myLoc != null)
		//			myLoc.enableCompass();

		ConfigurationContext.getWhereAmIInstance(this).resumeUpdate();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Log.d(this.getLocalClassName(), " ---- ON PAUSE CALLED -----");

		if (where != null) {
			where.pauseUpdates();
			where.unregisterMap();
		}
		//		if (myLoc != null)
		//			myLoc.disableCompass();

		ConfigurationContext.getWhereAmIInstance(this).pauseUpdates();
		ConfigurationContext.setUsedStation(null);
	}

	public void runOnFirstTime() {
		determineReasonForOpeningMap();

		setUpFilter();

		findCenterAndStationsAsThread();
	}

	private void findCenterAndStationsAsThread() {
		pgd = ProgressDialog.show(this, null, getString(R.string.nearest_station_progress_dialog_wait), true, true);

		Thread t = new Thread() {
			public void run() {
				Looper.prepare();
				setMapCenter();
				findStationsToDisplay(mapCenter);
				handler.sendEmptyMessage(UI_PROGRESS_DISMISS_AND_NO_GEOLOC);
			}
		};

		t.start();
	}

	public synchronized void updateStationOnMapBackground() {
		if (openMapFor == OPEN_FOR_GEOLOC) {
			setUpMyCurrentPosition();

			//			Vector<Station> prev = (Vector<Station>) nearestFullStations.clone();

			Thread t = new Thread() {
				public void run() {
					Looper.prepare();
					findStationsToDisplay(mapCenter);
				}
			};

			t.start();

			setUpMapOverlays(true);

			//setUpMapOverlays(needToUpdateScreen(prev));
		}
	}

	private void findStationsAsThread(final GeoPoint center, final boolean forceNoGeoloc) {
		pgd = ProgressDialog.show(this, null, getString(R.string.nearest_station_progress_dialog_wait), true, true);

		Thread t = new Thread() {
			public void run() {
				Looper.prepare();
				findStationsToDisplay(center);
				handler.sendEmptyMessage(forceNoGeoloc ? UI_PROGRESS_DISMISS_AND_NO_GEOLOC : UI_PROGRESS_DISMISS_AND_GEOLOC);
			}
		};

		t.start();

	}

	private void determineReasonForOpeningMap() {
		Bundle b = this.getIntent().getExtras();
		if (b != null && b.getString(Constant.NEW_STATION_IN_BUNDLE_SEARCH_ADDRESS_KEY) != null) {
			openMapFor = OPEN_FOR_ADDRESS;
		} else if (ConfigurationContext.getUsedStation() != null) {
			openMapFor = OPEN_FROM_LAT_LON;
		} else {
			openMapFor = OPEN_FOR_GEOLOC;
			where = ConfigurationContext.getWhereAmIInstance(this);
			where.registerMap(this);
//			zoomToSpan = false;
			where.resumeUpdate();
		}
	}

	private void setUpMapOverlays(boolean displayCenter) {
		List<Overlay> list = map.getOverlays();

		//if (displayCenter) {
		list.clear();
		//		
		//		myLoc = new MyLocationOverlay(this, map);
		//		myLoc.enableCompass();
		//		list.add(myLoc);

		if (mapCenter != null) {
			veloidMapOverlay = new VeloidStationsOverlay(this, nearestFullStations, true);
			list.add(veloidMapOverlay);
		}
		//		} else {
		//			int i = 0;
		//			int toremove = -1;
		//			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
		//				Overlay overlay = (Overlay) iterator.next();
		//				if (overlay instanceof VeloidCenterOverlay) {
		//					toremove = i;
		//				}
		//				i++;
		//			}
		//			if (toremove >= 0) {
		//				map.getOverlays().remove(toremove);
		//			}
		//			map.getOverlays().add(new VeloidCenterOverlay(thisInstance));
		//		}

		map.postInvalidate();
	}

	private void setMapCenter() {
		Bundle b = this.getIntent().getExtras();
		switch (openMapFor) {
		case OPEN_FOR_ADDRESS:
			String address = b.getString(Constant.NEW_STATION_IN_BUNDLE_SEARCH_ADDRESS_KEY);
			geolocateAddress(address);
			break;
		case OPEN_FOR_GEOLOC:
			setUpMyCurrentPosition();
			break;
		case OPEN_FROM_LAT_LON:
			mapCenter = new VeloidMapCenter(ConfigurationContext.getUsedStation().getMicroDegreeLatitude(), ConfigurationContext.getUsedStation().getMicroDegreeLongitude());
			break;
		}
	}

	private void setUpFilter() {
		filter = new StationFilter();
		// Bundle b = this.getIntent().getExtras();
		// if (b != null) {
		// int minFreeSlotFilter = b.getInt(Constant.FILTER_MIN_FREE_SLOTS);
		// int minAvBikesFilter = b.getInt(Constant.FILTER_MIN_AVAILABLE_BIKES);
		// if (minFreeSlotFilter != 0) {
		// filter.set(Constant.FILTER_MIN_FREE_SLOTS, minFreeSlotFilter);
		// }
		// if (minAvBikesFilter != 0) {
		// filter.set(Constant.FILTER_MIN_AVAILABLE_BIKES, minAvBikesFilter);
		// }
		// }

		int searchedUnits = ConfigurationContext.getLastSearchedUnits();

		switch (ConfigurationContext.getLastSearchedType()) {
		case Constant.SEARCH_TYPE_BIKES:
			filter.set(Constant.FILTER_MIN_AVAILABLE_BIKES, searchedUnits);
			break;
		case Constant.SEARCH_TYPE_SLOTS:
			filter.set(Constant.FILTER_MIN_FREE_SLOTS, searchedUnits);
			break;
		case Constant.SEARCH_TYPE_BIKES_AND_SLOTS:
			filter.set(Constant.FILTER_MIN_AVAILABLE_BIKES, searchedUnits);
			filter.set(Constant.FILTER_MIN_FREE_SLOTS, searchedUnits);
			break;
		default:
			break;
		}
	}

	void geolocateAddress(String address) {
		try {
			final List<Address> proposedAddress = MapUtility.getAddressList(address, this);
			// mapCenter = MapUtility.getGeoCode(address, this);
			switch (proposedAddress.size()) {
			case 0:
				// --- No address found
				// handler.sendEmptyMessage(UI_PROGRESS_DISMISS);
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
				for (Iterator<Address> iterator = proposedAddress.iterator(); iterator.hasNext();) {
					Address oneAddress = (Address) iterator.next();
					addressesAsStr[i++] = FormatUtility.formatAdressForList(oneAddress);
				}

				severalAddresses = new AlertDialog.Builder(this).setTitle(R.string.nearest_station_warning_dialog_several_addresses).setItems(addressesAsStr, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						int lat = (int) (proposedAddress.get(which).getLatitude() * 1000000);
						int lon = (int) (proposedAddress.get(which).getLongitude() * 1000000);
						mapCenter = new VeloidMapCenter(lat, lon);

						findStationsAsThread(mapCenter, true);
						// veloidMapOverlay.setStationToDisplay(nearestFullStations);

						dialog.dismiss();
					}
				}).create();

				// handler.sendEmptyMessage(UI_PROGRESS_DISMISS);
				handler.sendEmptyMessage(UI_ERROR_SEVERAL_ADDR_SHOW);

				break;
			}
		} catch (IOException ioe) {
			Log.e("MAP", ioe.getMessage());
		}
	}

	private void initDialogs() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.nearest_station_gm__popup_no_address_found_title);
		builder.setIcon(R.drawable.warning);
		builder.setMessage(R.string.nearest_station_gm__popup_no_address_found_label);
		android.content.DialogInterface.OnClickListener dlglsnr = new android.content.DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
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
				Intent i = new Intent("changetab");
				i.putExtra("tab", "signets");

				sendBroadcast(i);
			}
		};
		builder2.setPositiveButton(R.string.map_continue_without_location, new android.content.DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder2.setNegativeButton(R.string.btn_back, dlglsnr2);
		unableToLocateDlg = builder2.create();

		noInternetDlg = new AlertDialog.Builder(this).setMessage(this.getString(R.string.error_no_internet_connection)).setPositiveButton(R.string.dialog_btn_ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();

						Intent i = new Intent("changetab");
						i.putExtra("tab", "signets");

						sendBroadcast(i);
					}
				}).setIcon(R.drawable.warning).create();

	}

	private void setUpMyCurrentPosition() {
		Location lastcenter = where.getLastLocation();
		// Log.d(this.getLocalClassName(), "----LATITUDE -----" + (lastcenter.getLatitude() * 1000000));
		if (lastcenter != null) {
			mapCenter = new VeloidMapCenter((int) (lastcenter.getLatitude() * 1000000), (int) (lastcenter.getLongitude() * 1000000));
		} else {
			mapCenter = null;
			handler.sendEmptyMessage(UI_ERROR_UNABLE_TO_LOCATE_SHOW);
		}
	}

	private void setUpCurrentMapCenter() {
		mapCenter = new VeloidMapCenter(map.getMapCenter().getLatitudeE6(), map.getMapCenter().getLongitudeE6());
	}

	public void findStationsToDisplay(GeoPoint center) {

		// Log.d("MAP", "restore minimum");
		Vector<Station> stations = mgr.restoreAllStationWithminimumInfoFromDataBase();

		// Log.d("MAP", "find nearest");
		Vector<Station> nearestStations = MapUtility.getNearestStations(mapCenter, ConfigurationContext.getMaxStationFiltered(), stations);

		Vector<Station> filteredStations = null;
		try {
			filteredStations = filter.filterStationsWithRealTimeChecking(nearestStations, mgr);
			nearestFullStations = mgr.fillInformationFromDB(filteredStations);
		} catch (NoInternetConnection e) {
			// handler.sendEmptyMessage(UI_PROGRESS_DISMISS);
			handler.sendEmptyMessage(UI_ERROR_NO_INTERNET);
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public VeloidMapCenter getMapCenter() {
		return mapCenter;
	}

	public MapView getMapView() {
		return map;
	}

	// public void addDirectlySignet(int stationid) {
	// Toast.makeText(this, R.string.nearest_station_popup_favorite_added, Toast.LENGTH_SHORT).show();
	// Station station = new Station();
	// station.setId(String.valueOf(stationid));
	// station.setNetwork(mgr.getNetworkId());
	// mgr.setStationAsSignet(station);
	// }

	// public void addDirectlySignet(Station station) {
	// Toast.makeText(this, R.string.nearest_station_popup_favorite_added, Toast.LENGTH_SHORT).show();
	// mgr.setStationAsSignet(station);
	// mgr.saveStationIntoDB(station);
	// }

	// public void removeDirectlySignet(Station station) {
	// Toast.makeText(this, R.string.nearest_station_popup_favorite_removed, Toast.LENGTH_SHORT).show();
	// mgr.removeStationAsSignet(station);
	// mgr.saveStationIntoDB(station);
	// }

	// public void removeDirectlySignet(int stationid) {
	// Toast.makeText(this, R.string.nearest_station_popup_favorite_removed, Toast.LENGTH_SHORT).show();
	// Station station = new Station();
	// station.setId(String.valueOf(stationid));
	// station.setNetwork(mgr.getNetworkId());
	// mgr.removeStationAsSignet(station);
	// }

	public void refreshCurrentPostion() {
		openMapFor = OPEN_FOR_GEOLOC;
		setUpMyCurrentPosition();
		findStationsAsThread(mapCenter, true);
		if (where != null) {
			where.registerMap(this);
		//	zoomToSpan = true;
		}
	}

	public void refreshFromCurrentMapCenter() {
		setUpCurrentMapCenter();
		findStationsAsThread(mapCenter, true);
	}

	public void refreshDataOnMap() {
		setUpMapOverlays(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.amenumap_actions, menu);

		if (!mgr.isSupportedbyMolib()) {
			menu.removeItem(R.id.menuMolib);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuMapEnterAddress:
			displayAndProcessAddressPopUp();
			return true;
		case R.id.menuMapCurrentMapCenter:
			refreshFromCurrentMapCenter();
			return true;
		case R.id.menuMapRefresh:
			refreshCurrentPostion();
			return true;

		case R.id.menuMolib:
			try {
				Uri uri = Uri.parse("http://m.layar.com/open/molib");
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;

			// Generic catch all for all the other menu resources
		default:
			// Don't toast text when a submenu is clicked
			if (!item.hasSubMenu()) {
				Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
				return true;
			}
			break;
		}

		return false;
	}

	public void displayStationDetailedInformation(Station station) {
		LayoutInflater vi = getLayoutInflater();
		final LinearLayout layoutInfo = (LinearLayout) vi.inflate(R.layout.amap_detailed_station_info, null);

		TextView name = (TextView) layoutInfo.findViewById(R.id.mapDiName);
		name.setText(station.getName());

		Log.d(this.toString(), station.getFavorite() + " !!!!");

		if (station.getFavorite() == 1) {
			ImageView img = (ImageView) layoutInfo.findViewById(R.id.imgIndicatorFavorite);
			img.setImageResource(R.drawable.favorite);
			// layoutInfo.removeView(img);
		}

		TextView addr = (TextView) layoutInfo.findViewById(R.id.mapDiAddress);
		if (station.getAddress() == null || station.getAddress().trim().equals("")) {
			layoutInfo.removeView(addr);
		} else {
			addr.setText(station.getAddress());
		}

		TextView bike = (TextView) layoutInfo.findViewById(R.id.mapDiBikes);
		bike.setText(FormatUtility.getTwoDigitsFormatedNumber(station.getAvailableBikes()));

		TextView slots = (TextView) layoutInfo.findViewById(R.id.mapDiSlots);
		slots.setText(FormatUtility.getTwoDigitsFormatedNumber(station.getFreeSlot()));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setView(layoutInfo);

		android.content.DialogInterface.OnClickListener addOrRemoveFavoriteLsnr = new StationInfoDetailButtonLstnr(station);
		builder.setPositiveButton((station.getFavorite() == 1 ? R.string.btn_remove_from_fav : R.string.btn_add_to_fav), addOrRemoveFavoriteLsnr);

		android.content.DialogInterface.OnClickListener dlgCloselsnr = new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		builder.setNegativeButton(R.string.btn_back, dlgCloselsnr);

		builder.create().show();
	}

	private void displayAndProcessAddressPopUp() {

		LayoutInflater vi = getLayoutInflater();
		final LinearLayout layoutAddr = (LinearLayout) vi.inflate(R.layout.adialog_enter_addr, null);

		final Spinner citiesSpn = (Spinner) layoutAddr.findViewById(R.id.dialogAddressSpinnerCity);
		try {
			ArrayAdapter<String> adapterCities = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ConfigurationContext.getCurrentStationManager(this).getCities());
			citiesSpn.setAdapter(adapterCities);
		} catch (NullPointerException e) {
		}

		final EditText addr = (EditText) layoutAddr.findViewById(R.id.dialogAddressTextInput);
		if (ConfigurationContext.getLastAddr() != null) {
			addr.setText(ConfigurationContext.getLastAddr());
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setView(layoutAddr);

		android.content.DialogInterface.OnClickListener dlgGoToAddrLsnr = new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				String address = addr.getText().toString();
				ConfigurationContext.setLastAddr(address);
				ConfigurationContext.saveConfig(thisInstance);
				// complete the address with city
				address += ", " + citiesSpn.getSelectedItem();
				dialog.dismiss();
				geolocateAddress(address);
				findStationsAsThread(mapCenter, true);
			}
		};
		builder.setPositiveButton(R.string.address_i_go, dlgGoToAddrLsnr);

		android.content.DialogInterface.OnClickListener dlgCloselsnr = new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		builder.setNegativeButton(R.string.dialog_btn_cancel, dlgCloselsnr);

		builder.create().show();
	}

	class StationInfoDetailButtonLstnr implements android.content.DialogInterface.OnClickListener {

		private Station	station;

		public StationInfoDetailButtonLstnr(Station station) {
			this.station = station;
		}

		public void onClick(DialogInterface dialog, int which) {
			if (station.getFavorite() == 0) {
				mgr.setStationAsSignet(station);
				station.setFavorite(1);
				Toast.makeText(thisInstance, R.string.nearest_station_popup_favorite_added, Toast.LENGTH_SHORT).show();
			} else {
				mgr.removeStationAsSignet(station);
				station.setFavorite(0);
				Toast.makeText(thisInstance, R.string.nearest_station_popup_favorite_removed, Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();
		}
	};

	class MapTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (where != null) {
				//zoomToSpan = false;
			}

			return false;
		}
	}

	private boolean needToUpdateScreen(Vector<Station> previousStations) {

		//check all stations name and availabilities
		for (Iterator<Station> iterator2 = nearestFullStations.iterator(); iterator2.hasNext();) {
			Station station2 = (Station) iterator2.next();
			boolean found = false;
			for (Iterator<Station> iterator = previousStations.iterator(); iterator.hasNext();) {
				Station station = (Station) iterator.next();
				if (station2.getId().equals(station.getId())) {
					found = true;
					if (station2.getAvailableBikes() != station.getAvailableBikes() || station2.getFreeSlot() != station.getFreeSlot()) {
						return true;
					}
				}
			}
			if (!found) {
				return true;
			}
		}

		return false;
	}

}
