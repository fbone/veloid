package com.xirgonium.android.veloid.veloid2.map;

import java.util.Iterator;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.xirgonium.android.veloid.R;

public class VeloidCenterOverlay extends Overlay {

	// private final android.graphics.Point shiftParameterForStationIndicator =
	// new android.graphics.Point(8, 16);
	private final android.graphics.Point	shiftParameterForCenterIndicator	= new android.graphics.Point(10, 10);

	VeloidMap								itsMapActivity						= null;
	Paint									paint								= null;
	boolean									displayCenter						= false;

	private Bitmap							centerIndicator						= null;
	ViewGroup								main								= null;


	public VeloidCenterOverlay(VeloidMap map) {
		itsMapActivity = map;

		main = (ViewGroup) itsMapActivity.findViewById(R.id.mainLayout);

		paint = new Paint();
		paint.setAntiAlias(true);

		centerIndicator = BitmapFactory.decodeResource(this.itsMapActivity.getResources(), R.drawable.you_are_there_16x16);

	}

	@Override
	public void draw(Canvas canvas, MapView view, boolean b) {
		// Log.d("MAP", "Display " + stationsToDisplay.size() + " stations");
		super.draw(canvas, itsMapActivity.getMapView(), b);

		// --- Draw a buddy where you are
		Point centerCoords = new Point();
		mapPointToScreenCoords(itsMapActivity.getMapCenter(), centerCoords);
		canvas.drawBitmap(centerIndicator, centerCoords.x - shiftParameterForCenterIndicator.x, centerCoords.y - shiftParameterForCenterIndicator.y, paint);

	}

	private void mapPointToScreenCoords(GeoPoint p, Point pointOnScreen) {
		if (p != null) {
			itsMapActivity.getMapView().getProjection().toPixels(p, pointOnScreen);
		}
	}

	public GeoPoint getCenter() {
		return itsMapActivity.getMapCenter();
	}

	public void clearStationInfoChildren() {

		int nbV = main.getChildCount();

		Vector<View> toRemove = new Vector<View>();

		for (int i = 0; i < nbV; i++) {
			View v = main.getChildAt(i);
			if (v instanceof StationInformationView) {
				toRemove.add(v);
			}
		}

		for (Iterator<View> iterator = toRemove.iterator(); iterator.hasNext();) {
			View view = (View) iterator.next();
			main.removeView(view);
		}
	}

}
