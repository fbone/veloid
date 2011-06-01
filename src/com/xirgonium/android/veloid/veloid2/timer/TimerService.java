package com.xirgonium.android.veloid.veloid2.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.veloid.R;
import com.xirgonium.android.veloid.TimerRunActivity;
import com.xirgonium.android.veloid.veloid2.VeloidMain;

public class TimerService extends Service {
	int				timerSecondes	= 0;
	TimerService	thisInstance;

	@Override
	public void onStart(Intent i, int startId) {
		// TODO Auto-generated method stub
		super.onStart(i, startId);
		timerSecondes = i.getIntExtra(Constant.TIMER_MINUTE_BUNDLE_KEY, 0) * 60;

	}

	/**
	 * This is a list of callbacks that have been registered with the service. Note that this is package scoped (instead of private) so that it can be accessed more efficiently from inner classes.
	 */
	final RemoteCallbackList<ITimerServiceCallback2>	mCallbacks	= new RemoteCallbackList<ITimerServiceCallback2>();

	int													timerValue	= 0;
	NotificationManager									mNM;

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		thisInstance = this;
		notifyStart();
		ConfigurationContext.setTimerServiceRunning(true);
		mHandler.sendEmptyMessage(REPORT_TIMER_CHANGE);
	}

	@Override
	public void onDestroy() {
		Log.d("SERVICE", "DESTROY");

		// set a static flag indicating that the service stops running
		ConfigurationContext.setTimerServiceRunning(false);

		// mNM.cancel(Constant.SERVICE_TIMER);
		// Toast.makeText(this, R.string.timer_service_stopped, Toast.LENGTH_SHORT).show();
		mCallbacks.kill();
		mHandler.removeMessages(REPORT_TIMER_CHANGE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (ITimerServiceMain2.class.getName().equals(intent.getAction())) {
			return mBinder;
		}
		return null;
	}

	/**
	 * The IRemoteInterface is defined through IDL
	 */
	private final ITimerServiceMain2.Stub	mBinder				= new ITimerServiceMain2.Stub() {
																	public void registerCallback(ITimerServiceCallback2 cb) {
																		if (cb != null)
																			mCallbacks.register(cb);
																	}

																	public void unregisterCallback(ITimerServiceCallback2 cb) {
																		if (cb != null)
																			mCallbacks.unregister(cb);
																	}

																	public int getPid() {
																		return Process.myPid();
																	}
																};
	private static final int				REPORT_TIMER_CHANGE	= 1;

	private final Handler					mHandler			= new Handler() {

																	private void notifyUpdateTime(int duration) {

																		String minutes = FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(duration)[0]);
																		String seconds = FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(duration)[1]);

																		Intent appIntent = new Intent(thisInstance, VeloidMain.class);
																		appIntent.putExtra("tab", "timer");

																		CharSequence title = getText(R.string.timer_service_notification_title);
																		CharSequence subtitle = getText(R.string.timer_service_notification_subtitle_running) + " (" + minutes + ":" + seconds + ")";

																		Notification notification = new Notification(R.drawable.icon, "", System.currentTimeMillis());

																		// The PendingIntent to launch our activity if the user selects this notification
																		PendingIntent contentIntent = PendingIntent.getActivity(thisInstance, 0, appIntent, 0);

																		// Set the info for the views that show in the notification panel.
																		notification.setLatestEventInfo(thisInstance, title, subtitle, contentIntent);

																		NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
																		mNM.notify(Constant.SERVICE_TIMER, notification);

																	}

																	@Override
																	public void handleMessage(Message msg) {
																		switch (msg.what) {
																			case REPORT_TIMER_CHANGE: {
																				// Up it goes.
																				--timerSecondes;

																				timerSecondes = timerSecondes < 0 ? 0 : timerSecondes;

																				if (timerSecondes == 0) {
																					ConfigurationContext.setTimerWaitsForCustomerEndValidation(true);
																				} else {
																					if((timerSecondes%5) == 0)
																					notifyUpdateTime(timerSecondes);
																				}

																				// Broadcast to all clients the new value.
																				final int N = mCallbacks.beginBroadcast();
																				for (int i = 0; i < N; i++) {
																					try {

																						mCallbacks.getBroadcastItem(i).getTimerValue(timerSecondes);

																					} catch (Throwable e) {
																						// e.printStackTrace();
																					}
																				}
																				mCallbacks.finishBroadcast();
																				// Repeat every 1 second. until the end -- 1000 is the good value = 1 second
																				sendMessageDelayed(obtainMessage(REPORT_TIMER_CHANGE), 1000);

																			}
																				break;
																			default:
																				super.handleMessage(msg);
																		}
																	}
																};

	/**
	 * Show a notification while this service is running.
	 */
	private void notifyStart() {
		// This is who should be launched if the user selects our notification.
		// Intent i = new Intent(this, TimerRunActivity.class);
		// PendingIntent contentIntent = PendingIntent.getService(this.getApplicationContext(), 0, i, PendingIntent.FLAG_ONE_SHOT);

		// This is who should be launched if the user selects the app icon in the notification,
		// (in this case, we launch the same activity for both)
		Intent appIntent = new Intent(this, VeloidMain.class);
		appIntent.putExtra("tab", "timer");
		// appIntent.putExtra(Constant.CALLED_FROM_NOTIFICATION, true);

		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.timer_service_started);

		Notification notification = new Notification(R.drawable.icon, text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, appIntent, 0);

		// Set the info for the views that show in the notification panel.
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence title = getText(R.string.timer_service_notification_title);
		CharSequence subtitle = getText(R.string.timer_service_notification_subtitle_running);
		notification.setLatestEventInfo(this, title, subtitle, contentIntent);

		mNM.notify(Constant.SERVICE_TIMER, notification);

		Log.d("SERVICE", "START");

		ConfigurationContext.setTimerServiceRunning(true);

	}
}
