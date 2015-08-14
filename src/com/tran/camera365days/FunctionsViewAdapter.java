package com.tran.camera365days;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

 
public class FunctionsViewAdapter extends BaseAdapter {
	private Context context;
	private final String[] itemValues;
 
	public FunctionsViewAdapter(Context context, String[] itemValues) {
		this.context = context;
		this.itemValues = itemValues;
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
 
		if (convertView == null) {
 
			gridView = new View(context);
 
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.functions_view_item, null);
 
			// set value into textview
			TextView textView = (TextView) gridView
					.findViewById(R.id.grid_item_label);
			textView.setText(itemValues[position]);
 
			// set image based on selected text
			ImageView imageView = (ImageView) gridView
					.findViewById(R.id.grid_item_image);
 
			String item_text = itemValues[position];
 
			if (item_text.equals("Mode")) {
				imageView.setImageResource(R.drawable.mode_icon);
			} else if (item_text.equals("Contrast Effect")) {
				imageView.setImageResource(R.drawable.effect_icon);
			} else if (item_text.equals("Background")) {
				imageView.setImageResource(R.drawable.background_icon);
			} else if (item_text.equals("Frame")) {
				imageView.setImageResource(R.drawable.frame_icon);
			} else if (item_text.equals("Face detection")) {
				imageView.setImageResource(R.drawable.facedetect_icon);
			} else if (item_text.equals("Sticker")) {
				imageView.setImageResource(R.drawable.sticker_icon);
			}
 
		} else {
			gridView = (View) convertView;
		}
 
		return gridView;
	}
 
	@Override
	public int getCount() {
		return itemValues.length;
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
 
}