package com.xirgonium.android.veloid.veloid2.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.veloid.R;

public class SearchForBikesOrSlotsActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.abikes_or_slots);

		final TextView searchedUnits = (TextView) findViewById(R.id.searchUnits);

		searchedUnits.setText(FormatUtility.getTwoDigitsFormatedNumber(ConfigurationContext.getLastSearchedUnits()));

		// --- Buttons to add or remove minutes to the timer
		ImageButton addUnitsBtn = (ImageButton) findViewById(R.id.searchMoreUnits);
		addUnitsBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View clicked) {
				ConfigurationContext.setLastSearchedUnits(ConfigurationContext.getLastSearchedUnits() + 1);
				// need to set because of inner class
				searchedUnits.setText(FormatUtility.getTwoDigitsFormatedNumber(ConfigurationContext.getLastSearchedUnits()));
			}
		});

		ImageButton removeUnitsBtn = (ImageButton) findViewById(R.id.searchLessUnits);
		removeUnitsBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View clicked) {
				ConfigurationContext.setLastSearchedUnits(ConfigurationContext.getLastSearchedUnits() - 1);
				// need to set because of inner class
				searchedUnits.setText(FormatUtility.getTwoDigitsFormatedNumber(ConfigurationContext.getLastSearchedUnits()));
			}
		});

		Button bikes = (Button) findViewById(R.id.btnSearchBikes);
		bikes.setOnClickListener(this);
		Button slots = (Button) findViewById(R.id.btnSearchSlots);
		slots.setOnClickListener(this);
		
//		private void setSpecificActionController() {
//		if (mgr.canExecuteASpecificAction()) {
//			TextView textSA = (TextView) findViewById(R.id.special_action_text);
//			textSA.setText(mgr.getSpecialActionText(this));
//			textSA.setOnClickListener(thisVeloid);
//			textSA.setVisibility(View.VISIBLE);
//		} else {
//			TextView textSA = (TextView) findViewById(R.id.special_action_text);
//			textSA.setText("");
//			textSA.setVisibility(View.GONE);
//		}
//	}
	
//	mgr.specialAction(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		ConfigurationContext.saveConfig(this);
	}

	public void onClick(View v) {
		Intent i = new Intent("changetab");
		i.putExtra("tab", "map");

		TextView searchedUnits = (TextView) findViewById(R.id.searchUnits);
		int filteredvalue = Integer.parseInt(searchedUnits.getText().toString());
		
		//Bundle b = new Bundle();

		if (v.getId() == R.id.btnSearchBikes) {
			ConfigurationContext.setLastSearchedType(Constant.SEARCH_TYPE_BIKES);
			ConfigurationContext.setLastSearchedUnits(filteredvalue);
			
			//b.putInt(Constant.FILTER_MIN_AVAILABLE_BIKES, filteredvalue);
		}else if (v.getId() == R.id.btnSearchSlots) {
			//b.putInt(Constant.FILTER_MIN_FREE_SLOTS, filteredvalue);

			ConfigurationContext.setLastSearchedType(Constant.SEARCH_TYPE_BIKES);
			ConfigurationContext.setLastSearchedUnits(filteredvalue);
		}
		//i.putExtras(b);

		sendBroadcast(i);
	}

}
