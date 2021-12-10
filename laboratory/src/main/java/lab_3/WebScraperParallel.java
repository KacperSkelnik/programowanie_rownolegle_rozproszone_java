package lab_3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class WebScraperParallel {
    String urlToScrap;
    String saveTarget;
    Document doc;
    Elements pngs;


    public WebScraperParallel(String url, String target) throws IOException {
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
}
