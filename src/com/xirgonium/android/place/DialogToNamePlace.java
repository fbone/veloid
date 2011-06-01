package com.xirgonium.android.place;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xirgonium.android.veloid.R;

public class DialogToNamePlace extends AlertDialog {

	private Context				ctx;
	FavoritePlacesManager		mgr;
	FavoritePlace				fav				= null;
	boolean						nameClickedFlag	= false;

	private DialogToNamePlace	thisDialog		= null;

	public DialogToNamePlace(Context context, FavoritePlace place) {
		super(context);
		this.ctx = context;
		thisDialog = this;
		this.fav = place;
		mgr = new FavoritePlacesManager(ctx);

		fillView();
	}

	public void fillView() {

		LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mainView = vi.inflate(R.layout.place_dialog_add_content, null);

		final EditText name = (EditText) mainView.findViewById(R.id.newFavPlaceName);

		if (fav.getName() == null || fav.getName().trim().equals("")) {
			name.setText(R.string.favorite_dialog_add_name_lbl);
			name.setTextColor(Color.GRAY);
		}

		CustomTextFocusListener custoMgr = new CustomTextFocusListener(fav);
		name.setOnClickListener(custoMgr);
		name.setOnKeyListener(custoMgr);

		// Add the cancel button
		setButton(AlertDialog.BUTTON_NEUTRAL, ctx.getString(R.string.dialog_btn_ok),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String nameSet = name.getText().toString();

						if (nameSet.trim().equals("") || !nameClickedFlag) {

							(new AlertDialog.Builder(ctx)).setTitle(
									R.string.favorite_popup_error_name_title).setIcon(
									android.R.drawable.ic_dialog_alert).setMessage(
									R.string.favorite_popup_error_name).setPositiveButton(
									R.string.dialog_btn_ok, new OnClickListener() {
										public void onClick(DialogInterface dialog2, int which) {
											dialog2.dismiss();
											thisDialog.show();
										}
									}).show();
							return;
						} else {
							fav.setName(nameSet);
							Log.d(this.toString(), " STATION SAUVEGARDEE AVEC ID " + fav.getId());
							mgr.saveFavoritePlaceIntoDB(fav);
							Toast.makeText(ctx, R.string.favorite_popup_add_confirmation, Toast.LENGTH_SHORT);
						// thisDialog.dismiss();
						}
					}
				});

		// Add the cancel button
		setButton(AlertDialog.BUTTON_NEGATIVE, ctx.getString(R.string.dialog_btn_cancel),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// thisDialog.dismiss();
					}
				});

		setView(mainView);

		setTitle(R.string.favorite_dialog_add_title);
		setIcon(R.drawable.icon_small);

	}

	class CustomTextFocusListener implements android.view.View.OnClickListener,
			android.view.View.OnKeyListener {

		FavoritePlace	fav;

		public CustomTextFocusListener(FavoritePlace fav) {
			this.fav = fav;
		}

		/*
		 * public void onFocusChange(View v, boolean hasFocus) {
		 * 
		 * TextView tv = (TextView) v;
		 * 
		 * /*if (fav.getName() == null || fav.getName().trim().equals("")) {
		 * tv.setText(R.string.favorite_dialog_add_name_lbl);
		 * tv.setText(Color.GRAY); //fav.setName(tv.getText().toString()); }
		 * 
		 * 
		 * if (hasFocus) { tv.setTextColor(Color.BLACK); if (fav.getName() ==
		 * null || fav.getName().trim().equals("")) { tv.setText(""); } else {
		 * tv.setText(fav.getName()); } } else { if (fav.getName() == null ||
		 * fav.getName().trim().equals("")) {
		 * tv.setText(R.string.favorite_dialog_add_name_lbl);
		 * tv.setText(Color.GRAY); } fav.setName(tv.getText().toString()); }
		 * 
		 * }
		 */

		public void onClick(View v) {

			TextView tv = (TextView) v;
			tv.setTextColor(Color.BLACK);
			tv.setText("");
			nameClickedFlag = true;
		}

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (!nameClickedFlag) {
				TextView tv = (TextView) v;
				tv.setTextColor(Color.BLACK);
				tv.setText("");
				nameClickedFlag=true;
			}
			return false;
		}
	}
}
