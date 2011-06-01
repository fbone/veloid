package com.xirgonium.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.R;

public class FavoriteItemView extends LinearLayout {

  private Paint      innerPaint, borderPaint;
  private int        colorsTheme = 0;
  private TypedArray a           = null;

  public FavoriteItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    a = getContext().obtainStyledAttributes(attrs, R.styleable.FavoriteItemView);
    init();
  }

  public FavoriteItemView(Context context, int colorTheme) {
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
    setColors();
  }

  void setColors() {

    int borderColor = a.getResourceId(R.styleable.FavoriteItemView_borderColor, R.color.item_border);
    int innerColor = a.getResourceId(R.styleable.FavoriteItemView_innerColor, -999);

    borderPaint.setColor(getResources().getColor(borderColor));

    if (innerColor == -999) {
      switch (colorsTheme) {
      case Constant.COLOR_THEME_ITEM_ODD:
        innerPaint.setColor(getResources().getColor(R.color.item_odd_background));
        break;
      case Constant.COLOR_THEME_ITEM_EVEN:
        innerPaint.setColor(getResources().getColor(R.color.item_even_background));
        break;
      case Constant.COLOR_THEME_ITEM_ALPHA:
        innerPaint.setColor(getResources().getColor(R.color.item_info_alpha));
        break;
      default:
        innerPaint.setColor(getResources().getColor(R.color.header_favorite));
        break;
      }
    } else {
      innerPaint.setColor(getResources().getColor(innerColor));
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

  public int getColorsTheme() {
    return colorsTheme;
  }

  public void setColorsTheme(int colorsTheme) {
    this.colorsTheme = colorsTheme;
  }

  public void swapColor() {
    // innerPaint.setColor(Color.CYAN);
    // this.invalidate();
    // this.refreshDrawableState();
    // try {
    // Thread.sleep(500);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // //setColors();
    // invalidate();
  }

}
