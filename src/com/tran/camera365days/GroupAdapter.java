package com.tran.camera365days;

import java.util.List;

import com.tran.camera365days.NativeImageLoader.NativeImageCallBack;
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

public class GroupAdapter extends BaseAdapter{

	private List<ImageBean> lstImageBeans; 
	private Point mPoint = new Point(0, 0);
	private GridView mGridView; 
	protected LayoutInflater mInflater; 
	
    public GroupAdapter(Context context, List<ImageBean> lst, GridView mGridView){  
        this.lstImageBeans = lst;  
        this.mGridView = mGridView;  
        mInflater = LayoutInflater.from(context);  
    }  
	
	@Override
	public int getCount() {
		return lstImageBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return lstImageBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final GroupViewHolder viewHolder;  
        ImageBean mImageBean = lstImageBeans.get(position);  
        String path = mImageBean.getLastImagePath();  
        if(convertView == null){  
            viewHolder = new GroupViewHolder();  
            convertView = mInflater.inflate(R.layout.all_albums_item, null);  
            viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.group_image);  
            viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);  
            viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);  
              
            viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {  
                  
                @Override  
                public void onMeasureSize(int width, int height) {  
                    mPoint.set(width, height);  
                }  
            });  
              
            convertView.setTag(viewHolder);  
        }else{  
            viewHolder = (GroupViewHolder) convertView.getTag();  
            viewHolder.mImageView.setImageResource(R.drawable.bg);  
        }  
          
        viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());  
        viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getNumberOfImages()));  
        viewHolder.mImageView.setTag(path);  
          
          
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {  
              
            @Override  
            public void onImageLoader(Bitmap bitmap, String path) {  
                ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);  
                if(bitmap != null && mImageView != null){  
                    mImageView.setImageBitmap(bitmap);  
                }  
            }  
        });  
        if(bitmap != null){  
            viewHolder.mImageView.setImageBitmap(bitmap);  
        }else{  
            viewHolder.mImageView.setImageResource(R.drawable.undefined);  
        }  
          
          
        return convertView; 
	}
    
  public static class GroupViewHolder{  
      public MyImageView mImageView;  
      public TextView mTextViewTitle;  
      public TextView mTextViewCounts;  
  }  
}
