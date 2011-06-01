package com.xirgonium.android.veloid.veloid2.map;

import java.util.Iterator;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AbsoluteLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.xirgonium.android.listener.FavoriteAddPlaceListener;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.veloid.R;

public class VeloidStationsOverlay extends Overlay implements OnClickListener {

	// private final android.graphics.Point shiftParameterForStationIndicator =
	// new android.graphics.Point(8, 16);
	private final android.graphics.Point	shiftParameterForCenterIndicator	= new android.graphics.Point(10, 10);

	VeloidMap								itsMapActivity						= null;
	Vector<Station>							stationsToDisplay					= null;
	Paint									paint								= null;
	boolean									zoomToSpan						= false;

	private Bitmap							centerIndicator						= null;
	ViewGroup								main								= null;

	private int								minLatitude							= (int) (+81 * 1E6);
	private int								maxLatitude							= (int) (-81 * 1E6);
	private int								minLongitude						= (int) (+181 * 1E6);
	private int								maxLongitude						= (int) (-181 * 1E6);

	private boolean							mappingDone							= false;

	public VeloidStationsOverlay(VeloidMap map, Vector<Station> dispStations, boolean zoomToSpan) {
		itsMapActivity = map;

		main = (ViewGroup) itsMapActivity.findViewById(R.id.mainLayout);

		setStationToDisplay(dispStations);
		paint = new Paint();
		paint.setAntiAlias(true);

		centerIndicator = BitmapFactory.decodeResource(this.itsMapActivity.getResources(), R.drawable.you_are_there_16x16);

		this.zoomToSpan = zoomToSpan;
	}

