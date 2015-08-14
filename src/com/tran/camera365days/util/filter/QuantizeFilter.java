package com.tran.camera365days.util.filter;

import com.tran.camera365days.util.filter.PixelUtils;

import android.graphics.Rect;

public class QuantizeFilter extends WholeImageFilter {

	/**
	 * Floyd-Steinberg dithering matrix.
	 */
	protected final static int[] matrix = {
	 	 0, 0, 0,
	 	 0, 0, 7,
	 	 3, 5, 1,
	};
	private int sum = 3+5+7+1;

	private boolean dither;
	private int numColors = 256;
	private boolean serpentine = true;

	/**
	 * Set the number of colors to quantize to.
	 * @param numColors the number of colors. The default is 256.
	 */
	public void setNumColors(int numColors) {
		this.numColors = Math.min(Math.max(numColors, 8), 256);
	}

	/**
	 * Get the number of colors to quantize to.
	 * @return the number of colors.
	 */
	public int getNumColors() {
		return numColors;
	}

	/**
	 * Set whether to use dithering or not. If not, the image is posterized.
	 * @param dither true to use dithering
	 */
	public void setDither(boolean dither) {
		this.dither = dither;
	}

	/**
	 * Return the dithering setting
	 * @return the current setting
	 */
	public boolean getDither() {
		return dither;
	}

	/**
	 * Set whether to use a serpentine pattern for return or not. This can reduce 'avalanche' artifacts in the output.
	 * @param serpentine true to use serpentine pattern
	 */
	public void setSerpentine(boolean serpentine) {
		this.serpentine = serpentine;
	}
	
	/**
	 * Return the serpentine setting
	 * @return the current setting
	 */
	public boolean getSerpentine() {
		return serpentine;
	}
	
	public void quantize(int[] inPixels, int[] outPixels, int width, int height, int numColors, boolean dither, boolean serpentine) {
		int count = width*height;
		Quantizer quantizer = new OctTreeQuantizer();
		quantizer.setup(numColors);
		quantizer.addPixels(inPixels, 0, count);
		int[] table =  quantizer.buildColorTable();

		if (!dither) {
			for (int i = 0; i < count; i++)
				outPixels[i] = table[quantizer.getIndexForColor(inPixels[i])];
		} else {
			int index = 0;
			for (int y = 0; y < height; y++) {
				boolean reverse = serpentine && (y & 1) == 1;
				int direction;
				if (reverse) {
					index = y*width+width-1;
					direction = -1;
				} else {
					index = y*width;
					direction = 1;
				}
				for (int x = 0; x < width; x++) {
					int rgb1 = inPixels[index];
					int rgb2 = table[quantizer.getIndexForColor(rgb1)];

					outPixels[index] = rgb2;

					int r1 = (rgb1 >> 16) & 0xff;
					int g1 = (rgb1 >> 8) & 0xff;
					int b1 = rgb1 & 0xff;

					int r2 = (rgb2 >> 16) & 0xff;
					int g2 = (rgb2 >> 8) & 0xff;
					int b2 = rgb2 & 0xff;

					int er = r1-r2;
					int eg = g1-g2;
					int eb = b1-b2;

					for (int i = -1; i <= 1; i++) {
						int iy = i+y;
						if (0 <= iy && iy < height) {
							for (int j = -1; j <= 1; j++) {
								int jx = j+x;
								if (0 <= jx && jx < width) {
									int w;
									if (reverse)
										w = matrix[(i+1)*3-j+1];
									else
										w = matrix[(i+1)*3+j+1];
									if (w != 0) {
										int k = reverse ? index - j : index + j;
										rgb1 = inPixels[k];
										r1 = (rgb1 >> 16) & 0xff;
										g1 = (rgb1 >> 8) & 0xff;
										b1 = rgb1 & 0xff;
										r1 += er * w/sum;
										g1 += eg * w/sum;
										b1 += eb * w/sum;
										inPixels[k] = (PixelUtils.clamp(r1) << 16) | (PixelUtils.clamp(g1) << 8) | PixelUtils.clamp(b1);
									}
								}
							}
						}
					}
					index += direction;
				}
			}
		}
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rect transformedSpace ) {
		int[] outPixels = new int[width*height];
		
		quantize(inPixels, outPixels, width, height, numColors, dither, serpentine);

		return outPixels;
	}

	public String toString() {
		return "Colors/Quantize...";
	}
	
}
