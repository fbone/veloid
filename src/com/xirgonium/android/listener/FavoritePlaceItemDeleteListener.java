package com.xirgonium.android.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import com.xirgonium.android.place.DialogForPlace;
import com.xirgonium.android.place.FavoritePlace;
import com.xirgonium.android.place.FavoritePlacesManager;
import com.xirgonium.android.veloid.R;

public class FavoritePlaceItemDeleteListener implements OnClickListener {

	private Context					ctx;
	private FavoritePlace			favoritePlace;
	private DialogForPlace	mainDialog;

	public FavoritePlaceItemDeleteListener(DialogForPlace act, Context ctx, FavoritePlace fav) {
		this.ctx = ctx;
		this.mainDialog = act;
		this.favoritePlace = fav;
	}

	public void onClick(View clicked) {
		final FavoritePlacesManager mgr = new FavoritePlacesManager(ctx);

		new AlertDialog.Builder(ctx).setMessage(
				ctx.getString(R.string.favorite_dialog_delete_confirmation)).setPositiveButton(
				R.string.del_station_warning_yes_btn, new MyDialogListener(mgr, favoritePlace, ctx, mainDialog)).setNegativeButton(R.string.del_station_warning_no_btn,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).setIcon(R.drawable.warning).create().show();
	}
}

class MyDialogListener implements android.content.DialogInterface.OnClickListener {
	FavoritePlacesManager	mgr;
	FavoritePlace			place;
	Context					ctx;
	DialogForPlace			mainDialog;

	public MyDialogListener(FavoritePlacesManager mgr, FavoritePlace place, Context ctx,
			DialogForPlace mainDialog) {
		this.mgr = mgr;
		this.place = place;
		this.ctx = ctx;
		this.mainDialog = mainDialog;
	}

	public void onClick(DialogInterface dialog, int whichButton) {
		mgr.deleteFavoritePlace(place);
		mainDialog.fillView();
		mainDialog.dismiss();
		mainDialog = new DialogForPlace(ctx);
		mainDialog.show();
	}

}
