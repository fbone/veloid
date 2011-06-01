package com.xirgonium.android.veloid.veloid2.favorite;

import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.R;

public class FavoriteAddListActivity extends Activity {

	private FavoriteAddListActivity	thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		thisActivity = this;

		setContentView(R.layout.anew_signet_list);
		
		

		

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// --- Get given information
		Bundle b = this.getIntent().getExtras();
		String pattern = b.getString(Constant.NEW_STATION_IN_BUNDLE_SEARCH_PATTERN_KEY);

		// Fill with stations found from pattern
		final CommonStationManager mgr = ConfigurationContext.getCurrentStationManager(this);
		
//		TextView tv = (TextView)findViewById(R.id.network_name);
//		tv.setText(mgr.getCommonName());
		
		final Vector<Station> matchingStation = mgr.foundStationFromParameters(pattern);

		Button addBtn = (Button) findViewById(R.id.btnValidateSelectedStation);

		if (matchingStation.size() == 0) {
			// indicate that there is no station found
			TextView noStatioFOundIndicator = new TextView(this);
			noStatioFOundIndicator.setText(getString(R.string.new_station_s2_selection_no_station_found_label) + pattern);
			LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout main = (LinearLayout) findViewById(R.id.errorMessage);
			main.addView(noStatioFOundIndicator, 0, lay);
			// set add button not clickable
			addBtn.setClickable(false);
		}

		// add a radio button for each station found
		final RadioGroup radioGrp = (RadioGroup) findViewById(R.id.newFavoriteMatchingStationRadioGroup);
		LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);

		int index = 1;
		for (Iterator<Station> iterator = matchingStation.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();

			// View rbtnbg =
			// (View)vi.inflate(R.layout.new_station_step2_db_item, null,
			// null);// new RadioButtonBackgrounded(this);
			RadioButton rbtn = new RadioButton(this);// (RadioButton)rbtnbg.findViewById(R.id.new_station_s2_radio_button);
			rbtn.setId(Integer.parseInt(station.getId()));
			rbtn.setText(station.getName());
			rbtn.setTextSize(20);
			rbtn.setTextColor(index % 2 == 0 ? getResources().getColor(R.color.favorite_text_color) : getResources().getColor(R.color.item_odd_text_color));

			rbtn.setBackgroundColor(getResources().getColor(index++ % 2 == 0 ? R.color.green_one : R.color.green_two));

			radioGrp.addView(rbtn, 0, layoutParams);
		}

		addBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				// get the selection id
				int selection = radioGrp.getCheckedRadioButtonId();

				if (selection != -1) {
					// Set the station as favorite
					for (Iterator<Station> iterator = matchingStation.iterator(); iterator.hasNext();) {
						Station station = (Station) iterator.next();
						if (Integer.parseInt(station.getId()) == selection) {
							mgr.setStationAsSignet(station);
							break;
						}
					}

					setResult(Constant.RETURN_CODE_VALID);
					finish();
				} else {
					new AlertDialog.Builder(thisActivity).setMessage(thisActivity.getString(R.string.new_favorite_error_no_selection)).setPositiveButton(R.string.dialog_btn_ok,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int whichButton) {
									dialog.dismiss();
								}
							}).create().show();
				}

			}
		});

		// Close the windows without returning
		Button backBtn = (Button) findViewById(R.id.btnCancelSearchFavorite);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				setResult(Constant.RETURN_CODE_CANCEL);
				finish();
			}
		});
	}

}
