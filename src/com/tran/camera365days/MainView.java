package com.tran.camera365days;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.tran.camera365days.ui.DrawingView;


/**
 * Displaying and processing the main actions
 *
 */
public class MainView extends Activity implements CameraCallback{

	 static boolean active = false;

	 private FrameLayout cameraHolder = null;
     private CameraSurfaceView cameraSurfaceView = null;
     private Context myContext;

     private Button capture, switchCamera, functions, setting;
 	 private ImageButton flash;
     private ImageView preview, arrow_up, arrow_down;
     private ImageView scene_frame, sticker, checkbox1, checkbox2, checkbox3; 
     private Bitmap bitmapPrev;
     private RelativeLayout toolbar_layout, settingbar_layout;
     private DrawingView drawingView;
     private SeekBar volumeControl = null;
     ListView list;

     private boolean enableFlash = false;
     
     private boolean cameraFront;
	 private boolean faceDetection;
     public int toolId = 0;
     private boolean hasBackground;
     private boolean hasFrame;
     private boolean hasSticker;
     
     private int xVal, yVal;
     
     File latestFile, storagePath;
     int toolbar_position = 0;
     Drawable drawable;
     private boolean settingVisible = false;

     
     //for making all toolbars
     String[] effectName = {"None", "Mono", "Negative", "Posterize", "Sepia", "Solarize", "Aqua"};
     Integer[] imageEffectId = {
         R.drawable.none, R.drawable.mono, R.drawable.negative, R.drawable.posterize,
         R.drawable.sepia, R.drawable.solarize_icon, R.drawable.aqua_icon
     };
     
     String[] modeName = {"Auto", "Sunset", "Beach", "Candle", "Fireworks", "Snow", "Landscape", "Theatre"} ;
     Integer[] imageModeId = {
         R.drawable.auto, R.drawable.sunset, R.drawable.beach, R.drawable.candle, R.drawable.fireworks,
         R.drawable.snow, R.drawable.landscape, R.drawable.theatre
     };
     
     String[] backgroundName = {"Paris", "Sydney", "Pisa", "Sky", "Lightning", "Snowing", "Autumn"};
     Integer[] imageBackgroundId = {
         R.drawable.paris_icon, R.drawable.sydney_icon, R.drawable.pisa_icon,
	     R.drawable.sky_icon, R.drawable.lightning_icon, R.drawable.snowing_icon, R.drawable.autumn_icon
     };
 	 
     String[] frameName = {"", "", "", "", "", "", ""};
     Integer[] imageFramedId = {
         R.drawable.frame1_icon, R.drawable.frame2_icon, R.drawable.frame3_icon, R.drawable.frame4_icon, 
		 R.drawable.frame5_icon, R.drawable.frame6_icon, R.drawable.frame7_icon
     };
     
     String[] stickerName = {"", "", "", "", "", "", "", "", "", "", "", ""};
     Integer[] imageStickerdId = {
         R.drawable.sticker1, R.drawable.sticker2, R.drawable.sticker3, R.drawable.sticker4, R.drawable.sticker5, 
         R.drawable.sticker6, R.drawable.sticker7, R.drawable.sticker8, R.drawable.sticker9, R.drawable.sticker10, 
         R.drawable.sticker11, R.drawable.sticker12  
     };
     
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
         setContentView(R.layout.main_view);
         
         //setting brightness for camera mainview
         WindowManager.LayoutParams layout = getWindow().getAttributes();
         layout.screenBrightness = 0.37F;
         getWindow().setAttributes(layout);
         
         myContext = this;
         
         cameraHolder = (FrameLayout)findViewById(R.id.frame_preview);
         
 	     Intent intent = getIntent();	    
 	     cameraFront = intent.getExtras().getBoolean("cameraFront");
         toolId = intent.getExtras().getInt("toolId");
         faceDetection = intent.getExtras().getBoolean("faceDetection");
         hasBackground = intent.getExtras().getBoolean("hasBackground");
         hasFrame = intent.getExtras().getBoolean("hasFrame");
         hasSticker = intent.getExtras().getBoolean("hasSticker");
         
