package com.xirgonium.android.view;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.config.SelectRingPreference;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.R;

public class DialogSelectRingtone extends AlertDialog implements OnItemClickListener, OnClickListener, android.view.View.OnClickListener, OnCompletionListener {

  private List<File>        directoryEntries = new ArrayList<File>();
  private File              currentDirectory = new File("/");
  ListView                  listOfFiles      = null;
  MusicFileFilter           musicFilter      = new MusicFileFilter();
  File                      selectedRingtone = null;
  SelectRingtoneListAdapter directoryList    = null;
  View                      dlgContent       = null;
  SelectRingPreference      preference       = null;
  MediaPlayer               player           = null;
  Button                    btnPlayer        = null;

  public DialogSelectRingtone(Context context, SelectRingPreference pref) {
    super(context);
    this.preference = pref;
  }

  public DialogSelectRingtone(Context context, int theme) {
    super(context, theme);

  }

  public DialogSelectRingtone(Context context, boolean cancelable, OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }

  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    if (position == 0) {
      // Refresh
      this.browseTo(this.currentDirectory, -1);
    } else if (position == 1) {
      this.upOneLevel();
    } else {
      File clickedFile = directoryEntries.get(position);

      if (clickedFile.isDirectory()) {
        this.browseTo(clickedFile, -1);
        selectedRingtone = null;
      } else {
        directoryList = new SelectRingtoneListAdapter(getContext(), R.layout.select_ring_list_row, R.id.select_ringtone_row_txt, directoryEntries);
        directoryList.setSelectedFileIndex(position);
        listOfFiles.setAdapter(directoryList);
        listOfFiles.setSelection(position);
        selectedRingtone = clickedFile;
        preference.setCustomRingtone(clickedFile);
      }
    }
  }

  public void onClick(DialogInterface dialog, int which) {
    if (player != null) {
      player.stop();
      player.release();
      player = null;
    }
    if (which == Dialog.BUTTON1) {
      dialog.dismiss();
    } else {
      dialog.dismiss();
    }
  }

  public void showDialog() {
    LayoutInflater inflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Application.LAYOUT_INFLATER_SERVICE);
    View dlgContent = inflater.inflate(R.layout.dialog_select_ringtone, null);

    listOfFiles = (ListView) dlgContent.findViewById(R.id.ring_list_files);
    listOfFiles.setOnItemClickListener(this);
    browseToRoot();

    btnPlayer = (Button) dlgContent.findViewById(R.id.select_ringtone_player);
    btnPlayer.setOnClickListener(this);

    new DialogSelectRingtone.Builder(getContext()).setView(dlgContent).setPositiveButton(R.string.dialog_btn_ok, this).create().show();

  }

  private void browseToRoot() {
    String root = "/";
    int index = -1;
    String soundPath = ConfigurationContext.getSoundPath();
    if (!soundPath.equals(Constant.DEFAULT_SOUND_PATH) && !soundPath.equals("")) {
      selectedRingtone = new File(soundPath);
      File parent = selectedRingtone.getParentFile();
      root = parent.getAbsolutePath();
      String[] files = parent.list();
      int tmp = 2;// because . and .. in the list
      for (String aFile : files) {
        if (!(root + File.separator + aFile).equals(soundPath)) {
          tmp++;
        } else {
          index = tmp;
          break;
        }
      }
    }
    browseTo(new File(root), index);

  }

  private void upOneLevel() {
    if (this.currentDirectory.getParent() != null)
      this.browseTo(this.currentDirectory.getParentFile(), -1);
  }

  private void browseTo(final File aDirectory, int index) {
    if (aDirectory.isDirectory()) {
      this.currentDirectory = aDirectory;
      fill(aDirectory.listFiles(musicFilter), index);
    }
  }

  private void fill(File[] files, int preselected) {
    this.directoryEntries.clear();

    this.directoryEntries.add(this.currentDirectory);
    if (this.currentDirectory.getParent() != null)
      this.directoryEntries.add(this.currentDirectory.getParentFile());

    for (File file : files) {
      this.directoryEntries.add(file);

    }

    directoryList = new SelectRingtoneListAdapter(getContext(), R.layout.select_ring_list_row, R.id.select_ringtone_row_txt, directoryEntries);

    // for the first tiem launch
    if (preselected >= 0) {

      directoryList.setSelectedFileIndex(preselected);

    }

    listOfFiles.setAdapter(directoryList);
    if (preselected >= 0) {
      listOfFiles.setSelection(preselected);
    }

  }

  public void onClick(View v) {
    if (player != null) {
      btnPlayer.setText(R.string.dialog_search_ringtone_btn_play_play);
      player.stop();
      player.release();
      player = null;
    } else if (selectedRingtone != null) {
      btnPlayer.setText(R.string.dialog_search_ringtone_btn_play_stop);
      player = new MediaPlayer();
      player.setOnCompletionListener(this);
      try {
        player.setDataSource(selectedRingtone.getAbsolutePath());
        player.prepare();
        player.start();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalStateException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void onCompletion(MediaPlayer mp) {
    btnPlayer.setText(R.string.dialog_search_ringtone_btn_play_play);
    player = null;
  }
}

class MusicFileFilter implements FileFilter {

  // Description et extensions acceptées par le filtre
  private List<String> extensions;

  // Constructeur à partir de la description
  public MusicFileFilter() {
    this.extensions = new ArrayList<String>();
    this.extensions.add(".mp3");
    this.extensions.add(".wav");
    this.extensions.add(".ogg");
    this.extensions.add(".mid");
    this.extensions.add(".midi");
  }

  // Implémentation de FileFilter
  public boolean accept(File file) {
    if (file.isDirectory() || extensions.size() == 0) {
      return true;
    }
    String nomFichier = file.getName().toLowerCase();
    for (String extension : extensions) {
      if (nomFichier.endsWith(extension)) {
        return true;
      }
    }
    return false;
  }
}
