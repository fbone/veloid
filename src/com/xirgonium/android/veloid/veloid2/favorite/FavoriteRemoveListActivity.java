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

public class FavoriteRemoveListActivity extends Activity {


	private FavoriteRemoveListActivity	thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		thisActivity = this;
		setContentView(R.layout.aremove_signet_list);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		final CommonStationManager mgr = ConfigurationContext.getCurrentStationManager(this);
		final Vector<Station> favorites = mgr.restoreFavoriteFromDataBase();

		Button addBtn = (Button) findViewById(R.id.btnValidateRemoveFavorite);

		// add a radio button for each station found
		final RadioGroup radioGrp = (RadioGroup) findViewById(R.id.removeFavoriteStationRadioGroup);
		LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);

		int index = 1;
		for (Iterator<Station> iterator = favorites.iterator(); iterator.hasNext();) {
			Station station = (Station) iterator.next();

			RadioButton rbtn = new RadioButton(this);
			rbtn.setId(Integer.parseInt(station.getId()));
			rbtn.setText(station.getName());
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
					for (Iterator<Station> iterator = favorites.iterator(); iterator.hasNext();) {
						Station station = (Station) iterator.next();
						if (Integer.parseInt(station.getId()) == selection) {
							mgr.removeStationAsSignet(station);
							break;
						}
					}
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
		Button backBtn = (Button) findViewById(R.id.btnCancelRemoveFavorite);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				setResult(Constant.RETURN_CODE_CANCEL);
				finish();
			}
		});
		
	}

}


