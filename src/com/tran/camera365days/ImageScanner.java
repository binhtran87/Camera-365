package com.tran.camera365days;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

public class ImageScanner {
	 private Context mContext; 
     
	    public ImageScanner(Context context){ 
	        this.mContext = context; 
	    } 
	     
	    /**
	     * Using ContentProvider scanning of mobile phone in the picture, will scan the Cursor callback to ScanCompleteCallBack
	     * ScanComplete interface, this method in the running thread in the sub
	     */ 
	    public void scanImages(final ScanCompleteCallBack callback) { 
	    //public void scanImages() { 
	        final Handler mHandler = new Handler() { 
	 
	            @Override 
	            public void handleMessage(Message msg) { 
	                super.handleMessage(msg); 
	                callback.scanComplete((Cursor)msg.obj); 
	            } 
	        }; 
	         
	        new Thread(new Runnable() { 
	 
	            @Override 
	            public void run() { 
	                //Send broadcast scan the entire SD card
//	                mContext.sendBroadcast(new Intent(  
//	                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,  
//	                            Uri.parse("file://" + Environment.getExternalStorageDirectory()))); 
//	                 
	                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; 
	                ContentResolver mContentResolver = mContext.getContentResolver(); 
	                //final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
	        		final String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC"; //ASC
	        		//String folderName="%/Camera/%";
	                //Cursor imageCursor = mContentResolver.query( mImageUri,
	                //		null,MediaStore.Images.Media.DATA + " like ? ",new String[] {"%/Camera/%"},orderBy);
	                Cursor imageCursor = mContentResolver.query( mImageUri,
	                		null,MediaStore.Images.Media.MIME_TYPE + "=? or " 
	                		 + MediaStore.Images.Media.MIME_TYPE + "=?",new String[] { "image/jpeg", "image/png" },orderBy);
	                //Cursor mCursor = mContentResolver.query(mImageUri, null, null, null, MediaStore.Images.Media.DATE_ADDED); 
	                 
	                //Using Handler to inform the calling thread
	                Message msg = mHandler.obtainMessage(); 
	                msg.obj = imageCursor; 
	                mHandler.sendMessage(msg); 
	            } 
	        }).start(); 
	 
	    } 
	     
	    /**
	     * After the completion of the callback interface scanning
	     *
	     */ 
	    public static interface ScanCompleteCallBack{ 
	        public void scanComplete(Cursor cursor); 
	    }

}
