package com.xirgonium.android.config;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.xirgonium.android.veloid.R;

/**
 * This is an example of a custom preference type. The preference counts the number of clicks it has received and stores/retrieves it from the storage.
 */
public class QuickStartPreference extends Preference {

  private static final int OPEN_WEB_BROWSER = 0;

  private Handler          handler          = new Handler() {

                                              @Override
                                              public void handleMessage(Message msg) {
                                                switch (msg.what) {

                                                case OPEN_WEB_BROWSER:
                                                  thisInstance.openWebBrowser();
                                                  break;
                                                }

                                              }
                                            };

  QuickStartPreference     thisInstance     = null;

  // This is the constructor called by the inflater
  public QuickStartPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    thisInstance = this;
  }

  @Override
  protected void onBindView(View view) {
    super.onBindView(view);
  }

  @Override
  protected void onClick() {
    handler.sendEmptyMessage(OPEN_WEB_BROWSER);
  }

  protected void openWebBrowser() {
    try {
      Uri uri = Uri.parse(thisInstance.getContext().getString(R.string.quick_start_url));
      thisInstance.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
