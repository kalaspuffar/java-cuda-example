package org.ea.gpu;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class JavaFilterMulti {
    private static final int[] filterDelute = new int[] {
            1, 2, 1,
            2, 4, 2,
            1, 2, 1
    };

    public static void main(String[] args) {
        try {

            ExecutorService fixedPool = Executors.newFixedThreadPool(10);

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

            int part = h / 10;

            CompletionService completionService =
                    new ExecutorCompletionService(fixedPool);

            int[] resultVertical = new int[w * h];

            for(int i=0; i<10; i++) {
                completionService.submit(new Runner(i * part, (i + 1) * part, w, resultVertical, pixels, filterDelute));
            }

            int received = 0;
            while(received < 10) {
                completionService.take();
                received++;
            }

            fixedPool.shutdown(); // shut down

            //int[] resultVertical = runFilter(w, h, pixels, filterDelute);

            System.out.println("Time " + (System.currentTimeMillis() - start));
            grayscaleImg.getRaster().setPixels(0, 0, w, h, resultVertical);

            ImageIO.write(grayscaleImg, "PNG", new File("java-result-multi.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
