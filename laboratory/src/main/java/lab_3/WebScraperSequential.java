package lab_3;

import lab_2.MandelbrotParallelThreads;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;

import static utils.TestUtils.saveToFile;
import static utils.TestUtils.testPerformance;


public class WebScraperSequential {
    String urlToScrap;
    String saveTarget;
    Document doc;
    Elements pngs;


    public WebScraperSequential(String url, String target) throws IOException {
        urlToScrap = url;
        doc = Jsoup.connect(urlToScrap).get();
        pngs = doc.select("a[href$=.png]");
        saveTarget = target;
    }


    public void downloadBlurredImages() {
        for (Element link : pngs) {
            BufferedImage image;
            try{
                URL url =new URL(urlToScrap + link.attr("href"));
                image = ImageIO.read(url);
                new GaussianBlur(image, 3).makeBlurSequential(saveTarget+link.attr("href"));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }


    public void downloadImages() {
        for (Element link : pngs) {
            BufferedImage image;
            try{
                URL url =new URL(urlToScrap + link.attr("href"));
                image = ImageIO.read(url);
                ImageIO.write(image, "png",new File(saveTarget+link.attr("href")));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebScraperSequential webScraper = new WebScraperSequential("http://www.if.pw.edu.pl/~mrow/dyd/wdprir/",
                "laboratory/src/main/java/lab_3/imgs/tests/");


        ArrayList<Double> times = new ArrayList<>();
        Method downloadImages = WebScraperSequential.class.getMethod("downloadImages");
        times.add(testPerformance(webScraper, downloadImages, 20));
        saveToFile(times, "laboratory/src/main/java/lab_3/dane_do_wykresu_seq.txt");


        ArrayList<Double> times2 = new ArrayList<>();
        Method downloadBlurredImages = WebScraperSequential.class.getMethod("downloadBlurredImages");
        times2.add(testPerformance(webScraper, downloadBlurredImages, 20));
        saveToFile(times2, "laboratory/src/main/java/lab_3/dane_do_wykresu_seq_blurred.txt");
    }
}