	@Override
	public void draw(Canvas canvas, MapView view, boolean b) {

		if (stationsToDisplay == null) {
			Log.w("MAP", "NO stations to display");
			return;
		}

		// Log.d("MAP", "Display " + stationsToDisplay.size() + " stations");
		super.draw(canvas, itsMapActivity.getMapView(), b);

		Point screenCoords = new Point();

			// --- Draw a buddy where you are
			Point centerCoords = new Point();
			mapPointToScreenCoords(itsMapActivity.getMapCenter(), centerCoords);

			canvas.drawBitmap(centerIndicator, centerCoords.x - shiftParameterForCenterIndicator.x, centerCoords.y - shiftParameterForCenterIndicator.y, paint);
		
		// --- process for each station
		for (Iterator<Station> iterator = stationsToDisplay.iterator(); iterator.hasNext();) {
			Station aStation = (Station) iterator.next();
			GeoPoint p = new GeoPoint(aStation.getMicroDegreeLatitude(), aStation.getMicroDegreeLongitude());
			mapPointToScreenCoords(p, screenCoords);
			StationInformationView stationView = (StationInformationView) itsMapActivity.findViewById(Integer.parseInt(aStation.getId()));

			if (stationView == null) {

				// Log.d(Constant.LOG_NS2_MAP, "Create a new station view");

				// --- Draw a information rectangle above each station
				LayoutInflater vi = itsMapActivity.getLayoutInflater();
				final StationInformationView stationInfoView = (StationInformationView) vi.inflate(R.layout.amap_station_info, null);
				stationInfoView.setId(Integer.parseInt(aStation.getId()));
				stationInfoView.setMicroDegreeLatitude(aStation.getMicroDegreeLatitude());
				stationInfoView.setMicroDegreeLongitude(aStation.getMicroDegreeLongitude());
				stationInfoView.setStation(aStation);

				// --- Define the text
				// //((TextView) stationInfoView.findViewById(R.id.map_station_address)).setText(aStation.getName().trim());
				// ((TextView) stationInfoView.findViewById(R.id.map_station_available_bike)).setText(itsMapActivity.getString(R.string.nearest_station_info_available_bike_prefix_lbl)
				// + String.valueOf(aStation.getAvailableBikes()));
				// ((TextView) stationInfoView.findViewById(R.id.map_station_free_slot)).setText(itsMapActivity.getString(R.string.nearest_station_info_freeslots_prefix_lbl)
				// + String.valueOf(aStation.getFreeSlot()));

				((TextView) stationInfoView.findViewById(R.id.map_station_available_bike)).setText(FormatUtility.getTwoDigitsFormatedNumber(aStation.getAvailableBikes()));
				((TextView) stationInfoView.findViewById(R.id.map_station_free_slot)).setText(FormatUtility.getTwoDigitsFormatedNumber(aStation.getFreeSlot()));

				// --- Draw the information on stations
				LayoutParams absLayoutparams = getLayoutForStationInfoView(aStation.getName(), screenCoords);
				// Log.d(Constant.LOG_NS2_MAP, "Absolute layout at creation : "
				// + absLayoutparams.x + " - " + absLayoutparams.y);

				// --- specific process if already a favorite
				if (aStation.getFavorite() == 1) {
					// final ImageButton addFavBtn = (ImageButton) stationInfoView.findViewById(R.id.map_station_add_link);
					// addFavBtn.setClickable(false);
					// addFavBtn.setImageResource(R.drawable.bookmark_16x16);
				} else {
					// ((ImageButton) stationInfoView.findViewById(R.id.map_station_add_link)).setOnClickListener(new OnClickListener() {
					//
					// public void onClick(View clicked) {
					// itsMapActivity.addDirectlySignet(((StationInformationView) clicked.getParent().getParent().getParent()).getId());
					// final ImageButton addFavBtn = (ImageButton) stationInfoView.findViewById(R.id.map_station_add_link);
					// addFavBtn.setImageResource(R.drawable.bookmark_16x16);
					// }
					// });
				}

				// stationInfoView.findViewById(R.id.map_station_addr_container).setOnClickListener(this);
				// stationInfoView.findViewById(R.id.map_station_address).setOnClickListener(this);
				stationInfoView.setOnClickListener(this);
				stationInfoView.findViewById(R.id.map_station_available_bike).setOnClickListener(this);
				stationInfoView.findViewById(R.id.map_station_free_slot).setOnClickListener(this);

				stationInfoView.setLayoutParams(absLayoutparams);

				setMinMaxLatLong(aStation.getMicroDegreeLatitude(), aStation.getMicroDegreeLongitude());

				// --- show it on the map
				// Log.d(Constant.LOG_NS2_MAP, "Add station on the map");
				stationInfoView.draw(canvas);

				main.addView(stationInfoView);

			} else {

				LayoutParams absLayoutparams = getLayoutForStationInfoView(aStation.getName(), screenCoords);
				// Log.d(Constant.LOG_NS2_MAP, "Absolute layout at change : " +
				// absLayoutparams.x + " - " + absLayoutparams.y);
				stationView.setLayoutParams(absLayoutparams);
			}

			// --- Draw the pin indicator
			// canvas.drawBitmap(stationIndicator, screenCoords.x -
			// shiftParameterForStationIndicator.x, screenCoords.y -
			// shiftParameterForStationIndicator.y, paint);
			// Log.d(Constant.LOG_NS2_MAP, "Point on the map for station : " +
			// aStation.getAddress());

		}

		/*
		 * VeloidMapCenter mapCenter = itsMapActivity.getMapCenter();
		 * 
		 * Point centerCoords = new Point(); mapPointToScreenCoords(itsMapActivity.getMapCenter(), centerCoords);
		 * 
		 * mapPointToScreenCoords(mapCenter, centerCoords);
		 * 
		 * switch (mapCenter.getType()) { case VeloidMapCenter.TYPE_FAVORITE: canvas.drawBitmap(centerIndicator, centerCoords.x - shiftParameterForCenterIndicator.x, centerCoords.y -
		 * shiftParameterForCenterIndicator.y, paint); break; default:
		 * 
		 * ImageButton btnCenter = new ImageButton(itsMapActivity); btnCenter.setBackgroundColor(R.color.map_station_item_transparent); btnCenter.setImageResource(R.drawable.bookmark_add_16x16);
		 * 
		 * btnCenter.setLayoutParams(getLayoutForStationInfoView("", centerCoords)); btnCenter.draw(canvas); }
		 */

		setupMap();
	}

	public void setStationToDisplay(Vector<Station> stations) {
		mappingDone = false;
		resetMinMaxLatLong();
		clearStationInfoChildren();
		this.stationsToDisplay = stations;
	}

	private void mapPointToScreenCoords(GeoPoint p, Point pointOnScreen) {
		if (p != null) {
			itsMapActivity.getMapView().getProjection().toPixels(p, pointOnScreen);
		}
	}