         setupCaptureMode(cameraFront, faceDetection, hasBackground, hasFrame, hasSticker);

         //setup UI buttons
         setting = (Button) findViewById(R.id.setting_button);
         setting.setOnClickListener(onButtonClick);
 		 
 		 flash = (ImageButton) findViewById(R.id.flash_button);
 		 flash.setOnClickListener(onButtonClick);
 		
 		 arrow_up = (ImageView) findViewById(R.id.arrow_button_up);
 		 arrow_up.setOnClickListener(onButtonClick);
 		 arrow_up.setVisibility(android.view.View.VISIBLE);
 		 
 	 	 arrow_down = (ImageView) findViewById(R.id.arrow_button_down);
		 arrow_down.setOnClickListener(onButtonClick);
		 arrow_down.setVisibility(android.view.View.INVISIBLE);
 		 
 		 capture = (Button) findViewById(R.id.capture_button);
 		 capture.setOnClickListener(onButtonClick);
 				
 		 switchCamera = (Button) findViewById(R.id.switch_button);
 		 switchCamera.setOnClickListener(onButtonClick);
 		
 		 functions = (Button) findViewById(R.id.functions_button);
 		 functions.setOnClickListener(onButtonClick);
		 
		 drawingView = (DrawingView) findViewById(R.id.drawing_surface);
		 cameraSurfaceView.setDrawingView(drawingView);
		
		 scene_frame = (ImageView) findViewById(R.id.scene_frame);
		 sticker = (ImageView) findViewById(R.id.sticker);
		 
		//make folder for My Album if it is not exist
	     storagePath = new File(Environment.getExternalStorageDirectory() + "/Camera365/"); 
	     if (!storagePath.exists()) {
	    	 //if you cannot make this folder return
	    	 if (!storagePath.mkdirs()) {
	    		 //warning 
	    	 }
	     }

		 BitmapFactory.Options options = new BitmapFactory.Options();
         options.inPreferredConfig = Config.RGB_565;
		 
 		 preview = (ImageView) findViewById(R.id.preview_button);
 		 preview.setOnClickListener(onButtonClick);
 		 
		 //set icon for preview_button with the latest image
 		 //if not exist, set preview_icon 
		 latestFile = getLatestFilefromDir("/sdcard/Camera365");
		 if (latestFile != null) {
			 bitmapPrev = BitmapFactory.decodeFile(latestFile.getAbsolutePath(),options);
		     preview.setImageBitmap(bitmapPrev);
		 } else {
			 preview.setImageResource(R.drawable.preview_icon);
		 }
	     
	     MarginLayoutParams mlp = (MarginLayoutParams) preview.getLayoutParams();
	     mlp.leftMargin = 8;
	     mlp.rightMargin = 12;
	     mlp.bottomMargin = 50;
	     mlp.height = 70;
	     mlp.width = 70;
         preview.setLayoutParams(mlp);
         
         //for display setting bar
         settingbar_layout = (RelativeLayout) findViewById(R.id.settingbar);
         checkbox1 = (ImageView) findViewById(R.id.checkbox1);
         checkbox1.setOnClickListener(onButtonClick);
         checkbox2 = (ImageView) findViewById(R.id.checkbox2);
         checkbox2.setOnClickListener(onButtonClick);
         checkbox3 = (ImageView) findViewById(R.id.checkbox3);
         checkbox3.setOnClickListener(onButtonClick);
		 
		 //for displaying all toolbars
		 toolbar_layout = (RelativeLayout) findViewById(R.id.toolbar);
		 
