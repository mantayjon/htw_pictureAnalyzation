package de.htw.ba.ue03.matching;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Erstellt nur eine Kopie von dem Template Bild
 * 
 * @author Nico
 *
 */
/**
 * ABSOLUTE
 * wir packen alle distanzen in unser Float-Array ein
 * dann, wenn wir es anzeigen wollen, Normalisieren wir es und zeigen es dann an
 * 2. dann mit einer Maximuns findung können wir sehen, wo unsere Templates sein sollen
 *
 *
 * unser Ziel Array ist nicht genauso groß wie unser eingangsBild
 * RANDBEHANDLUNG: Wir machen kein randbehandlung, wir hören dann auf,
 * wenn unser Template aus dem Bild rausragen würde
 */
public class TemplateMatcherAbsolute extends TemplateMatcherBase {

	public TemplateMatcherAbsolute(int[] templatePixel, int templateWidth, int templateHeight) {
		super(templatePixel, templateWidth, templateHeight);
	}
	
	@Override
	public double[][] getDistanceMap(int[] srcPixels, int srcWidth, int srcHeight) {
		int dstHeight = srcHeight-templateHeight;
		int dstWidth = srcWidth-templateWidth;

		double[][] distanceMap = new double[dstHeight][dstWidth];

		for (int y = 0; y < dstHeight; y++){
			for (int x = 0; x < dstWidth; x++){

				int d = 0;

				for(int yTemp = 0; yTemp < templateHeight; yTemp++){
					for(int xTemp = 0; xTemp < templateWidth; xTemp++) {

						int xFilter = x + xTemp;
						int yFilter = y + yTemp;

						int posTemp = yTemp * templateWidth +xTemp;
						int posPix = yFilter * srcWidth + xFilter;


						d += Math.abs((srcPixels[posPix] &0xFF)  -  (templatePixel[posTemp]&0xFF));

					}
				}

				distanceMap[y][x] = d;

			}
		}


		return distanceMap;
	}

	@Override
	public void distanceMapToIntARGB(double[][] distanceMap, int[] dstPixels, int dstWidth, int dstHeight) {

		normalizeDistanceMap(distanceMap, dstHeight, dstWidth);

		for (int y = 0; y < dstHeight; y++) {
			for (int x = 0; x < dstWidth; x++) {

				int dstPosition = y * dstWidth + x;

				double grey = distanceMap[y][x];

				dstPixels[dstPosition] = 0xFF000000 + (((int) grey & 0xff) << 16) + (((int) grey & 0xff) << 8)
						+ ((int) grey & 0xff);
			}
		}


	}


	@Override
	public List<Point> findMaximas(double[][] distanceMap) {

		List<Point> max = new ArrayList<>();

		int dstWidth = distanceMap[0].length;
		int dstHeight = distanceMap.length;

		double maxVal = 0.0;
		for (int i = 0; i < dstHeight; i++) {
			for (int n = 0; n < dstWidth; n++) {
				if (distanceMap[i][n] > maxVal) {
					maxVal = distanceMap[i][n];
				}
			}
		}

		int threshhold = (int)(maxVal * 0.6);
		System.out.println(threshhold);

		int kernelSize = 5;
		boolean isMax;
		int hotspot;


		for (int y = 0; y < dstHeight; y++) {
			for (int x = 0; x < dstWidth; x++) {

				if (distanceMap[y][x] > threshhold) {
					isMax = true;
					hotspot = (int) distanceMap[y][x];

					for (int yKernel = -kernelSize; yKernel < kernelSize; yKernel++) {
						for (int xKernel = -kernelSize; xKernel < kernelSize; xKernel++) {
							int posX = x + xKernel;
							int posY = y + yKernel;

							if (posX < 0 || posX >= dstWidth || posY < 0 || posY >= dstHeight || ((posX == x) && (posY == y))) {
								continue;
							}

							if(distanceMap[posY][posX] >= hotspot) isMax = false;
						}
					}
					if (isMax) {
						Point m = new Point(x,y);
						max.add(m);
					}
				}
			}
		}
		return max;
	}



	public void normalizeDistanceMap(double[][] distanceMap, int dstHeight, int dstWidth){

		double max = 0;
		for (int y = 0; y < dstHeight; y++) {
			for (int x = 0; x < dstWidth; x++) {

				if(max < distanceMap[y][x]){
					max = distanceMap[y][x];
				}

			}
		}
		for (int y = 0; y < dstHeight; y++) {
			for (int x = 0; x < dstWidth; x++) {

				distanceMap[y][x] = 255 - (distanceMap[y][x]/max)*255;

			}
		}

	}
}
