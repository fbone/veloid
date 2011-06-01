package com.xirgonium.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.R;

public class RadioButtonBackgrounded extends LinearLayout {
    
    private Paint innerPaint, borderPaint;
    private int   colorsTheme = 0;
    
    public RadioButtonBackgrounded(Context context){
        super(context);
        init();
    }
    public RadioButtonBackgrounded(Context context, AttributeSet attrs){
        super(context, attrs);
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
    
    void setColors(){
        borderPaint.setColor(getResources().getColor(R.color.item_border));

        switch (colorsTheme) {
            case Constant.COLOR_THEME_ITEM_ODD:
                innerPaint.setColor(getResources().getColor(R.color.item_odd_background));
                break;
            case Constant.COLOR_THEME_ITEM_EVEN:
                innerPaint.setColor(getResources().getColor(R.color.item_even_background));
                break;
            default:               
                break;
        }
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
        drawRect.set(1, 1, getMeasuredWidth()-2, getMeasuredHeight() - 2);
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

    public int getColorsTheme() {
        return colorsTheme;
    }

    public void setColorsTheme(int colorsTheme) {
        this.colorsTheme = colorsTheme;
    }
}