		 if (toolId == 1) {  //Mode toolbar
			 CustomList modeAdapter = new CustomList(MainView.this, modeName, imageModeId);
		     list=(ListView)findViewById(R.id.list);
			 list.setAdapter(modeAdapter);
			 list.setOnItemClickListener(new AdapterView.OnItemClickListener() {			                
				 @Override              
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {           
					 Toast.makeText(MainView.this, "You've choosen " +modeName[+ position] +" mode", Toast.LENGTH_SHORT).show();             
					 cameraSurfaceView.changeMode(position);		 
				 }      
				 
			 });
		 } else if (toolId == 2) {   //Contrast effect toolbar
			 CustomList effectAdapter = new CustomList(MainView.this, effectName, imageEffectId);
		     list=(ListView)findViewById(R.id.list);
			 list.setAdapter(effectAdapter);
			 list.setOnItemClickListener(new AdapterView.OnItemClickListener() {			                
				 @Override              
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {           
					 Toast.makeText(MainView.this, "You've choosen " +effectName[+ position] +" effect", Toast.LENGTH_SHORT).show();             
					 cameraSurfaceView.changeEffect(position);		 
				 }      
				 
			 });
		 } else if (toolId == 3) {     //Background toolbar
			 CustomList backgroundAdapter = new CustomList(MainView.this, backgroundName, imageBackgroundId);
		     list=(ListView)findViewById(R.id.list);
			 list.setAdapter(backgroundAdapter);
			 list.setOnItemClickListener(new AdapterView.OnItemClickListener() {			                
				 @Override              
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {           
					 Toast.makeText(MainView.this, "You've choosen " +backgroundName[+ position] +" background", Toast.LENGTH_SHORT).show();             
					 //scene_frame.setImageResource(imageBackgroundIndex(position));
					 scene_frame.setBackgroundResource(imageBackgroundIndex(position));
					 toolbar_position = position;
				 }      
				 
			 });
		 } else if (toolId == 4) {      //Frame toolbar
			 CustomList frameAdapter = new CustomList(MainView.this, frameName, imageFramedId);
		     list=(ListView)findViewById(R.id.list);
			 list.setAdapter(frameAdapter);
			 list.setOnItemClickListener(new AdapterView.OnItemClickListener() {			                
				 @Override              
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {           
					 Toast.makeText(MainView.this, "You've choosen " +frameName[+ position] +" frame", Toast.LENGTH_SHORT).show();             
					 //scene_frame.setImageResource(imageFrameIndex(position));
					 scene_frame.setBackgroundResource(imageFrameIndex(position));
					 toolbar_position = position;
				 }      
				 
			 });
		 } else if (toolId == 5) {      //Sticker toolbar
			 CustomList stickerAdapter = new CustomList(MainView.this, stickerName, imageStickerdId);
		     list=(ListView)findViewById(R.id.list);
			 list.setAdapter(stickerAdapter);
			 list.setOnItemClickListener(new AdapterView.OnItemClickListener() {			                
				 @Override              
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                     
					 sticker.setImageResource(imageStickerIndex(position));
					 toolbar_position = position;
				 }      
				 
			 });
		 } else if (toolId == 0) {  //disable all toolbars
			 toolbar_layout.setVisibility(View.INVISIBLE);
        	 arrow_up.setVisibility(View.INVISIBLE);
        	 arrow_down.setVisibility(View.INVISIBLE);
		 }

		 //displaying Seek-bar for zooming
		 volumeControl = (SeekBar) findViewById(R.id.seek1);

		 volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			 int progressChanged = 0;
			 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				 progressChanged = progress;
			 }
			 public void onStartTrackingTouch(SeekBar seekBar) {
				 // TODO Auto-generated method stub
			 }
			 public void onStopTrackingTouch(SeekBar seekBar) {
				 Toast.makeText(MainView.this,"seek bar progress:"+progressChanged, 
						 Toast.LENGTH_SHORT).show();
				 cameraSurfaceView.zoomScrolling(progressChanged);
			 }
		 });
		 
     }
     
     private void setupCaptureMode(boolean front, boolean face, boolean background, boolean frame, boolean sticker) {
    	 cameraSurfaceView = new CameraSurfaceView(this, front, face, background, frame, sticker);         
         cameraHolder.addView(cameraSurfaceView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));        
         cameraSurfaceView.setCallback(this);
     }

     @Override
     public void onJpegPictureTaken(byte[] data, Camera camera) {

	     //take the current timeStamp and setting name for Image
	     String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	     File myImage = new File(storagePath, "IMG_" + timeStamp + ".jpg");
    	 
	     
	     if (hasFrame || hasBackground || hasSticker) {
	    	 //Image
	    	 BitmapFactory.Options options = new BitmapFactory.Options();
	         options.inPreferredConfig = Config.RGB_565;
	         
	    	 Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

	    	 int  width = cameraBitmap.getWidth();
		     int  height = cameraBitmap.getHeight();
        
		     
	    	 Bitmap cs = null; 
	    	 cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 
	    	 
	    	 Canvas comboImage = new Canvas(cs); 
		     
	    	 comboImage.drawBitmap(cameraBitmap, 0f, 0f, null); 
	    	 
		     //drawing frame/background or sticker onto the Image
		     if (hasBackground) {    	 
		    	 Bitmap icon_background = BitmapFactory.decodeResource(getResources(),
		    			 imageBackgroundIndex(toolbar_position), options);
		    	 comboImage.drawBitmap(icon_background, 0f, 0f, null); 
		    	 
		     } else if (hasFrame) {
		    	 Bitmap icon_frame = BitmapFactory.decodeResource(getResources(),
		    			 imageFrameIndex(toolbar_position), options);
		    	 comboImage.drawBitmap(icon_frame, 0f, 0f, null); 
		     } else if (hasSticker){
		    	 Bitmap icon_sticker = BitmapFactory.decodeResource(getResources(),
		    			 imageStickerIndex(toolbar_position), options);
		    	 		    	 		    	 
    			 if(cameraFront) {
    				 Matrix matrix = new Matrix();            
        			 matrix.postRotate(180);           
        			 Bitmap icon_sticker_invert = Bitmap.createBitmap(icon_sticker, 0, 0, icon_sticker.getWidth(), icon_sticker.getHeight(), matrix, true);
    		    	 
        			 comboImage.drawBitmap(icon_sticker_invert, xVal/1.5f, yVal/1.5f, null); 
    			  } else {
    				 comboImage.drawBitmap(icon_sticker, xVal/1.5f, yVal/1.5f, null); 
    			 }
		     }
     
	         //Save Image and frame overlay
		     try {	    	 
		    	 FileOutputStream out = new FileOutputStream(myImage);
		    	 cs.compress(Bitmap.CompressFormat.JPEG, 100, out);
		    	 
		    	 out.flush();
		    	 out.close();
		    	 
		     } catch(FileNotFoundException e) {
		    	 Log.d("In Saving File", e + "");    
		     } catch(IOException e) {
		    	 Log.d("In Saving File", e + "");
		     }
		     
		     
		     //rotatating image if taken by front camera - it is not a good way
    		 if (cameraFront) {	    		
    			 File oldImage1 = new File("/sdcard/Camera365/"+myImage.getName());
    			 
    			 //BitmapFactory.Options options = new BitmapFactory.Options();
    	         //options.inPreferredConfig = Config.RGB_565;
    			 
    			 Bitmap loadImage1 = BitmapFactory.decodeFile("/sdcard/Camera365/"+oldImage1.getName(),options);    		
    			 Matrix matrix1 = new Matrix();            
    			 matrix1.postRotate(180);           
    			 Bitmap realImage1 = Bitmap.createBitmap(loadImage1, 0, 0, loadImage1.getWidth(), loadImage1.getHeight(), matrix1, true);
	
    			 try {	  
    				 FileOutputStream output1 = new FileOutputStream(oldImage1);
			
    				 realImage1.compress(Bitmap.CompressFormat.JPEG, 100, output1); 				
    				 output1.flush(); 				
    				 output1.close();    		
    			 } catch (Exception e) {	 				
    				 e.printStackTrace();	    		
    			 }
    		 }
    		 
		     
		     Toast toast = Toast.makeText(myContext, "Picture saved in your SdCard", Toast.LENGTH_LONG);
		     toast.show();

		     //change image of Preview icon after taken photo
		     //BitmapFactory.Options options = new BitmapFactory.Options();
	         //options.inPreferredConfig = Config.RGB_565;
	         
		     bitmapPrev = BitmapFactory.decodeFile("/sdcard/Camera365/"+myImage.getName(),options);
		     preview.setImageBitmap(bitmapPrev);
		     
		     MarginLayoutParams mlp = (MarginLayoutParams) preview.getLayoutParams();
		     mlp.leftMargin = 8;
		     mlp.rightMargin = 12;
		     mlp.bottomMargin = 50;
		     mlp.height = 70;
		     mlp.width = 70;
		     preview.setLayoutParams(mlp);
		     
	     } else {
	    	 try {
	    		 //write all data to picture file
	    		 FileOutputStream fos = new FileOutputStream(myImage);
	    		 fos.write(data);
	    		 fos.close();
	    		 
                 //rotatating image if taken by front camera - it is not a good way
	    		 if (cameraFront) {	    		
	    			 File oldImage2 = new File("/sdcard/Camera365/"+myImage.getName());
	    			 
	    			 BitmapFactory.Options options = new BitmapFactory.Options();
	    	         options.inPreferredConfig = Config.RGB_565;
	    			 
	    			 Bitmap loadImage2 = BitmapFactory.decodeFile("/sdcard/Camera365/"+oldImage2.getName(),options);    		
	    			 Matrix matrix2 = new Matrix();            
	    			 matrix2.postRotate(180);           
	    			 Bitmap realImage2 = Bitmap.createBitmap(loadImage2, 0, 0, loadImage2.getWidth(), loadImage2.getHeight(), matrix2, true);
		
	    			 try {	  
	    				 FileOutputStream output2 = new FileOutputStream(oldImage2);
				
	    				 realImage2.compress(Bitmap.CompressFormat.JPEG, 100, output2); 				
	    				 output2.flush(); 				
	    				 output2.close();    		
	    			 } catch (Exception e) {	 				
	    				 e.printStackTrace();	    		
	    			 }
	    		 }


	    		 Toast toast = Toast.makeText(myContext, "Picture saved in your SdCard", Toast.LENGTH_LONG);
	    		 toast.show();
	    		 
	    		 
	    		 //change image of Preview icon after taken photo
	    		 BitmapFactory.Options options = new BitmapFactory.Options();
	             options.inPreferredConfig = Config.RGB_565;
	    		 
	    		 bitmapPrev = BitmapFactory.decodeFile("/sdcard/Camera365/"+myImage.getName(),options);
			     preview.setImageBitmap(bitmapPrev);

				 MarginLayoutParams mlp = (MarginLayoutParams) preview.getLayoutParams();
			     mlp.leftMargin = 8;
			     mlp.rightMargin = 12;
			     mlp.bottomMargin = 50;
			     mlp.height = 70;
			     mlp.width = 70;
				 preview.setLayoutParams(mlp);
	    		 
	    	 } catch (FileNotFoundException e) {
	    	 } catch (IOException e) {
	    	 } 
	     }
	     
	     //Send broadcast scan the entire SD card
		 sendBroadcast(new Intent(  
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,  
                Uri.fromFile(new File("/sdcard/Camera365/"+myImage.getName())))); 

	     cameraSurfaceView.startPreview();

     }
     
     @Override
     public void onPreviewFrame(byte[] data, Camera camera) {
     }

     @Override
     public void onRawPictureTaken(byte[] data, Camera camera) {
     }
     
     @Override
     public void onShutter() {
     }

     /** set functions for all buttons in MainView **/
     private View.OnClickListener onButtonClick = new View.OnClickListener() {
         @Override
         public void onClick(View v) {
                 switch(v.getId())
                 {
                         case R.id.setting_button:     
                        	 //setting bar will invisible when clicked again
                        	 if (settingVisible) {
                        		 settingbar_layout.setVisibility(View.INVISIBLE);
                        		 settingVisible = false;
                        	 } else {
                        		 settingbar_layout.setVisibility(View.VISIBLE);
                        		 settingVisible = true;
                        	 }                    	 
                        	 break;
                        	 
                         case R.id.switch_button:    
							if (!cameraFront) {
                        		 cameraFront = true;
								 //disable flash for front camera
    		                     enableFlash = false; 
    		                     flash.setImageResource(R.drawable.flash_off);
                        	 } else {
                        		 cameraFront = false;
                                 enableFlash = true; 
                        	 }
                        	 //get the number of cameras        		
                        	 int camerasNumber = Camera.getNumberOfCameras();
                        	 if (camerasNumber > 1) {
                        		 //release the old camera instance and switching camera
                        		 cameraSurfaceView.releaseCamera();
                        		 cameraSurfaceView.chooseCamera();

                        	 } else {
                        		 Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                     			 toast.show();
                        	 }
                        	 break;
                        	 
                         case R.id.flash_button:       
                        		 if (cameraSurfaceView.turnONFlash(enableFlash)) {        		 
                        			 flash.setImageResource(R.drawable.flash_on);		 
                        			 enableFlash = false;
                        		 } else { 
                        			 flash.setImageResource(R.drawable.flash_off);     		
                        			 enableFlash = true;
                        		 }

                        	 break;
                        	 
                         case R.id.functions_button:               
                        	 Intent functionsIntent = new Intent(MainView.this, FunctionsView.class);
                             startActivity(functionsIntent);    
 
                        	 break;
                        	 
                         case R.id.capture_button:            
                        	 cameraSurfaceView.takePicture();       	 
                        	 break;
                        	 
                         case R.id.preview_button:               
                 			 Intent imagePreviewIntent = new Intent(MainView.this, ImagePreview.class);
                 			 File latestFile = getLatestFilefromDir("/sdcard/Camera365");
                 			 String fileName = latestFile.getName();
                 			 imagePreviewIntent.putExtra("fileName", fileName);
							 imagePreviewIntent.putExtra("preview", true);
                             startActivity(imagePreviewIntent);    
                             //finish();
							 break;
                        	 
                         case R.id.arrow_button_up:   
                        	 toolbar_layout.setVisibility(View.VISIBLE);
                             arrow_up.setVisibility(View.INVISIBLE);
                        	 arrow_down.setVisibility(View.VISIBLE);
                        	 break;
                        	 
                         case R.id.arrow_button_down:                        	     
                        	 toolbar_layout.setVisibility(View.INVISIBLE);
                        	 arrow_up.setVisibility(View.VISIBLE);
                        	 arrow_down.setVisibility(View.INVISIBLE);
                        	 break; 	
                        	 
                         case R.id.checkbox1:                        	     
                        	 if (cameraSurfaceView.soundOn) {
                        		 checkbox1.setImageResource(R.drawable.uncheck_icon);
                        		 cameraSurfaceView.soundOn = false;
                        	 } else {
                        		 checkbox1.setImageResource(R.drawable.check_icon);
                        		 cameraSurfaceView.soundOn = true;
                        	 }
                        	 settingbar_layout.setVisibility(View.INVISIBLE);
                        	 break; 
                  
                         case R.id.checkbox2:                        	     
                             if (cameraSurfaceView.touch2Shoot) {
                            	 checkbox2.setImageResource(R.drawable.uncheck_icon);
                            	 cameraSurfaceView.touch2Shoot = false;
                        	 } else {
                        			 checkbox2.setImageResource(R.drawable.check_icon);
                            		 cameraSurfaceView.touch2Shoot = true;
                            	     checkbox3.setImageResource(R.drawable.check_icon);
                        		     cameraSurfaceView.autoFocus = true;                     		 
                        	 }
                             settingbar_layout.setVisibility(View.INVISIBLE);
                        	 break; 
                        	 
                         case R.id.checkbox3:                        	     
                             if (cameraSurfaceView.autoFocus) {
                            	 checkbox3.setImageResource(R.drawable.uncheck_icon);
                            	 cameraSurfaceView.autoFocus = false;
                        	 } else {
                        		 checkbox3.setImageResource(R.drawable.check_icon);
                        		 cameraSurfaceView.autoFocus = true;
                        	 }
                             settingbar_layout.setVisibility(View.INVISIBLE);
                        	 break; 
                 }
          }
      };

      //return index of image in Drawable resource
      public int imageBackgroundIndex(int position) {
  		int index=0; 
  		 			
  		switch (position)   			
  		{ 	
  		case 0: 		
  			index = R.drawable.paris; break;
  		case 1: 		
  			index = R.drawable.sydney; break;
  		case 2: 		
  			index = R.drawable.pisa; break;
  		case 3: 		
  			index = R.drawable.sky; break;
  		case 4: 
  			index = R.drawable.lightning; break;
  		case 5: 
  			index = R.drawable.snowing; break;
  		case 6: 		
  			index = R.drawable.autumn; break;
  		}
  		
  		return index;
      }    
       
      public int imageFrameIndex(int position) {
  		int index=0; 
  		if (position == 0) {
  			index = R.drawable.frame1;
  		} else if (position == 1) {
  			index = R.drawable.frame2;
  		} else if (position == 2) {
  			index = R.drawable.frame3;
  		} else if (position == 3) {
  			index = R.drawable.frame4;
  		} else if (position == 4) {
  			index = R.drawable.frame5;
  		} else if (position == 5) {
  			index = R.drawable.frame6;
  		} else if (position == 6) {
  			index = R.drawable.frame7;
  		}
  		
  		return index;
      }

     public int imageStickerIndex(int position) {
    	 int index = 0;
    	 if (position == 0) {
    		 index = R.drawable.sticker1;
    	 } else if (position == 1) {
  			index = R.drawable.sticker2;
    	 } else if (position == 2) {
  			index = R.drawable.sticker3;
    	 } else if (position == 3) {
  			index = R.drawable.sticker4;	
    	 } else if (position == 4) {
  			index = R.drawable.sticker5;	
    	 } else if (position == 5) {
  			index = R.drawable.sticker6;	
    	 } else if (position == 6) {
  			index = R.drawable.sticker7;  		
    	 } else if (position == 7) {
  			index = R.drawable.sticker8;
    	 } else if (position == 8) {
  			index = R.drawable.sticker9;
    	 } else if (position == 9) {
  			index = R.drawable.sticker10;	
    	 } else if (position == 10) {
  			index = R.drawable.sticker11;	
    	 } else if (position == 11) {
  			index = R.drawable.sticker12;	
    	 }
    	 return index;
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
      public boolean onTouchEvent(MotionEvent event) {
    	  if (hasSticker) {
    		  
    		  if(event.getAction() == MotionEvent.ACTION_DOWN){    	    		
    			  float x = event.getX();    	    		
    			  float y = event.getY();
  	   	     
    			  MarginLayoutParams mlp = (MarginLayoutParams) sticker.getLayoutParams();  		    
    			  mlp.height = 200;   		    
    			  mlp.width = 200;  		   
    			  mlp.topMargin = (int)y-100;  		    
    			  mlp.leftMargin = (int)x-100;   	        
    			  sticker.setLayoutParams(mlp);
    			  
    			  xVal = (int)x;
    			  yVal = (int)y;
    		  }
    		  
    	  }
    	  return false;
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
