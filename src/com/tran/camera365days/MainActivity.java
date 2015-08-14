package com.tran.camera365days;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
/*
 * Array of options --> ArrayAdapter--> ListView
 * List view: {views: data_items.xml}
 */
public class MainActivity extends Activity {

	private List<MainScreenItemsView> lstItems= new ArrayList<MainScreenItemsView>();
	
	private boolean cameraFront;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		populateListView();
		populateListItems();
		registerClickCallback();
	}


	//Create list of items
	private void populateListItems() {
		lstItems.add(new MainScreenItemsView("Camera", R.drawable.camera_icon));
		lstItems.add(new MainScreenItemsView("Selfie",R.drawable.selfie));
		lstItems.add(new MainScreenItemsView("Gallery",R.drawable.galary_icon));
	}
	private void populateListView() {
		
		//Build Adapter
		ArrayAdapter<MainScreenItemsView> adapter = new MyListAdapter();
		ListView listView = (ListView) findViewById(R.id.lstItemView);
		
		//Configure the List View
		listView.setAdapter(adapter);
		listView.setDivider(null);
		
	}

	private void registerClickCallback() {
		ListView list = (ListView) findViewById(R.id.lstItemView);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				//Perform an action
				//ListItemsView clickedItem = lstItems.get(position);
				//String message = "You clicked position " + position
				//				+ " Which is item " + clickedItem.getStrName();
				//Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
				if(position==0){ //get into the main view interface
					Intent  mainViewIntent= new Intent(MainActivity.this,MainView.class);
					mainViewIntent.putExtra("toolId", 1);
					mainViewIntent.putExtra("cameraFront", false);
					mainViewIntent.putExtra("faceDetection", false);
					mainViewIntent.putExtra("hasBackground", false);
					mainViewIntent.putExtra("hasFrame", false);
					startActivity(mainViewIntent);
				}
				if(position==1){ //get into the selfie interface
					Intent  selfieViewIntent= new Intent(MainActivity.this,MainView.class);
					selfieViewIntent.putExtra("cameraFront", true);
					selfieViewIntent.putExtra("faceDetection", false);
					selfieViewIntent.putExtra("hasBackground", false);
					selfieViewIntent.putExtra("hasFrame", false);
					selfieViewIntent.putExtra("toolId", 1);
					startActivity(selfieViewIntent);
				}
				if (position==2){//Album interface

					Intent  AllAlbum= new Intent(MainActivity.this,AllAlbumsView.class);
					startActivity(AllAlbum);
				}
				
			}
		});
	}
	private class MyListAdapter extends ArrayAdapter<MainScreenItemsView> {
		public MyListAdapter() {
			super(MainActivity.this, R.layout.main_screen_item_view, lstItems);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.main_screen_item_view, parent, false);
			}
			
			// Find the item to work with.
			MainScreenItemsView currentItem= lstItems.get(position);
			
			// Fill the view
			ImageView imageView = (ImageView)itemView.findViewById(R.id.item_icon);
			imageView.setImageResource(currentItem.getIconID());
			
			// Make:
			TextView makeText = (TextView) itemView.findViewById(R.id.item_txtName);
			makeText.setText(currentItem.getStrName());

			return itemView;
		}	
	}
	
}

