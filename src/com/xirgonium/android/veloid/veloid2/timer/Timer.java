package com.xirgonium.android.veloid.veloid2.timer;

import java.io.IOException;
import java.security.acl.LastOwnerException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.veloid.R;
import com.xirgonium.android.veloid.StationNearActivity;
import com.xirgonium.android.veloid.service.ITimerServiceMain;
import com.xirgonium.android.veloid.veloid2.VeloidMain;
import com.xirgonium.android.view.FilterAdapter;

public class Timer extends Activity implements OnClickListener {

	ITimerServiceMain2	timerService		= null;
	TextView			remainingMin		= null;
	TextView			remainingSec		= null;
	Timer				thisInstance		= null;

	boolean				geolocAtTimerEnd	= ConfigurationContext.isGeolocAtTimerEnd();

	static int			VIEW_SET			= 0;
	static int			VIEW_RUN			= 1;

	int					currentView			= VIEW_SET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisInstance = this;

	}

	@Override
	protected void onResume() {
		super.onResume();
		ConfigurationContext.restoreConfig(this);
		if (!ConfigurationContext.isTimerServiceRunning()) {
			setUpTimerSetView();
			if (ConfigurationContext.isTimerWaitsForCustomerEndValidation()) {
				// displayPopUpToStopNotification();
			}
		} else {
			boolean binded = getParent().bindService(new Intent(ITimerServiceMain2.class.getName()), mConnection, Context.BIND_AUTO_CREATE);
			setUpTimerRunView();
		}
	}

	@Override
	protected void onPause() {
		try {
			getParent().unbindService(mConnection);
		} catch (Exception e) {
		}
		super.onPause();
	}

	private void setUpTimerSetView() {
		currentView = VIEW_SET;
		setContentView(R.layout.atimer_set);

		// ----- Number of minutes

		final TextView minutesTxtView = (TextView) findViewById(R.id.timerMinutes);

		minutesTxtView.setText(FormatUtility.getTwoDigitsFormatedNumber(ConfigurationContext.getTimerMinutes()));

		// --- Buttons to add or remove minutes to the timer
		ImageButton addUnitsBtn = (ImageButton) findViewById(R.id.timerUp);
		addUnitsBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View clicked) {
				int newTimerValue = ConfigurationContext.getTimerMinutes() + 1;
				ConfigurationContext.setTimerMinutes(newTimerValue);
				// need to set because of inner class
				minutesTxtView.setText(FormatUtility.getTwoDigitsFormatedNumber(newTimerValue));
			}
		});

		ImageButton removeUnitsBtn = (ImageButton) findViewById(R.id.timerDown);
		removeUnitsBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View clicked) {
				int newTimerValue = ConfigurationContext.getTimerMinutes() - 1;
				ConfigurationContext.setTimerMinutes(newTimerValue);
				// need to set because of inner class
				minutesTxtView.setText(FormatUtility.getTwoDigitsFormatedNumber(newTimerValue));
			}
		});

		// ----- Spinners
		String[] units = new String[] { "1", "2", "3", "4", "5" };
		String[] filterTypes = new String[] { getString(R.string.timer_set_filter_slots), getString(R.string.timer_set_filter_slots_and_bikes) };

		final Spinner unitsSpinner = (Spinner) findViewById(R.id.timerSetFilterUnit);
		FilterAdapter unitsFilterAdapter = new FilterAdapter((Context) this, units);
		unitsFilterAdapter.setTextSize(20);
		unitsSpinner.setAdapter(unitsFilterAdapter);

		final Spinner filterTypesSpinner = (Spinner) findViewById(R.id.timerSetFilterType);
		FilterAdapter filterTypesAdapter = new FilterAdapter((Context) this, filterTypes);
		filterTypesAdapter.setTextSize(20);
		filterTypesSpinner.setAdapter(filterTypesAdapter);

		// ----- Checkboxes
		CheckBox activateGeolocCB = (CheckBox) findViewById(R.id.timerSetGeolocalizeAtEnd);
		activateGeolocCB.setChecked(ConfigurationContext.isGeolocAtTimerEnd());

		final CheckBox activateFilterCB = (CheckBox) findViewById(R.id.timerSetActivateFilter);

		FilterAdapter unitsAdapter = (FilterAdapter) unitsSpinner.getAdapter();
		FilterAdapter filterAdapter = (FilterAdapter) filterTypesSpinner.getAdapter();

		unitsSpinner.setAdapter(unitsAdapter);

		filterTypesSpinner.setAdapter(filterAdapter);

		if (ConfigurationContext.getLastSearchedType() == Constant.SEARCH_TYPE_SLOTS) {
			activateFilterCB.setChecked(true);
			unitsSpinner.setSelection(ConfigurationContext.getLastSearchedUnits() - 1);
			filterTypesSpinner.setSelection(0);
			unitsSpinner.setClickable(true);
			unitsAdapter.setEnabled(true);
			filterTypesSpinner.setClickable(true);
			filterAdapter.setEnabled(true);

		} else if (ConfigurationContext.getLastSearchedType() == Constant.SEARCH_TYPE_BIKES_AND_SLOTS) {
			activateFilterCB.setChecked(true);
			unitsSpinner.setSelection(ConfigurationContext.getLastSearchedUnits() - 1);
			filterTypesSpinner.setSelection(1);
			unitsSpinner.setClickable(true);
			unitsAdapter.setEnabled(true);
			filterTypesSpinner.setClickable(true);
			filterAdapter.setEnabled(true);
			
		} else {
			unitsSpinner.setClickable(false);
			unitsAdapter.setEnabled(false);

			filterTypesSpinner.setClickable(false);
			filterAdapter.setEnabled(false);
		}

		activateFilterCB.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Grey command

				FilterAdapter unitsAdapter = (FilterAdapter) unitsSpinner.getAdapter();
				FilterAdapter filterAdapter = (FilterAdapter) filterTypesSpinner.getAdapter();

				if (!activateFilterCB.isChecked()) {
					unitsSpinner.setClickable(false);
					unitsAdapter.setEnabled(false);
					unitsSpinner.setAdapter(unitsAdapter);

					filterTypesSpinner.setClickable(false);
					filterAdapter.setEnabled(false);
					filterTypesSpinner.setAdapter(filterAdapter);

				} else {
					unitsSpinner.setClickable(true);
					unitsAdapter.setEnabled(true);
					unitsSpinner.setAdapter(unitsAdapter);

					filterTypesSpinner.setClickable(true);
					filterAdapter.setEnabled(true);
					filterTypesSpinner.setAdapter(filterAdapter);
				}
			}
		});

		// ----- Button start
		Button btnStartTimer = (Button) findViewById(R.id.timerSetStartBtn);
		btnStartTimer.setOnClickListener(this);

	}

	private void setUpTimerRunView() {
		currentView = VIEW_RUN;
		setContentView(R.layout.atimer_run);

		// ----- Number of minutes
		remainingMin = (TextView) findViewById(R.id.timerRunCurrentMinuteValue);
		remainingSec = (TextView) findViewById(R.id.timerRunCurrentSecondeValue);

		TextView geolocationInfo = (TextView) findViewById(R.id.timer_run_geoloc_at_end);
		TextView filterOnBikes = (TextView) findViewById(R.id.timer_run_filter_av_bikes);
		TextView filterOnSlots = (TextView) findViewById(R.id.timer_run_filter_free_slots);

		geolocationInfo.setText(ConfigurationContext.isGeolocAtTimerEnd() ? R.string.yes : R.string.no);
		switch (ConfigurationContext.getLastSearchedType()) {
			case Constant.SEARCH_TYPE_BIKES:
				filterOnSlots.setText(R.string.timer_run_filter_not_filtered);
				filterOnBikes.setText(String.valueOf(ConfigurationContext.getLastSearchedUnits()));
				break;
			case Constant.SEARCH_TYPE_SLOTS:
				filterOnSlots.setText(String.valueOf(ConfigurationContext.getLastSearchedUnits()));
				filterOnBikes.setText(R.string.timer_run_filter_not_filtered);
				break;
			case Constant.SEARCH_TYPE_BIKES_AND_SLOTS:
				filterOnSlots.setText(String.valueOf(ConfigurationContext.getLastSearchedUnits()));
				filterOnBikes.setText(String.valueOf(ConfigurationContext.getLastSearchedUnits()));
				break;

			default:
				filterOnSlots.setText(R.string.timer_run_filter_not_filtered);
				filterOnBikes.setText(R.string.timer_run_filter_not_filtered);
				break;
		}

		Button stopTimerBtn = (Button) findViewById(R.id.timerRunBtnStop);
		stopTimerBtn.setOnClickListener(this);

	}

	public void onClick(View v) {
		if (v.getId() == R.id.timerSetStartBtn) {
			// ----- Start timer
			CheckBox activateGeolocCB = (CheckBox) findViewById(R.id.timerSetGeolocalizeAtEnd);
			ConfigurationContext.setGeolocAtTimerEnd(activateGeolocCB.isChecked());

			CheckBox activateFilterCB = (CheckBox) findViewById(R.id.timerSetActivateFilter);
			if (activateFilterCB.isChecked()) {
				Spinner unitsSpinner = (Spinner) findViewById(R.id.timerSetFilterUnit);
				Spinner filterTypesSpinner = (Spinner) findViewById(R.id.timerSetFilterType);

				int filterUnit = Integer.parseInt((String) unitsSpinner.getSelectedItem());
				ConfigurationContext.setLastSearchedUnits(filterUnit);

				String filterType = (String) filterTypesSpinner.getSelectedItem();
				if (filterType.equals(getString(R.string.timer_set_filter_slots))) {
					ConfigurationContext.setLastSearchedType(Constant.SEARCH_TYPE_SLOTS);
				} else if (filterType.equals(getString(R.string.timer_set_filter_slots_and_bikes))) {
					ConfigurationContext.setLastSearchedType(Constant.SEARCH_TYPE_BIKES_AND_SLOTS);
				}
			}

			ConfigurationContext.saveConfig(this);

			try {

				TextView minutesTxtView = (TextView) findViewById(R.id.timerMinutes);
				int minutes = Integer.parseInt(minutesTxtView.getText().toString());

				((VeloidMain) getParent()).startTimer(minutes);

				boolean binded = getParent().bindService(new Intent(ITimerServiceMain2.class.getName()), mConnection, Context.BIND_AUTO_CREATE);

				// Log.d("Timer class", "Binded to timer= " + binded);
				setUpTimerRunView();
			} catch (Exception e) {
				// TODO Auto-generated catch blocks
			}

		} else if (v.getId() == R.id.timerRunBtnStop) {

			// Log.d("Timer Class", "Stop Btn puched");

			stopTimerActions(false);
			setUpTimerSetView();
		}

	}

	private void stopTimerActions(boolean triggerNotif) {

		// --- Remove the notification for the running service
		NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(Constant.SERVICE_TIMER);
		// unbind
		getParent().unbindService(mConnection);

		getParent().stopService(new Intent(ITimerServiceMain2.class.getName()));

		ConfigurationContext.restoreConfig(thisInstance);
		ConfigurationContext.setTimerServiceRunning(false);

		// set a static flag indicating that the service stops running (just force here)
		ConfigurationContext.setTimerServiceRunning(false);

		// NOTIFICATION !
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// --- Remove the notification for the running service

		if (triggerNotif) {
			final Vibrator vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			MediaPlayer player = null;
			if (ConfigurationContext.getPlayer() != null) {
				player = ConfigurationContext.getPlayer();
			} else {
				String sound = ConfigurationContext.getSoundPath();
				if (Constant.DEFAULT_SOUND_PATH.equals(sound) || "".equals(sound)) {
					player = MediaPlayer.create(thisInstance, R.raw.beep);
				} else {
					player = new MediaPlayer();
					try {
						player.setDataSource(sound);
						player.prepare();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			ConfigurationContext.setPlayer(player);

			Notification mNotify = new Notification();

			if (ConfigurationContext.isNotifyLED() || ConfigurationContext.isNotifyVibra() || ConfigurationContext.isNotifySound()) {

				if (ConfigurationContext.isNotifyLED()) {
					mNotify.ledOnMS = Constant.NOTIFY_LED_ON_DURATION;
					mNotify.ledOffMS = Constant.NOTIFY_LED_OFF_DURATION;
					mNM.notify(R.string.menu_notification, mNotify);
				}

				if (ConfigurationContext.isNotifyVibra()) {
					vibrate.vibrate(Constant.NOTIFY_VIBRA_PATTERN, -1);
				}

				if (ConfigurationContext.isNotifySound()) {
					player.setLooping(true);
					player.start();
				}
			}
		}

	}

	public void displayPopUpToStopNotification() {
		final NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		final Vibrator vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		final MediaPlayer player = ConfigurationContext.getPlayer();

		// --- Pop up.
		AlertDialog.Builder builder3 = new AlertDialog.Builder(thisInstance);
		builder3.setIcon(R.drawable.warning);

		builder3.setMessage(getString(ConfigurationContext.isGeolocAtTimerEnd() ? R.string.timer_run_popup_geolocate : R.string.timer_run_popup_timer_ends));
		builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dl, int which) {

				vibrate.cancel();
				if (player != null && player.isPlaying()) {
					player.stop();
					player.release();
				}
				ConfigurationContext.setPlayer(null);
				mNM.cancel(R.string.menu_notification);
				mNM.cancel(Constant.SERVICE_TIMER);

				if (ConfigurationContext.isGeolocAtTimerEnd()) {
					Intent i = new Intent("changetab");
					i.putExtra("tab", "map");
					sendBroadcast(i);
				}

				setUpTimerSetView();
			}
		});

		builder3.create().show();
	}

	private ServiceConnection		mConnection	= new ServiceConnection() {

													public void onServiceConnected(ComponentName className, IBinder service) {
														// Log.d("Timer Class", "Connected service");
														timerService = ITimerServiceMain2.Stub.asInterface(service);
														try {
															// Log.d("Timer Class", "Register callback");
															timerService.registerCallback(timerCbk);
														} catch (RemoteException e) {
															// Toast.makeText(, "Error", Toast.LENGTH_SHORT).show();
														}
													}

													public void onServiceDisconnected(ComponentName arg0) {
														timerService = null;
													}

												};

	private ITimerServiceCallback2	timerCbk	= new ITimerServiceCallback2.Stub() {

													public void getTimerValue(int value) {
														remainingMin.setText(FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(value)[0]));
														remainingSec.setText(FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(value)[1]));

														// Log.d("TIMER CLASS", "Get Timer value " + value);
														// mHandler.sendMessage(mHandler.obtainMessage(0, value, 0));
														if (value == 0) {
															try {
																timerService.unregisterCallback(this);
																stopTimerActions(true);
																thisInstance.displayPopUpToStopNotification();
															} catch (RemoteException e) {
																// do nothing
															}
														}
													}
												};

}
