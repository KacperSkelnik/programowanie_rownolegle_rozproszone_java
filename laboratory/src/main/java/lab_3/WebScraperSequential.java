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


    public static void main(String[] args) throws IOException {
        WebScraperSequential webScraper = new WebScraperSequential("http://www.if.pw.edu.pl/~mrow/dyd/wdprir/",
                "laboratory/src/main/java/lab_3/imgs/");
        webScraper.downloadImages();
    }
}
