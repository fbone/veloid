package com.xirgonium.android.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.util.StationFilter;
import com.xirgonium.android.veloid.R;

public class FilterInformationView extends LinearLayout {
    private Paint innerPaint;
    StationFilter filter = null;

    
    public FilterInformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public FilterInformationView(Context context) {
        super(context);
        init();
    }

    private void init() {
        innerPaint = new Paint();
        innerPaint.setColor(getResources().getColor(R.color.filter_info_background));
        innerPaint.setAntiAlias(true);
    }

    public void setInnerPaint(Paint innerPaint) {
        this.innerPaint = innerPaint;
    }

    protected void dispatchDraw(Canvas canvas) {
        RectF drawRect = new RectF();
        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        canvas.drawRoundRect(drawRect,5,5, innerPaint);

        super.dispatchDraw(canvas);
    }

    protected void onDraw(Canvas canvas) {
        RectF drawRect = new RectF();
        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        canvas.drawRoundRect(drawRect,5,5, innerPaint);       

        super.onDraw(canvas);
    }

    public String getTextFromFilter() {

        StringBuffer buf = new StringBuffer(getContext().getString(R.string.nearest_station_filter_info_header));
        String avBikeFilter = filter.getFiltervalues(Constant.FILTER_MIN_AVAILABLE_BIKES)!=null?FormatUtility.getTwoDigitsFormatedNumber((int)((Integer)filter.getFiltervalues(Constant.FILTER_MIN_AVAILABLE_BIKES))):getResources().getString(R.string.nearest_station_filter_info_not_filtered); 
        String freeSlotFilter = filter.getFiltervalues(Constant.FILTER_MIN_FREE_SLOTS)!=null?FormatUtility.getTwoDigitsFormatedNumber((int)((Integer)filter.getFiltervalues(Constant.FILTER_MIN_FREE_SLOTS))):getResources().getString(R.string.nearest_station_filter_info_not_filtered);
        buf.append(" ");
        buf.append(getContext().getString(R.string.nearest_station_filter_info_av_bikes));
        buf.append(avBikeFilter);
        buf.append(getContext().getString(R.string.nearest_station_filter_info_free_slots));
        buf.append(freeSlotFilter);

        return buf.toString();
    }

    public void setFilter(StationFilter filter) {
        this.filter = filter;
        ((TextView) findViewById(R.id.filter_info_msg)).setText(getTextFromFilter());
    }
    
    public float getTextSize(){
        return innerPaint.measureText(getTextFromFilter());
    }
}
