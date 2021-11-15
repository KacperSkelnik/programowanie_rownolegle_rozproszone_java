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

}
