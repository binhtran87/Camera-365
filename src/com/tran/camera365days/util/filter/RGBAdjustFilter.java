package com.tran.camera365days.util.filter;

import com.tran.camera365days.util.filter.PixelUtils;

public class RGBAdjustFilter extends PointFilter {
	
	public float rFactor, gFactor, bFactor;

	public RGBAdjustFilter() {
		this(0, 0, 0);
	}

	public RGBAdjustFilter(float r, float g, float b) {
		rFactor = 1+r;
		gFactor = 1+g;
		bFactor = 1+b;
		canFilterIndexColorModel = true;
	}

	public void setRFactor( float rFactor ) {
		this.rFactor = 1+rFactor;
	}
	
	public float getRFactor() {
		return rFactor-1;
	}
	
	public void setGFactor( float gFactor ) {
		this.gFactor = 1+gFactor;
	}
	
	public float getGFactor() {
		return gFactor-1;
	}
	
	public void setBFactor( float bFactor ) {
		this.bFactor = 1+bFactor;
	}
	
	public float getBFactor() {
		return bFactor-1;
	}

	public int[] getLUT() {
		int[] lut = new int[256];
		for ( int i = 0; i < 256; i++ ) {
			lut[i] = filterRGB( 0, 0, (i << 24) | (i << 16) | (i << 8) | i );
		}
		return lut;
	}
	
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = PixelUtils.clamp((int)(r * rFactor));
		g = PixelUtils.clamp((int)(g * gFactor));
		b = PixelUtils.clamp((int)(b * bFactor));
		return a | (r << 16) | (g << 8) | b;
	}

	public String toString() {
		return "Colors/Adjust RGB...";
	}
}

