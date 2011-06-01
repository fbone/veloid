package com.xirgonium.android.veloid.veloid2.map;

import com.xirgonium.android.object.Station;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class StationInformationView extends LinearLayout {

//    private Paint innerPaint, borderPaint;
    private int microDegreeLatitude = 0;
    private int microDegreeLongitude = 0;
    private Station station = null;

    public StationInformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StationInformationView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context ctx) {

//        innerPaint = new Paint();
//        innerPaint.setColor(getResources().getColor(R.color.map_station_item_background));
//        innerPaint.setAntiAlias(true);
//
//        borderPaint = new Paint();
//        borderPaint.setColor(getResources().getColor(R.color.map_station_item_border));
//        borderPaint.setAntiAlias(true);
//        borderPaint.setStyle(Style.STROKE);
//        borderPaint.setStrokeWidth(2);
        
    }

//    public void setInnerPaint(Paint innerPaint) {
//        this.innerPaint = innerPaint;
//    }
//
//    public void setBorderPaint(Paint borderPaint) {
//        this.borderPaint = borderPaint;
//    }

//    protected void dispatchDraw(Canvas canvas) {
//        
//        RectF drawRect = new RectF();
//        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
//
//        canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
//        canvas.drawRoundRect(drawRect, 5, 5, borderPaint);
//        super.dispatchDraw(canvas);
//    }
//
//    protected void onDraw(Canvas canvas) {
//        RectF drawRect = new RectF();
//        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
//
//        canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
//        canvas.drawRoundRect(drawRect, 5, 5, borderPaint);
//        super.onDraw(canvas);
//    }

    public int getMicroDegreeLatitude() {
        return microDegreeLatitude;
    }

    public void setMicroDegreeLatitude(int microDegreeLatitude) {
        this.microDegreeLatitude = microDegreeLatitude;
    }

    public int getMicroDegreeLongitude() {
        return microDegreeLongitude;
    }

    public void setMicroDegreeLongitude(int microDegreeLongitude) {
        this.microDegreeLongitude = microDegreeLongitude;
    }

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}    
}
