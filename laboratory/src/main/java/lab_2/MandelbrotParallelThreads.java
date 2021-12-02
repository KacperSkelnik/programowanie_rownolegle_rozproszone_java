package lab_2;

import org.apache.commons.math3.complex.Complex;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static utils.MathUtils.chunkArray;
import static utils.MathUtils.linspace;
import static utils.PaintUtils.paint;
import static utils.PaintUtils.toColor;
import static utils.TestUtils.saveToFile;
import static utils.TestUtils.testPerformance;


public class MandelbrotParallelThreads {
    int pictureSize; // pixels
    int numberOfWorkers;
    Double[] cornersWidth;
    Double[] cornersHigh;
    Double[] Re;
    Double[] Im;


    public MandelbrotParallelThreads(int size, int workers) {
        pictureSize = size;
        numberOfWorkers = workers;

        cornersWidth = new Double[]{-2.1, 0.6};
        cornersHigh = new Double[]{-1.2, 1.2};

        Re = linspace(cornersWidth[0], cornersWidth[1], size);
        Im = linspace(cornersHigh[0], cornersHigh[1], size);
    }


    public static class MandelbrotPartRunnable implements Runnable {
        Double[] cornersWidth;
        Double[] cornersHigh;
        int pictureWidth;
        int pictureHigh;
        int maxNumIterations;

        Double[] Re;
        Double[] Im;
        Color[][] Mandelbrot;


        public MandelbrotPartRunnable(Double a, Double b, Double c, Double d, int width, int high) {
            this.cornersWidth = new Double[]{a, b};
            this.cornersHigh = new Double[]{c, d};
            this.pictureWidth = width;
            this.pictureHigh = high;
            this.maxNumIterations = 200;

            this.Re = linspace(cornersWidth[0], cornersWidth[1], pictureWidth);
            this.Im = linspace(cornersHigh[0], cornersHigh[1], pictureHigh);
            this.Mandelbrot = new Color[pictureWidth][pictureHigh];
        }

        @Override
        public void run(){
            for(int i = 0; i < this.pictureWidth; i++){
                for(int j = 0; j < this.pictureHigh; j++){
                    this.Mandelbrot[i][j] = toColor(0, maxNumIterations, isConvergent(this.Re[i], this.Im[j], this.maxNumIterations));
                }
            }
        }

        public Color[][] get() {
            return Mandelbrot;
        }
    }


    public static int isConvergent(Double re, Double im, int maxNumIterations){
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


    public Color[][] create() {
        int chunkSize = (int) Math.ceil((float) pictureSize/numberOfWorkers);
        Double[][] chunkedIm = chunkArray(Im, chunkSize);

        ArrayList<MandelbrotPartRunnable> runnables = new ArrayList<>();
        for (Double[] chunkIm: chunkedIm){
            runnables.add(new MandelbrotPartRunnable(cornersWidth[0], cornersWidth[1],
                    chunkIm[0], chunkIm[chunkIm.length-1], pictureSize, chunkSize));
        }

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i=0; i<numberOfWorkers; i++){
            threads.add(new Thread(runnables.get(i)));
            threads.get(i).start();
        }

        try {
            for (int i=0; i<numberOfWorkers; i++){
                threads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Color[][]> result = new ArrayList<>();
        for (int i=0; i<numberOfWorkers; i++){
            result.add(runnables.get(i).get());
        }

        Color[][] Mandelbrot = new Color[pictureSize][pictureSize];
        int y = 0;
        for (Color[][] r: result){
            for (int i=0; i<pictureSize; i++){
                if (chunkSize >= 0) System.arraycopy(r[i], 0, Mandelbrot[i], (y * chunkSize), chunkSize);
            }
            if (y < chunkedIm.length){
                y++;
            }
        }

        return Mandelbrot;
    }


    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException,
             IllegalAccessException {
        MandelbrotParallelThreads Mandelbrot = new MandelbrotParallelThreads(8192, 8);
        Color[][] c = Mandelbrot.create();

        paint(Mandelbrot.pictureSize, Mandelbrot.pictureSize, c,
                "laboratory/src/main/java/lab_2/piękny_rysunek_na_laboratorium2.png");

        Integer[] params = new Integer[]{32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
        ArrayList<Double> times = new ArrayList<>();
        for (Integer param : params) {
            MandelbrotParallelThreadPool MandelbrotTest = new MandelbrotParallelThreadPool(param, param);

            Method create = MandelbrotParallelThreadPool.class.getMethod("create");
            times.add(testPerformance(MandelbrotTest, create, 10));
        }

        saveToFile(times, "laboratory/src/main/java/lab_2/dane_do_wykresu.txt");
    }
}
