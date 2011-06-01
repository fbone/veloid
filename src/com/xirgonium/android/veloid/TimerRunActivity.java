package com.xirgonium.android.veloid;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.veloid.service.ITimerServiceCallback;
import com.xirgonium.android.veloid.service.ITimerServiceMain;

public class TimerRunActivity extends Activity {

  ITimerServiceMain timerService            = null;
  TextView          remainingMin            = null;
  TextView          remainingSec            = null;
  TimerRunActivity  thisInstance            = null;
  boolean           displayStopWarningPopUp = false;
  boolean           calledFromNotification  = false;

  public boolean isDisplayStopWarningPopUp() {
    return displayStopWarningPopUp;
  }

  public void setDisplayStopWarningPopUp(boolean displayStopWarningPopUp) {
    this.displayStopWarningPopUp = displayStopWarningPopUp;
  }

  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    thisInstance = this;
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.timer_run);

    final Bundle initBundle = this.getIntent().getExtras();

    calledFromNotification = getIntent().getBooleanExtra(Constant.CALLED_FROM_NOTIFICATION, false);

    ConfigurationContext.restoreConfig(this);

    /*******************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************
     * GUI DEFINITION
     ******************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/

    // Define a gradient for the list
    GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { getResources().getColor(R.color.list_top_color), getResources().getColor(R.color.list_bottom_color) });
    TableLayout container = (TableLayout) findViewById(R.id.timer_run_filter_set);
    container.setBackgroundDrawable(grad);

    remainingMin = (TextView) findViewById(R.id.timer_run_current_min_val);
    remainingSec = (TextView) findViewById(R.id.timer_run_current_sec_val);
    TextView separatorTV = (TextView) findViewById(R.id.timer_run_current_separator);

    Typeface customForTimer = Typeface.createFromAsset(getAssets(), Constant.TITLE_FONT);
    remainingMin.setTypeface(customForTimer);
    remainingSec.setTypeface(customForTimer);
    separatorTV.setTypeface(customForTimer);

    TextView custoTitleBar = (TextView) findViewById(R.id.timer_run_title);
    // font
    custoTitleBar.setTypeface(customForTimer);

    int timerInit = 0;
    if (initBundle != null && initBundle.getInt(Constant.TIMER_MINUTE_BUNDLE_KEY) != 0) {
      timerInit = initBundle.getInt(Constant.TIMER_MINUTE_BUNDLE_KEY);
      remainingMin.setText(FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(timerInit)[0]));
      remainingSec.setText(FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(timerInit)[1]));
    }

    if (initBundle != null || calledFromNotification) {
      int filterMinBike = ConfigurationContext.getFilterMinAvailableBike();// initBundle.getInt(Constant.FILTER_MIN_AVAILABLE_BIKES);
      int filterMinSlot = ConfigurationContext.getFilterMinSlot();// initBundle.getInt(Constant.FILTER_MIN_FREE_SLOTS);

      if (filterMinSlot != 0) {
        TextView viewMinSLot = (TextView) findViewById(R.id.timer_run_filter_free_slots);
        viewMinSLot.setText(String.valueOf(filterMinSlot));
      }

      if (filterMinBike != 0) {
        TextView viewMinBike = (TextView) findViewById(R.id.timer_run_filter_av_bikes);
        viewMinBike.setText(String.valueOf(filterMinBike));
      }

      boolean geoloc = ConfigurationContext.isGeolocAtTimerEnd();// initBundle.getBoolean(Constant.TIMER_GELOCALIZE_OPT_BUNDLE_KEY);
      if (geoloc) {
        TextView filterSet = (TextView) findViewById(R.id.timer_run_geoloc_at_end);
        filterSet.setText(getString(R.string.timer_run_activated));
      }
    }
    ImageButton stopBtn = (ImageButton) findViewById(R.id.timer_run_btn_stop);
    stopBtn.setOnClickListener(new OnClickListener() {

      public void onClick(View arg0) {
        // --- Remove the notification for the running service
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM.cancel(Constant.SERVICE_TIMER);

        // unbind
        unbindService(mConnection);

        stopService(new Intent(ITimerServiceMain.class.getName()));
        ConfigurationContext.setTimerServiceRunning(false);

        // try {
        // Process.killProcess(timerService.getPid());
        // } catch (RemoteException e) {
        // e.printStackTrace();
        // }

        TimerRunActivity.this.setResult(Constant.RETURN_CODE_CANCEL, null);
        if (calledFromNotification) {
          // here we need to reopen the timer set function
          Intent intent = new Intent(thisInstance, TimerSetActivity.class);
          intent.putExtra(Constant.CALLED_FROM_NOTIFICATION, true);
          thisInstance.startActivity(intent);
        }
        TimerRunActivity.this.finish();
      }
    });

    ImageButton stopAndGeolocBtn = (ImageButton) findViewById(R.id.timer_run_btn_geoloc);
    stopAndGeolocBtn.setOnClickListener(new OnClickListener() {

      public void onClick(View arg0) {
        // --- Remove the notification for the running service
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM.cancel(Constant.SERVICE_TIMER);

        // unbind
        unbindService(mConnection);

        stopService(new Intent(ITimerServiceMain.class.getName()));

        // try {
        // Process.killProcess(timerService.getPid());
        // } catch (RemoteException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        // Stop the service at the timer end

        ConfigurationContext.setTimerServiceRunning(false);

        // geolocalisation and open map
        Intent i = new Intent(TimerRunActivity.this, StationNearActivity.class);
        Bundle b = new Bundle();
        b.putBoolean(Constant.MAP_FIND_GEOLOC_KEY, true);
        b.putAll(ConfigurationContext.getTimerBundle());
        i.putExtras(b);
        startActivityForResult(i, Constant.ACTIVITY_NEW_STATION_S2_GOOGLEMAPS_START);

      }
    });

    if (!ConfigurationContext.isTimerServiceRunning() && initBundle != null) {
      Intent i = new Intent(ITimerServiceMain.class.getName());
      i.putExtras(initBundle);
      startService(i);
      ConfigurationContext.setTimerBundle(initBundle);
    }

    // -- -if called from the notification manager
    if (calledFromNotification && displayStopWarningPopUp) {
      displayPopUpToStopNotification();
    }

    try {
      bindService(new Intent(ITimerServiceMain.class.getName()), mConnection, Context.BIND_AUTO_CREATE);
    } catch (Exception e) {
      // TODO Auto-generated catch blocks
    }
  }

  public void displayPopUpToStopNotification() {
    final NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    final Vibrator vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    final MediaPlayer player = ConfigurationContext.getPlayer();

    // --- Pop up.
    AlertDialog.Builder builder3 = new AlertDialog.Builder(thisInstance);
    builder3.setIcon(R.drawable.warning);

    if (ConfigurationContext.getTimerBundle() != null && ConfigurationContext.getTimerBundle().getBoolean(Constant.TIMER_GELOCALIZE_OPT_BUNDLE_KEY) == true) {

      builder3.setMessage(getString(R.string.timer_run_popup_geolocate));
      builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dl, int which) {
          if (which == AlertDialog.BUTTON1) {

            vibrate.cancel();
            if (player.isPlaying())
              player.stop();
            player.release();
            ConfigurationContext.setPlayer(null);
            mNM.cancel(R.string.menu_notification);

            // geolocalisation and open map
            Intent i = new Intent(TimerRunActivity.this, StationNearActivity.class);
            Bundle b = new Bundle();
            b.putBoolean(Constant.MAP_FIND_GEOLOC_AFTER_WARNING_KEY, true);
            b.putAll(ConfigurationContext.getTimerBundle());
            i.putExtras(b);
            mNM.cancel(Constant.SERVICE_TIMER);
            startActivityForResult(i, Constant.ACTIVITY_NEW_STATION_S2_GOOGLEMAPS_START);

          }
        }
      });
    } else {
      builder3.setMessage(getString(R.string.timer_run_popup_timer_ends));
      builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dl, int which) {
          if (which == AlertDialog.BUTTON1) {
            vibrate.cancel();
            if (player.isPlaying())
              player.stop();
            player.release();
            ConfigurationContext.setPlayer(null);
            mNM.cancel(R.string.menu_notification);
            mNM.cancel(Constant.SERVICE_TIMER);
            thisInstance.finish();
          }
        }
      });

    }

    //       
    builder3.create().show();
  }

  /**
   * Class for interacting with the main interface of the service.
   */
  private ServiceConnection     mConnection = new ServiceConnection() {

                                              public void onServiceConnected(ComponentName className, IBinder service) {

                                                timerService = ITimerServiceMain.Stub.asInterface(service);
                                                // Toast.makeText(TimerRunActivity.this, "Timer Binded", Toast.LENGTH_SHORT).show();
                                                try {
                                                  timerService.registerCallback(timerCbk);
                                                } catch (RemoteException e) {
                                                  Toast.makeText(TimerRunActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                              }

                                              public void onServiceDisconnected(ComponentName arg0) {
                                                timerService = null;
                                              }

                                            };

  private ITimerServiceCallback timerCbk    = new ITimerServiceCallback.Stub() {

                                              public void getTimerValue(int value) {
                                                mHandler.sendMessage(mHandler.obtainMessage(0, value, 0));
                                                if (value == 0) {
                                                  try {
                                                    timerService.unregisterCallback(this);
                                                  } catch (RemoteException e) {
                                                    // do nothing
                                                  }
                                                }
                                              }
                                            };

  private Handler               mHandler    = new Handler() {

                                              @Override
                                              public void handleMessage(Message msg) {

                                                switch (msg.what) {
                                                case 0:
                                                  remainingMin.setText(FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(msg.arg1)[0]));
                                                  remainingSec.setText(FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(msg.arg1)[1]));

                                                  if (msg.arg1 == 0) {

                                                    // Stop the service at the timer end
                                                    stopService(new Intent(ITimerServiceMain.class.getName()));

                                                    ConfigurationContext.restoreConfig(thisInstance);

                                                    // set a static flag indicating that the service stops running (just force here)
                                                    ConfigurationContext.setTimerServiceRunning(false);

                                                    // NOTIFICATION !
                                                    final NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                    // --- Remove the notification for the running service

                                                    final Vibrator vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    MediaPlayer player = null;
                                                    if (ConfigurationContext.getPlayer() != null) {
                                                      player = ConfigurationContext.getPlayer();
                                                    } else {
                                                      String sound = ConfigurationContext.getSoundPath();
                                                      if(Constant.DEFAULT_SOUND_PATH.equals(sound) || "".equals(sound)){
                                                      player = MediaPlayer.create(thisInstance, R.raw.beep);
                                                      }else{
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

                                                    notifyStop();

                                                    try {
                                                      thisInstance.displayPopUpToStopNotification();
                                                    } catch (Exception e) {
                                                      thisInstance.setDisplayStopWarningPopUp(true);
                                                    }
                                                  } else if (msg.arg1 % 5 == 0) {
                                                    notifyUpdateTime(msg.arg1);
                                                  }

                                                  break;
                                                default:
                                                  super.handleMessage(msg);
                                                  break;
                                                }
                                              }

                                              private void notifyStop() {
                                                // This is who should be launched if the user selects our notification.
                                                // Intent i = new Intent(thisInstance, TimerRunActivity.class);
                                                // PendingIntent contentIntent = PendingIntent.getService(this.getApplicationContext(), 0, i, PendingIntent.FLAG_ONE_SHOT);

                                                // This is who should be launched if the user selects the app icon in the notification,
                                                // (in this case, we launch the same activity for both)
                                                Intent appIntent = new Intent(thisInstance, TimerRunActivity.class);
                                                appIntent.putExtra(Constant.CALLED_FROM_NOTIFICATION, true);

                                                // In this sample, we'll use the same text for the ticker and the expanded notification
                                                CharSequence text = getText(R.string.timer_service_stopped);
                                                CharSequence title = getText(R.string.timer_service_notification_title);
                                                CharSequence subtitle = getText(R.string.timer_service_notification_subtitle_stop);

                                                Notification notification = new Notification(R.drawable.clock, text, System.currentTimeMillis());

                                                // The PendingIntent to launch our activity if the user selects this notification
                                                PendingIntent contentIntent = PendingIntent.getActivity(thisInstance, 0, appIntent, 0);

                                                // Set the info for the views that show in the notification panel.
                                                notification.setLatestEventInfo(thisInstance, title, subtitle, contentIntent);

                                                NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                mNM.notify(Constant.SERVICE_TIMER, notification);

                                                ConfigurationContext.setTimerServiceRunning(true);

                                              }

                                              private void notifyUpdateTime(int duration) {

                                                String minutes = FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(duration)[0]);
                                                String seconds = FormatUtility.getTwoDigitsFormatedNumber(FormatUtility.convertSecondDurationInMinSec(duration)[1]);

                                                // This is who should be launched if the user selects our notification.
                                                // Intent i = new Intent(thisInstance, TimerRunActivity.class);
                                                // PendingIntent contentIntent = PendingIntent.getService(this.getApplicationContext(), 0, i, PendingIntent.FLAG_ONE_SHOT);

                                                // This is who should be launched if the user selects the app icon in the notification,
                                                // (in this case, we launch the same activity for both)
                                                Intent appIntent = new Intent(thisInstance, TimerRunActivity.class);
                                                appIntent.putExtra(Constant.CALLED_FROM_NOTIFICATION, true);

                                                // In this sample, we'll use the same text for the ticker and the expanded notification
                                                // CharSequence text = getText(R.string.timer_service_stopped);
                                                CharSequence title = getText(R.string.timer_service_notification_title);
                                                CharSequence subtitle = getText(R.string.timer_service_notification_subtitle_running) + " (" + minutes + ":" + seconds + ")";

                                                Notification notification = new Notification(R.drawable.clock, "", System.currentTimeMillis());

                                                // The PendingIntent to launch our activity if the user selects this notification
                                                PendingIntent contentIntent = PendingIntent.getActivity(thisInstance, 0, appIntent, 0);

                                                // Set the info for the views that show in the notification panel.
                                                notification.setLatestEventInfo(thisInstance, title, subtitle, contentIntent);

                                                NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                mNM.notify(Constant.SERVICE_TIMER, notification);

                                                ConfigurationContext.setTimerServiceRunning(true);

                                              }

                                            };

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    if (!ConfigurationContext.isTimerServiceRunning()) {
      Intent intent = new Intent(thisInstance, TimerSetActivity.class);
      intent.putExtra(Constant.CALLED_FROM_NOTIFICATION, true);
      thisInstance.startActivity(intent);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onPause() {
    super.onPause();
    try {
      unbindService(mConnection);
    } catch (Exception e) {

    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // unbindService(mConnection);
  }

}
