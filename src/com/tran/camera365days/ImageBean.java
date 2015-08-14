package com.tran.camera365days;

/**
 * 
 * This class will be used in displaying all albums
 *
 */
public class ImageBean {

	private String lastImagePath;
	private String folderName;
	private int numberOfImages;
	public String getLastImagePath() {
		return lastImagePath;
	}
	public void setLastImagePath(String lastImagePath) {
		this.lastImagePath = lastImagePath;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getNumberOfImages() {
		return numberOfImages;
	}
	public void setNumberOfImages(int numberOfImages) {
		this.numberOfImages = numberOfImages;
	}
	
	
}
