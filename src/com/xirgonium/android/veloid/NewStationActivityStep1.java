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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.view.FilterAdapter;

public class NewStationActivityStep1 extends Activity {

    CheckBox activateFilterCB;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_station_step1);
        
        TextView custoTitleBar = (TextView) findViewById(R.id.add_fav_title);
        // font
        Typeface customForTimer = Typeface.createFromAsset(getAssets(), Constant.TITLE_FONT);
        custoTitleBar.setTypeface(customForTimer);

        // define the gradient for the filter container
        // Define a gradient for the list
        GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { getResources().getColor(R.color.list_top_color), getResources().getColor(R.color.list_bottom_color) });
        LinearLayout filterContainer = (LinearLayout) findViewById(R.id.new_station_s1_filter_container);
        filterContainer.setBackgroundDrawable(grad);

        // --- Reset the last address entered
        ((EditText) findViewById(R.id.new_station_s1_part_address_input)).setText(ConfigurationContext.getLastAddr());

        // --- Define the content of filter spinners

        activateFilterCB = (CheckBox) findViewById(R.id.new_station_s1_activate_filter_cb);
        activateFilterCB.setChecked((ConfigurationContext.getFilterMinSlot() != 0) || (ConfigurationContext.getFilterMinAvailableBike() != 0));

        Spinner valMinSLot = (Spinner) findViewById(R.id.new_station_s1_filter_min_slot_val);
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

        Spinner valMinBikes = (Spinner) findViewById(R.id.new_station_s1_filter_min_avb_val);
        FilterAdapter admab = new FilterAdapter((Context) this, getResources().getStringArray(R.array.filter_available_bikes_values));
        admab.setEnabled(false);
        valMinBikes.setAdapter(admab);
        if (ConfigurationContext.getFilterMinAvailableBike() != 0) {
            valMinBikes.setSelection(ConfigurationContext.getFilterMinAvailableBike());
            valMinBikes.setClickable(true);
            admab.setEnabled(true);           
        } else {
        }

        //--- Spinner for cities
        // Create the array adapter for the spinner
        try {
            ArrayAdapter<String> adapterCities = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ConfigurationContext.getCurrentStationManager(this).getCities());
            Spinner citiesSpn = (Spinner) findViewById(R.id.new_station_s1_part_address_city);
            citiesSpn.setAdapter(adapterCities);
        } catch (NullPointerException e) {
        }

        ImageButton addBtn = (ImageButton) findViewById(R.id.new_station_s1_btn_valid);
        // --- Add the pattern in a bundle and open step 2
        addBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                String pattern = ((EditText) findViewById(R.id.new_station_s1_part_info_input)).getText().toString();
                String address = ((EditText) findViewById(R.id.new_station_s1_part_address_input)).getText().toString();

                ConfigurationContext.setLastAddr(address);

                //complete the address with city
                address += ", ";
                address += (String) ((Spinner) findViewById(R.id.new_station_s1_part_address_city)).getSelectedItem();

                if (!pattern.equals("")) {
                    Intent i = new Intent(NewStationActivityStep1.this, NewStationActivityStep2Database.class);
                    Bundle b = new Bundle();
                    b.putString(Constant.NEW_STATION_IN_BUNDLE_SEARCH_PATTERN_KEY, pattern);
                    i.putExtras(b);
                    startActivityForResult(i, Constant.ACTIVITY_NEW_STATION_S2_DATABASE_START);
                } else if (!address.equals("")) {
                    Intent i = new Intent(NewStationActivityStep1.this, StationNearActivity.class);
                    Bundle b = new Bundle();
                    b.putString(Constant.NEW_STATION_IN_BUNDLE_SEARCH_ADDRESS_KEY, address);

                    activateFilterCB = (CheckBox) findViewById(R.id.new_station_s1_activate_filter_cb);
                    if (activateFilterCB.isChecked()) {
                        Spinner valMinSLot = (Spinner) findViewById(R.id.new_station_s1_filter_min_slot_val);
                        Spinner valMinBikes = (Spinner) findViewById(R.id.new_station_s1_filter_min_avb_val);

                        if (valMinSLot.getSelectedItemPosition() != -1) {
                            b.putInt(Constant.FILTER_MIN_FREE_SLOTS, valMinSLot.getSelectedItemPosition());
                            ConfigurationContext.setFilterMinSlot(valMinSLot.getSelectedItemPosition());
                        }

                        if (valMinBikes.getSelectedItemPosition() != -1) {
                            b.putInt(Constant.FILTER_MIN_AVAILABLE_BIKES, valMinBikes.getSelectedItemPosition());
                            ConfigurationContext.setFilterMinAvailableBike(valMinBikes.getSelectedItemPosition());
                        }
                    } else {
                        ConfigurationContext.setFilterMinSlot(0);
                        ConfigurationContext.setFilterMinAvailableBike(0);
                    }
                    i.putExtras(b);
                    startActivityForResult(i, Constant.ACTIVITY_NEW_STATION_S2_GOOGLEMAPS_START);
                }
            }
        });

        // cancel button, do nothin
        ImageButton cancelBtn = (ImageButton) findViewById(R.id.new_station_s1_btn_cancel);
        cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                String address = ((EditText) findViewById(R.id.new_station_s1_part_address_input)).getText().toString();             
                ConfigurationContext.setLastAddr(address);

                NewStationActivityStep1.this.setResult(Constant.RETURN_CODE_CANCEL, null);
                NewStationActivityStep1.this.finish();
            }
        });

        // check box : grey when not selected
        activateFilterCB = (CheckBox) findViewById(R.id.new_station_s1_activate_filter_cb);
        activateFilterCB.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                // Grey command
                Spinner valMinSLot = (Spinner) findViewById(R.id.new_station_s1_filter_min_slot_val);
                Spinner valMinBikes = (Spinner) findViewById(R.id.new_station_s1_filter_min_avb_val);

                FilterAdapter admb = (FilterAdapter) valMinBikes.getAdapter();
                FilterAdapter adfs = (FilterAdapter) valMinSLot.getAdapter();

                if (!activateFilterCB.isChecked()) {
                    valMinSLot.setClickable(false);
                    adfs.setEnabled(false);
                    valMinSLot.setAdapter(adfs);
                    //valMinSLot.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown_neither));

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
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        // ---- When the Step2 ends
        if (requestCode == Constant.ACTIVITY_NEW_STATION_S2_DATABASE_START) {
            if (resultCode == Constant.RETURN_CODE_VALID) {
                NewStationActivityStep1.this.setResult(Constant.RETURN_CODE_VALID, i);
                NewStationActivityStep1.this.finish();
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        ConfigurationContext.saveConfig(this);
    }
}
