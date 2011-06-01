package com.xirgonium.android.view;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xirgonium.android.util.FormatUtility;
import com.xirgonium.android.veloid.R;

public class SelectRingtoneListAdapter extends ArrayAdapter<File> {

  List<File> listedFiles       = null;
  int        selectedFileIndex = -1;

  public SelectRingtoneListAdapter(Context context, int resource, int textViewResourceId, List<File> objects) {
    super(context, resource, textViewResourceId, objects);
    listedFiles = objects;
  }

  public int getSelectedFileIndex() {
    return selectedFileIndex;
  }

  public void setSelectedFileIndex(int selectedFileIndex) {
    this.selectedFileIndex = selectedFileIndex;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final LinearLayout oneFileInfo = (LinearLayout) vi.inflate(R.layout.select_ring_list_row, null);
    ImageView img = (ImageView) oneFileInfo.findViewById(R.id.select_ringtone_row_img);
    TextView tv = (TextView) oneFileInfo.findViewById(R.id.select_ringtone_row_txt);
    ImageView imgSel = (ImageView) oneFileInfo.findViewById(R.id.select_ringtone_row_selecgted_indicator);

    if (position == 0) {
      img.setImageResource(R.drawable.folder);
      tv.setText(R.string.current_dir);

    } else if (position == 1) {
      img.setImageResource(R.drawable.folder);
      tv.setText(R.string.up_one_level);

    } else if (listedFiles.get(position).isDirectory()) {
      img.setImageResource(R.drawable.folder);
      tv.setText(listedFiles.get(position).getName());

    } else {
      img.setImageResource(R.drawable.sound);
      tv.setText(FormatUtility.removeFileExtension(listedFiles.get(position).getName()));
    }

    if (selectedFileIndex == position) {
      imgSel.setImageResource(R.drawable.ok);
    } else {
      imgSel.setImageResource(0);
    }

    return oneFileInfo;
  }

}
