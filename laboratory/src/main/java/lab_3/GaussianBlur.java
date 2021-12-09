package lab_3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.exp;
import static java.lang.Math.max;


public class GaussianBlur {
    BufferedImage im;
    String fileName;
    int size;
    double sigma;
    double[][] filter;
    BufferedImage output;

    public GaussianBlur(String imageFile, int kernelSize) throws IOException {
        fileName = imageFile;
        size = kernelSize;
        sigma = max(kernelSize/4, 1);
        setKernelVals();
        getBufferedImage();

        output = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public double gaussFun(int x, int y){
        double eExpression = exp(-(x * x + y * y)/(2 * sigma * sigma));
        return (eExpression / (2 * Math.PI * sigma * sigma));
    }

    public void setKernelVals(){
        filter = new double[size][size];
        double sum = 0d;
        int radius = (size-1)/2;
        for (int i=0; i < size; i++){
            for (int j=0; j < size; j++){
                double gauss = gaussFun(i-radius,j-radius);
                filter[i][j] = gauss;
                sum += gauss;
            }
        }

        for (int i=0; i < size; i++){
            for (int j=0; j < size; j++){
                filter[i][j] = filter[i][j]/sum;
            }
        }
    }

    public void getBufferedImage() throws IOException {
        im = ImageIO.read(new File(fileName));
    }

    public void blur(int level){
        int blue = 0;
        int green = 0;
        int red = 0;

        int x_filter = 0;
        for(int i=0; i<im.getWidth()-size; i++){
            int y_filter = 0;
            for(int j=level; j<level+size; j++){
                int color = im.getRGB(i,j);
                double factor = filter[x_filter][y_filter];
                blue += (color & 0xFF) * factor;
                green += ((color >>> 8) & 0xFF) * factor;
                red += ((color >>> 16) & 0xFF) * factor;

                y_filter++;
            }
            x_filter++;
            if(x_filter==size){
                x_filter=0;

                for(int k=i-size+1; k<i+1; k++){
                    for(int l=level; l<level+size; l++){
                        output.setRGB(k, l, (red << 16) | (green << 8) | blue | 0xFF000000);
                    }
                }

                blue = 0;
                green = 0;
                red = 0;
            }
        }
    }


    public void makeBlurSequential(String outputfile) throws IOException {
        for(int i=0; i<im.getHeight()-size; i+=size){
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
