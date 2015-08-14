package com.tran.camera365days;

import java.util.List;

import com.tran.camera365days.NativeImageLoader.NativeImageCallBack;
import com.tran.camera365days.util.StickyGridHeadersSimpleAdapter;
import com.tran.camera365days.ui.MyImageView;
import com.tran.camera365days.ui.MyImageView.OnMeasureListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter{
	    private List<GridItem> hasHeaderIdList; 
	    private LayoutInflater mInflater; 
	    private GridView mGridView; 
	    private Point mPoint = new Point(0, 0);//Used to package ImageView the width and height of the object
	 
	    public AlbumAdapter(Context context, List<GridItem> hasHeaderIdList,GridView mGridView) { 
	        mInflater = LayoutInflater.from(context); 
	        this.mGridView = mGridView; 
	        this.hasHeaderIdList = hasHeaderIdList; 
	    } 
	 
	 
	    @Override 
	    public int getCount() { 
	        return hasHeaderIdList.size(); 
	    } 
	 
	    @Override 
	    public Object getItem(int position) { 
	        return hasHeaderIdList.get(position); 
	    } 
	 
	    @Override 
	    public long getItemId(int position) { 
	        return position; 
	    } 
	 
	    @Override 
	    public View getView(int position, View convertView, ViewGroup parent) { 
	        ViewHolder mViewHolder; 
	        if (convertView == null) { 
	            mViewHolder = new ViewHolder(); 
	            convertView = mInflater.inflate(R.layout.gridview_item, parent, false); 
	            mViewHolder.imageView = (MyImageView) convertView
	            		// mViewHolder.mImageView = (MyImageView) convertView 
	                    .findViewById(R.id.image); 
	            convertView.setTag(mViewHolder); 
	             
	             //To monitor ImageView width and height
	            mViewHolder.imageView.setOnMeasureListener(new OnMeasureListener() {   
	                   
	                @Override   
	                public void onMeasureSize(int width, int height) {   
	                    mPoint.set(width, height);   
	                }   
	            });  
	             
	        } else { 
	            mViewHolder = (ViewHolder) convertView.getTag();
	        } 
	 
	        String path = hasHeaderIdList.get(position).getPath(); 
	        mViewHolder.imageView.setTag(path); 
	 
	        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, 
	                new NativeImageCallBack() { 
	                    @Override 
	                    public void onImageLoader(Bitmap bitmap, String path) { 
	                        ImageView mImageView = (ImageView) mGridView.findViewWithTag(path); 
	                        if (bitmap != null && mImageView != null) { 
	                            mImageView.setImageBitmap(bitmap); 
	                        } 
	                    } 
	                }); 
	        if (bitmap != null) { 
	            mViewHolder.imageView.setImageBitmap(bitmap); 
	        } else { 
	            mViewHolder.imageView.setImageResource(R.drawable.undefined); 
	        } 
	 
	        return convertView; 
	    } 
	    
	
	    @Override 
	    public View getHeaderView(int position, View convertView, ViewGroup parent) { 
	        HeaderViewHolder mHeaderHolder; 
	         
	        if (convertView == null) { 
	            mHeaderHolder = new HeaderViewHolder(); 
	            convertView = mInflater.inflate(R.layout.header, parent, false); 
	            mHeaderHolder.mTextView = (TextView) convertView 
	                    .findViewById(R.id.header); 
	            convertView.setTag(mHeaderHolder); 
	        } else { 
	            mHeaderHolder = (HeaderViewHolder) convertView.getTag(); 
	        } 
	        mHeaderHolder.mTextView.setText(hasHeaderIdList.get(position).getTime()); 
	         
	        return convertView; 
	    } 
	     
	    /**
	     * Access to HeaderId, if HeaderId is not equal to add a Header
	     */ 
	    @Override 
	    public long getHeaderId(int position) { 
	        return hasHeaderIdList.get(position).getHeaderId(); 
	    } 
	 
	     
	    public static class ViewHolder { 
	        public MyImageView imageView; 
	    } 
	 
	    public static class HeaderViewHolder { 
	        public TextView mTextView; 
	    } 
	 

}
