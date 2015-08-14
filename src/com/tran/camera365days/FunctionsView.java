package com.tran.camera365days;

import com.tran.camera365days.FunctionsViewAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
 
public class FunctionsView extends Activity {
	static boolean active = false;
	
	AdView adView;
	GridView gridView;
 
	static final String[] ITEMS_TEXT = new String[] { 
		"Mode", "Contrast Effects", "Background", "Frame", "Face detection", "Sticker" };
 
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.functions_view);
		
		//setting advertise area here
		adView = (AdView)findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		gridView = (GridView) findViewById(R.id.gridView1);
 
		gridView.setAdapter(new FunctionsViewAdapter(this, ITEMS_TEXT));
 
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.grid_item_label)).getText(), 
						                Toast.LENGTH_SHORT).show();

				if (position == 0) {               //call Mode activity
                    Intent  modeIntent= new Intent(FunctionsView.this,MainView.class);
					modeIntent.putExtra("cameraFront", false);
                    modeIntent.putExtra("faceDetection", false);
                    modeIntent.putExtra("hasBackground", false);
                    modeIntent.putExtra("hasFrame", false);
                    modeIntent.putExtra("hasSticker", false);
					modeIntent.putExtra("toolId", 1);
					startActivity(modeIntent);		
					//finish();
				} else if (position == 1) {        //call Contrast effect activity 
					Intent  contrastIntent= new Intent(FunctionsView.this,MainView.class);
					contrastIntent.putExtra("cameraFront", false);
					contrastIntent.putExtra("faceDetection", false);
					contrastIntent.putExtra("hasBackground", false);
					contrastIntent.putExtra("hasFrame", false);
					contrastIntent.putExtra("hasSticker", false);
					contrastIntent.putExtra("toolId", 2);
					startActivity(contrastIntent);
					//finish();
				} else if (position == 2) {        //call Background activity
					Intent  backgroundIntent= new Intent(FunctionsView.this,MainView.class);
					backgroundIntent.putExtra("cameraFront", false);
                    backgroundIntent.putExtra("faceDetection", false);
                    backgroundIntent.putExtra("hasBackground", true);
                    backgroundIntent.putExtra("hasFrame", false);
                    backgroundIntent.putExtra("hasSticker", false);
					backgroundIntent.putExtra("toolId", 3);
					startActivity(backgroundIntent);
					//finish();
				} else if (position == 3) {       //call Frame activity
					Intent  frameIntent= new Intent(FunctionsView.this,MainView.class);
					frameIntent.putExtra("cameraFront", false);
					frameIntent.putExtra("faceDetection", false);
					frameIntent.putExtra("hasBackground", false);
					frameIntent.putExtra("hasFrame", true);
					frameIntent.putExtra("hasSticker", false);
					frameIntent.putExtra("toolId", 4);
					startActivity(frameIntent);
					//finish();
				} else if (position == 4) {        //call Face detection activity
					Intent  faceDetectionIntent= new Intent(FunctionsView.this,MainView.class);
					faceDetectionIntent.putExtra("cameraFront", false);
                    faceDetectionIntent.putExtra("faceDetection", true);
                    faceDetectionIntent.putExtra("hasBackground", false);
                    faceDetectionIntent.putExtra("hasFrame", false);
                    faceDetectionIntent.putExtra("hasSticker", false);
					faceDetectionIntent.putExtra("toolId", 0);
					startActivity(faceDetectionIntent);
					//finish();
				} else if (position == 5) {        //call Sticker attachtion activity
					Intent  stickerIntent= new Intent(FunctionsView.this,MainView.class);
					stickerIntent.putExtra("cameraFront", false);
                    stickerIntent.putExtra("faceDetection", false);
                    stickerIntent.putExtra("faceDetection", false);
                    stickerIntent.putExtra("hasBackground", false);
                    stickerIntent.putExtra("hasSticker", true);
					stickerIntent.putExtra("toolId", 5);
					startActivity(stickerIntent);
					//finish();
				}
 
			}
		});
 
	}
	
	  
	@Override      
	public void onStart() {
		super.onStart();        
		active = true;     
	} 

	@Override     
	public void onStop() {      
		super.onStop();      
		active = false;
	}	
 
}