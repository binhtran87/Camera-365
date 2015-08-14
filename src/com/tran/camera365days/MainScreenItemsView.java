package com.tran.camera365days;
/**
 * Store information about item in the main screen
 *
 */
public class MainScreenItemsView {
	private String strName;
	private int iconID;
	public MainScreenItemsView(String strName, int iconID){
		super();
		this.strName=strName;
		this.iconID=iconID;
	}
	public String getStrName() {
		return strName;
	}
	public void setStrName(String strName) {
		this.strName = strName;
	}
	public int getIconID() {
		return iconID;
	}
	public void setIconID(int iconID) {
		this.iconID = iconID;
	}
	
}
