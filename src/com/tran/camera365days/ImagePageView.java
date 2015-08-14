package com.tran.camera365days;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tran.camera365days.util.VerticalViewPager;
 
public class ImagePageView extends Activity {

	// Declare Variable
	ImageView imageview;
	Button editButton, shareButton, deleteButton;
    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.75f;
    
	private List<String> lstOfImagePaths= new ArrayList<String>();
	private int position,currentPosition;
	private ImagePageViewAdapter imgAdapter;
	private VerticalViewPager verticalViewPager;
	
  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setContentView(R.layout.image_page_view);

	verticalViewPager=(VerticalViewPager) findViewById(R.id.pager);
    
    //Get the list of the paths of images from the previous activity
    lstOfImagePaths=getIntent().getStringArrayListExtra("list_of_paths_of_images");  
    position = getIntent().getIntExtra("POSITION_OF_IMAGE",0); //the default value is the first one.   
    imgAdapter= new ImagePageViewAdapter(this,lstOfImagePaths);
    verticalViewPager.setAdapter(imgAdapter); 
    verticalViewPager.setCurrentItem(position);
    verticalViewPager.setPageMarginDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_green_dark)));
    verticalViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
       
    	@Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationY(vertMargin - horzMargin / 2);
                } else {
                    view.setTranslationY(-vertMargin + horzMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    });

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
                    	  Intent editImageViewIntent = new Intent(ImagePageView.this, EditImageView.class);
                    	  currentPosition = verticalViewPager.getCurrentItem();  
                    	  editImageViewIntent.putExtra("filePath", lstOfImagePaths.get(currentPosition)); 
                    	  editImageViewIntent.putExtra("preview", false);
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
   
   // Method to share any image.
   private void shareImage() {
       Intent share = new Intent(Intent.ACTION_SEND);

       share.setType("image/*");

       currentPosition = verticalViewPager.getCurrentItem();
       
       String imagePath = lstOfImagePaths.get(currentPosition);

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
	        	currentPosition = verticalViewPager.getCurrentItem();	            
	            String imagePath = lstOfImagePaths.get(currentPosition);

	        	File fileToDelete =  new File(imagePath);
	        	//boolean fileDeleted =  fileToDelete.delete();
	             
	            // Set up the projection (we only need the ID)
	            String[] projection = { MediaStore.Images.Media._ID };

	            // Match on the file path
	            String selection = MediaStore.Images.Media.DATA + " = ?";
	            String[] selectionArgs = new String[] { fileToDelete.getAbsolutePath() };

	            // Query for the ID of the media matching the file path
	            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            ContentResolver contentResolver = getContentResolver();
	            Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
	            if (c.moveToFirst()) {
	                // We found the ID. Deleting the item via the content provider will also remove the file
	                long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
	                Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
	                contentResolver.delete(deleteUri, null, null);
	            } else {
	                // File not found in media store DB
	            }
	            c.close();
	        	
	        	Toast.makeText(ImagePageView.this, "Picture deleted", Toast.LENGTH_SHORT).show(); 

	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            //No button clicked
	            break;
	        }
	    }
	};

	public String getFolderPath(String imagePath) {
	    String folderPath = "";
		int lastIndex = 0;

        lastIndex = imagePath.lastIndexOf("/"); 
		folderPath = imagePath.substring(0,lastIndex);

		return folderPath;
	}

}