/**
 * @author Nico Hezel
 */
package de.htw.ba.ue01.controller;

import java.awt.*;
import java.sql.Array;
import java.text.ParseException;

public class EdgeDetectionController extends EdgeDetectionBase {

	protected static enum Methods {
		Kopie, Graustufen, X_Gradient, Y_Gradient, x_Gradient_Sobel, y_Gradient_Sobel, GradientenBetrag_Sobel,
		GradientenWinkel_Sobel, GradientenWinkel_Sobel_Farbe, kombiniertes_Bild_Sobel_Winkel_Betrag

	};

	@Override
	public void runMethod(Methods currentMethod, int[] srcPixels, int[] dstPixels, int width, int height) throws Exception {
		switch (currentMethod) {
			case Graustufen:
				double parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				break;
			case Kopie:
			default:
				doCopy(srcPixels, dstPixels, width, height);
				break;
			case X_Gradient:
				parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				doXGrad(srcPixels, dstPixels, width, height, parameter1);
				break;
			case Y_Gradient:
				parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				doYGrad(srcPixels, dstPixels, width, height, parameter1);
				break;
			case x_Gradient_Sobel:
				parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				doXGradSobel(srcPixels, dstPixels, width, height, parameter1);
				break;
			case y_Gradient_Sobel:
				parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				doYGradSobel(srcPixels, dstPixels, width, height, parameter1);
				break;
			case GradientenBetrag_Sobel:
				parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				doGradABS(srcPixels, dstPixels, width, height, parameter1);
				break;
			case GradientenWinkel_Sobel:
				parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				doGradGrey(srcPixels, dstPixels, width, height, parameter1);
				break;
			case GradientenWinkel_Sobel_Farbe:
				parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				doGradColor(srcPixels, dstPixels, width, height, parameter1);
				break;
			case kombiniertes_Bild_Sobel_Winkel_Betrag:
				parameter1 = getParameter();
				doGray(srcPixels, dstPixels, width, height, parameter1);
				doGradAngleColor(srcPixels, dstPixels, width, height, parameter1);
				break;
		}
	}

