package utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MathUtils {

    public static Double[] linspace(double start, double end, int numPoints) {
        return IntStream.range(0, numPoints)
                .boxed()
                .map(i -> start + i * (end - start) / (numPoints - 1))
                .collect(Collectors.toList())
                .toArray(new Double[numPoints]);
    }

    public static Double[][] chunkArray(Double[] array, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)array.length / chunkSize);
        Double[][] output = new Double[numOfChunks][];
        for(int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            Double[] temp = new Double[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }
        return output;
    }

}
