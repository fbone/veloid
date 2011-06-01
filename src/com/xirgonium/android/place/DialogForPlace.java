package com.xirgonium.android.place;

import java.util.Iterator;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xirgonium.android.listener.FavoritePlaceItemDeleteListener;
import com.xirgonium.android.listener.FavoritePlaceItemListener;
import com.xirgonium.android.veloid.R;

public class DialogForPlace extends AlertDialog {

	private Context			ctx;

	private DialogForPlace	thisDialog	= null;

	public DialogForPlace(Context context) {
		super(context);
		this.ctx = context;
		thisDialog = this;
		mgr = new FavoritePlacesManager(ctx);
		
		//mgr.cleanFavroitePlace();
		
		//-- TEST BEGIN
		Vector<FavoritePlace> places = mgr.getListOfFavoritePlaces();
		for (Iterator<FavoritePlace> iterator = places.iterator(); iterator.hasNext();) {
			FavoritePlace favoritePlace = (FavoritePlace) iterator.next();
		//	mgr.deleteFavoritePlace(favoritePlace);
		}

		
		FavoritePlace a = new FavoritePlace();
		a.setId(1l);
		a.setName("ma place");
		a.setLatitude(45);
		a.setLongitude(3);

		mgr.saveFavoritePlaceIntoDB(a);

		FavoritePlace b = new FavoritePlace();
		b.setId(2l);
		b.setName("ma place 2");
		b.setLatitude(46);
		b.setLongitude(12);

		mgr.saveFavoritePlaceIntoDB(b);
		//--- TEST END
		
		fillView();
	}

	FavoritePlacesManager	mgr;
	
	public void fillView() {

		LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mainView = vi.inflate(R.layout.place_dialog_content, null);
		

		Vector<FavoritePlace> places = mgr.getListOfFavoritePlaces();
		LinearLayout container = (LinearLayout) mainView.findViewById(R.id.placeDialogContainer);
		container.removeAllViews();

		//Log.d(this.getClass().getName(), " ---NOMBRE DE FAVORI : " + places.size());
		
		if (places.size() > 0) {

			for (Iterator iterator = places.iterator(); iterator.hasNext();) {
				FavoritePlace favoritePlace = (FavoritePlace) iterator.next();

				View onePlaceView = vi.inflate(R.layout.place_dialog_item, null);
				// process the view here
				TextView placeName = (TextView) onePlaceView.findViewById(R.id.placeName);
				placeName.setText(favoritePlace.getName());

				FavoritePlaceItemListener lsnr = new FavoritePlaceItemListener(getContext(),
						favoritePlace);
				placeName.setOnClickListener(lsnr);

				ImageButton btn = (ImageButton) onePlaceView.findViewById(R.id.place_delete_btn);
				btn
						.setOnClickListener(new FavoritePlaceItemDeleteListener(this, ctx,
								favoritePlace));

				container.addView(onePlaceView);
				Log.d("DIALOG", "++ Views");
			}
		}

		// Add the cancel button
		setButton(AlertDialog.BUTTON_NEGATIVE ,ctx.getString(R.string.dialog_btn_cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				thisDialog.dismiss();
			}
		});
		
		mainView.refreshDrawableState();
		
		setView(mainView);
		
		setTitle(R.string.favorite_dialog_select_title);
		setIcon(R.drawable.icon_small);
	}
}
