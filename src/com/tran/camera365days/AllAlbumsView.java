package com.tran.camera365days;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.tran.camera365days.ImageScanner.ScanCompleteCallBack;

public class AllAlbumsView extends Activity {

	 private HashMap<String, List<GridItem>> mGruopMap = new HashMap<String, List<GridItem>>();  
	 private List<ImageBean> lstImageBeans = new ArrayList<ImageBean>(); 
	 private final static int SCAN_OK = 1;  
	 private ProgressDialog mProgressDialog; 
	 private ImageScanner mScanner; 
	 private GroupAdapter adapter; 
	 private GridView mGroupGridView;
	 private Handler mHandler = new Handler(){  
   	  
	        @Override  
	        public void handleMessage(Message msg) {  
	            super.handleMessage(msg);  
	            switch (msg.what) {  
	            case SCAN_OK:   
	                mProgressDialog.dismiss();  
	                lstImageBeans=subGroupOfImage(mGruopMap);
	                adapter=new GroupAdapter(AllAlbumsView.this, lstImageBeans, mGroupGridView);
	                mGroupGridView.setAdapter(adapter); 
	                break;  
	            }  
	        }

	          
	    };

	private List<ImageBean> subGroupOfImage(HashMap<String, List<GridItem>> mGruopMap) {
	   if(mGruopMap.size() == 0){  
            return null;  
        }  
        List<ImageBean> lstImageBeans = new ArrayList<ImageBean>();  
          
        Iterator<Map.Entry<String, List<GridItem>>> it = mGruopMap.entrySet().iterator();  
        while (it.hasNext()) {  
            Map.Entry<String, List<GridItem>> entry = it.next();  
            ImageBean mImageBean = new ImageBean();  
            String key = entry.getKey();  
            List<GridItem> value = entry.getValue();  
              
            mImageBean.setFolderName(key);  
            mImageBean.setNumberOfImages(value.size());  
            mImageBean.setLastImagePath(value.get(0).getPath());
            Log.i(AllAlbumsView.class.getSimpleName(),"=====PATH OF LAST IMAGE===== " + mImageBean.getLastImagePath()); 
            lstImageBeans.add(mImageBean);  
        }  
          
        return lstImageBeans;  
	}  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);       
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.albums_view);

		mGroupGridView=(GridView) findViewById(R.id.all_albums);
		//reset list of images
		lstImageBeans = new ArrayList<ImageBean>(); 
        
        mScanner = new ImageScanner(this); 
        mScanner.scanImages(new ScanCompleteCallBack() {
	        {
		    	mProgressDialog = ProgressDialog.show(AllAlbumsView.this, null, "Loading...");
	        }
			@Override
			public void scanComplete(Cursor imageCursor) {

                if(imageCursor==null){
                	return;
                }
                while(imageCursor.moveToNext()){
                	// Path get pictures
                    String path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)); 
                    String parentName=new File(path).getParentFile().getName();
                    long times = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)); 
                    GridItem mGridItem = new GridItem(path, paserTimeToYMD(times, "yyyy-MM"));
                    if (!mGruopMap.containsKey(parentName)) {  
                        List<GridItem> chileList = new ArrayList<GridItem>();  
                        chileList.add(mGridItem);  
                        mGruopMap.put(parentName, chileList);  
                    } else {  
                        mGruopMap.get(parentName).add(mGridItem);  
                    }  
                } 
                mHandler.sendEmptyMessage(SCAN_OK);  
                imageCursor.close();
			}
	    		
	    }); 
        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                List<GridItem> childList = mGruopMap.get(lstImageBeans.get(position).getFolderName());   
                  
                Intent mIntent = new Intent(AllAlbumsView.this, AlbumView.class);  
                mIntent.putExtra("data_album", (ArrayList<GridItem>)childList);  
                startActivity(mIntent);  
				finish();
            }
        });  
	}

    /**
     * The number of milliseconds loaded into pattern this format, I here is converted to date
     * @param time
     * @param pattern
     * @return
     */ 
    public static String paserTimeToYMD(long time, String pattern ) { 
        System.setProperty("user.timezone", "Canada/Eastern"); 
        TimeZone tz = TimeZone.getTimeZone("Canada/Eastern"); 
        TimeZone.setDefault(tz); 
        SimpleDateFormat format = new SimpleDateFormat(pattern); 
        return format.format(new Date(time * 1000L)); 
    } 

}
