package com.tran.camera365days;

import java.util.List;

import com.tran.camera365days.NativeImageLoader.NativeImageCallBack;
import com.tran.camera365days.util.TouchImageView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tran.camera365days.util.VerticalViewPager;

public class ImagePageViewAdapter extends PagerAdapter{

	private List<String> _imagePaths;
	private Activity _activity;
    private LayoutInflater inflater;
   // private Point mPoint = new Point(0, 0);//Used to package ImageView the width and height of the object
    
    public ImagePageViewAdapter(Activity activity,
            List<String> lstOfImagePaths) {
        this._activity = activity;
        this._imagePaths = lstOfImagePaths;
    }
    
	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object){
		return view == ((RelativeLayout) object);
	}
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
        //ImageView imgDisplay;
  
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.image_view_item, container,
                false);
  
       // imgDisplay = (ImageView) viewLayout.findViewById(R.id.image_viewing);
         
        TouchImageView imageview = (TouchImageView)viewLayout.findViewById(R.id.image_viewing);
        String path=_imagePaths.get(position);
        imageview.setTag(path);
        //imgDisplay.setTag(path);

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, 
                new NativeImageCallBack() { 
                    @Override 
                    public void onImageLoader(Bitmap bitmap, String path) { 
                        ImageView mImageView = (ImageView) viewLayout.findViewWithTag(path); 
                        if (bitmap != null && mImageView != null) { 
                            mImageView.setImageBitmap(bitmap); 
                        } 
                    } 
                }); 
       // String imagePath = photoFile.getAbsolutePath();             // photoFile is a File type.
        //Bitmap myBitmap  = BitmapFactory.decodeFile(imagePath);

       // Bitmap orientedBitmap = ExifUtil.rotateBitmap(path, bitmap);
        
        if(bitmap!=null){
            //imgDisplay.setImageBitmap(bitmap);
        	imageview.setImageBitmap(bitmap);
        }else{
        	//imgDisplay.setImageResource(R.drawable.test); 	
        	imageview.setImageResource(R.drawable.undefined);
        }
        
        ((VerticalViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((VerticalViewPager) container).removeView((RelativeLayout) object);
  
    }
}
