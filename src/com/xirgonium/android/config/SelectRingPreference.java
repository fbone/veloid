package com.xirgonium.android.config;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.veloid.R;
import com.xirgonium.android.view.DialogSelectRingtone;

/**
 * This is an example of a custom preference type. The preference counts the number of clicks it has received and stores/retrieves it from the storage.
 */
public class SelectRingPreference extends DialogPreference implements OnClickListener, OnCheckedChangeListener {

  final int            NO_RINGTONE      = 0;
  final int            DEFAULT_RINGTONE = 1;
  final int            CUSTOM_RINGTONE  = 2;

  SelectRingPreference thisInstance     = null;
  DialogSelectRingtone dialog           = null;
  File                 customRingtone   = null;
  View                 contenair        = null;
  RadioButton          customBtn        = null;
  private Handler      handler          = new Handler() {

                                          @Override
                                          public void handleMessage(Message msg) {
                                            thisInstance.displayDialogToSelectRingtone();
                                          }
                                        };

  public SelectRingPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    setSummaryAccordingSelectedOption();
    thisInstance = this;
  }

  public SelectRingPreference(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setSummaryAccordingSelectedOption();
    thisInstance = this;
  }

  private void setSummaryAccordingSelectedOption() {
    if (ConfigurationContext.getSoundPath() == null || ConfigurationContext.getSoundPath().equals("")) {
      this.setSummary(R.string.pref_notif_sound_subtitle_no_ringtone);
    } else if (ConfigurationContext.getSoundPath().equals(Constant.DEFAULT_SOUND_PATH)) {
      this.setSummary(R.string.pref_notif_sound_subtitle_default_ringtone);
    } else {
      customRingtone = new File(ConfigurationContext.getSoundPath());
      this.setSummary(getContext().getString(R.string.pref_notif_sound_select_ringtone_custom_title) + " " + FormatUtility.removeFileExtension(customRingtone.getName()));
    }
  }

  public File getCustomRingtonePath() {
    return customRingtone;
  }

  public void setCustomRingtone(File customRingtone) {
    this.customRingtone = customRingtone;
    setSelectedOption(CUSTOM_RINGTONE);

  }

  @Override
  protected View onCreateDialogView() {
    contenair = super.onCreateDialogView();

    // define the option already selected
    String soundPath = ConfigurationContext.getSoundPath();
    // empty = no sound
    if (soundPath == null || soundPath.equals("")) {
      setSelectedOption(NO_RINGTONE);
    } else if (soundPath.equals(Constant.DEFAULT_SOUND_PATH)) {
      setSelectedOption(DEFAULT_RINGTONE);
    } else {
      customRingtone = new File(soundPath);
      setSelectedOption(CUSTOM_RINGTONE);
    }

    Button btnBrowse = (Button) contenair.findViewById(R.id.select_ringtone_browse);
    btnBrowse.setOnClickListener(this);

    customBtn = (RadioButton) contenair.findViewById(R.id.select_ringtone_custom);
    customBtn.setOnCheckedChangeListener(this);

    return contenair;
  }

  private int getSelectedOption() {
    RadioButton radioBtn = (RadioButton) contenair.findViewById(R.id.select_ringtone_no_ringtone);
    if (radioBtn.isChecked()) {
      return NO_RINGTONE;
    } else {
      radioBtn = (RadioButton) contenair.findViewById(R.id.select_ringtone_default);
      if (radioBtn.isChecked()) {
        return DEFAULT_RINGTONE;
      } else {
        radioBtn = (RadioButton) contenair.findViewById(R.id.select_ringtone_custom);
        if (radioBtn.isChecked()) {
          return CUSTOM_RINGTONE;
        }
      }
    }
    return DEFAULT_RINGTONE;
  }

  private void setSelectedOption(int selection) {
    RadioButton radioBtn;
    switch (selection) {
    case NO_RINGTONE:
      radioBtn = (RadioButton) contenair.findViewById(R.id.select_ringtone_no_ringtone);
      radioBtn.setChecked(true);
      this.setSummary(R.string.pref_notif_sound_subtitle_no_ringtone);
      break;
    case DEFAULT_RINGTONE:
      radioBtn = (RadioButton) contenair.findViewById(R.id.select_ringtone_default);
      radioBtn.setChecked(true);
      this.setSummary(R.string.pref_notif_sound_subtitle_default_ringtone);
      break;
    case CUSTOM_RINGTONE:
      radioBtn = (RadioButton) contenair.findViewById(R.id.select_ringtone_custom);
      radioBtn.setChecked(true);
      radioBtn.setText(getContext().getString(R.string.pref_notif_sound_select_ringtone_custom_title) + " " + FormatUtility.removeFileExtension(customRingtone.getName()));
      this.setSummary(getContext().getString(R.string.pref_notif_sound_select_ringtone_custom_title) + " " + FormatUtility.removeFileExtension(customRingtone.getName()));
      break;
    }
  }

  public void displayDialogToSelectRingtone() {
    dialog = new DialogSelectRingtone(getContext(), this);
    dialog.showDialog();
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {

    String ringtonePath = null;
    if (which == Dialog.BUTTON1) {
      // VALIDATE
      switch (getSelectedOption()) {
      case NO_RINGTONE:
        ringtonePath = "";
        setSelectedOption(NO_RINGTONE);
        break;
      case DEFAULT_RINGTONE:
        ringtonePath = Constant.DEFAULT_SOUND_PATH;
        setSelectedOption(DEFAULT_RINGTONE);
        break;
      case CUSTOM_RINGTONE:
        ringtonePath = customRingtone.getAbsolutePath();
        setSelectedOption(CUSTOM_RINGTONE);
        break;
      }

      ConfigurationContext.setSoundPath(ringtonePath);
      ConfigurationContext.saveConfig(getContext());
      super.onClick(dialog, which);
    }
    super.onClick(dialog, which);
  }

  public void onClick(View v) {
    if (v.getId() == R.id.select_ringtone_browse) {
      handler.sendEmptyMessage(0);
    }
  }

  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (buttonView.getId() == R.id.select_ringtone_custom) {
      if (customRingtone == null) {
        displayDialogToSelectRingtone();
      }
    }
  }


  
  
}
