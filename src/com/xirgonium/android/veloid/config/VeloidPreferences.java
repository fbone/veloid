package com.xirgonium.android.veloid.config;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.Window;

import com.xirgonium.android.veloid.R;

public class VeloidPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
       
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		this.finish();
//		return true;
//	}
    


}
