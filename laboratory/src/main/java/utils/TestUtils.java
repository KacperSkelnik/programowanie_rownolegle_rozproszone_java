package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TestUtils {

    public static Double testPerformance(Object object, Method method, int numIter)
            throws InvocationTargetException, IllegalAccessException {

        double duration = 0.0;
        for(int i = 0; i < numIter; i++){
            long startTime = System.nanoTime();
            method.invoke(object);
            long endTime = System.nanoTime();
            duration += (endTime - startTime); //ms
        }
        return duration/1000000/numIter; //milliseconds
    }

    public static Double testPerformance(Object object, Method method, int numIter, Object... args)
            throws InvocationTargetException, IllegalAccessException {

        double duration = 0.0;
        for(int i = 0; i < numIter; i++){
            long startTime = System.nanoTime();
            method.invoke(object, args);
            long endTime = System.nanoTime();
            duration += (endTime - startTime); //ms
        }
        return duration/1000000/numIter; //milliseconds
    }

    public static void saveToFile(ArrayList toSave, String fileName) throws IOException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
        for (Object data : toSave)
            pw.println(data.toString());
        pw.close();
    }

}
