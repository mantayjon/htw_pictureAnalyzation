package de.htw.ba.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Collections;

import de.htw.ba.model.ImageData;
import de.htw.ba.model.PixelCodebook;
import javafx.scene.image.Image;

/**
 * Codebook viewer. Lists all image files in a directory.
 * Provides an image and image patch viewer. Furthermore is visualizes the codebook.
 * <p>
 * The controller handles all events of the CodebookViewer.fxml view.
 *
 * @author Nico Hezel
 */
public class CodebookViewerController extends CodebookViewerBase {

    /**
     * Splits the image into several sub regions. The regions have a size of patchSize x patchSize.
     * All the patches will be listed in scan line order into an array list. Each entry in the list
     * contains all the pixels of this sub region and their corresponding RGB pixels. There
     * are patchSize x patchSize x RGB (e.g. 16x16x3=768) values in a patch and every one of
     * them has a value between 0 and 255. The values are sorted in signed bytes:
     * <p>
     * int i = 200;				// prints 200
     * byte b = (byte) 200;		// prints -256
     * int r = b & 0xFF;		// prints 200
     * <p>
     * A similar technique is used to read and write ARGB values from and into an integer.
     * <p>
     * Example of a 6x4 pixel images divided into 3x2 sub regions with a patch size of 2.
     * -------------------------------
     * | RGB RGB | RGB RGB | RGB RGB |
     * | RGB RGB | RGB RGB | RGB RGB |
     * -------------------------------
     * | RGB RGB | RGB RGB | RGB RGB |
     * | RGB RGB | RGB RGB | RGB RGB |
     * -------------------------------
     *
     * @param image
     * @param patchSize
     * @return list of patches
     */
    @Override
    public List<byte[]> extractPatches(Image image, int patchSize) {

        int patchTotalNumber = (int) Math.pow((image.getWidth() / patchSize), 2);
        ArrayList<byte[]> patches = new ArrayList<>(patchTotalNumber);

        for (int yIm = 0; yIm < image.getHeight(); yIm += patchSize) {
            for (int xIm = 0; xIm < image.getWidth(); xIm += patchSize) {

                byte[] patch = new byte[patchSize * patchSize * 3];

                for (int y = 0; y < patchSize; y++) {
                    for (int x = 0; x < patchSize; x++) {

                        int pos = (y * patchSize + x) * 3;

                        int pixel = image.getPixelReader().getArgb(x + xIm, y + yIm);

                        int red = (pixel >> 16) & 0xFF;
                        int green = (pixel >> 8) & 0xFF;
                        int blue = pixel & 0xFF;

                        patch[pos] = (byte) red;        // red
                        patch[pos + 1] = (byte) green;    // green
                        patch[pos + 2] = (byte) blue;        // blue

                    }
                }
                patches.add(patch);
            }
        }
        return patches;
    }

    /**
     * Creates a codebook by taking the pixel of a patch as a feature vector
     * and clustering all provided feature vectors into a few clusters.
     * The amount of cluster is determined by the codebook size.
     *
     * @param patches
     * @param codebookSize
     * @return codebook
     */


    @Override
    public PixelCodebook buildCodebook(List<byte[]> patches, int codebookSize) {


        List<byte[]> codeBook = new ArrayList<>();
        List<byte[]> clusterCenters = getRandomClusterCenters(patches,codebookSize);

        boolean centersChanged = false;

        while (!centersChanged) {
            List<List<byte[]>> patchGroups = new ArrayList<>();

            for (int n = 0; n < codebookSize; n++) {
                patchGroups.add(new ArrayList<>());
            }

            for (byte[] patch : patches) {

                double smallestDistance = Double.MAX_VALUE;
                int idx = 0;
                for (int j = 0; j < codebookSize; j++) {

                    double distance = calcDistance(patch, clusterCenters.get(j));

                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        idx = j;
                    }
                }
                patchGroups.get(idx).add(patch);
            }

            for (int j = 0; j < codebookSize; j++) {
                byte[] newCenter = calculateCenter(patchGroups.get(j));
                if (!Arrays.equals(clusterCenters.get(j), newCenter)) {
                    clusterCenters.set(j, newCenter);
                    centersChanged = true;
                }
            }
        }

