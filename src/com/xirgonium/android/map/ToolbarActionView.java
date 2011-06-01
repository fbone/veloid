package com.xirgonium.android.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xirgonium.android.veloid.R;

public class ToolbarActionView extends LinearLayout {

    private Paint innerPaint;//, borderPaint;

    public ToolbarActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToolbarActionView(Context context) {
        super(context);
        init();
    }

    private void init() {
        innerPaint = new Paint();
        innerPaint.setColor(getResources().getColor(R.color.map_toolbar_background));
        innerPaint.setAntiAlias(true);

//        borderPaint = new Paint();
//        borderPaint.setColor(getResources().getColor(R.color.map_toolbar_border));
//        borderPaint.setAntiAlias(true);
//        borderPaint.setStrokeWidth(2);
    }

    public void setInnerPaint(Paint innerPaint) {
        this.innerPaint = innerPaint;
    }

//    public void setBorderPaint(Paint borderPaint) {
//        this.borderPaint = borderPaint;
//    }

    protected void dispatchDraw(Canvas canvas) {        
        
        RectF drawRect = new RectF();
        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        
        canvas.drawRect(drawRect, innerPaint);
       // canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

        super.dispatchDraw(canvas);
    }

    protected void onDraw(Canvas canvas) {
        RectF drawRect = new RectF();
        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        
        canvas.drawRect(drawRect, innerPaint);
       // canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

        super.onDraw(canvas);
    }
}
