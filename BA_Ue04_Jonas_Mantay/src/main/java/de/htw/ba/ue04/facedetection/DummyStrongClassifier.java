package de.htw.ba.ue04.facedetection;

import java.awt.Dimension;
import java.awt.Graphics2D;

import de.htw.ba.facedetection.IntegralImage;
import de.htw.ba.facedetection.StrongClassifier;
import de.htw.ba.facedetection.WeakClassifier;

public class DummyStrongClassifier implements StrongClassifier {

	private Dimension size = new Dimension();
	
	public DummyStrongClassifier(int width, int height) {
		size = new Dimension(width, height);
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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
