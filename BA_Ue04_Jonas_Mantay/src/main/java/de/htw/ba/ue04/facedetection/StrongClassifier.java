package de.htw.ba.ue04.facedetection;

import de.htw.ba.facedetection.IntegralImage;
import de.htw.ba.facedetection.WeakClassifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StrongClassifier implements de.htw.ba.facedetection.StrongClassifier {

 	//Liste mit WeakClassifier
	private Dimension size;

	private List<WeakClassifier> weakClassifiers;


	public StrongClassifier(int width, int height) {

		size = new Dimension(width, height);
		weakClassifiers = new ArrayList<>();
	}

	@Override
	public void setSize(Dimension size) {
		this.size = size;
	}

	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public void addWeakClassifier(WeakClassifier classifier) {
		weakClassifiers.add(classifier);
	}

	@Override
	public void normalizeWeights() {
		// TODO Auto-generated method stub

	}

	@Override
	public MatchingResult matchingAt(IntegralImage image, int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setThreshold(double threshold) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void drawAt(Graphics2D g2d, int x, int y) {

		int w = size.width;
		int h = size.height;

		g2d.setColor(Color.RED);
		g2d.drawRect(x, y, w, h);

		for (WeakClassifier weakClassifier : weakClassifiers){
			weakClassifier.drawAt(g2d,x,y);
		}

		//g2d.drawRect(50, 50, 30, 30);

	}

}
