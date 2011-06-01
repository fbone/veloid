package com.xirgonium.android.listener;

import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.xirgonium.android.object.Station;
import com.xirgonium.android.veloid.R;
import com.xirgonium.android.veloid.Veloid;

public class ItemOnMainScreenListener implements OnClickListener, OnKeyListener, OnFocusChangeListener {

  private Veloid        activity;
  private boolean       isEdited = false;
  private Station       station;
  private ColorDrawable oldColor = null;

  public ItemOnMainScreenListener(Veloid act, Station station) {
    this.activity = act;
    this.station = station;
    oldColor = null;
  }

  public void onClick(View clicked) {
    // change the look and feel
    if (oldColor == null)
      oldColor = (ColorDrawable) clicked.getBackground();
    ((EditText) clicked).setCursorVisible(true);
    ((EditText) clicked).setBackgroundColor(activity.getResources().getColor(R.color.list_bottom_color));
    ((EditText) clicked).setTextColor(activity.getResources().getColor(R.color.list_top_color));
    isEdited = true;
  }

  public boolean onKey(View v, int keyCode, KeyEvent event) {
    //Log.d("LSNR ", "On a clické : "+keyCode);
    switch (keyCode) {
    case 4:
      activity.finish();
    case 66:
      //enter
      saveAndRestoreOldState(v);
      return true;
    case 82:
      //menu
      return false;
    default:
     return !isEdited;
    }
  }

  public void onFocusChange(View v, boolean hasFocus) {
    if (!hasFocus) {
      saveAndRestoreOldState(v);
    }
  }

  private void saveAndRestoreOldState(View v) {
    String comment = ((EditText) v).getText().toString();
    if (comment.trim().length() == 0) {
      comment = station.getName();
      ((EditText) v).setText(comment);
    }
    ((EditText) v).setCursorVisible(false);
    ((EditText) v).setBackgroundDrawable(oldColor);
    ((EditText) v).setTextColor(activity.getResources().getColor(R.color.item_odd_text_color));
    station.setComment(comment);
    Veloid.getMgr().updateStation(station);
    isEdited = false;
  }

}
