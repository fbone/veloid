package com.xirgonium.android.veloid;

/*
 * IDEE : 
 * 
 * 
 sqlite3 /data/data/com.xirgonium.android/databases/veloidDB

 adb.exe -s emulator-5554 shell

 */

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.listener.AdvancedMenuListener;
import com.xirgonium.android.listener.ExpressSeachBikesMenuListener;
import com.xirgonium.android.listener.ExpressSeachSlotsMenuListener;
import com.xirgonium.android.listener.ItemOnMainScreenListener;
import com.xirgonium.android.listener.UpdateGUIShowMenuHandler;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.util.NetworkSkeletonParameter;
import com.xirgonium.android.util.NetworkSkeletonParameters;
import com.xirgonium.android.veloid.config.VeloidPreferences;
import com.xirgonium.android.view.FavoriteItemView;
import com.xirgonium.android.view.FilterAdapter;
import com.xirgonium.android.view.MenuAdvancedView;
import com.xirgonium.exception.NoInternetConnection;

public class Veloid extends Activity implements OnClickListener {

	private static final int			NO_INTERNET_CONNECTION_MSG						= 1;
	private static final int			DISPLAY_GATHERED_STATIONS_MSG					= 0;
	private static final int			DISPLAY_UPDATE_STATIONS_MSG						= 2;
	private static final int			DISPLAY_FIRST_TIME_NETWORK						= 3;
	private static final int			DISPLAY_QUICK_START								= 4;
	private static final int			DISMISS_PROGRESS_UPDATING_STATION				= 5;
	private static final int			REFRESH_TIMEOUT_MS								= 1000 * 60 * 2;

	private static Date					lastUpdate										= null;
	static private CommonStationManager	mgr												= null;

	protected Vector<Station>			signets											= new Vector<Station>();
	int									lastFavoriteCount								= 0;
	static Veloid						thisVeloid										= null;
	static ProgressDialog				pd;
	// AlertDialog dialogValidateUpdate;
	private boolean						waitForFirstChoice								= false;
	private boolean						updateStationsDisplayed							= false;

	// private int advancedActionsWidth = 0;
	private boolean						firstLaunch										= false;
	final int[]							locationOnScreenForAnimatedAdvancedMenu			= new int[2];
	final int[]							locationOnScreenForAnimatedExpressSearchBike	= new int[2];
	final int[]							locationOnScreenForAnimatedExpressSearchSlots	= new int[2];

	//WhereAmI							gpsManager;

	/**
	 * Handler, needed to close the progress dialog while updating station list
	 */

	private Handler						handler											= new Handler() {

																							@Override
																							public void handleMessage(Message msg) {
																								switch (msg.what) {
																								case NO_INTERNET_CONNECTION_MSG:
																									if (pd != null)
																										pd.dismiss();
																									thisVeloid.displayNoConnection();
																									break;
																								case DISMISS_PROGRESS_UPDATING_STATION:
																									// System.err.println("Message received");
																									if (pd != null) {
																										// System.err.println("pd != null");
																										pd.dismiss();
																									}
																									if (signets.size() != 0) {
																										lastUpdate = new Date();
																									} else {
																										lastUpdate = null;
																									}
																									for (Iterator<Station> iterator = signets.iterator(); iterator.hasNext();) {
																										Station aStation = (Station) iterator.next();
																										if (aStation.getUpdateStatus() < 0) {
																											lastUpdate = null;
																											break;
																										}
																									}

																									((TextView) findViewById(R.id.last_update_lbl)).setText(FormatUtility.generateLastUpdateField(
																											lastUpdate, thisVeloid));

																									refreshMainView();
																									break;
																								case DISPLAY_GATHERED_STATIONS_MSG:
																									pd.dismiss();
																									thisVeloid.displayTheNumberOfStations();
																									break;
																								case DISPLAY_UPDATE_STATIONS_MSG:
																									if (pd != null)
																										pd.dismiss();
																									if (!waitForFirstChoice) {
																										thisVeloid.displayUpdateStationDialog(R.string.main_update_station_list_after_chage);

																									}
																									break;
																								case DISPLAY_FIRST_TIME_NETWORK:
																									thisVeloid.selectNetworkForTheFirstTime();
																									break;
																								case DISPLAY_QUICK_START:
																									if (firstLaunch)
																										thisVeloid.displayQuickstart();
																									break;

																								}

																							}
																						};

	private UpdateGUIShowMenuHandler	menuHandler										= null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		lastUpdate = null;

		// --- Restore configuration
		ConfigurationContext.restoreConfig(this);

		thisVeloid = this;

		NetworkSkeletonParameters.init(this);

		/* TESTING CODE */

		//mgr.clearListOfStationFromDatabase();
		//Intent i = new Intent(this, VeloidMain.class);
		//startActivity(i);

		// Intent i = new Intent(this, NetworkTest.class);
		// startActivity(i);

