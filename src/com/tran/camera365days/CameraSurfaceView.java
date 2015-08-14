package com.tran.camera365days;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tran.camera365days.ui.DrawingView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener{    
	
	private boolean cameraFront;
	private boolean faceDetection;
	private boolean hasBackground;
	private boolean hasFrame;
	private boolean hasSticker;	
	
	private Camera mCamera = null;
    private SurfaceHolder holder = null;
    private CameraCallback callback = null;
    private GestureDetector gesturedetector = null;
    private String[] supportedColorEffects = null;
    private String[] supportedWhiteBalances = null;
    private int currentZoom = 0;
    private boolean isZoomIn = true;
    private boolean isStarted = true;
    private boolean takePicture = false;
    
    private DrawingView drawingView;
    private boolean drawingViewSet = false;
    
    boolean soundOn = true;
    boolean autoFocus = true;
    boolean touch2Shoot = false;
    
    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }
 
    public CameraSurfaceView(Context context, boolean front, boolean face, boolean background, boolean frame, boolean sticker) {
        super(context);      
        cameraFront = front;
		faceDetection = face;
		hasBackground = background;
		hasFrame = frame;
		hasSticker = sticker;
		
        initialize(context);
    }
    
    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);       
        initialize(context);
    }

    private void initialize(Context context) {
        holder = getHolder();
        
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        gesturedetector = new GestureDetector(this);

    }
    
    public void setCallback(CameraCallback callback){
        this.callback = callback;
    }


    public void startPreview(){
    	mCamera.startPreview();
    }
    
    /**
    *   TAKING PHOTO
    *  
    **/
    public void takePicture()
    {
    	takePicture = true;
    	if (cameraFront) {
    		if (soundOn) {
				mCamera.takePicture(shutterCallback, rawPictureCallback, picture);
			} else {
				mCamera.takePicture(null, rawPictureCallback, picture);
			}
    	} else {
    		mCamera.autoFocus(autoFocusCallback);
    	}

    }
    
    AutoFocusCallback autoFocusCallback = new AutoFocusCallback()
    {
    	public void onAutoFocus(boolean arg0, Camera camera) 
    	{
    		if ( takePicture )
    		{   
    			if (soundOn) {
    				camera.takePicture(shutterCallback, rawPictureCallback, picture);
    			} else {
    				camera.takePicture(null, rawPictureCallback, picture);
    			}
    			takePicture = false;
    		}
    	}
    };
    
    ShutterCallback shutterCallback = new ShutterCallback()
    {
    	 public void onShutter() 
    	 {
    		 //nothing.
    	 }
    };
    PictureCallback rawPictureCallback = new PictureCallback()
    {
    	 public void onPictureTaken(byte[] data, Camera camera) 
    	 {
    		 //nothing.
    	 }
    };
    PictureCallback picture = new PictureCallback() {
    	 @Override
         public void onPictureTaken(byte[] data, Camera camera){
                 if(null != callback) callback.onJpegPictureTaken(data, camera);
         }
    };
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
            /*if(null != mCamera && isStarted)
            {
            	mCamera.startPreview();
            }*/
    	Camera.Parameters parameters;		
		parameters = mCamera.getParameters();
	
		Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
	    parameters.setPreviewSize(s.width, s.height);
	    s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
	    
	    //set size for image to save on sdcard
	    if (hasFrame || hasBackground || hasSticker) {
	    	//for saving image and frame with low quality
	        parameters.setPictureSize(640, 480);
	    } else {
	        //for saving image with hight quality
	    	parameters.setPictureSize(s.width, s.height);
	    }
	    
	    //set size for preview image
        //parameters.setPreviewSize(1280, 960);      
		//imageFormat = parameters.getPreviewFormat();

		parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
	    if(parameters.isAutoExposureLockSupported()) {	    	
	    	parameters.setAutoExposureLock(false);
	    }

		mCamera.setParameters(parameters);
		mCamera.startPreview();
    }
    
    /** Getting the best size for the image supported by device **/
    private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	if (cameraFront) {
    		mCamera = Camera.open(findFrontFacingCamera());
    	} else {
    		mCamera = Camera.open(findBackFacingCamera());
    	}
    	
    	//setting initialization modes and exposure
    	Camera.Parameters params = mCamera.getParameters();
        //if (params.getSupportedWhiteBalance().contains(
        //		Camera.Parameters.WHITE_BALANCE_AUTO)) {
        //    params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        //}
        //if (params.getSupportedSceneModes().contains(
        //		Camera.Parameters.SCENE_MODE_LANDSCAPE)) {
        //   params.setSceneMode(Camera.Parameters.SCENE_MODE_LANDSCAPE);
        //}
    	params.setExposureCompensation(params.getMaxExposureCompensation());
    	if(params.isAutoExposureLockSupported()) {
    	 params.setAutoExposureLock(false);
    	}
        mCamera.setParameters(params);
    	
		//start face detection here
		if (faceDetection)
		{
            mCamera.setFaceDetectionListener(faceDetectionListener);
            mCamera.startFaceDetection();
		}
    	 
            try {
            	mCamera.setPreviewDisplay(holder);
            	mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                    if(null != callback) callback.onPreviewFrame(data, camera);
                            }
                    });
                    
                    final List<String> coloreffects = mCamera.getParameters().getSupportedColorEffects();
                    final List<String> whiteBalances = mCamera.getParameters().getSupportedWhiteBalance();
                    if(coloreffects != null)
                    {
                    	supportedColorEffects = new String[coloreffects.size()];
                    	coloreffects.toArray(supportedColorEffects);
                    }
                    if(whiteBalances != null)
                    {
                    	supportedWhiteBalances = new String[whiteBalances.size()];
                    	whiteBalances.toArray(supportedWhiteBalances);
                    }
                    
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
            isStarted = false;
            
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.setFaceDetectionListener(null);
            mCamera.release();           
            mCamera = null;
    }
    
