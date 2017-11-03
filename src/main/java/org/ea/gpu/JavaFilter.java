package org.ea.gpu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class JavaFilter {
    private static final int[] filterDelute = new int[] {
            1, 2, 1,
            2, 4, 2,
            1, 2, 1
    };

    public static final int FILTER_SIZE = 3;

    public static int[] apply3x3Filter(int imageX, int imageY, int imageWidth, int[] img, int[] filter, int[] newImage) {

        float sum = 0f;
        for(int j=0; j<1000000; j++) {
            sum = 0f;
            for (int filterY = 0; filterY < FILTER_SIZE; filterY++) {
                for (int filterX = 0; filterX < FILTER_SIZE; filterX++) {
                    sum += img[((imageY + filterY) * imageWidth) + imageX + filterX] * filter[(filterY * FILTER_SIZE) + filterX];
                }
            }
        }

        newImage[((imageY + 1) * imageWidth) + imageX + 1] = (int)Math.floor(sum / 16f);

        return newImage;
    }

    public static int[] runFilter(int w, int h, int[] pixels, int[] filter) {
        int[] newImage = new int[w * h];
        Arrays.fill(newImage, 0);

        for(int y=0; y<h-FILTER_SIZE-1; y++) {
            for(int x=0; x<w-FILTER_SIZE-1; x++) {
                newImage = apply3x3Filter(x, y, w, pixels, filter, newImage);
            }
        }

        return newImage;
    }

    public static void main(String[] args) {
        try {
            BufferedImage bi = ImageIO.read(new File("test.png"));
            int w = bi.getWidth();
            int h = bi.getHeight();
            int[] pixels = new int[w * h];

            BufferedImage grayscaleImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = grayscaleImg.getGraphics();
            g.drawImage(bi, 0, 0, null);
            g.dispose();

            ImageIO.write(grayscaleImg, "PNG", new File("before.png"));

            pixels = grayscaleImg.getRaster().getPixels(0, 0, w, h, pixels);

            long start = System.currentTimeMillis();
            int[] resultVertical = runFilter(w, h, pixels, filterDelute);
            System.out.println("Time " + (System.currentTimeMillis() - start));
            grayscaleImg.getRaster().setPixels(0, 0, w, h, resultVertical);

            ImageIO.write(grayscaleImg, "PNG", new File("java-result.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
