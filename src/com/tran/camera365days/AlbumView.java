package com.tran.camera365days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

public class AlbumView extends Activity{
	    private GridView mGridView; 
	    
	    /**
	     * No HeaderId List
	     */ 
	    private List<GridItem> nonHeaderIdList = new ArrayList<GridItem>(); 
	    private List<GridItem> hasHeaderIdList=new ArrayList<GridItem>(); 
	    
	    //@SuppressWarnings("unchecked")
		@Override 
	    protected void onCreate(Bundle savedInstanceState) { 
	        super.onCreate(savedInstanceState); 
	    	requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.album_view); 
	         
	        mGridView = (GridView) findViewById(R.id.grid_item); 
	        //Add to the non-header list of images in this album
	        //The serializable method should be replaced by Parcelable in Android for better performance.
	        nonHeaderIdList=(List<GridItem>)getIntent().getSerializableExtra("data_album");
	        //Convert it to the header list
	        hasHeaderIdList = generateHeaderId(nonHeaderIdList);	
            Collections.sort(hasHeaderIdList, new YMDComparator()); 
            mGridView.setAdapter(new AlbumAdapter(AlbumView.this, hasHeaderIdList, mGridView));           
            mGridView.setOnItemClickListener(onClick);
		}
		private AdapterView.OnItemClickListener onClick = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//String path=hasHeaderIdList.get(position).getPath();
				List<String> lst=getListOfImagePaths();
				Log.i(AlbumView.class.getSimpleName(),"=====SIZE OF ALBUM===== " + lst.size()); 
				Intent iPageView = new Intent(AlbumView.this, ImagePageView.class);  
				iPageView.putStringArrayListExtra("list_of_paths_of_images", (ArrayList<String>) lst);  
				iPageView.putExtra("POSITION_OF_IMAGE",position);
                startActivity(iPageView);  
                finish();
			}
			
		};
		private List<String> getListOfImagePaths(){
			List<String> lst= new ArrayList<String>();
			Iterator<GridItem> it=hasHeaderIdList.iterator();
			while(it.hasNext()){
				GridItem item=it.next();
				String path=item.getPath();
				lst.add(path);
			}
			return lst;
		}

	    /**
	     * Item HeaderId of GridView, according to the time of adding pictures of the year, month, day to generate HeaderId
	     * Year, month, day equal to HeaderId the same
	     * @param nonHeaderIdList
	     * @return
	     */ 
	    private List<GridItem> generateHeaderId(List<GridItem> nonHeaderIdList) { 
	        Map<String, Integer> mHeaderIdMap = new HashMap<String, Integer>(); 
	        int mHeaderId = 0; 
	        List<GridItem> hasHeaderIdList; 
	         
	        for(ListIterator<GridItem> it = nonHeaderIdList.listIterator(); it.hasNext();){ 
	            GridItem mGridItem = it.next(); 
	            String ymd = mGridItem.getTime(); 
	            if(!mHeaderIdMap.containsKey(ymd)){ 
	                mGridItem.setHeaderId(mHeaderId); 
	                mHeaderIdMap.put(ymd, mHeaderId); 
	                mHeaderId ++; 
	            }else{ 
	                mGridItem.setHeaderId(mHeaderIdMap.get(ymd)); 
	            } 
	        } 
	        hasHeaderIdList = nonHeaderIdList; 
	         
	        return hasHeaderIdList; 
	    } 
	 
	     
	    @Override 
	    protected void onDestroy() { 
	        super.onDestroy(); 
	        //Exit pages removal in LRUCache Bitmap memory
	        try {
				NativeImageLoader.getInstance().trimMemCache();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
	        System.gc();
	    } 
	 
}