        for (byte[] clusterCenter : clusterCenters) {

            double smallestDistance = Double.MAX_VALUE;

            byte[] closestPatch = patches.get(0);

            for (byte[] patch : patches) {

                double distance = calcDistance(patch, clusterCenter);

                if (distance < smallestDistance) {
                    closestPatch = patch;
                    smallestDistance = distance;
                }
            }
            codeBook.add(closestPatch);
        }

        return new PixelCodebook() {

            @Override
            public byte[] getCode(int codeIndex) {
                return codeBook.get(codeIndex);
            }

            @Override
            public int findClosestCode(byte[] featureVector) {

                double smallestDistance = Double.MAX_VALUE;
                int closestCode = 0;

                for (int i = 0; i < codeBook.size(); i++) {

                    byte[] singleCode = codeBook.get(i);

                    double distance = calcDistance(featureVector, singleCode);

                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        closestCode = i;
                    }
                }
                return closestCode;
            }

            @Override
            public float[] computeHistogram(List<byte[]> featureVectors) {
                float[] histogram = new float[codeBook.size()];

                for (byte[] featureVector : featureVectors) {
                    int closestCodeIndex = findClosestCode(featureVector);
                    histogram[closestCodeIndex]++;
                }

                int vectorCount = featureVectors.size();
                for (int i = 0; i < histogram.length; i++) {
                    histogram[i] /= vectorCount;
                }

                return histogram;
            }

        };
    }



    /**
     * Converts the feature vectors of a patch into the feature vector of the
     * nearest cluster center from the codebook.
     *
     * @param codebook
     * @param patches
     * @return
     */
    @Override
    public List<byte[]> convertPatchToCodes(PixelCodebook codebook, List<byte[]> patches) {
        final List<byte[]> codes = new ArrayList<>(patches.size());
        for (int i = 0; i < patches.size(); i++) {
            final int codeIndex = codebook.findClosestCode(patches.get(i));
            codes.add(codebook.getCode(codeIndex));
        }
        return codes;
    }

    @Override
    public void computeHistogram(PixelCodebook codebook, ImageData imageData, int patchSize) {
        // create patches
        final List<byte[]> patches = extractPatches(imageData.getImage(), patchSize);

        // compute histogram with patches and codebook
        imageData.setHistogram(codebook.computeHistogram(patches));
    }

    /**
     * Sorts the elements in the database based on the euclidean distance to the search query.
     * The distance will be calculated between the histogram of the query and the histogram
     * of every database entry. A sorted list of all database entries will be returned.
     *
     * @param query
     * @param database
     * @return sorted database list
     */
    @Override
    public List<ImageData> retrieve(ImageData query, Collection<ImageData> database) {
        return new ArrayList<>(database);
    }

    public List<byte[]> getRandomClusterCenters(List<byte[]> patches, int codebookSize) {

        List<byte[]> clusterCenters = new ArrayList<>();
        List<byte[]> patchesCopy = new ArrayList<>();

        for (byte[] patch : patches) {
            patchesCopy.add(Arrays.copyOf(patch, patch.length));
        }

        Collections.shuffle(patchesCopy);
        for (int i = 0; i < codebookSize; i++) {
            clusterCenters.add(Arrays.copyOf(patchesCopy.get(i), patchesCopy.get(i).length));
        }
        return clusterCenters;
    }

    private double calcDistance(byte[] firstPatch, byte[] secondPatch) {

        double distance = 0.0;

        for (int i = 0; i < firstPatch.length; i++) {

            int difference = secondPatch[i] - firstPatch[i];
            difference = (int) Math.pow(difference, 2);
            distance += difference;

        }
        return distance;
    }


    private byte[] calculateCenter(List<byte[]> patches) {

        int size = patches.get(0).length;

        byte[] newCenter = new byte[size];

        for (int i = 0; i < size; i++) {
            int sum = 0;
            for (byte[] patch : patches) {
                sum += patch[i];
            }
            newCenter[i] = (byte) (sum / patches.size());
        }
        return newCenter;
    }
}
