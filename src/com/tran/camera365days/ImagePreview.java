package com.tran.camera365days;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.tran.camera365days.util.TouchImageView;
 
public class ImagePreview extends Activity {

	// Declare Variable
	ImageView imageview;

	Button editButton, shareButton, deleteButton, backButton;

	String fileName, filePath;	
	boolean preview;
	Bitmap imagePreview;
	
  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setContentView(R.layout.image_preview);
	
	Bundle extras = getIntent().getExtras();
	if(extras !=null) {
	    fileName = extras.getString("fileName");
	    filePath = extras.getString("filePath");
	    preview = getIntent().getExtras().getBoolean("preview");
	}
	    
	TouchImageView imageview = (TouchImageView) findViewById(R.id.imageView);

	if (preview) {	
		imageview.setImageBitmap(
		    decodeSampledBitmapFromFolder("/sdcard/Camera365/"+fileName, 640, 480));
	} else {
		imageview.setImageBitmap(
			    decodeSampledBitmapFromFolder(filePath, 640, 480));
	}
	
    editButton = (Button) findViewById(R.id.edit_button);
    editButton.setOnClickListener(onButtonClick);
	 
    shareButton = (Button) findViewById(R.id.share_button);
    shareButton.setOnClickListener(onButtonClick);
 
    deleteButton = (Button) findViewById(R.id.delete_button);
    deleteButton.setOnClickListener(onButtonClick);
    
  }

/** set functions for all buttons in MainView **/
  private View.OnClickListener onButtonClick = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
              switch(v.getId())
              {
                      case R.id.edit_button:        	
                     	 Intent editImageViewIntent = new Intent(ImagePreview.this, EditImageView.class);
                     	 if (preview) {
                     		editImageViewIntent.putExtra("fileName", fileName);
                     		editImageViewIntent.putExtra("preview", true);
                     	 } else {
                     		editImageViewIntent.putExtra("filePath", filePath);
                     		editImageViewIntent.putExtra("preview", false);
                     	 }
                     	 startActivity(editImageViewIntent);  
                         finish();
                     	 break;
                     	 
                      case R.id.share_button:              
                    	 shareImage();          
                     	 break;
                     	 
                      case R.id.delete_button:              
            	         showDeleteDialog();  
                      	 break;
              }
       }
   };
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
   

   public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
   
	   // Raw height and width of image
	   final int height = options.outHeight;
	   final int width = options.outWidth;
	   int inSampleSize = 1;
   
	   if (height > reqHeight || width > reqWidth) {
		   final int halfHeight = height / 2;
		   final int halfWidth = width / 2;
       
		   // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		   // height and width larger than the requested height and width.
		   while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
			   inSampleSize *= 2;
		   }
	   }
   
	   return inSampleSize;
   }
   
   public static Bitmap decodeSampledBitmapFromFolder(String fileName, int reqWidth, int reqHeight) {
	    
	   // First decode with inJustDecodeBounds=true to check dimensions	    
	   final BitmapFactory.Options options = new BitmapFactory.Options();
	    
	   options.inJustDecodeBounds = true;	    
	   BitmapFactory.decodeFile(fileName, options);
	    
	   // Calculate inSampleSize	    
	   options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    
	   // Decode bitmap with inSampleSize set	    
	   options.inJustDecodeBounds = false;
	   
	   return BitmapFactory.decodeFile(fileName, options);
   }

   // Method to share any image.
   private void shareImage() {
       Intent share = new Intent(Intent.ACTION_SEND);

       share.setType("image/*");

       String imagePath = "/sdcard/Camera365/"+fileName;

       File imageFileToShare = new File(imagePath);

       Uri uri = Uri.fromFile(imageFileToShare);
       share.putExtra(Intent.EXTRA_STREAM, uri);

       startActivity(Intent.createChooser(share, "Sharing this Image via:"));
   }
   
   private void showDeleteDialog() {
	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	   builder.setMessage("Do you want delete this image?").setPositiveButton("Yes", dialogClickListener)
	       .setNegativeButton("No", dialogClickListener).show();
   }
   
   DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	            //Yes button clicked
	        	File file = new File("/sdcard/Camera365/"+fileName);
	        	boolean deleted = file.delete();
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            //No button clicked
	            break;
	        }
	    }
	};

}