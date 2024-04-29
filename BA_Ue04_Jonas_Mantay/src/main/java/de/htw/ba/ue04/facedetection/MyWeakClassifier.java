package de.htw.ba.ue04.facedetection;

import de.htw.ba.facedetection.IntegralImage;
import de.htw.ba.facedetection.WeakClassifier;

import java.awt.*;

public class MyWeakClassifier implements de.htw.ba.facedetection.WeakClassifier {
	int width;
	int height;
	int xOffset;
	int yOffset;


	public MyWeakClassifier (int width, int height, int xOffset,int yOffset){
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}


	@Override
	public WeakMatchingResult matchingAt(IntegralImage image, int x, int y) {

		int xRight = x + xOffset + width;
		int xLeft = x + xOffset + width;

		int yUp = y + yOffset;
		int yDown = y +yOffset + height;

		int posUpLeft = yUp * image.getWidth() + xLeft;
		int posUpRight = yUp * image.getWidth() + xRight;
		int posDownLeft =  yDown * image.getWidth() + xLeft;
		int posDownRight = yDown * image.getWidth() + xRight;

		/*
		int brightArea = image[posDownRight] - image[posUpRight] - image[posDownLeft] + image[posUpLeft];



		int yODark = yO + height/2;

		int darkArea = image[];
*/


		return null;
	}

	@Override
	public void setThreshold(double threshold) {

	}

	@Override
	public double getThreshold() {
		return 0;
	}

	@Override
	public void setWeight(double weight) {

	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public void drawAt(Graphics2D g2d, int x, int y) {

		int xO = x + xOffset;
		int yO = y + yOffset;

		int xODark = xO;
		int yODark = yO + height/2;

		Color Green50 = new Color(0, 255, 0, 125);

		g2d.setColor(Color.GREEN);
		g2d.drawRect(xO, yO, width, height);

		g2d.setColor(Green50);
		g2d.fillRect(xODark, yODark, width, height/2);



	}
}
