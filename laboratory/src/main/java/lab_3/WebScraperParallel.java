package lab_3;

import lab_2.MandelbrotParallelThreadPool;
import lab_2.MandelbrotParallelThreads;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static utils.TestUtils.saveToFile;
import static utils.TestUtils.testPerformance;


public class WebScraperParallel{
    String urlToScrap;
    String saveTarget;
    Document doc;
    Elements pngs;
    Boolean blur;


    public WebScraperParallel(String url, String target, Boolean makeBlur) throws IOException {
        urlToScrap = url;
        doc = Jsoup.connect(urlToScrap).get();
        pngs = doc.select("a[href$=.png]");
        saveTarget = target;
        blur = makeBlur;
    }

    public static class WebScraperCallable implements Callable<Boolean> {
        String webUrl;
        String href;
        String saveTarget;
        Boolean blur;

        public WebScraperCallable(String url, String image, String target, Boolean makeBlur){
            this.webUrl = url;
            this.href = image;
            this.saveTarget = target;
            this.blur = makeBlur;
        }

        @Override
        public Boolean call() {
            BufferedImage image;
            try{
                URL url =new URL(this.webUrl+this.href);
                image = ImageIO.read(url);
                if(this.blur){
                    new GaussianBlur(image, 3).makeBlurSequential(this.saveTarget+this.href);
                }
                else{
                    ImageIO.write(image, "png",new File(this.saveTarget+this.href));
                }
                return true;
            }catch(IOException e){
                e.printStackTrace();
                return false;
            }
        }
    }

    public void getImages() throws InterruptedException {
        ArrayList<WebScraperParallel.WebScraperCallable> callableList = new ArrayList<>();
        for (Element link : pngs) {
            callableList.add(new WebScraperParallel.WebScraperCallable(urlToScrap, link.attr("href"),
                    saveTarget, blur));
        }

        ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Boolean>> futures = ex.invokeAll(callableList);
        ex.shutdown();
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        WebScraperParallel webScraper = new WebScraperParallel("http://www.if.pw.edu.pl/~mrow/dyd/wdprir/",
                "laboratory/src/main/java/lab_3/imgs/tests/", false);

        WebScraperParallel webScraperBlur = new WebScraperParallel("http://www.if.pw.edu.pl/~mrow/dyd/wdprir/",
                "laboratory/src/main/java/lab_3/imgs/tests/", true);

        ArrayList<Double> times = new ArrayList<>();
        ArrayList<Double> timesBlurred = new ArrayList<>();
        Method downloadImages = WebScraperParallel.class.getMethod("getImages");


        times.add(testPerformance(webScraper, downloadImages, 20));
        saveToFile(times, "laboratory/src/main/java/lab_3/dane_do_wykresu_parallel.txt");

        timesBlurred.add(testPerformance(webScraperBlur, downloadImages, 20));
        saveToFile(timesBlurred, "laboratory/src/main/java/lab_3/dane_do_wykresu_parallel_blurred.txt");
    }
}
