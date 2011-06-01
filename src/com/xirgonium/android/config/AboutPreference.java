package com.xirgonium.android.config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.R;

/**
 * This is an example of a custom preference type. The preference counts the
 * number of clicks it has received and stores/retrieves it from the storage.
 */
public class AboutPreference extends Preference {

    // This is the constructor called by the inflater
    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.preference_update_station_list);
        StringBuffer msg = new StringBuffer(getContext().getString(R.string.pref_about_sub_author));
        msg.append("\n");
        msg.append(getContext().getString(R.string.pref_about_sub_version));
        msg.append(" ");
        msg.append(getVersionNumber());
        msg.append(" - ");
        msg.append(getContext().getString(R.string.pref_about_sub_website));
        setSummary(msg.toString());        
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);       
    }

    private String getVersionNumber() {
        String version = "?";
        try {
            PackageInfo pi = getContext().getPackageManager().getPackageInfo(Constant.PACKAGE_NAME, 1);
            version = pi.versionName; 
        } catch (PackageManager.NameNotFoundException e) {
           // Log.e("ABOUT", "Package name not found", e);
        }
        ;
        return version;
    }
}
