package com.xirgonium.android.veloid.veloid2.favorite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.R;

public class FavoriteAddEnterInfoActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.anew_signet);
		
//		CommonStationManager mgr = ConfigurationContext.getCurrentStationManager(this);
//
//		TextView tv = (TextView)findViewById(R.id.network_name);
//		tv.setText(mgr.getCommonName());
		
		Button btnSearch = (Button)findViewById(R.id.btnSearchFavorite);
		btnSearch.setOnClickListener(this);
		Button btnCancelSearch = (Button)findViewById(R.id.btnCancelSearchFavorite);
		btnCancelSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btnSearchFavorite) {
			String searchPattern = ((EditText) findViewById(R.id.newFavoriteSearchPattern)).getText().toString();
			if (searchPattern != null) {
				Intent i = new Intent(this, FavoriteAddListActivity.class);
				Bundle b = new Bundle();
				b.putString(Constant.NEW_STATION_IN_BUNDLE_SEARCH_PATTERN_KEY, searchPattern);
				i.putExtras(b);
				startActivityForResult(i, Constant.ACTIVITY_NEW_STATION_S2_DATABASE_START);
			}

		}else{
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Constant.ACTIVITY_NEW_STATION_S2_DATABASE_START && resultCode == Constant.RETURN_CODE_VALID){
			finish();
		}
	}
}
