package de.htw.ba.ue05.facedetection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import de.htw.ba.facedetection.IntegralImage;
import de.htw.ba.facedetection.StrongClassifier;
import de.htw.ba.facedetection.TestImage;
import de.htw.ba.facedetection.WeakClassifier;

public class AdaBoost {

	StrongClassifier trainedStrongClassifier;
	final TestImage testImage;
	IntegralImage integralImage;
	List<WeakClassifier> classifiers;
	int iterations ;
	List<Rectangle> faces;
	List<Double> faceWeights;
	List<Rectangle> noFaces;
	List<Double> nonFaceWeights;

	public AdaBoost(TestImage testImage, IntegralImage integralImage, List<WeakClassifier> classifiers, int iterations) {
		this.testImage = testImage;
		this.integralImage = integralImage;
		this.classifiers = classifiers;
		this.iterations = iterations;

		faces = testImage.getFaceRectangles();
		noFaces = testImage.getNonFaceRectangles();
		faceWeights = new ArrayList<>();
		nonFaceWeights = new ArrayList<>();
	}

	public StrongClassifier createTrainedClassifier() {

		for (int i = 0; i < faces.size(); i++) {
				faceWeights.add(1.0/(faces.size()*2));
		}
		for (int i = 0; i < noFaces.size(); i++) {
			nonFaceWeights.add(1.0/(noFaces.size()*2));
		}

		Dimension dimension = testImage.getAverageFaceDimensions().getSize();
		StrongClassifier strongClassifier = new StrongClassifierImpl(dimension);
		List<WeakClassifier> selectedClassifiers = new ArrayList<>(iterations);

		selectClassifiers(classifiers, iterations, selectedClassifiers, strongClassifier);

		strongClassifier.normalizeWeights();
		trainedStrongClassifier = strongClassifier;

		return trainedStrongClassifier;
	}

	private void selectClassifiers(List<WeakClassifier> classifiers, int iterations,
								   List<WeakClassifier> selectedClassifiers, StrongClassifier strongClassifier) {

		for (int i = 0; i < iterations; i++) {
			normalizeImageWeights();
			double bestError = Double.MAX_VALUE;
			WeakClassifier bestClassifier = null;

			for (WeakClassifier classifier : classifiers) {
				double error = calculateError(classifier);
				if (error < bestError) {
					bestError = error;
					bestClassifier = classifier;
				}
			}

			if (bestClassifier != null) {
				selectedClassifiers.add(bestClassifier);
				updateWeights(bestClassifier, bestError);
				classifiers.remove(bestClassifier);
			}
		}
		for(WeakClassifier classifier : selectedClassifiers){
			strongClassifier.addWeakClassifier(classifier);
		}
	}

	private void updateWeights(WeakClassifier classifier, double error) {

		if(error == 0.0) error = 0.000000000001;

		double beta = error / (1.0 - error);

		for(Rectangle face: faces){
			int faceWeightIndex = 0;
			boolean falseNegative = !classifier.matchingAt(integralImage,face.x,face.y).isDetected;

			if(falseNegative){
				double newValue = faceWeights.get(faceWeightIndex) * beta;
				faceWeights.set(faceWeightIndex,newValue);
			}

		}
		for(Rectangle noFace: noFaces){
			int noFaceWeightIndex = 0;
			boolean falsePositive = classifier.matchingAt(integralImage, noFace.x, noFace.y).isDetected;

			if(falsePositive){
				double newValue = nonFaceWeights.get(noFaceWeightIndex) * beta;
				nonFaceWeights.set(noFaceWeightIndex,newValue);
			}
		}
	}

	private double calculateError(WeakClassifier classifier) {

		double error = 0.0;

		for(Rectangle face: faces){
			int faceWeightIndex = 0;
			boolean falseNegative = !classifier.matchingAt(integralImage,face.x,face.y).isDetected;
			if(falseNegative){
				error += faceWeights.get(faceWeightIndex);
			}

		}
		for(Rectangle nonface: noFaces){
			int nonFaceWeightIndex = 0;
			boolean falsePositive = classifier.matchingAt(integralImage, nonface.x, nonface.y).isDetected;
			if(falsePositive){
				error += nonFaceWeights.get(nonFaceWeightIndex);
			}

		}
		return error;
	}

	private void normalizeImageWeights() {
		double sum = 0;
		for(int i = 0; i<faces.size(); i++){
			sum += faceWeights.get(i);
		}
		for(int i = 0; i< noFaces.size(); i++){
			sum += nonFaceWeights.get(i);
		}
		for(int i = 0; i<faces.size(); i++){
			double newValue = faceWeights.get(i) / sum;
			faceWeights.set(i,newValue);
		}
		for(int i = 0; i< noFaces.size(); i++){
			double newValue = nonFaceWeights.get(i) / sum;
			nonFaceWeights.set(i,newValue);
		}
	}

}

