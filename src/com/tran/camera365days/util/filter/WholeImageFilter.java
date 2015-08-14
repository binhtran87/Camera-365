package com.tran.camera365days.util.filter;

import android.graphics.Rect;


public abstract class WholeImageFilter {

	/**
     * The output image bounds.
     */
    protected Rect transformedSpace;

	/**
     * The input image bounds.
     */
	protected Rect originalSpace;
	
	/**
	 * Construct a WholeImageFilter.
	 */
	public WholeImageFilter() {
	}

    public int[] filter( int[] src, int w, int h ) {
        int width = w;
        int height =h;        
		originalSpace = new Rect(0, 0, width, height);
		transformedSpace = new Rect(0, 0, width, height);
		transformSpace(transformedSpace);

		int[] inPixels = src;
		inPixels = filterPixels( width, height, inPixels, transformedSpace );

        return inPixels;
    }

	/**
     * Calculate output bounds for given input bounds.
     * @param rect input and output rectangle
     */
	protected void transformSpace(Rect rect) {
	}
	
	/**
     * Actually filter the pixels.
     * @param width the image width
     * @param height the image height
     * @param inPixels the image pixels
     * @param transformedSpace the output bounds
     * @return the output pixels
     */
	protected abstract int[] filterPixels( int width, int height, int[] inPixels, Rect transformedSpace );
}