/**
*   CHANGING EFFECT, MODE,... OF CAMERA 
*  
**/
    public void changeEffect(int positionEffect) {
    	Camera.Parameters parameters = mCamera.getParameters();
        
    	List<String> colorModes = parameters.getSupportedColorEffects();
      
        if (positionEffect == 0) {
        	parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
        } else if (positionEffect == 1) {
        	if (colorModes.contains(Camera.Parameters.EFFECT_MONO))
   			 parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
            else
           	 parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
        } else if (positionEffect == 2) {
        	if (colorModes.contains(Camera.Parameters.EFFECT_NEGATIVE))
   			 parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
            else
           	 parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
        } else if (positionEffect == 3) {
        	if (colorModes.contains(Camera.Parameters.EFFECT_POSTERIZE))
   			 parameters.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
            else
           	 parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
        } else if (positionEffect == 4) {
        	if (colorModes.contains(Camera.Parameters.EFFECT_SEPIA))
   			 parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
            else
           	 parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
        } else if (positionEffect == 5) {
        	if (colorModes.contains(Camera.Parameters.EFFECT_SOLARIZE))
   			 parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
            else
           	 parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
        } else if (positionEffect == 6) {
        	if (colorModes.contains(Camera.Parameters.EFFECT_AQUA))
   			 parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
            else
           	 parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
        }
        mCamera.setParameters(parameters);
    }
    
    public void changeMode(int positionMode) {
    	 Camera.Parameters parameters = mCamera.getParameters();
    	 List<String> sceneModes = parameters.getSupportedSceneModes();
         
         //parameters.setWhiteBalance(supportedWhiteBalances[positionMode]);
    	 if (positionMode == 0) {
    		 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    	 } else if (positionMode == 1) {
    		 if (sceneModes.contains(Camera.Parameters.SCENE_MODE_SUNSET))
    			 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SUNSET);
             else
            	 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    	 } else if (positionMode == 2) {
    		 if (sceneModes.contains(Camera.Parameters.SCENE_MODE_BEACH))
    			 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_BEACH);
             else
            	 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    	 } else if (positionMode == 3) {
    		 if (sceneModes.contains(Camera.Parameters.SCENE_MODE_CANDLELIGHT))
    			 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_CANDLELIGHT);
             else
            	 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    	 } else if (positionMode == 4) {
    		 if (sceneModes.contains(Camera.Parameters.SCENE_MODE_FIREWORKS))
    			 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_FIREWORKS);
             else
            	 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    	 } else if (positionMode == 5) {
    		 if (sceneModes.contains(Camera.Parameters.SCENE_MODE_SNOW))
    			 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SNOW);
             else
            	 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    	 } else if (positionMode == 6) {
    		 if (sceneModes.contains(Camera.Parameters.SCENE_MODE_LANDSCAPE))
    			 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_LANDSCAPE);
             else
            	 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    	 } else if (positionMode == 7) {
    		 if (sceneModes.contains(Camera.Parameters.SCENE_MODE_THEATRE))
    			 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_THEATRE);
             else
            	 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    	 }

         mCamera.setParameters(parameters);

    }
    
/**
*   FACE DETECTION
*  
**/    
    private FaceDetectionListener faceDetectionListener = new FaceDetectionListener() {
        @Override
        public void onFaceDetection(Face[] faces, Camera camera) {
            //Log.d("onFaceDetection", "Number of Faces:" + faces.length);
            // Update the view now
            drawingView.setFaces(faces);
        }
    };
    
