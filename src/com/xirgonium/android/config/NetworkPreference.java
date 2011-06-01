package com.xirgonium.android.config;

import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.util.NetworkSkeletonParameters;
import com.xirgonium.android.veloid.R;
import com.xirgonium.exception.NoInternetConnection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class NetworkPreference extends ListPreference {

	private static final int	NO_INTERNET_CONNECTION_MSG		= 1;
	private static final int	DISPLAY_GATHERED_STATIONS_MSG	= 0;
	NetworkPreference			thisInstance;

	private Handler				handler							= new Handler() {
																	@Override
																	public void handleMessage(Message msg) {
																		switch (msg.what) {
																			case NO_INTERNET_CONNECTION_MSG:
																				pd.dismiss();
																				thisInstance.displayNoConnection();
																				break;
																			case DISPLAY_GATHERED_STATIONS_MSG:
																				pd.dismiss();
																				thisInstance.displayTheNumberOfStations();
																				break;
																		}

																	}
																};

	static ProgressDialog		pd;

	// --- OLD BEGIN

	public NetworkPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NetworkPreference(Context context) {
		super(context);
		init();
	}

	private void init() {
		thisInstance = this;

		String[] entries = NetworkSkeletonParameters.getDescriptionArray();
		String[] entryValues = NetworkSkeletonParameters.getIdArray();

		setEntries(entries);
		setEntryValues(entryValues);
	}

	// -- OLD END

	@Override
	protected boolean callChangeListener(Object newValue) {
		boolean toreturn = super.callChangeListener(newValue);
		CommonStationManager newMgr = ConfigurationContext.getStationManagerFromName(getContext(), (String) newValue);

		if (!newMgr.isThereAtLeastOneStationInDBForNetwork()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setIcon(R.drawable.warning);
			builder.setMessage(getContext().getString(R.string.change_network_update));

			builder.setPositiveButton(R.string.del_station_warning_yes_btn, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dl, int which) {
					if (which == AlertDialog.BUTTON1) {
						pd = ProgressDialog.show(getContext(), getContext().getString(R.string.main_update_progress_dialog_title), getContext().getString(
								R.string.main_update_progress_dialog_station_updating), true, false);

						Thread thread = new Thread() {
							public void run() {

								try {
									ConfigurationContext.getCurrentStationManager(getContext()).updateStationListDynamicaly();
								} catch (NoInternetConnection e) {
									handler.sendEmptyMessage(NO_INTERNET_CONNECTION_MSG);
								}
								handler.sendEmptyMessage(DISPLAY_GATHERED_STATIONS_MSG);
							}
						};

						thread.start();
						dl.dismiss();
					}
				}
			});
			builder.setNegativeButton(R.string.del_station_warning_no_btn, null);
			builder.create().show();
		}
		return toreturn;
	}

	public void displayTheNumberOfStations() {
		CommonStationManager mgr = ConfigurationContext.getCurrentStationManager(getContext());

		StringBuffer buf = new StringBuffer();

		buf.append(mgr.getNbStationsInDB());
		buf.append(" ");
		buf.append(getContext().getString(R.string.main_nb_stations_grabbed_for_network));
		buf.append(" ");
		buf.append(mgr.getCommonName());

		new AlertDialog.Builder(getContext()).setMessage(buf.toString()).setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		}).create().show();
	}

	public void displayNoConnection() {
		new AlertDialog.Builder(getContext()).setMessage(getContext().getString(R.string.error_no_internet_connection)).setPositiveButton(R.string.dialog_btn_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).setIcon(R.drawable.warning).create().show();
	}

}
