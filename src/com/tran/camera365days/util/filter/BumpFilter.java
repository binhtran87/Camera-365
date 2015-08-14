package com.tran.camera365days.util.filter;

/**
 * A simple embossing filter. 
 */
public class BumpFilter extends ConvolveFilter {
	
	private static float[] embossMatrix = {
		-1.0f, -1.0f,  0.0f,
		-1.0f,  1.0f,  1.0f,
		 0.0f,  1.0f,  1.0f
	};

	public BumpFilter() {
		super(embossMatrix);
	}

	public String toString() {
		return "Blur/Emboss Edges";
	}
}
