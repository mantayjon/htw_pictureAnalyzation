/**
 * @author Nico Hezel
 * modified by K. Jung, 28.10.2016
 */
package de.htw.ba.ue02.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class HoughTransformationController extends HoughTransformationBase {



    protected static enum Methods {
        Empty, Accumulator, Maximum, Line
    }

    ;

    @Override
    public void runMethod(Methods currentMethod, int[] srcPixels, int srcWidth, int srcHeight, int[] dstPixels, int dstWidth, int dstHeight, float sliderValue) throws Exception {
        switch (currentMethod) {
            case Accumulator:
                showAcc(srcPixels, srcWidth, srcHeight, dstPixels, dstWidth, dstHeight);
                break;
            case Maximum:
                showMax(srcPixels, srcWidth, srcHeight, dstPixels, dstWidth, dstHeight, sliderValue);
                break;
            case Line:
                showLines(srcPixels, srcWidth, srcHeight, dstPixels, dstWidth, dstHeight, sliderValue);
                break;
            case Empty:
            default:
                empty(dstPixels, dstWidth, dstHeight);
                break;
        }
    }

    private void empty(int[] dstPixels, int dstWidth, int dstHeight) {
        // all pixels black
        Arrays.fill(dstPixels, 0xff000000);
    }

    private void showAcc(int[] srcPixels, int srcWidth, int srcHeight, int[] dstPixels, int dstWidth, int dstHeight) {

        empty(dstPixels, dstWidth, dstHeight);
        int[][] Akkumulator = createAcc(srcPixels, srcWidth, srcHeight, dstWidth, dstHeight);

        double max = findMax(Akkumulator, dstWidth, dstHeight);

        for (int i = 0; i < dstHeight; i++) {
            for (int n = 0; n < dstWidth; n++) {
                int pos = i * dstWidth + n;
                double pixelAkk = ((double) Akkumulator[i][n] / max) * 255;

                dstPixels[pos] = 0xFF000000 + (((int) pixelAkk & 0xff) << 16) + (((int) pixelAkk & 0xff) << 8)
                        + ((int) pixelAkk & 0xff);
            }
        }
    }

    private void showMax(int[] srcPixels, int srcWidth, int srcHeight, int[] dstPixels, int dstWidth,
                         int dstHeight, float sliderValue) {

        empty(dstPixels, dstWidth, dstHeight);

        int[][] akkumulator = createAcc(srcPixels, srcWidth, srcHeight, dstWidth, dstHeight);
        int[][] max = new int[dstHeight][dstWidth];

        double maxRange = findMax(akkumulator, dstWidth, dstHeight) * sliderValue;
        int kernelSize = 10;
        boolean isMax;
        int hotspot;

        for (int y = 0; y < dstHeight; y++) {
            for (int x = 0; x < dstWidth; x++) {
                int pos = y * dstWidth + x;

                if (akkumulator[y][x] > maxRange) {
                    isMax = true;
                    hotspot = akkumulator[y][x];

                    for (int yKernel = -kernelSize; yKernel < kernelSize; yKernel++) {
                        for (int xKernel = -kernelSize; xKernel < kernelSize; xKernel++) {
                            int posX = x + xKernel;
                            int posY = y + yKernel;

                            if (posX < 0 || posX >= dstWidth || posY < 0 || posY >= dstHeight || ((posX == x) && (posY == y))) {
                                continue;
                            }

                            if(akkumulator[posY][posX] >= hotspot) isMax = false;
                        }
                    }
                    if (isMax) {
                        dstPixels[pos] = 0xFFFFFFFF;
                    }
                }
            }
        }
    }

    private void showLines(int[] srcPixels, int srcWidth, int srcHeight, int[] dstPixels, int dstWidth, int dstHeight, float sliderValue) {

        empty(dstPixels, dstWidth, dstHeight);
        showMax(srcPixels, srcWidth, srcHeight, dstPixels, dstWidth, dstHeight, sliderValue);


        double[] phis = generatePhis(dstWidth);
        double radMax = Math.sqrt(Math.pow(srcWidth, 2) + Math.pow(srcHeight, 2)) / 2;
        int[][] acc = createAcc(srcPixels, srcWidth, srcHeight, dstWidth, dstHeight);
        double max = findMax(acc,dstWidth,dstHeight );


        BufferedImage bufferedImage = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, srcWidth, srcHeight, srcPixels, 0, srcWidth);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.RED);
        g2d.drawLine(0, 0, srcWidth, srcHeight);
        g2d.drawLine(srcWidth, 0, 0, srcHeight);
        g2d.dispose();
        bufferedImage.getRGB(0, 0, srcWidth, srcHeight, srcPixels, 0, srcWidth);
    }

    private int[][] createAcc(int[] srcPixels, int srcWidth, int srcHeight, int dstWidth, int dstHeight) {


        int[][] Akkumulator = new int[dstHeight][dstWidth];
        double[] Phis = generatePhis(dstWidth);
        double radMax = Math.sqrt(Math.pow(srcWidth, 2) + Math.pow(srcHeight, 2)) / 2;


        for (int ySrc = 0; ySrc < srcHeight; ySrc++) {
            for (int xSrc = 0; xSrc < srcWidth; xSrc++) {

                int pos = ySrc * srcWidth + xSrc;

                if ((srcPixels[pos] & 0xff) == 255) {
                    int yTransformed = -(ySrc - (srcHeight / 2));
                    int xTransformed = xSrc - srcWidth / 2;

                    for (int n = 0; n < dstWidth; n++) {
                        double r = xTransformed * Math.cos(Phis[n]) + yTransformed * Math.sin(Phis[n]);
                        double rNorm = r / radMax;
                        rNorm = ((rNorm + 1) * ((double) (dstHeight) / 2));
                        int rAkku = Math.min(dstHeight - 1, (int) Math.max(0, rNorm));
                        Akkumulator[rAkku][n]++;
                    }
                }

            }
        }
        return Akkumulator;
    }

    private double findMax(int[][] Akkumulator, int dstWidth, int dstHeight) {

        double accuMax = 0.0;
        for (int i = 0; i < dstHeight; i++) {
            for (int n = 0; n < dstWidth; n++) {
                if (Akkumulator[i][n] > accuMax) {
                    accuMax = Akkumulator[i][n];
                }
            }
        }
        return accuMax;
    }

    private double[] generatePhis(int dstWidth){

        double phiAkkumulator = 0;
        double onePhi = Math.PI / dstWidth;
        double[] Phis = new double[dstWidth];

        for (int i = 0; i < dstWidth; i++) {
            Phis[i] = phiAkkumulator;
            phiAkkumulator += onePhi;
        }
        return  Phis;
    }

}