/**
 * @author Nico Hezel, Klaus Jung
 */
package de.htw.ba.ue05.controller;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.htw.ba.facedetection.StrongClassifier.MatchingResult;
import de.htw.ba.ue05.facedetection.*;
import de.htw.ba.facedetection.WeakClassifier;

public class FaceDetectionController extends FaceDetectionBase {

	@Override
	protected void calculateIntegralImage(int[] srcPixels, int width, int height) {
		System.out.println("calculateIntegralImage");

		int[] grayValues = new int[srcPixels.length];
		for (int pos = 0; pos < srcPixels.length; pos++)
			grayValues[pos] = ((srcPixels[pos] & 0xff) + ((srcPixels[pos] & 0xff00) >> 8) + ((srcPixels[pos] & 0xff0000) >> 16)) / 3;
		integralImage = new IntegralImageImpl(grayValues, width, height);
	}

	@Override
	protected void createManualClassifier() {
		System.out.println("createManualClassifier");

		Rectangle avgFace = testImage.getAverageFaceDimensions();

		StrongClassifierImpl strong = new StrongClassifierImpl(new Dimension(avgFace.width, avgFace.height));
		StrongClassifierImpl.detectWithWeakClassifiersFeatureValues = true;
		WeakClassifierImpl weak = null;
		
		// Angelina Jolie 
		if(isDemo) {
			// this single weak classifier is not enough for a save match
			weak = new WeakClassifierImpl(
					new Rectangle(19, 59-28, 37, 28*2), 
					WeakClassifierImpl.Type.Double, 
					WeakClassifierImpl.Orientation.Horizontal,
					WeakClassifierImpl.Mode.Normal);
			strong.addWeakClassifier(weak);
		} else {
			// manual adjusted weak classifiers
			weak = new WeakClassifierImpl(
					new Rectangle(19, 59-28, 96, 28*3), 
					WeakClassifierImpl.Type.Tripple, 
					WeakClassifierImpl.Orientation.Horizontal,
					WeakClassifierImpl.Mode.Normal);
			strong.addWeakClassifier(weak);

			weak = new WeakClassifierImpl(
					new Rectangle(2, 23, 16*2, 33), 
					WeakClassifierImpl.Type.Double, 
					WeakClassifierImpl.Orientation.Vertical,
					WeakClassifierImpl.Mode.Inverted);
			strong.addWeakClassifier(weak);

			weak = new WeakClassifierImpl(
					new Rectangle(116-25, 26, 25*2, 14), 
					WeakClassifierImpl.Type.Double, 
					WeakClassifierImpl.Orientation.Vertical,
					WeakClassifierImpl.Mode.Normal);
			strong.addWeakClassifier(weak);

			weak = new WeakClassifierImpl(
					new Rectangle(43, 155, 53, 12*2), 
					WeakClassifierImpl.Type.Double, 
					WeakClassifierImpl.Orientation.Horizontal,
					WeakClassifierImpl.Mode.Normal);
			weak.modifyWeight(0.5);
			strong.addWeakClassifier(weak);

			weak = new WeakClassifierImpl(
					new Rectangle(130-12, 112, 12*2, 17), 
					WeakClassifierImpl.Type.Double, 
					WeakClassifierImpl.Orientation.Vertical,
					WeakClassifierImpl.Mode.Normal);
			weak.modifyWeight(0.5);
			strong.addWeakClassifier(weak);

			weak = new WeakClassifierImpl(
					new Rectangle(28, 111, 22, 22), 
					WeakClassifierImpl.Type.Diagonal, 
					WeakClassifierImpl.Orientation.Horizontal,
					WeakClassifierImpl.Mode.Normal);
			weak.modifyWeight(0.1);
			strong.addWeakClassifier(weak);
		}

		strong.normalizeWeights();
		strongClassifier = strong;
	}
	
	@Override
	protected void createTrainedClassifier(int weakClassifierCount) {

		Rectangle avgFace = testImage.getAverageFaceDimensions();
		List<WeakClassifier> classifiers = new ArrayList<WeakClassifier>(weakClassifierCount);

		Random r = new Random(11);

		for(int i = 0; i<weakClassifierCount; i++){

			int width = generateNumber(r,avgFace.width/10, (int)(avgFace.width/4));
			int height = generateNumber(r,avgFace.width/10, avgFace.height/4);

			int xPos = generateNumber(r,0,avgFace.width-width);
			int yPos = generateNumber(r,0,avgFace.height-height);
			Rectangle pos = new Rectangle(xPos,yPos,width,height);

			WeakClassifierImpl.Type type = WeakClassifierImpl.Type.values()
					[r.nextInt(WeakClassifierImpl.Type.values().length)];
			WeakClassifierImpl.Orientation orientation = WeakClassifierImpl.Orientation.values()
					[r.nextInt(WeakClassifierImpl.Orientation.values().length)];
			WeakClassifierImpl.Mode mode = WeakClassifierImpl.Mode.values()
					[r.nextInt(WeakClassifierImpl.Mode.values().length)];

			WeakClassifierImpl weakClassifier = new WeakClassifierImpl(pos, type, orientation, mode);
			weakClassifier.setThreshold(r.nextFloat()*0.5);
			classifiers.add(weakClassifier);
		}

		int iterations = 30;

		AdaBoost trainer = new AdaBoost(testImage, integralImage, classifiers, iterations);
		strongClassifier = trainer.createTrainedClassifier();
	}

