package de.htw.ba.ue04.facedetection;

import java.util.Arrays;

public class IntegralImage implements de.htw.ba.facedetection.IntegralImage {

	private final int width;
	private final int height;

	private final long[] integral;

	public IntegralImage(int[] srcPixels, int width, int height) {
		this.width = width;
		this.height = height;
		this.integral = calcIntegral(srcPixels, width, height);

	}
	
	@Override
	public double meanValue(int x, int y, int width, int height) {
		double mean = 0.0;
		int pixSum = 0;

		for (int yArea = 0; yArea < y+height; yArea++){
			for (int xArea = 0; xArea < x+width; xArea++){
				int pos = y + yArea * this.width  + x+xArea;
				mean += integral[pos];
				pixSum++;
			}
		}

		if (pixSum != 0) mean = mean/pixSum;

		return mean;
	}

	@Override
	public void toIntARGB(int[] dstImage) {

		double max = findMax(integral);
		for (int i = 0; i <integral.length;i++){
			double grey = (integral[i]/max) *255;
			dstImage[i] = 0xFF000000 + (((int)grey & 0xff) << 16) + (( (int)grey & 0xff) << 8)
					+ ( (int)grey & 0xff);
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private static long[] calcIntegral(int[] srcPixels, int width, int height) {
		long[] integral = new long[srcPixels.length];

		for (int y = 0; y < height; y++) {

			int reihe = 0;

			for (int x = 0; x < width; x++) {

				int pos = y * width + x;
				reihe += (srcPixels[pos] &0xFF);
				integral[pos] = reihe;

			}

		}
		for (int x = 0; x < width; x++) {
			int spalte = 0;

			for (int y = 0; y < height; y++) {

				int pos = y * width + x;
				spalte += integral[pos];
				integral[pos] = spalte;
			}
		}

		return integral;
	}

	public double findMax(long[] integral){
		double max = 0.001;

		for (int i = 0; i <integral.length;i++){
			if(max<integral[i]) max = integral[i];
		}
		return max;
	}
}