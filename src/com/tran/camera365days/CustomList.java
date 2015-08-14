package com.tran.camera365days;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>{

	private final Activity context;

	private final String[] effect;

	private final Integer[] imageId;

	public CustomList(Activity context, String[] effect, Integer[] imageId) {

		super(context, R.layout.toolbar_item, effect);

		this.context = context;

		this.effect = effect;

		this.imageId = imageId;

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();

		View rowView= inflater.inflate(R.layout.toolbar_item, null, true);

		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		txtTitle.setTextColor(context.getResources().getColor(R.color.GREEN_COLOR));

		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

		txtTitle.setText(effect[position]);

		imageView.setImageResource(imageId[position]);

		return rowView;

	}
}