		// SharedPreferences ref =
		// Intent i = new Intent(this, DialogForPlace.class);
		/*
		 * startActivity(i);
		 * 
		 * DialogForPlace dialog = new DialogForPlace(this); FavoritePlace a = new FavoritePlace(); a.setId(1l); //a.setName("ma place"); a.setLatitude(45); a.setLongitude(3);
		 * 
		 * DialogToNamePlace dialog2 = new DialogToNamePlace(this, a);
		 * 
		 * dialog.setTitle("++ Favorite places ++"); //dialog.setIcon(R.drawable.icon_small); //dialog.setMessage("BLABLABLA"); // dialog2.show(); //dialog.show();
		 * 
		 * 
		 * // // Intent i = new Intent(this, StationNearActivity.class); // Bundle b = new Bundle(); // b.putInt(Constant.FILTER_MIN_AVAILABLE_BIKES, 1); // b.putBoolean(Constant.MAP_FIND_GEOLOC_KEY,
		 * true); // i.putExtras(b); // this.startActivityForResult(i, Constant.ACTIVITY_NEW_STATION_S2_GOOGLEMAPS_START);
		 * 
		 * // PreferenceManager.getDefaultSharedPreferences(this); // Log.d("TEST", "test - returned = " + returned); // displayUpdateStationDialog (R.string.main_update_station_list_after_chage); /*
		 * **********************************************************
		 */

		mgr = ConfigurationContext.getCurrentStationManager(this);
		// CALLED IN onStart gpsManager =
		// ConfigurationContext.initWhereAmI(this);

		menuHandler = new UpdateGUIShowMenuHandler(this);

		setContentView(R.layout.main);

