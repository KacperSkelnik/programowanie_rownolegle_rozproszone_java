package lab_2;

import org.apache.commons.math3.complex.Complex;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static utils.MathUtils.chunkArray;
import static utils.MathUtils.linspace;
import static utils.PaintUtils.paint;
import static utils.PaintUtils.toColor;
import static utils.TestUtils.saveToFile;
import static utils.TestUtils.testPerformance;


public class MandelbrotParallelThreadPool {
    int pictureSize; // pixels
    int chunkSize;
    Double[] cornersWidth;
    Double[] cornersHigh;
    Double[] Re;
    Double[] Im;


    public MandelbrotParallelThreadPool(int size, int workers) {
        pictureSize = size;
        chunkSize = (int) Math.ceil((float) pictureSize/workers); //workers; //jeśli chcemy testować w stosunku do wielkości chunka

        cornersWidth = new Double[]{-2.1, 0.6};
        cornersHigh = new Double[]{-1.2, 1.2};

        Re = linspace(cornersWidth[0], cornersWidth[1], size);
        Im = linspace(cornersHigh[0], cornersHigh[1], size);
    }


    public static class MandelbrotPartCallable implements Callable<Color[][]> {
        Double[] cornersWidth;
        Double[] cornersHigh;
        int chunkSize;
        int maxNumIterations;

        Double[] Re;
        Double[] Im;


        public MandelbrotPartCallable(Double a, Double b, Double c, Double d, int size) {
            this.cornersWidth = new Double[]{a, b};
            this.cornersHigh = new Double[]{c, d};
            this.chunkSize = size;
            this.maxNumIterations = 200;

            this.Re = linspace(cornersWidth[0], cornersWidth[1], size);
            this.Im = linspace(cornersHigh[0], cornersHigh[1], size);
        }

        @Override
        public Color[][] call(){
            Color[][] color = new Color[this.chunkSize][this.chunkSize];
            for(int i = 0; i < this.chunkSize; i++){
                for(int j = 0; j < this.chunkSize; j++){
                    color[i][j] = toColor(0, maxNumIterations, isConvergent(this.Re[i], this.Im[j], this.maxNumIterations));
                }
            }
            return color;
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


    public Color[][] create() throws InterruptedException, ExecutionException {
        ArrayList<Callable<Color[][]>> callableList = new ArrayList<>();
        Double[][] chunkedRe = chunkArray(Re, chunkSize);
        Double[][] chunkedIm = chunkArray(Im, chunkSize);

        for (Double[] chunkRe: chunkedRe){
            for (Double[] chunkIm: chunkedIm){
                callableList.add(new MandelbrotPartCallable(chunkRe[0], chunkRe[chunkRe.length-1],
                        chunkIm[0], chunkIm[chunkIm.length-1], chunkSize));
            }
        }

        ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Color[][]>> futures = ex.invokeAll(callableList);
        ex.shutdown();

        Color[][] Mandelbrot = new Color[pictureSize][pictureSize];
        int x = 0;
        int y = 0;
        for (Future<Color[][]> f: futures){
            for (int i=0; i<chunkSize; i++){
                for (int j=0; j<chunkSize; j++) {
                    Mandelbrot[i+(x*chunkSize)][j+(y*chunkSize)] = f.get()[i][j];
                }
            }
            y++;
            if (y >= chunkedRe.length){
                y = 0;
                x++;
            }
        }

        return Mandelbrot;
    }


    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException,
            InterruptedException, IllegalAccessException, ExecutionException {
        // Rysunek
        MandelbrotParallelThreadPool Mandelbrot = new MandelbrotParallelThreadPool(8192, 8);
        Color[][] c = Mandelbrot.create();

        paint(Mandelbrot.pictureSize, Mandelbrot.pictureSize, c,
                "laboratory/src/main/java/lab_2/piękny_rysunek_na_laboratorium.png");


        // Testy wydajności od wielkości
        Integer[] params = new Integer[]{32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
        ArrayList<Double> times = new ArrayList<>();
        for (Integer param : params) {
            MandelbrotParallelThreadPool MandelbrotTest = new MandelbrotParallelThreadPool(param, 8);

            Method create = MandelbrotParallelThreadPool.class.getMethod("create");
            times.add(testPerformance(MandelbrotTest, create, 10));
        }

        saveToFile(times, "laboratory/src/main/java/lab_2/dane_do_wykresu1.txt");


        // Testy wydajności op podziału dla różnych wielkości
        Integer[] params2 = new Integer[]{4, 8, 16, 32, 64, 128};
        ArrayList<Double> times2 = new ArrayList<>();
        for (Integer param : params2) {
            MandelbrotParallelThreadPool MandelbrotTest = new MandelbrotParallelThreadPool(4096, param);

            Method create = MandelbrotParallelThreadPool.class.getMethod("create");
            times.add(testPerformance(MandelbrotTest, create, 10));
        }

        saveToFile(times2, "laboratory/src/main/java/lab_2/dane_do_wykresu3_4096.txt");
    }
}
