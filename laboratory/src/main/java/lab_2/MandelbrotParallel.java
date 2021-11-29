package lab_2;

import org.apache.commons.math3.complex.Complex;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static utils.MathUtils.linspace;
import static utils.PaintUtils.paint;
import static utils.PaintUtils.toColor;
import static utils.TestUtils.saveToFile;
import static utils.TestUtils.testPerformance;

public class MandelbrotParallel {
    int pictureWidth; // pixels
    int pictureHigh;  // pixels
    Double[] cornersWidth;
    Double[] cornersHigh;
    int maxNumIterations;

    Double[] Re;
    Double[] Im;

    public MandelbrotParallel(int width, int high) {
        pictureWidth = width;
        pictureHigh = high;
        cornersWidth = new Double[]{-2.1, 0.6};
        cornersHigh = new Double[]{-1.2, 1.2};
        maxNumIterations = 200;

        Re = linspace(cornersWidth[0], cornersWidth[1], pictureWidth);
        Im = linspace(cornersHigh[0], cornersHigh[1], pictureHigh);
    }

    public int isConvergent(Double re, Double im){
        Complex z_0 = new Complex(0, 0);
        Complex C = new Complex(re, im);
        Complex z_1;
        for(int i = 0; i < maxNumIterations; i++){
            z_1 = z_0.multiply(z_0).add(C);
            if(z_1.abs() >= 2) return i;
            z_0 = z_1;
        }
        return maxNumIterations+1;
    }

    public Color[][] create() throws InterruptedException {
        ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Color[][] color = new Color[pictureWidth][pictureHigh];
        ex.execute(() -> {
            for (int i = 0; i < pictureWidth; i++) {
                for (int j = 0; j < pictureHigh; j++) {
                    color[i][j] = toColor(0, maxNumIterations, isConvergent(Re[i], Im[j]));
                }
            }
        });
        ex.shutdown();
        ex.awaitTermination(1, TimeUnit.HOURS);
        return color;
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException,
            InterruptedException, IllegalAccessException {
        MandelbrotParallel Mandelbrot = new MandelbrotParallel(8192, 8192);
        Color[][] c = Mandelbrot.create();
        paint(Mandelbrot.pictureWidth, Mandelbrot.pictureHigh, c,
                "laboratory/src/main/java/lab_2/piękny_rysunek_na_laboratorium.png");

        Integer[] params = new Integer[]{32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
        ArrayList<Double> times = new ArrayList<>();
        for (Integer param : params) {
            MandelbrotParallel MandelbrotTest = new MandelbrotParallel(param, param);

            Method create = MandelbrotParallel.class.getMethod("create");
            times.add(testPerformance(MandelbrotTest, create,20));
        }

        saveToFile(times, "laboratory/src/main/java/lab_2/dane_do_wykresu.txt");
    }
}
