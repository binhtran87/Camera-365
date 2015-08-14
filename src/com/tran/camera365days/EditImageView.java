package com.tran.camera365days;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tran.camera365days.util.filter.ColorHalftoneFilter;
import com.tran.camera365days.util.filter.ContourFilter;
import com.tran.camera365days.util.filter.EmbossFilter;
import com.tran.camera365days.util.filter.GainFilter;
import com.tran.camera365days.util.filter.InvertFilter;
import com.tran.camera365days.util.filter.LevelsFilter;
import com.tran.camera365days.util.filter.QuantizeFilter;
import com.tran.camera365days.util.filter.RGBAdjustFilter;
import com.tran.camera365days.util.filter.ShearFilter;
import com.tran.camera365days.util.filter.SolarizeFilter;
import com.tran.camera365days.util.filter.SphereFilter;
import com.tran.camera365days.util.filter.StampFilter;
import com.tran.camera365days.util.filter.ThresholdFilter;
import com.tran.camera365days.util.filter.TritoneFilter;
import com.tran.camera365days.util.filter.TwirlFilter;
import com.tran.camera365days.util.filter.WeaveFilter;
import com.tran.camera365days.util.filter.AndroidUtils;
import com.tran.camera365days.util.CropImageView;
 
public class EditImageView extends Activity {

	private static final int ROTATE_RIGHT = 90;
    private static final int ROTATE_LEFT = 270;

	private RelativeLayout toolbar_layout;
	ListView list;	
	private Button effectButton, cropButton, rotateButton, backButtuon, saveButton;
	private int toolId = 1; //display effect toolbar for default
	
	Bitmap croppedImage;
	CropImageView cropImageView;
	
	private boolean effect = true;
	private boolean crop = false;
	private boolean rotate = false;

	Bitmap imageProcessed;
	Bitmap mbitmap, imagePreview;
	String fileName, filePath;
	int width, height;
	boolean preview;
	int[] src, result;
    Intent  backActivity;
    
    private List<String> lstOfImagePaths= new ArrayList<String>();
	private int currentPosition;

     //all toolbars using for Edit Image
     String[] effectName = {"Purle", "Green", "Gain", "Invert", "Levels", "Quantize", "RGB", "Threshold",
                     		 "Solarize", "Sphere", "Twirl", "Halftone", "Contour", "Sand", "Emboss", "Stamp",
	                         "Weave", "Shear"} ;
     
     Integer[] imageEffectId = {
             R.drawable.tritone1, R.drawable.tritone2, R.drawable.gain, R.drawable.invert, R.drawable.levels,
             R.drawable.quantize, R.drawable.rgbadjust, R.drawable.threshold, R.drawable.solarize,
             R.drawable.sphere, R.drawable.twirl, R.drawable.halftone, R.drawable.contour, R.drawable.emboss1,
             R.drawable.emboss2, R.drawable.stamp, R.drawable.weave, R.drawable.shear };

     String[] rotateName = {"", "", "", ""};
     Integer[] imageRotateId = {
         R.drawable.rotate_left, R.drawable.rotate_right, R.drawable.flip2, R.drawable.flip1
	 };
 	 
  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setContentView(R.layout.edit_image_view);
    
    backButtuon = (Button) findViewById(R.id.back_button);
    backButtuon.setOnClickListener(onButtonClick);

    saveButton = (Button) findViewById(R.id.save_button);
    saveButton.setOnClickListener(onButtonClick);
	 
    effectButton = (Button) findViewById(R.id.effect_button);
    effectButton.setOnClickListener(onButtonClick);
 
    cropButton = (Button) findViewById(R.id.crop_button);
    cropButton.setOnClickListener(onButtonClick);

	rotateButton = (Button) findViewById(R.id.rotate_button);
    rotateButton.setOnClickListener(onButtonClick);

	//for displaying all toolbars
    toolbar_layout = (RelativeLayout) findViewById(R.id.toolbar);
		 
    if (toolId == 1) {          //Effect toolbar
    	makeToolbar(1);
    } else if (toolId == 2) {   //Crop toolbar
    	makeToolbar(2);
    } else if (toolId == 0) {   //invisible toolbar
    	makeToolbar(0);	
    }
    
