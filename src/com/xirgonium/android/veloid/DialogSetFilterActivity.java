package com.xirgonium.android.veloid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.MapUtility;
import com.xirgonium.android.view.FilterAdapter;

public class DialogSetFilterActivity extends Activity {

    static DialogSetFilterActivity thisDialog = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        thisDialog = this;
        setContentView(R.layout.dialog_set_filter);

        GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
                getResources().getColor(R.color.list_top_color),
                getResources().getColor(R.color.list_bottom_color) });
        LinearLayout filterContainer = (LinearLayout) findViewById(R.id.dialog_set_filter_main);
        filterContainer.setBackgroundDrawable(grad);

        Spinner valMinSLot = (Spinner) findViewById(R.id.dialog_set_filter_filter_min_slot_val);
        FilterAdapter adms = new FilterAdapter((Context) this,
                                               getResources().getStringArray(R.array.filter_free_slots_values));
        //valMinSLot.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown));
        valMinSLot.setAdapter(adms);
        if(ConfigurationContext.getFilterMinSlot()>0){
            valMinSLot.setSelection(ConfigurationContext.getFilterMinSlot());
        }
        
        Spinner valMinBikes = (Spinner) findViewById(R.id.dialog_set_filter_filter_min_avb_val);
        FilterAdapter admab = new FilterAdapter((Context) this,
                                                getResources().getStringArray(R.array.filter_available_bikes_values));
        //valMinBikes.setSelectorSkin(getResources().getDrawable(android.R.drawable.btn_dropdown_neither));
        valMinBikes.setAdapter(admab);
        if(ConfigurationContext.getFilterMinAvailableBike()>0){
            valMinBikes.setSelection(ConfigurationContext.getFilterMinAvailableBike());
        }
        

        ImageButton validBtn = (ImageButton) findViewById(R.id.dialog_set_filter_btn_valid);
        validBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Spinner valMinSLot = (Spinner) findViewById(R.id.dialog_set_filter_filter_min_slot_val);
                Spinner valMinBikes = (Spinner) findViewById(R.id.dialog_set_filter_filter_min_avb_val);

                Intent i = new Intent(DialogSetFilterActivity.this, StationNearActivity.class);
                Bundle b = new Bundle();

                if (valMinSLot.getSelectedItemPosition() != -1) {
                    b.putInt(Constant.FILTER_MIN_FREE_SLOTS, valMinSLot.getSelectedItemPosition());
                    ConfigurationContext.setFilterMinSlot(valMinSLot.getSelectedItemPosition());
                }

                if (valMinBikes.getSelectedItemPosition() != -1) {
                    b.putInt(Constant.FILTER_MIN_AVAILABLE_BIKES, valMinBikes.getSelectedItemPosition());
                    ConfigurationContext.setFilterMinAvailableBike(valMinBikes.getSelectedItemPosition());
                }                
                
                // geolocalisation and open map
                if (MapUtility.getLocationProviderToUse(thisDialog) == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisDialog);
                    builder.setIcon( R.drawable.warning);
                    builder.setMessage(R.string.nearest_station_location_providers_broken);
                    builder.setPositiveButton("OK",null);
                    builder.create().show();
//                    showAlert(null,
//                              R.drawable.warning,
//                              getString(R.string.nearest_station_location_providers_broken),
//                              "OK",
//                              null,
//                              null,
//                              null,
//                              null,
//                              null,
//                              true,
//                              null);
                } else {
                    b.putBoolean(Constant.MAP_FIND_GEOLOC_KEY, true);
                    i.putExtras(b);
                    ConfigurationContext.saveConfig(thisDialog);
                    thisDialog.startActivityForResult(i, Constant.ACTIVITY_NEW_STATION_S2_GOOGLEMAPS_START);
                }
            }
        });
        
        ImageButton cancelBtn = (ImageButton) findViewById(R.id.dialog_set_filter_btn_cancel);
        cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                thisDialog.finish();
            }
        });
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        this.finish();
    }
}