	private LayoutParams getLayoutForStationInfoView(String txt, Point screenCoords) {
		paint.setTextSize(10);
		// int maxComputedWidth = ((int) paint.measureText(txt) + 47);
		int minWidht = 59;// Constant.MAP_INFO_STATION_MIN_WIDTH;
		int height = Constant.MAP_INFO_STATION_OFFSET_Y + 1;// Constant.MAP_INFO_STATION_HEIGHT;
		int offsety = Constant.MAP_INFO_STATION_OFFSET_Y;
		int offsetx = Constant.MAP_INFO_STATION_OFFSET_X;

		// LayoutParams absLayoutparams = new LayoutParams(maxComputedWidth > minWidht ? maxComputedWidth : minWidht, height, screenCoords.x - offsetx, screenCoords.y - offsety);
		LayoutParams absLayoutparams = new LayoutParams(minWidht, height, screenCoords.x - offsetx, screenCoords.y - offsety);

		return absLayoutparams;
	}

	public GeoPoint getCenter() {
		return itsMapActivity.getMapCenter();
	}

	public void setupMap() {
		if (!mappingDone && zoomToSpan) {
			// Get the controller
			MapController mMapController = itsMapActivity.getMapView().getController();

			// // Zoom to span from the list of points
			mMapController.zoomToSpan((maxLatitude - minLatitude), (maxLongitude - minLongitude));

			// --------------------- Accurate zoom level
			MapView mapView = itsMapActivity.getMapView();
			int viewHeight = mapView.getHeight();
			int viewWidth = mapView.getWidth();
			int latitudeSpan = mapView.getLatitudeSpan();
			int longitudeSpan = mapView.getLongitudeSpan();
			int microDegreePerPixelLatitude = latitudeSpan / viewHeight;
			int microDegreePerPixelLongitude = longitudeSpan / viewWidth;

			int nbV = main.getChildCount();
			for (int i = 0; i < nbV; i++) {
				View v = main.getChildAt(i);
				if (v instanceof StationInformationView) {
					AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) v.getLayoutParams();
					int newMaxLat = ((StationInformationView) v).getMicroDegreeLatitude() + (params.height + 10) * microDegreePerPixelLatitude;
					int newMaxLong = ((StationInformationView) v).getMicroDegreeLongitude() + (params.width + 10) * microDegreePerPixelLongitude;

					// Log.d("OVERLAY",
					// "Compute new lat long with the printed information on screen : LAT="
					// + newMaxLat + ", LONG=" + newMaxLong);

					setMinMaxLatLong(newMaxLat, newMaxLong);
				}
			}

			// use map center:
			GeoPoint center = getCenter();
			if (center != null) {
				setMinMaxLatLong(center.getLatitudeE6(), center.getLongitudeE6());
			}
			// Zoom to span from the list of points with new max lat long
			mMapController.zoomToSpan((maxLatitude - minLatitude), (maxLongitude - minLongitude));

			// int eventScreenCoordX = (int) event.getX();
			// int eventScreenCoordY = (int) event.getY();
			// int deltaX = centerScreenX - eventScreenCoordX;
			// int deltaY = centerScreenY - eventScreenCoordY;
			// int deltaLatitude = microDegreePerPixelLatitude * deltaY;
			// int deltaLongitude = microDegreePerPixelLongitude * deltaX;

			// -----------------------------------------

			int latCenter = (maxLatitude - ((maxLatitude - minLatitude) / 2));
			int lngCenter = (maxLongitude - ((maxLongitude - minLongitude) / 2));

			mMapController.animateTo(new GeoPoint(latCenter, lngCenter));

			// Log.d("OVERLAY", "Animate to " + latCenter + " - " + lngCenter);
			mappingDone = true;
		}

	}

	// private void displayAndProcessAddressPopUp() {
	//
	// LayoutInflater vi = itsMapActivity.getLayoutInflater();
	// final LinearLayout layoutAddr = (LinearLayout) vi.inflate(R.layout.adialog_enter_addr, null);
	//
	// final Spinner citiesSpn = (Spinner) layoutAddr.findViewById(R.id.dialogAddressSpinnerCity);
	// try {
	// ArrayAdapter<String> adapterCities = new ArrayAdapter<String>(itsMapActivity, android.R.layout.simple_spinner_item, ConfigurationContext.getCurrentStationManager(itsMapActivity)
	// .getCities());
	// citiesSpn.setAdapter(adapterCities);
	// } catch (NullPointerException e) {
	// }
	//
	// final EditText addr = (EditText) layoutAddr.findViewById(R.id.dialogAddressTextInput);
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(itsMapActivity);
	//
	// builder.setView(layoutAddr);
	//
	// android.content.DialogInterface.OnClickListener dlgGoToAddrLsnr = new android.content.DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	//
	// String address = addr.getText().toString();
	// ConfigurationContext.setLastAddr(address);
	// // complete the address with city
	// address += ", " + citiesSpn.getSelectedItem();
	// dialog.dismiss();
	// itsMapActivity.geolocateAddress(address);
	// }
	// };
	// builder.setPositiveButton(R.string.address_i_go, dlgGoToAddrLsnr);
	//
	// android.content.DialogInterface.OnClickListener dlgCloselsnr = new android.content.DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// };
	// builder.setNegativeButton(R.string.dialog_btn_cancel, dlgCloselsnr);
	//
	// builder.create().show();
	// }

	public void clearStationInfoChildren() {

		int nbV = main.getChildCount();

		Vector<View> toRemove = new Vector<View>();

		for (int i = 0; i < nbV; i++) {
			View v = main.getChildAt(i);
			if (v instanceof StationInformationView) {
				toRemove.add(v);
			}
		}

		for (Iterator<View> iterator = toRemove.iterator(); iterator.hasNext();) {
			View view = (View) iterator.next();
			main.removeView(view);
		}
	}

	public int getMinLatitude() {
		return minLatitude;
	}

	public void setMinLatitude(int minLatitude) {
		this.minLatitude = minLatitude;
	}

	public int getMaxLatitude() {
		return maxLatitude;
	}

	public void setMaxLatitude(int maxLatitude) {
		this.maxLatitude = maxLatitude;
	}

	public int getMinLongitude() {
		return minLongitude;
	}

	public void setMinLongitude(int minLongitude) {
		this.minLongitude = minLongitude;
	}

	public int getMaxLongitude() {
		return maxLongitude;
	}

	public void setMaxLongitude(int maxLongitude) {
		this.maxLongitude = maxLongitude;
	}

	private void resetMinMaxLatLong() {
		minLatitude = (int) (+81 * 1E6);
		maxLatitude = (int) (-81 * 1E6);
		minLongitude = (int) (+181 * 1E6);
		maxLongitude = (int) (-181 * 1E6);
	}

	private void setMinMaxLatLong(int lat, int lng) {
		minLatitude = minLatitude > lat ? lat : minLatitude;
		maxLatitude = maxLatitude < lat ? lat : maxLatitude;
		minLongitude = minLongitude > lng ? lng : minLongitude;
		maxLongitude = maxLongitude < lng ? lng : maxLongitude;
		// Log.d("OVERLAY", "MIN MAX LAT LONG : [" + minLatitude + ", " +
		// maxLatitude + ", " + minLongitude + ", " + maxLongitude + "]");
	}

	public void onClick(View clicked) {
		ViewParent stationDisplay = null;
		if (clicked instanceof StationInformationView) {
			stationDisplay = (ViewParent) clicked;
		} else {
			stationDisplay = clicked.getParent();
			while (!(stationDisplay instanceof StationInformationView)) {
				stationDisplay = stationDisplay.getParent();
			}
		}

		itsMapActivity.displayStationDetailedInformation(((StationInformationView) stationDisplay).getStation());

		// main.removeView((StationInformationView) stationDisplay);
	}

	// @Override
	// public boolean onTap(GeoPoint p, MapView mapView) {
	//
	// String enterAddress = itsMapActivity.getString(R.string.map_ctx_menu_enter_address);
	// String currentMapCenter = itsMapActivity.getString(R.string.map_ctx_menu_current_map_center);
	// String currentLocation = itsMapActivity.getString(R.string.map_ctx_menu_current_location);
	// String refresh = itsMapActivity.getString(R.string.map_ctx_menu_refresh);
	//
	// String[] menuItems = new String[] { currentLocation, currentMapCenter, enterAddress, refresh };
	//
	// new AlertDialog.Builder(itsMapActivity).setItems(menuItems, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// switch (which) {
	// case 0:
	// // CurrentLocation
	// dialog.dismiss();
	// itsMapActivity.refreshCurrentPostion();
	// break;
	//
	// case 1:
	// // current map center
	// dialog.dismiss();
	// itsMapActivity.refreshFromCurrentMapCenter();
	// break;
	//
	// case 2:
	// displayAndProcessAddressPopUp();
	// break;
	//
	// case 3:
	// // refresh
	//
	// break;
	// }
	//
	// dialog.dismiss();
	//
	// }
	// }).create().show();
	//
	// return true;
	// }

}
