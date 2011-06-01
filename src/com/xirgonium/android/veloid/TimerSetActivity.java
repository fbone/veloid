package com.xirgonium.android.veloid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.view.FilterAdapter;

public class TimerSetActivity extends Activity {
	CheckBox	activateFilterCB;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.timer_set);
		
		
		TextView custoTitleBar = (TextView) findViewById(R.id.timer_set_title);
		// font
		Typeface customForTimer = Typeface.createFromAsset(getAssets(), Constant.TITLE_FONT);
		custoTitleBar.setTypeface(customForTimer);

		// if the service is already running then redirect to the activity showing the counter value
		if (ConfigurationContext.isTimerServiceRunning()) {
			Intent i = new Intent(TimerSetActivity.this, TimerRunActivity.class);
			
			Bundle b = new Bundle();

			b.putInt(Constant.FILTER_MIN_AVAILABLE_BIKES, ConfigurationContext.getFilterMinAvailableBike());
			b.putInt(Constant.FILTER_MIN_FREE_SLOTS, ConfigurationContext.getFilterMinSlot());
			
			b.putBoolean(Constant.TIMER_GELOCALIZE_OPT_BUNDLE_KEY, ConfigurationContext.isGeolocAtTimerEnd());
			
			i.putExtras(b);
			
			startActivityForResult(i, Constant.ACTIVITY_TIMER_RUN_START);
		}

		// define the gradient for the filter container
		// Define a gradient for the list
		GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { getResources().getColor(R.color.list_top_color), getResources().getColor(R.color.list_bottom_color) });
		LinearLayout container = (LinearLayout) findViewById(R.id.timer_set_main);
		container.setBackgroundDrawable(grad);

		// --- Define the content of filter spinners

		activateFilterCB = (CheckBox) findViewById(R.id.timer_set_activate_filter_cb);
		activateFilterCB.setChecked((ConfigurationContext.getFilterMinSlot() != 0) || (ConfigurationContext.getFilterMinAvailableBike() != 0));

		Spinner valMinSLot = (Spinner) findViewById(R.id.timer_set_filter_min_slot_val);
		FilterAdapter adms = new FilterAdapter((Context) this, getResources().getStringArray(R.array.filter_free_slots_values));

		adms.setEnabled(false);
		valMinSLot.setAdapter(adms);
		if (ConfigurationContext.getFilterMinSlot() != 0) {
			valMinSLot.setSelection(ConfigurationContext.getFilterMinSlot());
			valMinSLot.setClickable(true);
			adms.setEnabled(true);

		} else {
			//valMinSLot.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown_neither));

		}

		Spinner valMinBikes = (Spinner) findViewById(R.id.timer_set_filter_min_avb_val);
		FilterAdapter admab = new FilterAdapter((Context) this, getResources().getStringArray(R.array.filter_available_bikes_values));
		admab.setEnabled(false);
		valMinBikes.setAdapter(admab);
		if (ConfigurationContext.getFilterMinAvailableBike() != 0) {
			valMinBikes.setSelection(ConfigurationContext.getFilterMinAvailableBike());
			valMinBikes.setClickable(true);
			admab.setEnabled(true);
			//valMinBikes.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown_right_only));

		} else {

			//valMinBikes.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown_neither));

		}

		// in order to init the interface
		TextView minutes = (TextView) findViewById(R.id.timer_set_timer_val);

		minutes.setWidth(100);
		minutes.setTypeface(customForTimer);

		// Checkbox to geolocate
		CheckBox activateGeolocCB = (CheckBox) findViewById(R.id.timer_set_geolocalize_at_end);
		activateGeolocCB.setChecked(ConfigurationContext.isGeolocAtTimerEnd());

		// --- Buttons to add or remove minutes to the timer
		ImageButton addMinuteBtn = (ImageButton) findViewById(R.id.timer_set_timer_up_btn);
		addMinuteBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View clicked) {
				ConfigurationContext.setTimerMinutes(ConfigurationContext.getTimerMinutes() + 1);
				// need to set because of inner class
				TextView minutes = (TextView) findViewById(R.id.timer_set_timer_val);
				minutes.setText(FormatUtility.getTwoDigitsFormatedNumber((int) ConfigurationContext.getTimerMinutes()));
			}
		});

		ImageButton remMinuteBtn = (ImageButton) findViewById(R.id.timer_set_timer_less_btn);
		remMinuteBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View clicked) {
				if (ConfigurationContext.getTimerMinutes() > 0) {
					ConfigurationContext.setTimerMinutes(ConfigurationContext.getTimerMinutes() - 1);
				}
				// need to set because of inner class
				TextView minutes = (TextView) findViewById(R.id.timer_set_timer_val);
				minutes.setText(FormatUtility.getTwoDigitsFormatedNumber((int) ConfigurationContext.getTimerMinutes()));
			}
		});

		ImageButton validBtn = (ImageButton) findViewById(R.id.timer_set_btn_valid);
		// --- Add the pattern in a bundle and open step 2
		validBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View clicked) {

				Intent i = new Intent(TimerSetActivity.this, TimerRunActivity.class);

				Bundle b = new Bundle();

				// add the timer minutes
				b.putInt(Constant.TIMER_MINUTE_BUNDLE_KEY, ConfigurationContext.getTimerMinutes());

				activateFilterCB = (CheckBox) findViewById(R.id.timer_set_activate_filter_cb);
				if (activateFilterCB.isChecked()) {
					Spinner valMinSLot = (Spinner) findViewById(R.id.timer_set_filter_min_slot_val);
					Spinner valMinBikes = (Spinner) findViewById(R.id.timer_set_filter_min_avb_val);

					if (valMinSLot.getSelectedItemPosition() != -1) {
						b.putInt(Constant.FILTER_MIN_FREE_SLOTS, valMinSLot.getSelectedItemPosition());
						ConfigurationContext.setFilterMinSlot(valMinSLot.getSelectedItemPosition());
					}

					if (valMinBikes.getSelectedItemPosition() != -1) {
						b.putInt(Constant.FILTER_MIN_AVAILABLE_BIKES, valMinBikes.getSelectedItemPosition());
						ConfigurationContext.setFilterMinAvailableBike(valMinBikes.getSelectedItemPosition());

					}
				}else{
				    b.putInt(Constant.FILTER_MIN_FREE_SLOTS, 0);
                    ConfigurationContext.setFilterMinSlot(0);
                    
                    b.putInt(Constant.FILTER_MIN_AVAILABLE_BIKES, 0);
                    ConfigurationContext.setFilterMinAvailableBike(0);
				}

				CheckBox activateGeolocCB = (CheckBox) findViewById(R.id.timer_set_geolocalize_at_end);
				b.putBoolean(Constant.TIMER_GELOCALIZE_OPT_BUNDLE_KEY, activateGeolocCB.isChecked());
				ConfigurationContext.setGeolocAtTimerEnd(activateGeolocCB.isChecked());

				i.putExtras(b);
				startActivityForResult(i, Constant.ACTIVITY_TIMER_RUN_START);
			}
		});

		// cancel button, do nothing
		ImageButton cancelBtn = (ImageButton) findViewById(R.id.timer_set_btn_cancel);
		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
			  if (getIntent().getBooleanExtra(Constant.CALLED_FROM_NOTIFICATION, false)){
			    Intent i = new Intent(TimerSetActivity.this, Veloid.class);
			    startActivity(i);
			  }else{
			  
				TimerSetActivity.this.setResult(Constant.RETURN_CODE_CANCEL, null);
				TimerSetActivity.this.finish();
			  }
			}
		});

		// check box : grey when not selected

		activateFilterCB.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Grey command
				Spinner valMinSLot = (Spinner) findViewById(R.id.timer_set_filter_min_slot_val);
				Spinner valMinBikes = (Spinner) findViewById(R.id.timer_set_filter_min_avb_val);

				FilterAdapter admb = (FilterAdapter) valMinBikes.getAdapter();
				FilterAdapter adfs = (FilterAdapter) valMinSLot.getAdapter();

				if (!activateFilterCB.isChecked()) {
					valMinSLot.setClickable(false);
					adfs.setEnabled(false);
					valMinSLot.setAdapter(adfs);
					//valMinSLot.setSelectorSkin(getResources().getDrawable(android.R.drawable.alert_dark_frame));

					valMinBikes.setClickable(false);
					admb.setEnabled(false);
					valMinBikes.setAdapter(admb);
					//valMinBikes.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown_neither));

				} else {
					valMinSLot.setClickable(true);
					adfs.setEnabled(true);
					valMinSLot.setAdapter(adfs);
					//valMinSLot.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown_right_only));

					valMinBikes.setClickable(true);
					admb.setEnabled(true);
					valMinBikes.setAdapter(admb);
					//valMinBikes.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown_right_only));
				}
			}
		});

		// init the interface with initial value
		minutes.setText(FormatUtility.getTwoDigitsFormatedNumber((int) ConfigurationContext.getTimerMinutes()));

	}

	@Override
	protected void onPause() {
		super.onPause();
		ConfigurationContext.saveConfig(this);
	}
	

}
