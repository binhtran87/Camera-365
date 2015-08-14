package com.tran.camera365days.util.filter;

import android.graphics.Bitmap;

/**
 * Scales an image using bi-cubic interpolation, which can't be done with AffineTransformOp.
 */
public class BicubicScaleFilter {

	private int width;
	private int height;

	/**
     * Construct a BicubicScaleFilter which resizes to 32x32 pixels.
     */
    public BicubicScaleFilter() {
		this(32, 32);
	}

	/**
	 * Constructor for a filter which scales the input image to the given width and height using bicubic interpolation.
	 * Unfortunately, it appears that bicubic actually looks worse than bilinear interpolation on most Java implementations,
	 * but you can be the judge.
     * @param width the width of the output image
     * @param height the height of the output image
	 */
	public BicubicScaleFilter( int width, int height ) {
		this.width = width;
		this.height = height;
	}

    public int[] filter( int[] src, int w, int h ) {
        int[] dst = new int[width * height];
        
        Bitmap srcBitmap = Bitmap.createBitmap(src, w, h, Bitmap.Config.ARGB_8888);
        Bitmap dstBitmap = Bitmap.createScaledBitmap(srcBitmap, width, height, true);
        dstBitmap.getPixels(dst, 0, width, 0, 0, width, height);
        
        srcBitmap.recycle();
        dstBitmap.recycle();

        return dst;
    }

	public String toString() {
		return "Distort/Bicubic Scale";
	}

}
