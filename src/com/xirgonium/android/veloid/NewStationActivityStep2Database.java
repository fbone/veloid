package com.xirgonium.android.veloid;

import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
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

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;

public class NewStationActivityStep2Database extends Activity implements OnCheckedChangeListener, OnClickListener {

    int selectedStation;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // --- Define the view
        setContentView(R.layout.new_station_step2_db);

        //--- Set the gradient
        ScrollView scroll = (ScrollView) findViewById(R.id.new_station_s2_scroll);
        GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { getResources().getColor(R.color.list_top_color), getResources().getColor(R.color.list_bottom_color) });
        scroll.setBackgroundDrawable(grad);

        TextView custoTitleBar = (TextView) findViewById(R.id.add_fav_title);
        // font
        Typeface customForTimer = Typeface.createFromAsset(getAssets(), Constant.TITLE_FONT);
        custoTitleBar.setTypeface(customForTimer);
        
        // --- Get given information
        Bundle b = this.getIntent().getExtras();
        String pattern = b.getString(Constant.NEW_STATION_IN_BUNDLE_SEARCH_PATTERN_KEY);

        
        // Fill with stations found from pattern
        CommonStationManager mgr = ConfigurationContext.getCurrentStationManager(this);
        Vector<Station> matchingStation = mgr.foundStationFromParameters(pattern);

        ImageButton addBtn = (ImageButton) findViewById(R.id.new_station_s2_btn_valid);

        if (matchingStation.size() == 0) {
            //indicate that there is no station found
            TextView noStatioFOundIndicator = new TextView(this);
            noStatioFOundIndicator.setText(getString(R.string.new_station_s2_selection_no_station_found_label)
                + pattern);
            LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                          LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout main = (LinearLayout) findViewById(R.id.new_station_s2_main_layout);
            main.addView(noStatioFOundIndicator, 0, lay);
            //set add button not clickable
            addBtn.setClickable(false);
        }

        // add a radio button for each station found
        RadioGroup radioGrp = (RadioGroup) findViewById(R.id.new_station_s2_rgrp_station);
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT,
                                                                             RadioGroup.LayoutParams.WRAP_CONTENT);
        
        
        int index = 1;
        for (Iterator<Station> iterator = matchingStation.iterator(); iterator.hasNext();) {
            Station station = (Station) iterator.next();
            
            //View rbtnbg = (View)vi.inflate(R.layout.new_station_step2_db_item, null, null);// new RadioButtonBackgrounded(this);
            RadioButton rbtn = new RadioButton(this);//(RadioButton)rbtnbg.findViewById(R.id.new_station_s2_radio_button);
            rbtn.setId(Integer.parseInt(station.getId()));
            rbtn.setText(station.getName());
            rbtn.setTextColor(index % 2 == 0 ? getResources().getColor(R.color.item_even_text_color)
                    : getResources().getColor(R.color.item_odd_text_color));
           
            rbtn.setBackgroundColor(getResources().getColor(index++ % 2 == 0 ?R.color.item_odd_background:R.color.item_even_background));
            
            radioGrp.addView(rbtn, 0, layoutParams);
        }

        radioGrp.setOnCheckedChangeListener(this);

        addBtn.setOnClickListener(this);

        //Close the windows without returning
        ImageButton backBtn = (ImageButton) findViewById(R.id.new_station_s2_btn_back);
        backBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                NewStationActivityStep2Database.this.setResult(Constant.RETURN_CODE_CANCEL, null);
                NewStationActivityStep2Database.this.finish();
            }
        });
    }

    public void onCheckedChanged(RadioGroup arg0, int selected) {
        this.selectedStation = selected;

    }

    /**
     * Called only in the OK button.
     * Add the selected station ID to the result bundle
     */
    public void onClick(View arg0) {
        Intent i = new Intent();
        i.putExtra(Constant.NEW_STATION_IN_BUNDLE_ID_KEY, selectedStation);
        NewStationActivityStep2Database.this.setResult(Constant.RETURN_CODE_VALID, i);
        NewStationActivityStep2Database.this.finish();
    }

}
