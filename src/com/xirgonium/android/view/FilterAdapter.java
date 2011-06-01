package com.xirgonium.android.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xirgonium.android.veloid.R;

public class FilterAdapter extends BaseAdapter {
	 private Context context;
	    private String[] values;
	    private boolean enabled = true;
	    private int textSize = -1;
	    
	    public FilterAdapter(Context context, String[] values) { 
	        this.context = context;
	        this.values = values;
	    }

	    public int getCount() {                        
	        return values.length;
	    }

	    public Object getItem(int position) {     
	        return values[position];
	    }

	    public long getItemId(int position) {  
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) { 
	    	TextView result = new TextView(context);
	    	if(getTextSize() != -1){
	    	  result.setTextSize(textSize);
	    	}
	    	result.setText(values[position]);
	    	result.setPadding(2, 3, 0, 0);
	    	if(enabled){
	    		result.setTextColor(context.getResources().getColor(R.color.enabled));
	    	}else {
	    		result.setTextColor(context.getResources().getColor(R.color.disabled));
	    	}
	        return result;
	    }

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

    public void setTextSize(int textSize) {
      this.textSize = textSize;
    }

    public int getTextSize() {
      return textSize;
    }
		
}
