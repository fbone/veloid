package com.xirgonium.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xirgonium.android.veloid.R;

public class MenuAdvancedView extends LinearLayout {
    private Paint innerPaint, borderPaint;

    public MenuAdvancedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuAdvancedView(Context context, int colorTheme) {
        super(context);
        init();
    }

    private void init() {
        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Style.STROKE);
        borderPaint.setStrokeWidth(1);

    }

    void setColors() {
        borderPaint.setColor(getResources().getColor(R.color.item_border));   
        LinearGradient gradient = new LinearGradient(0, 0, 0, getMeasuredHeight() - 2, getContext().getResources().getColor(R.color.adv_top_gradient), getContext().getResources().getColor(R.color.adv_bottom_gradient), TileMode.CLAMP);
        innerPaint.setShader(gradient);
        //innerPaint.setColor(getResources().getColor(R.color.item_info_alpha));
    }

    public void setInnerPaint(Paint innerPaint) {
        this.innerPaint = innerPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    protected void dispatchDraw(Canvas canvas) {

        setColors();

        RectF drawRect = new RectF();
        drawRect.set(1, 1, getMeasuredWidth() - 2, getMeasuredHeight() - 2);
        
        canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
        canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

        super.dispatchDraw(canvas);
    }

    protected void onDraw(Canvas canvas) {

        setColors();

        RectF drawRect = new RectF();
        drawRect.set(1, 1, getMeasuredWidth() - 2, getMeasuredHeight() - 2);

        canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
        canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

        super.onDraw(canvas);
    }
}