/**
*   SWITCHING BETWEEN 2 CAMERAS
*  
**/
    private boolean hasCamera(Context context) {
    	//check if the device has camera
    	if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public void chooseCamera() {
    	//if the camera preview is the front
    	if (cameraFront) {
    		int cameraId = findBackFacingCamera();
    		if (cameraId >= 0) {
    			//open the backFacingCamera and refresh the preview
    			mCamera = Camera.open(cameraId);				
    			//mPicture = getPictureCallback();			
    			refreshCamera(mCamera);
    		}
    	} else {
    		int cameraId = findFrontFacingCamera();
    		if (cameraId >= 0) {
    			//open the frontFacingCamera and refresh the preview
    			mCamera = Camera.open(cameraId);
    			//mPicture = getPictureCallback();
    			refreshCamera(mCamera);
    		}
    	}
    }

    /** finding Id of the Front camera **/
    private int findFrontFacingCamera() {
    	int cameraId = -1;
    	//Search for the front facing camera
    	int numberOfCameras = Camera.getNumberOfCameras();

    	for (int i = 0; i < numberOfCameras; i++) {
    		CameraInfo info = new CameraInfo();
    		Camera.getCameraInfo(i, info);
    		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
    			cameraId = i;
    		    cameraFront = true;
    			break;
    		}
    	}
    	return cameraId;
    }

    /** finding Id of the Back camera **/
    private int findBackFacingCamera() {
    	int cameraId = -1;
    	//Search for the back facing camera and get the number of cameras
    	int numberOfCameras = Camera.getNumberOfCameras();
    	//for every camera check
    	for (int i = 0; i < numberOfCameras; i++) {
    		CameraInfo info = new CameraInfo();
    		Camera.getCameraInfo(i, info);
    		if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
    			cameraId = i;
    			cameraFront = false;
    			break;
    		}
    	}
    	return cameraId;
    }
    
    public void refreshCamera(Camera camera) {
    	if (holder.getSurface() == null) {
    		// preview surface does not exist
    		return;
    	}
    	// stop preview before making changes
    	try {
    		mCamera.stopPreview();
    	} catch (Exception e) {
    		// ignore: tried to stop a non-existent preview
    	}
    	
    	mCamera = camera;
    	try {
    		mCamera.setPreviewDisplay(holder);
    		mCamera.startPreview();
    	} catch (Exception e) {
    		//Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
    	}
    }
    
    public void releaseCamera() {
    	// stop and release camera
    	if (mCamera != null) {
    		mCamera.setPreviewCallback(null);
    		mCamera.release(); // release the camera for other applications
    		mCamera = null;
    	}
    }
    
    
    /**
    *   SETTING AUTO FOCUS 
    *  
    **/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
      
    	//return gesturedetector.onTouchEvent(event);
    	if(event.getAction() == MotionEvent.ACTION_DOWN){
    		float x = event.getX();
    		float y = event.getY();
    		
    		Rect touchRect = new Rect(
    				(int)(x - 50),
    				(int)(y - 50),
    				(int)(x + 50),
    				(int)(y + 50));	
    		
    		final Rect targetFocusRect = new Rect(
    				touchRect.left * 2000/this.getWidth() - 1000,
    				touchRect.top * 2000/this.getHeight() - 1000,
    				touchRect.right * 2000/this.getWidth() - 1000,
    				touchRect.bottom * 2000/this.getHeight() - 1000);

    		if (autoFocus) {
    			
    			doTouchFocus(targetFocusRect);
    			
    			if (drawingViewSet) {
    				drawingView.setHaveTouch(true, touchRect);
    				drawingView.invalidate();
    				// Remove the square after some time
    				Handler handler = new Handler();	
    				handler.postDelayed(new Runnable() {
    					@Override
    					public void run() {
    						drawingView.setHaveTouch(false, new Rect(0, 0, 0, 0));			
    						drawingView.invalidate();	
    					}
    				}, 1000);
    			}
    		}
    	}	
    	 return false;
    }
    
    public void doTouchFocus(final Rect tfocusRect) {
    	try {
    		final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
    		Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
    		focusList.add(focusArea);
    		
    		//set auto focus mode
    		Camera.Parameters parameters = mCamera.getParameters();
    		parameters.setFocusAreas(focusList);     //at least version api 14
    		parameters.setMeteringAreas(focusList);  //at least version api 14
    		parameters.setFocusMode("auto");
    		mCamera.setParameters(parameters);

    		//call the auto focus event
    		CameraStartAutoFocus();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void setDrawingView(DrawingView dView) {
    	drawingView = dView;
    	drawingViewSet = true;
    }
    
    public void CameraStartAutoFocus()
    {
    	if (touch2Shoot) {
    	   takePicture = true;
    	} else {
    	   takePicture = false;
    	}
    	mCamera.autoFocus(autoFocusCallback);
    }
    
    @Override
    public boolean onDown(MotionEvent e) {
            return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
            return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
    
    /** Zoom **/
    public void zoomScrolling(int zoomLevel) {
    	Camera.Parameters parameters = mCamera.getParameters();
    	parameters.setZoom((int)(zoomLevel/3.34));
    	mCamera.setParameters(parameters);
    }
    
    /** Flash **/
    public boolean turnONFlash(boolean enableFlash) {
    	 
    	boolean flashON = false;
    	Camera.Parameters parameters;
    	parameters = mCamera.getParameters();
    	String flashMode = parameters.getFlashMode();
    	 
    		if (enableFlash) {	
    			if ( flashMode != null )
    				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
    		
    			mCamera.setParameters(parameters);
    			flashON = true;
    		} else {
    			if ( flashMode != null )
    				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

    			mCamera.setParameters(parameters);
    			flashON = false;
    		}
    	return flashON;
    }
    
}