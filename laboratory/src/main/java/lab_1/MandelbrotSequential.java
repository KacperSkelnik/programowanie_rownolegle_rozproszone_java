package lab_1;

import org.apache.commons.math3.complex.Complex;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static utils.MathUtils.linspace;
import static utils.PaintUtils.*;
import static utils.TestUtils.*;

public class MandelbrotSequential {
    int pictureWidth; // pixels
    int pictureHigh;  // pixels
    Double[] cornersWidth;
    Double[] cornersHigh;
    int maxNumIterations;

    Double[] Re;
    Double[] Im;

    public MandelbrotSequential() {
        pictureWidth = 32;
        pictureHigh = 32;
        cornersWidth = new Double[]{-2.1, 0.6};
        cornersHigh = new Double[]{-1.2, 1.2};
        maxNumIterations = 200;

        Re = linspace(cornersWidth[0], cornersWidth[1], pictureWidth);
        Im = linspace(cornersHigh[0], cornersHigh[1], pictureHigh);
    }

    public MandelbrotSequential(int width, int high) {
        pictureWidth = width;
        pictureHigh = high;
        cornersWidth = new Double[]{-2.1, 0.6};
        cornersHigh = new Double[]{-1.2, 1.2};
        maxNumIterations = 200;

        Re = linspace(cornersWidth[0], cornersWidth[1], pictureWidth);
        Im = linspace(cornersHigh[0], cornersHigh[1], pictureHigh);
    }

    public MandelbrotSequential(int width, int high, Double[] cornersW, Double[] cornersH, int iterations) throws Exception {
        pictureWidth = width;
        pictureHigh = high;
        if (cornersW.length == 2) cornersWidth = cornersW; else throw new Exception("cornersW must have 2 elements");
        if (cornersH.length == 2) cornersHigh = cornersH; else throw new Exception("cornersH must have 2 elements");
        maxNumIterations = iterations;

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

    public Color[][] create(){
        Color[][] color = new Color[pictureWidth][pictureHigh];
        for(int i = 0; i < pictureWidth; i++){
            for(int j = 0; j < pictureHigh; j++){
                color[i][j] = toColor(0, maxNumIterations, isConvergent(Re[i], Im[j]));
            }
        }
        return color;
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        MandelbrotSequential Mandelbrot = new MandelbrotSequential(8192, 8192);
        Color[][] c = Mandelbrot.create();
        paint(Mandelbrot.pictureWidth, Mandelbrot.pictureHigh, c,
        "laboratory/src/main/java/lab_1/piękny_rysunek_na_laboratorium.png");

        Integer[] params = new Integer[]{32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
        ArrayList<Double> times = new ArrayList<>();
        for (Integer param : params) {
            MandelbrotSequential MandelbrotTest = new MandelbrotSequential(param, param);

            Method create = MandelbrotSequential.class.getMethod("create");
            times.add(testPerformance(MandelbrotTest, create,50));
        }

        saveToFile(times, "laboratory/src/main/java/lab_1/dane_do_wykresu.txt");
    }
}
