package com.xirgonium.android.config;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.veloid.R;
import com.xirgonium.exception.NoInternetConnection;

/**
 * This is an example of a custom preference type. The preference counts the
 * number of clicks it has received and stores/retrieves it from the storage.
 */
public class UpdateStationListPreference extends Preference {

    private static final int            NO_INTERNET_CONNECTION_MSG                    = 1;
    private static final int            DISPLAY_GATHERED_STATIONS_MSG                 = 0;
    
    private Handler             handler      = new Handler() {
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

    static ProgressDialog       pd;
    UpdateStationListPreference thisInstance = null;

    // This is the constructor called by the inflater
    public UpdateStationListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        thisInstance = this;
        setWidgetLayoutResource(R.layout.preference_update_station_list);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    protected void onClick() {
        // Display confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.warning);
        builder.setMessage(getContext().getString(R.string.main_update_station_list));
        builder.setPositiveButton(R.string.del_station_warning_yes_btn, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dl, int which) {
                if (which == AlertDialog.BUTTON1) {
                    pd = ProgressDialog.show(getContext(), getContext().getString(R.string.main_update_progress_dialog_title), getContext().getString(R.string.main_update_progress_dialog_station_updating), true, false);

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
        new AlertDialog.Builder(getContext()).setMessage(getContext().getString(R.string.error_no_internet_connection)).setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        }).setIcon(R.drawable.warning).create().show();
    }

}
