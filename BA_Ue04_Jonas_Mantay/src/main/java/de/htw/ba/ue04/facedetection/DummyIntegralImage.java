package de.htw.ba.ue04.facedetection;

import java.util.Arrays;

import de.htw.ba.facedetection.IntegralImage;

public class DummyIntegralImage implements IntegralImage {

	private int width = 0;
	private int height = 0;
	
	public DummyIntegralImage(int[] srcPixels, int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public double meanValue(int x, int y, int width, int height) {

		return 0;
	}

	@Override
	public void toIntARGB(int[] dstImage) {
		Arrays.fill(dstImage, 0xff808080);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}
