package org.ea.gpu;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class Runner implements Callable<Integer> {
    int sy, eh, w;
    int[] newImage = null;
    int[] pixels = null;
    int[] filter = null;


    public static final int FILTER_SIZE = 3;

    public static int[] apply3x3Filter(int imageX, int imageY, int imageWidth, int[] img, int[] filter, int[] newImage) {

        float sum = 0f;
        sum = 0f;
        for (int filterY = 0; filterY < FILTER_SIZE; filterY++) {
            for (int filterX = 0; filterX < FILTER_SIZE; filterX++) {
                sum += img[((imageY + filterY) * imageWidth) + imageX + filterX] * filter[(filterY * FILTER_SIZE) + filterX];
            }
        }

        newImage[((imageY + 1) * imageWidth) + imageX + 1] = (int)Math.floor(sum / 16f);

        return newImage;
    }

    public Runner(int sy, int eh, int w, int[] image, int[] pixels, int[] filter) {
        this.newImage = image;
        this.pixels = pixels;
        this.filter = filter;
        this.sy = sy;
        this.eh = eh;
        this.w = w;
        Arrays.fill(newImage, 0);
    }

    public Integer call() {
        for(int y=sy; y<eh-FILTER_SIZE-1; y++) {
            for(int x=0; x<w-FILTER_SIZE-1; x++) {
                newImage = apply3x3Filter(x, y, w, pixels, filter, newImage);
            }
        }
        return 1;
    }
}