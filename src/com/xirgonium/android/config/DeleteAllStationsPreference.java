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

/**
 * This is an example of a custom preference type. The preference counts the
 * number of clicks it has received and stores/retrieves it from the storage.
 */
public class DeleteAllStationsPreference extends Preference {

    private static final int    DISPLAY_CONFIRMATION = 0;

    private Handler             handler              = new Handler() {
                                                         @Override
                                                         public void handleMessage(Message msg) {
                                                             switch (msg.what) {

                                                                 case DISPLAY_CONFIRMATION:
                                                                     pd.dismiss();
                                                                     thisInstance.validateDelete();
                                                                     break;
                                                             }

                                                         }
                                                     };

    static ProgressDialog       pd;
    DeleteAllStationsPreference thisInstance         = null;

    // This is the constructor called by the inflater
    public DeleteAllStationsPreference(Context context, AttributeSet attrs) {
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

        CommonStationManager mgr = ConfigurationContext.getCurrentStationManager(getContext());

        // Display confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.warning);
        builder.setMessage(getContext().getString(R.string.pref_delete_all_station_confirmation) + " " + mgr.getCommonName() + " ? ");
        builder.setPositiveButton(R.string.del_station_warning_yes_btn, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dl, int which) {
                if (which == AlertDialog.BUTTON1) {
                    pd = ProgressDialog.show(getContext(), getContext().getString(R.string.main_update_progress_dialog_title), getContext().getString(R.string.main_update_progress_dialog_station_updating), true, false);

                    Thread thread = new Thread() {
                        public void run() {

                            ConfigurationContext.getCurrentStationManager(getContext()).clearListOfStationFromDatabase();

                            handler.sendEmptyMessage(DISPLAY_CONFIRMATION);
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

    public void validateDelete() {
        CommonStationManager mgr = ConfigurationContext.getCurrentStationManager(getContext());

        StringBuffer buf = new StringBuffer();
        buf.append(getContext().getString(R.string.pref_delete_all_station_confirmed));
        buf.append(" ");
        buf.append(mgr.getCommonName());

        new AlertDialog.Builder(getContext()).setMessage(buf.toString()).setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        }).create().show();
    }

}
