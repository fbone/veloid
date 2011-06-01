package com.xirgonium.android.veloid;

import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.xirgonium.android.util.Constant;

public class DeleteStationActivity extends Activity implements OnCheckedChangeListener {

	int			selectedStation;
	AlertDialog	al;
	DeleteStationActivity thisActivity = null;

	// public Spinner spnStations = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {		
		super.onCreate(icicle);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		thisActivity = this;
        
		// --- Define the view
		setContentView(R.layout.delete_station_form);
		ScrollView scroll = (ScrollView) findViewById(R.id.del_station_s2_scroll);
		GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { getResources().getColor(R.color.list_top_color), getResources().getColor(R.color.list_bottom_color) });
        scroll.setBackgroundDrawable(grad);
        
       
        TextView custoTitleBar = (TextView) findViewById(R.id.del_fav_title);
        Typeface customForTimer = Typeface.createFromAsset(getAssets(), Constant.TITLE_FONT);
        custoTitleBar.setTypeface(customForTimer);
        
        
		// Fill the data to build the list

		Bundle fromMainBundle = this.getIntent().getExtras();

		Set<String> keys = fromMainBundle.keySet();

		RadioGroup radioGrp = (RadioGroup) findViewById(R.id.del_station_s2_rgrp_station);
		LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);

		int index = 1;
		for (String key : keys) {
			RadioButton rbtn = new RadioButton(this);
			rbtn.setId(Integer.parseInt(key));
			rbtn.setText(fromMainBundle.getString(key));
			rbtn.setTextColor(index % 2 == 0 ? getResources().getColor(R.color.item_even_text_color) : getResources().getColor(R.color.item_odd_text_color));

			rbtn.setBackgroundColor(getResources().getColor(index++ % 2 == 0 ? R.color.item_odd_background : R.color.item_even_background));

			radioGrp.addView(rbtn, 0, layoutParams);

		}

		radioGrp.setOnCheckedChangeListener(this);

		// Find the button defined in the form del station
		ImageButton delBtn = (ImageButton) findViewById(R.id.del_station_btn_valid);
		

		delBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
			    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
			    builder.setIcon(R.drawable.warning);
			    builder.setMessage(getString(R.string.del_station_warning_message));
			    builder.setPositiveButton(R.string.del_station_warning_yes_btn, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dl, int which) {
                        if (which == AlertDialog.BUTTON1) {

                            Intent intent = new Intent();
                            
                            intent.putExtra(Constant.DEL_STATION_IN_BUNDLE_ID_KEY, (String.valueOf(selectedStation)));
                            
                            // Set the result
                            DeleteStationActivity.this.setResult(Constant.RETURN_CODE_VALID, intent);

                            // Close this Activity
                            DeleteStationActivity.this.finish();
                        }
                    }
                });
			    builder.setNegativeButton(R.string.del_station_warning_no_btn, null);
			    
			    builder.create().show();
			    
			}
		});

		// Find the button defined in the form add station
		ImageButton cancelBtn = (ImageButton) findViewById(R.id.del_station_btn_cancel);
		cancelBtn.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {

				DeleteStationActivity.this.setResult(Constant.RETURN_CODE_CANCEL, null);

				// Close this Activity
				DeleteStationActivity.this.finish();
			}
		});
	}

	public void onCheckedChanged(RadioGroup arg0, int selected) {
		selectedStation = selected;
	}
}