	private void doGray(int srcPixels[], int dstPixels[], int width, int height, double parameter) throws ParseException {
		// loop over all pixels of the destination image

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int validX = Math.max(0,Math.min(x, width-1));
				int validY = Math.max(0,Math.min(y, height-1));

				int pos = validY * width + validX;

				int c = srcPixels[pos];
				int r = (c >> 16) & 0xFF;
				int g = (c >> 8) & 0xFF;
				int b = (c) & 0xFF;

				int lum = (int) (0.299 * r + 0.587 * g + 0.114 * b + parameter);

				lum = Math.max(0,Math.min(255, lum));

				dstPixels[pos] = 0xFF000000 | (lum << 16) | (lum << 8) | lum;
			}
		}
	}

	private void doCopy(int srcPixels[], int dstPixels[], int width, int height) {
		// loop over all pixels of the destination image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				dstPixels[pos] = srcPixels[pos];
			}
		}
	}

	private void doXGrad(int srcPixels[], int dstPixels[], int width, int height, double parameter){

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int validX = Math.max(0,Math.min(x, width-1));
				int validY = Math.max(0,Math.min(y, height-1));
				int pos = validY * width + validX;

				//not a good solution for rn
				int nextPixel = srcPixels[Math.min(srcPixels.length-1,pos+1)] & 0xff;
				int beforePixel = srcPixels[Math.max(0,pos-1)]  & 0xff;

				int newPixel = (int)(((nextPixel - beforePixel)/2) + parameter)+127;

				dstPixels[pos] = 0xFF000000 | (newPixel << 16) | (newPixel << 8) | newPixel;
			}
		}
	}
	private void doYGrad(int srcPixels[], int dstPixels[], int width, int height, double parameter){

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int validX = Math.max(0,Math.min(x, width-1));
				int validY = Math.max(0,Math.min(y, height-1));
				int pos = validY * width + validX;

				int posBefore = Math.max(0,validY-1) * width + validX;
				int posAfter = Math.min(height-1,validY+1) * width + validX;

				//not a good solution for rn
				int beforePixel = srcPixels[posBefore]  & 0xff;
				int nextPixel = srcPixels[posAfter] & 0xff;

				int newPixel = (int)(((nextPixel - beforePixel)/2) + parameter)+127;

				dstPixels[pos] = 0xFF000000 | (newPixel << 16) | (newPixel << 8) | newPixel;
			}
		}
	}

	private void doXGradSobel(int srcPixels[], int dstPixels[], int width, int height, double parameter){
		double[][] kernel = {
				{-1.0/8.0, -2.0/8.0, -1.0/8.0},
				{ 0.0/8.0, 0.0/8.0, 0.0/8.0},
				{1.0/8.0, 2.0/8.0, 1.0/8.0}
		};

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int validX = Math.max(0,Math.min(x, width-1));
				int validY = Math.max(0,Math.min(y, height-1));
				int pos = validY * width + validX;


				double filteredPixel = 0;

				for(int k=0; k<3; k++) {
					for(int l=0; l<3; l++) {
						// Konstant fortsetzen
						int xFilter = x+k-3/2;
						if(xFilter<0) xFilter = 0;
						else if(xFilter>width-1) xFilter = width - 1;
						int yFilter = y+l-3/2;
						if(yFilter<0) yFilter = 0;
						else if(yFilter>height-1) yFilter = height - 1;

						int posFilter = yFilter * width + xFilter;
						int pixel = (srcPixels[posFilter] & 0x0000ff);
						filteredPixel += pixel * kernel[k][l];

					}
				}
				filteredPixel = filteredPixel+parameter;
				filteredPixel = Math.abs(filteredPixel);
				dstPixels[pos] = 0xFF000000 + (((int)filteredPixel & 0xff) << 16) + (((int)filteredPixel & 0xff) << 8) + ((int)filteredPixel & 0xff);
			}
		}
	}
	private void doYGradSobel(int srcPixels[], int dstPixels[], int width, int height, double parameter){

		double[][] kernel = {
				{-1.0/8.0, 0.0/8.0, 1.0/8.0},
				{-2.0/8.0, 0.0/8.0, 2.0/8.0},
				{-1.0/8.0, 0.0/8.0, 1.0/8.0}
		};

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int validX = Math.max(0,Math.min(x, width-1));
				int validY = Math.max(0,Math.min(y, height-1));
				int pos = validY * width + validX;

				double filteredPixel = 0;

				for(int k=0; k<3; k++) {
					for(int l=0; l<3; l++) {
						// Konstant fortsetzen
						int xFilter = x+k-3/2;
						if(xFilter<0) xFilter = 0;
						else if(xFilter>width-1) xFilter = width - 1;
						int yFilter = y+l-3/2;
						if(yFilter<0) yFilter = 0;
						else if(yFilter>height-1) yFilter = height - 1;

						int posFilter = yFilter * width + xFilter;
						int pixel = (srcPixels[posFilter] & 0x0000ff);
						filteredPixel += pixel * kernel[k][l];


					}
				}
				filteredPixel = filteredPixel+parameter;
				filteredPixel = Math.abs(filteredPixel);

				dstPixels[pos] = 0xFF000000 + (((int)filteredPixel & 0xff) << 16) + (((int)filteredPixel & 0xff) << 8) + ((int)filteredPixel & 0xff);
			}
		}
	}
	private void doGradABS(int srcPixels[], int dstPixels[], int width, int height, double parameter1){

		int[] xImage = new int[dstPixels.length];
		int[] yImage = new int[dstPixels.length];
		doXGradSobel(srcPixels, xImage, width, height, parameter1);
		doYGradSobel(srcPixels, yImage, width, height, parameter1);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int validX = Math.max(0, Math.min(x, width - 1));
				int validY = Math.max(0, Math.min(y, height - 1));
				int pos = validY * width + validX;

				double xGradient = xImage[pos] & 0xff; // Extract the red channel
				double yGradient = yImage[pos] & 0xff; // Extract the red channel
				double gradient = Math.hypot(xGradient, yGradient);

				gradient = Math.max(0, Math.min(255,gradient));

				//double gradient = Math.sqrt(Math.pow(xImage[pos] & 0xff,2)+Math.pow(yImage[pos] & 0xff,2))+127+parameter1;
				dstPixels[pos] = 0xFF000000 + (((int)gradient & 0xff) << 16) + (((int)gradient & 0xff) << 8)
						+ ((int) gradient & 0xff);
			}
		}
	}

	private void doGradGrey(int srcPixels[], int dstPixels[], int width, int height, double parameter1) throws ParseException {

			int[] xImage = new int[dstPixels.length];
			int[] yImage = new int[dstPixels.length];
			int[] finalImage = new int[dstPixels.length];
			doXGradSobel(srcPixels, xImage, width, height, parameter1);
			doYGradSobel(srcPixels, yImage, width, height, parameter1);

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {

					int validX = Math.max(0, Math.min(x, width - 1));
					int validY = Math.max(0, Math.min(y, height - 1));
					int pos = validY * width + validX;

					int xGradient = xImage[pos] & 0xff;
					int yGradient = yImage[pos] & 0xff;



					float angle = (float)Math.toDegrees(Math.atan2(xGradient,yGradient));

					int c = Color.HSBtoRGB(angle, 1, 1);

					finalImage[pos] = 0xFF000000 | c;



				}
			}

		doGray(finalImage, dstPixels, width, height, parameter1);
	}
	private void doGradColor(int srcPixels[], int dstPixels[], int width, int height, double parameter1){

		int[] xImage = new int[dstPixels.length];
		int[] yImage = new int[dstPixels.length];
		doXGradSobel(srcPixels, xImage, width, height, parameter1);
		doYGradSobel(srcPixels, yImage, width, height, parameter1);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int validX = Math.max(0, Math.min(x, width - 1));
				int validY = Math.max(0, Math.min(y, height - 1));
				int pos = validY * width + validX;

				int xGradient = xImage[pos] & 0xff;
				int yGradient = yImage[pos] & 0xff;

				float angle = (float)Math.toDegrees(Math.atan2(xGradient,yGradient));
				angle = (angle/90);

				int c = Color.HSBtoRGB(angle, 1, 1);

				dstPixels[pos] = 0xFF000000 | c;

			}
		}
	}
	private void doGradAngleColor(int srcPixels[], int dstPixels[], int width, int height, double parameter1){

		int[] xImage = new int[dstPixels.length];
		int[] yImage = new int[dstPixels.length];
		doXGradSobel(srcPixels, xImage, width, height, parameter1);
		doYGradSobel(srcPixels, yImage, width, height, parameter1);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int validX = Math.max(0, Math.min(x, width - 1));
				int validY = Math.max(0, Math.min(y, height - 1));
				int pos = validY * width + validX;

				int xGradient = xImage[pos] & 0xff;
				int yGradient = yImage[pos] & 0xff;

				double gradient = Math.hypot(xGradient, yGradient);
				gradient = Math.max(0, Math.min(255,gradient));
				gradient= gradient/255;

				float angle = (float) Math.toDegrees(Math.atan2(xGradient, yGradient));



				float brightness = (float) gradient;

				int c = Color.HSBtoRGB(angle, 1, brightness);
				dstPixels[pos] = 0xFF000000 | c;

			}
		}
	}
}
