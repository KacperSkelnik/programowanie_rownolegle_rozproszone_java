package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PaintUtils {

    public static void paint(int width, int height, Color[][] color, String fileName) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                image.setRGB(col, row, color[col][row].getRGB());
            }
        }

        ImageIO.write(image, "png", new File(fileName));
    }

    public static Color toColor(double minimum, double maximum, double value){
        double H = (value - minimum)/(maximum - minimum) * 0.4; // from red to green
        double S = 0.9; // Saturation
        double B = 0.9; // Brightness
        return Color.getHSBColor((float)H, (float)S, (float)B);
    }
}
