package lab_3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class GaussianBlur {
    BufferedImage im;
    String fileName;
    int size;
    double sigma;
    double[][] filter;
    BufferedImage output;
    double sum = 0d;

    public GaussianBlur(BufferedImage image, int kernelSize) {
        size = kernelSize;
        sigma = (kernelSize-1)/6;
        setKernelVals();
        im = image;

        output = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public GaussianBlur(String imageFile, int kernelSize) throws IOException {
        fileName = imageFile;
        size = kernelSize;
        sigma = (kernelSize-1)/6;
        setKernelVals();
        getBufferedImage();

        output = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public void setKernelVals(){
        filter = new double[size][size];
        double[] pascal = new double[size];
        for(int line = 1; line <= size; line++){
            int C=1;
            for(int i = 1; i <= line; i++) {
                pascal[i-1] = C;
                C = C * (line - i) / i;
            }
        }

        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                filter[i][j] = pascal[i]*pascal[j];
                sum += pascal[i]*pascal[j];
            }
        }
    }


    public void getBufferedImage() throws IOException {
        im = ImageIO.read(new File(fileName));
    }

    public void blur(int level){
        for(int i=(size-1)/2; i<im.getWidth()-(size-1)/2; i++){
            int blue = 0;
            int green = 0;
            int red = 0;
            for(int x_filter=0; x_filter<size; x_filter++){
                for(int y_filter=0; y_filter<size; y_filter++){
                    int color = im.getRGB(i+x_filter-(size-1)/2,level+y_filter-(size-1)/2);
                    blue += (color & 0xFF) * filter[x_filter][y_filter];
                    green += ((color >>> 8) & 0xFF) * filter[x_filter][y_filter];
                    red += ((color >>> 16) & 0xFF) * filter[x_filter][y_filter];
                }
            }
            blue /= sum;
            green /= sum;
            red /= sum;
            output.setRGB(i, level, (red << 16) | (green << 8) | blue | 0xFF000000);
        }
    }


    public void makeBlurSequential(String outputfile) throws IOException {
        for(int i=(size-1)/2; i<im.getHeight()-(size-1)/2; i++){
            blur(i);
        }

        File fileToSave = new File(outputfile);
        ImageIO.write(output, "png", fileToSave);
    }

    public static void main(String[] args) throws IOException {
        GaussianBlur gb = new GaussianBlur("laboratory/src/main/java/lab_3/imgs/img0.png",3);
        gb.makeBlurSequential("laboratory/src/main/java/lab_3/imgs/img0_blured.png");
    }


}