	private static int generateNumber(Random r, int min, int max) {
		return (int) ((r.nextFloat() * (max - min)) + min);
	}




	/**
     * Use strongClassifier to calculate a feature heat map.
     * Store all detected regions in the detectionResult list of rectangles.
     * The use of nonMaxSuppression is optional for the exercise.
	 * 
     * @param featureHeatMapPixels
     * @param width
     * @param height
     * @param threshold
     * @param nonMaxSuppression
     */
	protected void doDetection(int[] featureHeatMapPixels, int width, int height, float threshold, boolean nonMaxSuppression) {
		System.out.println("doDetection");
		
	   	// set current threshold for detection
    	strongClassifier.setThreshold(threshold);
     	
     	// detector size
		Dimension size = strongClassifier.getSize();
		
		double featureValue[] = new double[width * height];
		boolean isDetected[] = new boolean[width * height];

		// for all image positions that fully contain the detector region
     	for(int y = 0; y < height - size.height; y++) {	
			for(int x = 0; x < width - size.width; x++)	{
				int pos = y * width + x;
				
				// calculate feature value and classification result
				MatchingResult result = strongClassifier.matchingAt(integralImage, x, y);
				featureValue[pos] = result.featureValue;
				isDetected[pos] = result.isDetected;
				
				// draw feature map
				int gray = (int)(featureValue[pos] * 255 / threshold); // increase contrast by inverse threshold
				if(gray < 0)
					gray = 0;
				if(gray > 255)
					gray = 255;
				int red = gray;
				if(isDetected[pos]) {
					// colorize detected positions
					red = 255;
					gray = 0;
				}
				
				featureHeatMapPixels[pos] =  (0xFF << 24) | (red << 16) | (gray << 8) | gray;
			}
		}
		
     	detectionResult = new ArrayList<>();

     	// TODO: Optional in the exercise: Apply a non-maximum suppression (and adjust
     	// isDetected[] and featureHeatMapPixels[] accordingly).
     	
     	// TODO: Store all detected regions in the detectionResult list of rectangles. Each rectangle
     	// should have detector size.
     	
     	// TODO: remove solution
     	int h = height - size.height;
     	int w = width - size.width;
     	if(nonMaxSuppression) {
         	System.out.println("non-max suppression");
    		int range = 20;
         	for(int y = 0; y < h; y++) {	
    			for(int x = 0; x < w; x++)	{
    				int pos = y * width + x;
    				if(!isDetected[pos])
    					continue;
    				double corr = featureValue[pos];
    				boolean keepValue = true;
    				for(int dy = -range; dy < range && keepValue; dy++) {
    					for(int dx = -range; dx < range && keepValue; dx++) {
    						int x0 = x + dx;
    						int y0 = y + dy;
    						if(x0 < 0) x0 = 0;
    						if(x0 >= w) x0 = w-1;
    						if(y0 < 0) y0 = 0;
    						if(y0 >= h) y0 = h-1;
    						int pos0 = y0 * width + x0;
    						if(pos != pos0 && featureValue[pos0] >= corr) {
    							isDetected[pos] = false;
    							int gray = (int)(featureValue[pos] * 255 / threshold); // increase contrast by inverse threshold
    							if(gray < 0)
    								gray = 0;
    							if(gray > 255)
    								gray = 255;
    							featureHeatMapPixels[pos] =  (0xFF << 24) | (gray << 16) | (gray << 8) | gray;
    							featureValue[pos] = 0;
    							keepValue = false;
    						}
    					}
    				}
    			}
         	}
     	}
     	System.out.println("collect results");
     	for(int y = 0; y < h; y++) {	
			for(int x = 0; x < w; x++)	{
				int pos = y * width + x;
				if(isDetected[pos]) {
					detectionResult.add(new Rectangle(x, y, size.width, size.height));
				}
			}
     	}
	}
}
