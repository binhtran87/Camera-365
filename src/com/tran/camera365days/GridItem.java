package com.tran.camera365days;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class GridItem implements Serializable{

	private String path;
	private String time;
	private int headerId;
	public GridItem(String path, String time) { 
        super(); 
        this.path = path; 
        this.time = time; 
    } 
    public String getPath() { 
        return path; 
    } 
    public void setPath(String path) { 
        this.path = path; 
    } 
    public String getTime() { 
        return time; 
    } 
    public void setTime(String time) { 
        this.time = time; 
    } 
 
    public int getHeaderId() { 
        return headerId; 
    } 
 
    public void setHeaderId(int headerId) { 
        this.headerId = headerId; 
    }
}