    cropImageView = (CropImageView) findViewById(R.id.CropImageView);
    cropImageView.setInvisibleOverlay();
        
    fileName = getIntent().getExtras().getString("fileName");
    filePath = getIntent().getExtras().getString("filePath");
    preview = getIntent().getExtras().getBoolean("preview");

	if (preview) {
		imagePreview = decodeSampledBitmapFromFolder("/sdcard/Camera365/"+fileName, 640, 480);
	} else {
		//image from galery
		imagePreview = decodeSampledBitmapFromFolder(filePath, 640, 480);
	}
    
	width = imagePreview.getWidth();
    height = imagePreview.getHeight();
	cropImageView.setImageBitmap(imagePreview);
    
  }
  
  /** set functions for all buttons in MainView **/
  private View.OnClickListener onButtonClick = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
              switch(v.getId())
              {
                      case R.id.back_button:     	
                    		if (preview) {
                    			// with the image from the folder 'Camera365'
                    			backActivity= new Intent(EditImageView.this,ImagePreview.class);
                    			File latestFile = getLatestFilefromDir("/sdcard/Camera365");
                     			String fileName = latestFile.getName();
                     			backActivity.putExtra("fileName", fileName);
                     			backActivity.putExtra("preview", true);
                    		} else {
                    			// with the image from other folders
                    			backActivity= new Intent(EditImageView.this,ImagePreview.class);
                     			backActivity.putExtra("filePath", filePath);
                     			backActivity.putExtra("preview", false);
                    		}   				
                    		startActivity(backActivity);       
                    		finish();
                    		break;
                     	 
                      case R.id.save_button:       
						 //save image to My album and back to Image page view 
                     	 saveImageProcessed();
                     	 break;
                     	 
                      case R.id.effect_button:           
                     	 //display effect toolbar
                    	 makeToolbar(1);
                    	 
                    	 cropImageView.setInvisibleOverlay();
                    	 effect = true;
                    	 crop = false;
                    	 rotate = false;
                     	 break;
                     	 
                      case R.id.crop_button:         
            	         //display crop toolbar
                    	 makeToolbar(3);
                    	 
                    	 cropImageView.setVisibleOverlay();
                    	 effect = false;
                    	 crop = true;
                    	 rotate = false;
                      	 break;

				      case R.id.rotate_button:     
						 //display rotate toolbar 
				    	 makeToolbar(2);
				    	 
				    	 cropImageView.setInvisibleOverlay();
                    	 effect = false;
                    	 crop = false;
                    	 rotate = true;
                      	 break;

              }
       }
   };
   
   public void makeToolbar(int toolId) {
	   
	   if (toolId == 1) {  //Effect toolbar
		    toolbar_layout.setVisibility(View.VISIBLE);
			CustomList effectAdapter = new CustomList(EditImageView.this, effectName, imageEffectId);
		    list=(ListView)findViewById(R.id.list);
			list.setAdapter(effectAdapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {			                
				 
				@Override              
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {           
					doProcess(position);
				}      
				 
			 });

		 } else if (toolId == 2) {     //Rotate toolbar
			 toolbar_layout.setVisibility(View.VISIBLE);
			 CustomList rotateAdapter = new CustomList(EditImageView.this, rotateName, imageRotateId);
		     list=(ListView)findViewById(R.id.list);
			 list.setAdapter(rotateAdapter);
			 list.setOnItemClickListener(new AdapterView.OnItemClickListener() {			                
				 @Override              
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {           
					 //Toast.makeText(EditImageView.this, "You've choosen " +rotateName[+ position], Toast.LENGTH_SHORT).show();             

                     if (position == 0) {         //rotate left
                    	 cropImageView.rotateImage(ROTATE_LEFT);
					 } else if (position == 1) {  //rotate right
						 cropImageView.rotateImage(ROTATE_RIGHT);
                     } else if (position == 2) {  //flip image follow vertical
                    	 cropImageView.flip(1);
                     } else if (position == 3) {  //flip image follow horizontal
                    	 cropImageView.flip(2);
                     }
				 }      
				 
			 });
		 } else if (toolId == 3) {  //for crop
			 toolbar_layout.setVisibility(View.INVISIBLE);
		 } else if (toolId == 0) {  //for adjust bar
			 toolbar_layout.setVisibility(View.INVISIBLE);
		 }
   }
   
   /**
   * saving bitmap image to sdcard after processed
   *
   ***/
   public void saveImageProcessed() {
	   File savedImage;
	   
	   if (crop) {   
		   //crop image when clicked on save button
		   croppedImage = cropImageView.getCroppedImage();
		   cropImageView.setImageBitmap(croppedImage);
	   } 
	   
	   //save image processed to Sdcard	   
	   try {
		   if (imageProcessed != null) {
			   if (preview) {
				   savedImage = new File("/sdcard/Camera365/"+fileName);
			   } else {
				   //process for image from galery
				   savedImage = new File(filePath);
			   }
			   FileOutputStream outStream = new FileOutputStream(savedImage);  
			   BufferedOutputStream bos = new BufferedOutputStream(outStream);
			   imageProcessed.compress(CompressFormat.JPEG, 100, bos);
			   outStream.close();
			   Toast.makeText(EditImageView.this, "Picture saved to Sdcard", Toast.LENGTH_SHORT).show(); 
		   }
	   } catch (FileNotFoundException e) {
	   } catch (IOException e) {
	   }
   }
   
   public void doProcess(int position) {

	   //Change int Array into a bitmap
	   src = AndroidUtils.bitmapToIntArray(imagePreview);
	   
	   switch (position) {
	   case 0:
		   TritoneFilter tritoneFilter1 = new TritoneFilter();			 
		   tritoneFilter1.setHighColor(-6555543);
		   tritoneFilter1.setMidColor(-8761435);
		   tritoneFilter1.setShadowColor(-16777216);
		   
		   //Applies a filter.
		   result = tritoneFilter1.filter(src, width, height);
		   break;

	   case 1:
		   TritoneFilter tritoneFilter2 = new TritoneFilter();			 
		   tritoneFilter2.setHighColor(-12769326);
		   tritoneFilter2.setMidColor(-16000493);
		   tritoneFilter2.setShadowColor(-1957343);
		   
		   //Applies a filter.
		   result = tritoneFilter2.filter(src, width, height);
		   break;
		   
	   case 2:
		   GainFilter gainFilter = new GainFilter();			 
		   gainFilter.setGain(0.04f);			 
		   gainFilter.setBias(0.73f);
		   
		   //Applies a filter.
		   result = gainFilter.filter(src, width, height);
		   break;
	
	   case 3:
		   InvertFilter invertFilter = new InvertFilter();
		   
		   //Applies a filter.
		   result = invertFilter.filter(src, width, height);
		   break;
		   
	   case 4:
		   LevelsFilter levelFilter = new LevelsFilter();			 
		   levelFilter.setLowLevel(0.87f);
		   levelFilter.setHighLevel(0.83f);
		   levelFilter.setLowOutputLevel(0.14f);
		   levelFilter.setHighOutputLevel(0.97f);
		   
		   //Applies a filter.
		   result = levelFilter.filter(src, width, height);
		   break;
		   
	   case 5:
		   QuantizeFilter quantizeFilter = new QuantizeFilter();			 
		   quantizeFilter.setNumColors(4);
		   
		   //Applies a filter.
		   result = quantizeFilter.filter(src, width, height);
		   break;
		   
	   case 6:
		   RGBAdjustFilter rgbFilter = new RGBAdjustFilter();			 
		   rgbFilter.setRFactor(0.34f);
		   rgbFilter.setGFactor(0.78f);
		   rgbFilter.setBFactor(-0.88f);
		   
		   //Applies a filter.
		   result = rgbFilter.filter(src, width, height);
		   break;
		   
	   case 7:
		   ThresholdFilter thresholdFilter = new ThresholdFilter();			 
		   thresholdFilter.setLowerThreshold(111);
		   thresholdFilter.setUpperThreshold(236);
		   
		   //Applies a filter.
		   result = thresholdFilter.filter(src, width, height);
		   break;
		   
	   case 8:
		   SolarizeFilter solarFilter = new SolarizeFilter();
		   
		   //Applies a filter.
		   result = solarFilter.filter(src, width, height);
		   break;
		   
	   case 9:
		   SphereFilter sphereFilter = new SphereFilter();			 
		   sphereFilter.setCentreX(0.5f);
		   sphereFilter.setCentreY(0.5f);
		   sphereFilter.setRadius(220f);
		   sphereFilter.setRefractionIndex(1.81f);
		   
		   //Applies a filter.
		   result = sphereFilter.filter(src, width, height);
		   break;  
		   
	   case 10:
		   TwirlFilter twirlFilter = new TwirlFilter();			 
		   twirlFilter.setCentreX(0.5f);
		   twirlFilter.setCentreY(0.5f);
		   twirlFilter.setAngle(1.38f);
		   twirlFilter.setRadius(480.0f);
		   
		   //Applies a filter.
		   result = twirlFilter.filter(src, width, height);
		   break;  
		   
	   case 11:
		   ColorHalftoneFilter halftoneFilter = new ColorHalftoneFilter();			 
		   halftoneFilter.setdotRadius(1.0f);
		   halftoneFilter.setCyanScreenAngle(0);
		   halftoneFilter.setMagentaScreenAngle(0);
		   halftoneFilter.setYellowScreenAngle(0);
		   
		   //Applies a filter.
		   result = halftoneFilter.filter(src, width, height);
		   break;  
		   
	   case 12:
		   ContourFilter contourFilter = new ContourFilter();			 
		   contourFilter.setLevels(29.0f);
		   contourFilter.setOffset(0.97f);
		   contourFilter.setScale(0.97f);
		   
		   //Applies a filter.
		   result = contourFilter.filter(src, width, height);
		   break; 
		   
	   case 13:
		   EmbossFilter embossFilter1 = new EmbossFilter();			 
		   embossFilter1.setAzimuth(0.16f);
		   embossFilter1.setElevation(0.29f);
		   embossFilter1.setBumpHeight(0);
		   
		   //Applies a filter.
		   result = embossFilter1.filter(src, width, height);
		   break; 
	   case 14:
		   EmbossFilter embossFilter2 = new EmbossFilter();			 
		   embossFilter2.setAzimuth(0.57f);
		   embossFilter2.setElevation(0.29f);
		   embossFilter2.setBumpHeight(0.97f);
		   
		   //Applies a filter.
		   result = embossFilter2.filter(src, width, height);
		   break;  
		   
	   case 15:
		   StampFilter stampFilter = new StampFilter();			 
		   stampFilter.setRadius(3.0f);
		   stampFilter.setThreshold(0.73f);
		   stampFilter.setSoftness(0.39f);
		   stampFilter.setBlack(-9072125);
		   stampFilter.setWhite(-1);
		   
		   //Applies a filter.
		   result = stampFilter.filter(src, width, height);
		   break;  
		   
	   case 16:
		   WeaveFilter weaveFilter = new WeaveFilter();			 
		   weaveFilter.setXWidth(40.0f);
		   weaveFilter.setYWidth(40.0f);
		   weaveFilter.setXGap(4.0f);
		   weaveFilter.setYGap(4.0f);
		   
		   //Applies a filter.
		   result = weaveFilter.filter(src, width, height);
		   break; 
		   
	   case 17:
		   ShearFilter shearFilter = new ShearFilter();			 
		   shearFilter.setXAngle(0.14f);
		   shearFilter.setYAngle(0.32f);
		   
		   //Applies a filter.
		   result = shearFilter.filter(src, width, height);
		   break; 
		  
		   
       default: 
    	   
    	   break;
      
	   }
	   
	   //Change the Bitmap int Array (Supports only ARGB_8888)
	   imageProcessed = Bitmap.createBitmap(result, width, height, Config.ARGB_8888);
       cropImageView.setImageBitmap(imageProcessed);
   }

/**
* Bitmap processing for loading 
*
***/
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
  
   public File getLatestFilefromDir(String dirPath){
	    
 	  File dir = new File(dirPath);
 	  File[] files = dir.listFiles();
 
 	  if (files == null || files.length == 0) {
 		  return null;
 	  }

 	  File lastModifiedFile = files[0];
 
 	  for (int i = 1; i < files.length; i++) {
 		  if (lastModifiedFile.lastModified() < files[i].lastModified()) {
 			  lastModifiedFile = files[i];
 		  }
 	  }

 	  return lastModifiedFile;
   }
   	
   @Override  	 	
   public void onBackPressed() {    	
	   super.onBackPressed();  	
   }  
}