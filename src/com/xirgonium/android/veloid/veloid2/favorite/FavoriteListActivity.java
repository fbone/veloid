package com.xirgonium.android.veloid.veloid2.favorite;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.veloid.R;

public class FavoriteListActivity extends Activity {

	static private CommonStationManager	mgr									= null;
	protected Vector<Station>			signets								= new Vector<Station>();
//	private Menu						mMenu;
	static ProgressDialog				pd;
	private final int					DISMISS_PROGRESS_UPDATING_STATION	= 0;
	private final int					NO_INTERNET							= 1;
	private final int					FINALIZE_VIEW						= 2;

	FavoriteListActivity				thisINstance;

	Handler								handler								= new Handler() {

																				@Override
																				public void handleMessage(Message msg) {
																					switch (msg.what) {
																						case DISMISS_PROGRESS_UPDATING_STATION:
																							if (pd != null) {
																								pd.dismiss();
																							}
																							break;
																						case NO_INTERNET:
																							if (pd != null) {
																								pd.dismiss();
																							}
																							displayNoInternet();
																							break;
																						case FINALIZE_VIEW:
																							if (pd != null) {
																								pd.dismiss();
																							}
																							setViewsRegardingFavorites();
																							break;

																					}

																				}
																			};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asignets);
		thisINstance = this;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		ConfigurationContext.restoreConfig(this);

		mgr = ConfigurationContext.getCurrentStationManager(this);

		pd = ProgressDialog.show(thisINstance, getString(R.string.main_update_progress_dialog_title), getString(R.string.main_updating_favorite), true, true);

		Thread thread = new Thread() {

			public void run() {
				Looper.prepare();

				try {
					signets = mgr.restoreFavoriteFromDataBaseAndWeb();
					
				} catch (Exception e) {
					handler.sendEmptyMessage(NO_INTERNET);
				}

				handler.sendEmptyMessage(FINALIZE_VIEW);
			}
		};

		thread.start();
	}

	public void updateFavoritesName() {

		Thread thread = new Thread() {
			public void run() {
				Looper.prepare();

				try {
					signets = mgr.restoreFavoriteFromDataBaseAndWeb();
				} catch (Exception e) {
					handler.sendEmptyMessage(NO_INTERNET);
				}

				handler.sendEmptyMessage(FINALIZE_VIEW);
			}
		};

		thread.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void setViewsRegardingFavorites() {
		LinearLayout listOfFavorites = (LinearLayout) findViewById(R.id.favoriteListView);
		listOfFavorites.removeAllViews();
		
		if(ConfigurationContext.isSortFavoriteByColor()){
			Collections.sort(signets);
		}
		
		// Liste tous les favoris et les ajoute dans la liste
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// try {
		// signets = mgr.restoreFavoriteFromDataBaseAndWeb();
		// // signets = mgr.restoreFavoriteFromDataBase();
		//			
		// } catch (Exception e) {
		// handler.sendEmptyMessage(NO_INTERNET);
		// }

		int index = 0;

		for (Iterator<Station> iterator = signets.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();

			View oneStationView = vi.inflate(R.layout.asignet_item, null);

			TextView lbl = (TextView) oneStationView.findViewById(R.id.favLbl2);
			TextView stationName = ((TextView) oneStationView.findViewById(R.id.OneStationItemName));
			TextView bikesInStation = ((TextView) oneStationView.findViewById(R.id.OneStationItemBikesVal));
			TextView slotsInStation = ((TextView) oneStationView.findViewById(R.id.OneStationItemSlotsVal));
			stationName.setBackgroundColor(FormatUtility.getBackgroundColorForFavorite(station.getFavoriteColor(), this));
			stationName.setTextColor(FormatUtility.getTextColorForFavorite(station.getFavoriteColor(), this));

			stationName.setText(station.getComment());
			bikesInStation.setText(FormatUtility.getTwoDigitsFormatedNumber(station.getAvailableBikes()));
			slotsInStation.setText(FormatUtility.getTwoDigitsFormatedNumber(station.getFreeSlot()));

			if (index++ == 0)
				oneStationView.setPadding(0, 0, 0, 0);

			FavoriteLongClickListener lsnr = new FavoriteLongClickListener(station);

			stationName.setOnLongClickListener(lsnr);
			bikesInStation.setOnLongClickListener(lsnr);
			
			lbl.setOnLongClickListener(lsnr);

			listOfFavorites.addView(oneStationView);
		}

		thisINstance.handler.sendEmptyMessage(DISMISS_PROGRESS_UPDATING_STATION);

	}

	// private void setOnLongClickListenerToALlChildren(ViewGroup layout, FavoriteLongClickListener lsnr){
	// layout.setOnLongClickListener(lsnr);
	// int child = layout.getChildCount();
	// for (int i = 0; i < child; i++) {
	// View aChild = layout.getChildAt(i);
	// if(aChild instanceof ViewGroup){
	// setOnLongClickListenerToALlChildren((ViewGroup) aChild, lsnr);
	// }else{
	// aChild.setOnLongClickListener( lsnr);
	// }
	// }
	//		
	// }

	private void displayNoInternet() {
		LinearLayout listOfFavorites = (LinearLayout) findViewById(R.id.favoriteListView);
		listOfFavorites.removeAllViews();
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listOfFavorites.addView(vi.inflate(R.layout.ano_internet_label, null));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
//		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.amenufavorites_actions, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.btnAddFavorite:
				Intent i = new Intent(FavoriteListActivity.this, FavoriteAddEnterInfoActivity.class);
				startActivity(i);
				return true;

			case R.id.btnARemoveFavorite:
				Intent i2 = new Intent(FavoriteListActivity.this, FavoriteRemoveListActivity.class);
				startActivity(i2);
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

	public void displayNewFavoriteName(Station station) {

		LayoutInflater vi = getLayoutInflater();
		final LinearLayout layoutAddr = (LinearLayout) vi.inflate(R.layout.adialog_enter_new_name, null);

		final EditText newName = (EditText) layoutAddr.findViewById(R.id.dialogFavoriteNewNameTextInput);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setView(layoutAddr);

		ChangeNamBtnOnLClickListener chg = new ChangeNamBtnOnLClickListener(station, newName);

		builder.setPositiveButton(R.string.dialog_btn_ok, chg);
		builder.setNeutralButton(R.string.favorite_new_name_reset, chg);

		android.content.DialogInterface.OnClickListener dlgCloselsnr = new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		builder.setTitle(R.string.favorite_new_name);
		builder.setNegativeButton(R.string.dialog_btn_cancel, dlgCloselsnr);

		builder.create().show();

	}

	private void displayAndProcessColorPopUp(final Station station) {

		LayoutInflater vi = getLayoutInflater();
		final LinearLayout layoutAddr = (LinearLayout) vi.inflate(R.layout.adialog_pickup_color, null);

		final Spinner colorSpinner = (Spinner) layoutAddr.findViewById(R.id.dialogPickUpColorSpinnerColor);
		try {
			PickUpColorAdapter thisColor = new PickUpColorAdapter(this, 0);
			colorSpinner.setAdapter(thisColor);
		} catch (NullPointerException e) {
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setView(layoutAddr);

		ChangeColorBtnOnClickListener dlgGoToAddrLsnr = new ChangeColorBtnOnClickListener(station, colorSpinner);
		builder.setPositiveButton(R.string.dialog_btn_ok, dlgGoToAddrLsnr);

		// android.content.DialogInterface.OnClickListener dlgCloselsnr = new android.content.DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// };

		builder.setNegativeButton(R.string.dialog_btn_cancel, null);
		builder.setTitle(R.string.favorite_dialog_new_color);

		builder.create().show();
	}

	class ChangeNamBtnOnLClickListener implements android.content.DialogInterface.OnClickListener {
		Station		station;
		TextView	tv;

		public ChangeNamBtnOnLClickListener(Station station, TextView tvName) {
			this.station = station;
			this.tv = tvName;
		}

		public void onClick(DialogInterface dialog, int which) {

			if (which == AlertDialog.BUTTON_POSITIVE) {
				String newName = tv.getText().toString();
				station.setComment(newName);
				mgr.updateStation(station);
				updateFavoritesName();
				// thisINstance.setViewsRegardingFavorites();
				dialog.dismiss();
			} else if (which == AlertDialog.BUTTON_NEUTRAL) {
				station.setComment(station.getName());
				mgr.updateStation(station);
				// thisINstance.setViewsRegardingFavorites();
				updateFavoritesName();
				dialog.dismiss();
			}
		}

	}

	class FavoriteLongClickListener implements OnLongClickListener {

		Station	station;

		public FavoriteLongClickListener(Station station) {
			this.station = station;
		}

		public boolean onLongClick(View v) {

			String[] addressesAsStr = { getString(R.string.favorite_ctx_menu_rename), getString(R.string.favorite_ctx_menu_view_on_map),
					getString(R.string.favorite_ctx_menu_delete) + station.getName(), getString(R.string.favorite_ctx_menu_change_color) };

			AlertDialog actions = new AlertDialog.Builder(thisINstance).setItems(addressesAsStr, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							// display new name dialog
							thisINstance.displayNewFavoriteName(station);
							break;
						case 1:
							ConfigurationContext.setUsedStation(station);

							Intent i = new Intent("changetab");
							i.putExtra("tab", "map");
							sendBroadcast(i);
							break;
						case 2:
							// display new name dialog
							mgr.removeStationAsSignet(station);
							onResume();
							break;

						case 3:
							displayAndProcessColorPopUp(station);
							break;
					}

					dialog.dismiss();
				}
			}).create();
			actions.show();
			return false;
		}
	}

	class ChangeColorBtnOnClickListener implements android.content.DialogInterface.OnClickListener {
		Station	station;
		Spinner	spinner;

		public ChangeColorBtnOnClickListener(Station station, Spinner spinner) {
			this.station = station;
			this.spinner = spinner;
		}

		public void onClick(DialogInterface dialog, int which) {

			if (which == AlertDialog.BUTTON_POSITIVE) {
				int selectedColor = spinner.getSelectedItemPosition();
				station.setFavoriteColor(selectedColor);
				mgr.updateStation(station);

				setViewsRegardingFavorites();
			}

		}
	}

}
