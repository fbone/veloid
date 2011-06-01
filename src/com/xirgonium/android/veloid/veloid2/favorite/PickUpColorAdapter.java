package com.xirgonium.android.veloid.veloid2.favorite;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xirgonium.android.veloid.R;

public class PickUpColorAdapter extends ArrayAdapter {

	@SuppressWarnings("unchecked")
	public PickUpColorAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		add("1");
		add("2");
		add("3");
		add("4");
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView tv = new TextView(getContext());
		tv.setText(" ");
		tv.setTextSize(30);

		int color = 0;

		switch (position) {
			case 1:
				color = getContext().getResources().getColor(R.color.favorite_alternative_one);
				break;
			case 2:
				color = getContext().getResources().getColor(R.color.favorite_alternative_two);
				break;
			case 3:
				color = getContext().getResources().getColor(R.color.favorite_alternative_three);
				break;
			default:
				color = getContext().getResources().getColor(R.color.green_one);
				break;
		}

		tv.setBackgroundColor(color);
		return tv;// super.getDropDownView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getDropDownView(position, convertView, parent);
	}

}
