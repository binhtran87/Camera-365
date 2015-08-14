package com.tran.camera365days.util.filter;

/**
 * A filter which solarizes an image.
 */
public class SolarizeFilter extends TransferFilter {

	protected float transferFunction( float v ) {
		return v > 0.5f ? 2*(v-0.5f) : 2*(0.5f-v);
	}

	public String toString() {
		return "Colors/Solarize";
	}
}