		// --- Define a gradient for the list
		ScrollView scroll = (ScrollView) findViewById(R.id.main_scroll_lst_stations);
		GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { getResources().getColor(R.color.list_top_color), getResources().getColor(R.color.list_bottom_color) });
		scroll.setBackgroundDrawable(grad);

		initMainView();

		// --- update the current content
		// ---> This action is done on the onResume method

		// --- Button Show Menu
		ImageButton btnShowAdvancedMenu = (ImageButton) findViewById(R.id.main_show_advanced_menu_btn);
		LinearLayout showAdvLabel = (LinearLayout) findViewById(R.id.main_advanced_label);
		btnShowAdvancedMenu.setOnClickListener(this);
		showAdvLabel.setOnClickListener(this);

		// --- REFRESH button
		ImageButton btnRefresh = (ImageButton) findViewById(R.id.main_refresh_favorite_btn);
		btnRefresh.setOnClickListener(this);
		LinearLayout refreshLabel = (LinearLayout) findViewById(R.id.main_refresh_label);
		refreshLabel.setOnClickListener(this);

		// --- EXPRESS Bike button
		ImageButton btnExpressBike = (ImageButton) findViewById(R.id.main_express_geoloc_bike_btn);
		btnExpressBike.setOnClickListener(this);
		LinearLayout esBikeLabel = (LinearLayout) findViewById(R.id.main_express_geoloc_bike_label);
		esBikeLabel.setOnClickListener(this);

		// --- EXPRESS Slot button
		ImageButton btnExpressSlot = (ImageButton) findViewById(R.id.main_express_geoloc_slot_btn);
		btnExpressSlot.setOnClickListener(this);
		LinearLayout esSlotLabel = (LinearLayout) findViewById(R.id.main_express_geoloc_slot_label);
		esSlotLabel.setOnClickListener(this);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent i) {

		super.onActivityResult(requestCode, resultCode, i);

		// ---- When the ADD STATION ends
		if (requestCode == Constant.ACTIVITY_NEW_STATION_S1_START) {
			// If the OK button has been clicked
			if (resultCode == Constant.RETURN_CODE_VALID) {
				Station nStation = new Station();
				nStation.setId(String.valueOf(i.getIntExtra(Constant.NEW_STATION_IN_BUNDLE_ID_KEY, 0)));
				nStation.setNetwork(ConfigurationContext.getCurrentStationManager(thisVeloid).getNetworkId());

				mgr.setStationAsSignet(nStation);

				try {
					signets = mgr.restoreFavoriteFromDataBaseAndWeb();
				} catch (NoInternetConnection e) {
					displayNoConnection();
				}
				refreshMainView();
			}
		}

		// ---- When the DEL STATION ends
		if (requestCode == Constant.ACTIVITY_DEL_STATION_START) {
			// If the OK button has been clicked
			if (resultCode == Constant.RETURN_CODE_VALID) {
				Station dStation = new Station();
				dStation.setId(i.getStringExtra(Constant.DEL_STATION_IN_BUNDLE_ID_KEY));
				dStation.setNetwork(ConfigurationContext.getCurrentStationManager(thisVeloid).getNetworkId());

				mgr.removeStationAsSignet(dStation);
				refreshMainView();
				// updateFavorite(true);
			}
		}

		// --- When preferences has been updated
		if (requestCode == Constant.ACTIVITY_PREFERENCE) {
			// We update only if the network has been changed
			if (ConfigurationContext.getCurrentStationManager(this) != null && !mgr.getNetworkId().equals(ConfigurationContext.getCurrentStationManager(this).getNetworkId())) {
				mgr = ConfigurationContext.getCurrentStationManager(this);
				initMainView();

				if (!mgr.isThereAtLeastOneStationInDBForNetwork()) {
					// --- No -> Propose update
					handler.sendEmptyMessage(DISPLAY_UPDATE_STATIONS_MSG);
				} else {
					updateFavorite(true);
					refreshMainView();
				}
			}
		}

		// Log.d("ACTIVITY RESULT", String.valueOf(requestCode));
	}

	/*
	 * ------------------------------------
	 * 
	 * MENU
	 * 
	 * -------------------------------------
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		Intent intent = new Intent(this, VeloidPreferences.class);
		startActivityForResult(intent, Constant.ACTIVITY_PREFERENCE);

		return true;

	}

	/***********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************
	 * MAIN METHODS *
	 **********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/

	public void initMainView() {

		// --- define the custom title bar
		TextView custoTitleBar = (TextView) findViewById(R.id.main_veloid_title);
		// font
		Typeface customForTimer = Typeface.createFromAsset(getAssets(), Constant.TITLE_FONT);
		custoTitleBar.setTypeface(customForTimer);
		// network name

		TextView custoTitleBarNetwork = (TextView) findViewById(R.id.main_veloid_current_network);
		if (ConfigurationContext.isAcceptedEULA()) {
			custoTitleBarNetwork.setText(mgr.getCommonName());
		} else {
			custoTitleBarNetwork.setText("");
		}

		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TableLayout tl = (TableLayout) findViewById(R.id.main_table_lst_stations);
		tl.removeAllViewsInLayout();

		// compute the width of the title to be displayed on screen
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int widthStationTitle = dm.widthPixels - Constant.GUI_WIDTH_INFO_STATION;

		int index = 1;
		for (Iterator<Station> iterator = signets.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();

			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			View oneStationView = vi.inflate(R.layout.station_item, null);
			// ((EditText)
			// oneStationView.findViewById(R.id.si_station_title)).setText(station.getName());
			((EditText) oneStationView.findViewById(R.id.si_station_title)).setText(station.getComment());
			((EditText) oneStationView.findViewById(R.id.si_station_title)).setWidth(widthStationTitle);

			ItemOnMainScreenListener eventLsnr = new ItemOnMainScreenListener(this, station);

			oneStationView.findViewById(R.id.si_station_title).setOnClickListener(eventLsnr);
			oneStationView.findViewById(R.id.si_station_title).setOnKeyListener(eventLsnr);
			oneStationView.findViewById(R.id.si_station_title).setOnFocusChangeListener(eventLsnr);

			((TextView) oneStationView.findViewById(R.id.si_available_bikes_val)).setText("--");
			((TextView) oneStationView.findViewById(R.id.si_free_slots_val)).setText("--");

			((FavoriteItemView) oneStationView).setColorsTheme(index++ % 2 == 0 ? Constant.COLOR_THEME_ITEM_EVEN : Constant.COLOR_THEME_ITEM_ODD);

			FavoriteItemView informationOnStation = (FavoriteItemView) oneStationView.findViewById(R.id.si_bikes_and_slot_info);
			informationOnStation.setColorsTheme(Constant.COLOR_THEME_ITEM_ALPHA);

			tr.addView(oneStationView);

			TableLayout.LayoutParams itemLayParams = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			tl.addView(tr, itemLayParams);
		}

		custoTitleBar.requestFocus();

	}

	public void refreshMainView() {

		// // --- define the custom title bar
		TextView custoTitleBar = (TextView) findViewById(R.id.main_veloid_title);
		// // font
		// Typeface customForTimer = Typeface.createFromAsset(getAssets(),
		// Constant.TITLE_FONT);
		// custoTitleBar.setTypeface(customForTimer);
		// // network name
		//
		// TextView custoTitleBarNetwork = (TextView)
		// findViewById(R.id.main_veloid_current_network);
		// if (ConfigurationContext.isAcceptedEULA()) {
		// custoTitleBarNetwork.setText(mgr.getCommonName());
		// } else {
		// custoTitleBarNetwork.setText("");
		// }

		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TableLayout tl = (TableLayout) findViewById(R.id.main_table_lst_stations);
		tl.removeAllViewsInLayout();

		// compute the width of the title to be displayed on screen
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int widthStationTitle = dm.widthPixels - Constant.GUI_WIDTH_INFO_STATION;

		int index = 1;
		for (Iterator<Station> iterator = signets.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();

			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			View oneStationView = vi.inflate(R.layout.station_item, null);
			// ((EditText)
			// oneStationView.findViewById(R.id.si_station_title)).setText(station.getName());
			((EditText) oneStationView.findViewById(R.id.si_station_title)).setText(station.getComment());
			((EditText) oneStationView.findViewById(R.id.si_station_title)).setWidth(widthStationTitle);

			ItemOnMainScreenListener eventLsnr = new ItemOnMainScreenListener(this, station);

			oneStationView.findViewById(R.id.si_station_title).setOnClickListener(eventLsnr);
			oneStationView.findViewById(R.id.si_station_title).setOnKeyListener(eventLsnr);
			oneStationView.findViewById(R.id.si_station_title).setOnFocusChangeListener(eventLsnr);

			((TextView) oneStationView.findViewById(R.id.si_available_bikes_val)).setText(FormatUtility.getTwoDigitsFormatedNumber(station.getAvailableBikes()));
			((TextView) oneStationView.findViewById(R.id.si_free_slots_val)).setText(FormatUtility.getTwoDigitsFormatedNumber(station.getFreeSlot()));

			((FavoriteItemView) oneStationView).setColorsTheme(index++ % 2 == 0 ? Constant.COLOR_THEME_ITEM_EVEN : Constant.COLOR_THEME_ITEM_ODD);

			FavoriteItemView informationOnStation = (FavoriteItemView) oneStationView.findViewById(R.id.si_bikes_and_slot_info);
			informationOnStation.setColorsTheme(Constant.COLOR_THEME_ITEM_ALPHA);

			tr.addView(oneStationView);

			TableLayout.LayoutParams itemLayParams = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			tl.addView(tr, itemLayParams);
		}

		custoTitleBar.requestFocus();

	}

	public void updateFavorite(boolean force) {
		Date now = new Date();

		int newFavCount = mgr.restoreFavoriteFromDataBase().size();

		if (lastUpdate == null || ((now.getTime() - lastUpdate.getTime()) > REFRESH_TIMEOUT_MS) || force || (newFavCount != lastFavoriteCount)) {
			// System.err.println("Display the PG");
			// pd = ProgressDialog.show(thisVeloid,
			// getString(R.string.main_update_progress_dialog_title),
			// getString(R.string.main_updating_favorite), true, true);

			Thread thread = new Thread() {

				public void run() {
					// System.err.println("Start the thread");
					signets.clear();

					try {
						signets = mgr.restoreFavoriteFromDataBaseAndWeb();
						lastFavoriteCount = signets.size();
					} catch (NoInternetConnection nie) {
						((TextView) findViewById(R.id.last_update_lbl)).setText(getString(R.string.main_last_update_not_done));
						displayNoConnection();
					} finally {
						// System.err.println("Send the message");
						handler.sendEmptyMessage(DISMISS_PROGRESS_UPDATING_STATION);
					}

				}
			};

			thread.start();
		}
	}

	@Override
	protected void onResume() {
		if (!ConfigurationContext.isAcceptedEULA()) {
			showEULA();
			waitForFirstChoice = true;
		} else if (!waitForFirstChoice && !mgr.isThereAtLeastOneStationInDBForNetwork()) {
			// --- No -> Propose update
			handler.sendEmptyMessage(DISPLAY_UPDATE_STATIONS_MSG);
		}

		updateFavorite(false);
		// For special network action
		setSpecificActionController();
		super.onResume();

	}

	public void displayQuickstart() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon_small);
		builder.setTitle(R.string.quick_start_title);
		builder.setMessage(R.string.quick_start_message);
		builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {
				if (which == AlertDialog.BUTTON1) {
					try {
						Uri uri = Uri.parse(getString(R.string.quick_start_url));
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {
				if (which == AlertDialog.BUTTON2) {
					dl.dismiss();
				}
			}
		});

		// reset the first launch to avoid sde effect
		firstLaunch = false;

		builder.create().show();

	}

	public synchronized void displayUpdateStationDialog(int idTextDialog) {

		if (updateStationsDisplayed) {
			// to avoid two displays... that makes issues
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon_small);
		builder.setTitle(getString(R.string.menu_update_station_list));
		builder.setMessage(getString(idTextDialog));
		builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {
				if (which == AlertDialog.BUTTON1) {

					dl.dismiss();
					updateStationsDisplayed = false;
					pd = ProgressDialog.show(thisVeloid, getString(R.string.main_update_progress_dialog_title), getString(R.string.main_update_progress_dialog_station_updating), true, false);

					Thread thread = new Thread() {

						public void run() {
							try {
								mgr.updateStationListDynamicaly();

								handler.sendEmptyMessage(DISPLAY_GATHERED_STATIONS_MSG);
								// dialogValidateUpdate.dismiss();
							} catch (NoInternetConnection e) {
								handler.sendEmptyMessage(NO_INTERNET_CONNECTION_MSG);
							}

						}
					};

					thread.start();

				}
			}
		});
		builder.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {
				if (which == AlertDialog.BUTTON2) {
					dl.dismiss();
					updateStationsDisplayed = false;
					handler.sendEmptyMessage(DISPLAY_QUICK_START);
				}
			}
		});
		updateStationsDisplayed = true;
		builder.create().show();

		/*--------------------------------------
		 final LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		 View viewUpdateValidation = (View) vi.inflate(R.layout.dialog_validate_station_update, null);

		 TextView txt = (TextView) viewUpdateValidation.findViewById(R.id.dlg_validate_update_list_stations);
		 txt.setText(getString(idTextDialog));

		 dialogValidateUpdate = new AlertDialog.Builder(Veloid.this).setTitle(R.string.menu_update_station_list).setView(viewUpdateValidation).show();

		 ImageButton btnOKupdate = (ImageButton) dialogValidateUpdate.findViewById(R.id.input_update_station_lst_btn_valid);
		 btnOKupdate.setOnClickListener(new OnClickListener() {

		 public void onClick(View clicked) {}
		 });

		 ImageButton btnCancelUpdate = (ImageButton) dialogValidateUpdate.findViewById(R.id.input_update_station_lst_btn_cancel);
		 btnCancelUpdate.setOnClickListener(new OnClickListener() {

		 public void onClick(View clicked) {
		
		 }
		 });
		 */
	}

	/**
	 * Animate the menu, think about the show final menu method, that display the final one
	 * 
	 * @param clicked
	 */
	private void startShowAnimationFormenu(int typeOfMenu) {
		int x = 0, y = 0, layout = 0, id = 0, msgWhat = 0;

		switch (typeOfMenu) {
		case Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS:
			x = locationOnScreenForAnimatedAdvancedMenu[0];
			y = locationOnScreenForAnimatedAdvancedMenu[1];
			layout = R.layout.main_advanced_toolbar;
			id = Constant.MENU_ADVANCED_ACTION_ID;
			msgWhat = Constant.HANDLER_VELOID_MSG_SHOW_ADV_MENU;

			ImageButton btnShowAdvancedMenu = (ImageButton) findViewById(R.id.main_show_advanced_menu_btn);
			btnShowAdvancedMenu.setClickable(false);
			break;

		default:
		case Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE:
			x = locationOnScreenForAnimatedExpressSearchBike[0];
			y = locationOnScreenForAnimatedExpressSearchBike[1];
			layout = R.layout.main_express_search_bike;
			id = Constant.MENU_EXPRESS_SEARCH_BIKE_ID;
			msgWhat = Constant.HANDLER_VELOID_MSG_SHOW_EXPRESS_SEARCH_BIKE_MENU;
			break;

		case Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT:
			x = locationOnScreenForAnimatedExpressSearchSlots[0];
			y = locationOnScreenForAnimatedExpressSearchSlots[1];
			layout = R.layout.main_express_search_slots;
			id = Constant.MENU_EXPRESS_SEARCH_SLOT_ID;
			msgWhat = Constant.HANDLER_VELOID_MSG_SHOW_EXPRESS_SEARCH_SLOT_MENU;
			break;
		}

		LayoutInflater vi = getLayoutInflater();
		final MenuAdvancedView advMenu = (MenuAdvancedView) vi.inflate(layout, null);
		advMenu.setId(id);

		// Create root AnimationSet.
		AnimationSet rootSet = new AnimationSet(true);
		rootSet.setInterpolator(new AccelerateInterpolator());
		rootSet.setRepeatCount(0);
		rootSet.setFillAfter(true);
		// rootSet.setRepeatMode(Animation.NO_REPEAT);

		// Create and add first child, a motion animation.
		int[] trans = { 0, 0, 0, -(Constant.MENU_ADVANCED_HEIGHT + 23) };
		TranslateAnimation trans1 = new TranslateAnimation(trans[0], trans[1], trans[2], trans[3]);
		trans1.setStartOffset(0);
		trans1.setDuration(Constant.MENU_ANIMATION_DURATION_MS);
		trans1.setFillAfter(true);
		rootSet.addAnimation(trans1);
		rootSet.setFillAfter(true);

		// AbsoluteLayout.LayoutParams absLayoutparams = new
		// AbsoluteLayout.LayoutParams(
		// advancedActionsWidth + Constant.MENU_ADVANCED_MARGIN,
		// Constant.MENU_ADVANCED_HEIGHT,
		// x + Constant.MENU_ADVANCED_X_OFFSET,
		// y + 23);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;
		AbsoluteLayout.LayoutParams absLayoutparams = new AbsoluteLayout.LayoutParams(width, Constant.MENU_ADVANCED_HEIGHT, 0, height);

		ViewGroup v = (ViewGroup) findViewById(R.id.global_main_container);
		v.addView(advMenu, absLayoutparams);
		// --- protection

		// stat the animation
		advMenu.startAnimation(rootSet);

		sendDelayedMessage(msgWhat, Constant.MENU_ANIMATION_DURATION_MS);

	}

	private void sendDelayedMessage(final int msg, final int duration) {
		Thread t = new Thread() {

			public void run() {
				try {
					Thread.sleep(duration);
					menuHandler.sendEmptyMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	public void showFinalAdvancedMenuAfterAimation(int type) {
		int x = 0, y = 0, id = 0, layout = 0;
		// get data
		LayoutInflater vi = getLayoutInflater();
		MenuAdvancedView advMenu = null;
		switch (type) {
		case Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS:
			x = locationOnScreenForAnimatedAdvancedMenu[0];
			y = locationOnScreenForAnimatedAdvancedMenu[1] + Constant.MENU_ADVANCED_Y_FINAL_OFFSET;
			id = Constant.MENU_ADVANCED_ACTION_ID;
			layout = R.layout.main_advanced_toolbar;

			advMenu = (MenuAdvancedView) vi.inflate(layout, null);

			AdvancedMenuListener listener = new AdvancedMenuListener(this, advMenu);

			// ---- Prepare the menu
			ImageButton hideBtn = (ImageButton) advMenu.findViewById(R.id.adv_hide_btn);
			hideBtn.setOnClickListener(listener);

			FavoriteItemView addFavBtn = (FavoriteItemView) advMenu.findViewById(R.id.adv_add_station_btn);
			addFavBtn.setOnClickListener(listener);

			FavoriteItemView delFavBtn = (FavoriteItemView) advMenu.findViewById(R.id.adv_delete_station_btn);
			delFavBtn.setOnClickListener(listener);

			FavoriteItemView geolocBtn = (FavoriteItemView) advMenu.findViewById(R.id.adv_nearest_station_btn);
			geolocBtn.setOnClickListener(listener);

			FavoriteItemView timerBtn = (FavoriteItemView) advMenu.findViewById(R.id.adv_timer_btn);
			timerBtn.setOnClickListener(listener);

			break;
		case Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE:
			x = locationOnScreenForAnimatedExpressSearchBike[0];
			y = locationOnScreenForAnimatedExpressSearchBike[1] + Constant.MENU_ADVANCED_EXPRESS_S_Y_FINAL_OFFSET;
			id = Constant.MENU_EXPRESS_SEARCH_BIKE_ID;
			layout = R.layout.main_express_search_bike;

			ExpressSeachBikesMenuListener bkLsnr = new ExpressSeachBikesMenuListener(this);

			advMenu = (MenuAdvancedView) vi.inflate(layout, null);

			FavoriteItemView btn1 = (FavoriteItemView) advMenu.findViewById(R.id.express_search_1_btn);
			btn1.setOnClickListener(bkLsnr);

			FavoriteItemView btn2 = (FavoriteItemView) advMenu.findViewById(R.id.express_search_2_btn);
			btn2.setOnClickListener(bkLsnr);

			FavoriteItemView btn3 = (FavoriteItemView) advMenu.findViewById(R.id.express_search_3_btn);
			btn3.setOnClickListener(bkLsnr);

			FavoriteItemView btn4 = (FavoriteItemView) advMenu.findViewById(R.id.express_search_4_btn);
			btn4.setOnClickListener(bkLsnr);

			ImageButton hideESBBtn = (ImageButton) advMenu.findViewById(R.id.adv_hide_btn);
			hideESBBtn.setOnClickListener(bkLsnr);

			break;
		case Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT:
			x = locationOnScreenForAnimatedExpressSearchSlots[0];
			y = locationOnScreenForAnimatedExpressSearchSlots[1] + Constant.MENU_ADVANCED_EXPRESS_S_Y_FINAL_OFFSET;
			id = Constant.MENU_EXPRESS_SEARCH_SLOT_ID;
			layout = R.layout.main_express_search_slots;

			ExpressSeachSlotsMenuListener sltLsnr = new ExpressSeachSlotsMenuListener(this);

			advMenu = (MenuAdvancedView) vi.inflate(layout, null);

			btn1 = (FavoriteItemView) advMenu.findViewById(R.id.express_search_1_btn);
			btn1.setOnClickListener(sltLsnr);

			btn2 = (FavoriteItemView) advMenu.findViewById(R.id.express_search_2_btn);
			btn2.setOnClickListener(sltLsnr);

			btn3 = (FavoriteItemView) advMenu.findViewById(R.id.express_search_3_btn);
			btn3.setOnClickListener(sltLsnr);

			btn4 = (FavoriteItemView) advMenu.findViewById(R.id.express_search_4_btn);
			btn4.setOnClickListener(sltLsnr);

			ImageButton hideESSBtn = (ImageButton) advMenu.findViewById(R.id.adv_hide_btn);
			hideESSBtn.setOnClickListener(sltLsnr);

			break;
		}

		// AbsoluteLayout.LayoutParams finalLayoutparams = new
		// AbsoluteLayout.LayoutParams(advancedActionsWidth +
		// Constant.MENU_ADVANCED_MARGIN, Constant.MENU_ADVANCED_HEIGHT, x
		// + Constant.MENU_ADVANCED_X_OFFSET, y +
		// Constant.MENU_ADVANCED_Y_OFFSET);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		AbsoluteLayout.LayoutParams finalLayoutparams = new AbsoluteLayout.LayoutParams(width, Constant.MENU_ADVANCED_HEIGHT, 0, dm.heightPixels - Constant.MENU_ADVANCED_HEIGHT - 23);

		ViewGroup v = (ViewGroup) findViewById(R.id.global_main_container);
		v.removeView(findViewById(id));
		advMenu.setId(id);
		v.addView(advMenu, finalLayoutparams);

		setAllToolbarButtonClickable(false);
	}

	public void startHideAnimationForMenu(int menuType) {

		// Create root AnimationSet.
		AnimationSet rootSet = new AnimationSet(true);
		rootSet.setInterpolator(new AccelerateInterpolator());

		// Create and add first child, a motion animation.
		int[] trans = { 0, 0, 0, Constant.MENU_ADVANCED_HEIGHT };
		TranslateAnimation trans1 = new TranslateAnimation(trans[0], trans[1], trans[2], trans[3]);
		trans1.setStartOffset(0);
		trans1.setDuration(Constant.MENU_ANIMATION_DURATION_MS);
		trans1.setFillAfter(true);
		rootSet.addAnimation(trans1);

		switch (menuType) {
		case Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS:
			MenuAdvancedView menu = (MenuAdvancedView) findViewById(Constant.MENU_ADVANCED_ACTION_ID);
			if (menu != null) {
				menu.startAnimation(rootSet);
				sendDelayedMessage(Constant.HANDLER_VELOID_MSG_HIDE_ADV_MENU, Constant.MENU_ANIMATION_DURATION_MS);
			}
			break;

		case Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE:
			MenuAdvancedView menuExpressBike = (MenuAdvancedView) findViewById(Constant.MENU_EXPRESS_SEARCH_BIKE_ID);
			if (menuExpressBike != null) {
				menuExpressBike.startAnimation(rootSet);
				sendDelayedMessage(Constant.HANDLER_VELOID_MSG_HIDE_EXPRESS_SEARCH_BIKE_MENU, Constant.MENU_ANIMATION_DURATION_MS);
			}
			break;

		case Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT:
			MenuAdvancedView menuExpressSlot = (MenuAdvancedView) findViewById(Constant.MENU_EXPRESS_SEARCH_SLOT_ID);
			if (menuExpressSlot != null) {
				menuExpressSlot.startAnimation(rootSet);
				sendDelayedMessage(Constant.HANDLER_VELOID_MSG_HIDE_EXPRESS_SEARCH_SLOT_MENU, Constant.MENU_ANIMATION_DURATION_MS);
			}
			break;
		}
	}

	public void removeAdvancedMenuView(int menuType) {
		ViewGroup vg = (ViewGroup) findViewById(R.id.global_main_container);
		switch (menuType) {
		case Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS:
			MenuAdvancedView menu = (MenuAdvancedView) findViewById(Constant.MENU_ADVANCED_ACTION_ID);
			if (menu != null) {
				ImageButton btnShowAdvancedMenu = (ImageButton) findViewById(R.id.main_show_advanced_menu_btn);
				btnShowAdvancedMenu.setClickable(true);
				vg.removeView(menu);
			}
			break;
		case Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE:
			MenuAdvancedView exprBikes = (MenuAdvancedView) findViewById(Constant.MENU_EXPRESS_SEARCH_BIKE_ID);
			if (exprBikes != null) {
				ImageButton btnExBikes = (ImageButton) findViewById(R.id.main_express_geoloc_bike_btn);
				btnExBikes.setClickable(true);
				vg.removeView(exprBikes);
			}
			break;
		case Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT:
			MenuAdvancedView exprSlots = (MenuAdvancedView) findViewById(Constant.MENU_EXPRESS_SEARCH_SLOT_ID);
			if (exprSlots != null) {
				ImageButton btnExSlots = (ImageButton) findViewById(R.id.main_express_geoloc_slot_btn);
				btnExSlots.setClickable(true);
				vg.removeView(exprSlots);
			}
			break;
		default:
			break;
		}
		setAllToolbarButtonClickable(true);
	}

	public static void setMgr(CommonStationManager mgr) {
		Veloid.mgr = mgr;
	}

	public static CommonStationManager getMgr() {
		return mgr;
	}

	public Vector<Station> getSignets() {
		return signets;
	}

	public void displayTheNumberOfStations() {

		// if (dialogValidateUpdate.isShowing()) {
		// dialogValidateUpdate.dismiss();
		// }

		StringBuffer buf = new StringBuffer();

		buf.append(mgr.getNbStationsInDB());
		buf.append(" ");
		buf.append(getString(R.string.main_nb_stations_grabbed_for_network));
		buf.append(" ");
		buf.append(mgr.getCommonName());

		new AlertDialog.Builder(this).setMessage(buf.toString()).setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				handler.sendEmptyMessage(DISPLAY_QUICK_START);
			}
		}).create().show();
	}

	public void displayNoConnection() {
		new AlertDialog.Builder(this).setMessage(this.getString(R.string.error_no_internet_connection)).setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		}).setIcon(R.drawable.warning).create().show();
	}

	private void showEULA() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon_small);
		builder.setTitle(getString(R.string.eula_title));
		builder.setMessage(getString(R.string.eula_content));
		builder.setPositiveButton(R.string.eula_btn_accept, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {
				if (which == AlertDialog.BUTTON1) {
					ConfigurationContext.setAcceptedEULA(true);
					ConfigurationContext.saveConfig(thisVeloid);
					firstLaunch = true;
					handler.sendEmptyMessage(DISPLAY_FIRST_TIME_NETWORK);
				}
			}
		});
		builder.setNegativeButton(R.string.eula_btn_decline, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {
				if (which == AlertDialog.BUTTON2) {
					thisVeloid.finish();
				}
			}
		});

		builder.create().show();
	}

	private void selectNetworkForTheFirstTime() {
		final LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogSetBNetworkView = (View) vi.inflate(R.layout.dialog_select_network, null);

		final Spinner spnBNetwork = (Spinner) dialogSetBNetworkView.findViewById(R.id.select_network_spinner);
		FilterAdapter admab = new FilterAdapter((Context) this, NetworkSkeletonParameters.getDescriptionArray());
		admab.setTextSize(20);
		spnBNetwork.setAdapter(admab);

		// spnBNetwork.setSelection(mgr.getMenuSelectionIndex());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon_small);
		builder.setTitle(getString(R.string.menu_network));
		builder.setView(dialogSetBNetworkView);
		builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {
				int selected = spnBNetwork.getSelectedItemPosition();

				NetworkSkeletonParameter selectedNetwork = NetworkSkeletonParameters.getNetworks().elementAt(selected);
				ConfigurationContext.setNetwork(selectedNetwork.getId());

				ConfigurationContext.saveConfig(thisVeloid);
				mgr = ConfigurationContext.getCurrentStationManager(thisVeloid);
				refreshMainView();
				initMainView();
				waitForFirstChoice = false;
				handler.sendEmptyMessage(DISPLAY_UPDATE_STATIONS_MSG);
				dl.dismiss();
			}
		});

		builder.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {
				dl.dismiss();
				handler.sendEmptyMessage(DISPLAY_QUICK_START);
			}
		});

		builder.create().show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("TEST LCE", String.valueOf(keyCode));
		return super.onKeyDown(keyCode, event);
	}

	/*-********************************************
	 * 
	 *   INTERFACE METHODS
	 * 
	 **********************************************/

	public void onClick(View clicked) {
		switch (clicked.getId()) {
		case R.id.main_express_geoloc_bike_btn:
			clicked.getLocationOnScreen(locationOnScreenForAnimatedExpressSearchBike);
			// advancedActionsWidth = clicked.getWidth();
			startShowAnimationFormenu(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE);
			break;
		case R.id.main_express_geoloc_bike_label:
			clicked.getLocationOnScreen(locationOnScreenForAnimatedExpressSearchBike);
			// advancedActionsWidth = clicked.getWidth();
			startShowAnimationFormenu(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE);
			break;

		case R.id.main_express_geoloc_slot_btn:
			clicked.getLocationOnScreen(locationOnScreenForAnimatedExpressSearchSlots);
			// advancedActionsWidth = clicked.getWidth();
			startShowAnimationFormenu(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT);
			break;
		case R.id.main_express_geoloc_slot_label:
			clicked.getLocationOnScreen(locationOnScreenForAnimatedExpressSearchSlots);
			// advancedActionsWidth = clicked.getWidth();
			startShowAnimationFormenu(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT);
			break;

		case R.id.main_refresh_favorite_btn:
			updateFavorite(true);
			break;
		case R.id.main_refresh_label:
			updateFavorite(true);
			break;

		case R.id.main_show_advanced_menu_btn:
			clicked.getLocationOnScreen(locationOnScreenForAnimatedAdvancedMenu);
			// advancedActionsWidth = clicked.getWidth();
			startShowAnimationFormenu(Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS);
			break;
		case R.id.main_advanced_label:
			clicked.getLocationOnScreen(locationOnScreenForAnimatedAdvancedMenu);
			// advancedActionsWidth = clicked.getWidth();
			startShowAnimationFormenu(Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS);
			break;

		case R.id.special_action_text:
			mgr.specialAction(this);
			break;
		}
	}

	private void setSpecificActionController() {

		if (mgr.canExecuteASpecificAction()) {
			TextView textSA = (TextView) findViewById(R.id.special_action_text);
			textSA.setText(mgr.getSpecialActionText(this));
			textSA.setOnClickListener(thisVeloid);
			textSA.setVisibility(View.VISIBLE);
		} else {
			// LinearLayout specialActionContainer = (LinearLayout)
			// findViewById(R.id.special_action_container);
			// specialActionContainer.removeAllViews();
			TextView textSA = (TextView) findViewById(R.id.special_action_text);
			textSA.setText("");
			textSA.setVisibility(View.GONE);
		}
	}

	private void setAllToolbarButtonClickable(boolean clickable) {
		// --- Button Show Menu
		ImageButton btnShowAdvancedMenu = (ImageButton) findViewById(R.id.main_show_advanced_menu_btn);
		btnShowAdvancedMenu.setClickable(clickable);
		// --- REFRESH button
		ImageButton btnRefresh = (ImageButton) findViewById(R.id.main_refresh_favorite_btn);
		btnRefresh.setClickable(clickable);
		// --- EXPRESS Bike button
		ImageButton btnExpressBike = (ImageButton) findViewById(R.id.main_express_geoloc_bike_btn);
		btnExpressBike.setClickable(clickable);
		// --- EXPRESS Slot button
		ImageButton btnExpressSlot = (ImageButton) findViewById(R.id.main_express_geoloc_slot_btn);
		btnExpressSlot.setClickable(clickable);
	}

	

}