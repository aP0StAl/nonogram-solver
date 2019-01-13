package com.apostal.nonogramsolver;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class GenerateDataset {

    private final static String DATA[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final static String PARENT_PATH = "data";

    private static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CV_8UC(3));

        int r, g, b;
        UByteRawIndexer indexer = mat.createIndexer();
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                int rgb = bi.getRGB(x, y);

                r = (byte) ((rgb) & 0xFF);
                g = (byte) ((rgb >> 8) & 0xFF);
                b = (byte) ((rgb >> 16) & 0xFF);

                indexer.put(y, x, 0, r);
                indexer.put(y, x, 1, g);
                indexer.put(y, x, 2, b);
            }
        }
        indexer.release();
        return mat;
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private static Mat detectDigit(Mat img) {
        Mat res;
        opencv_core.MatVector countours = new opencv_core.MatVector();
        List<Rect> rects = new ArrayList<>();
        List<Double> araes = new ArrayList<>();
        bitwise_not(img, img);
        findContours(img, countours, opencv_imgproc.CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, new opencv_core.Point(0, 0));
        for (int i = 0; i < countours.size(); i++) {
            Mat c = countours.get(i);
            opencv_core.Rect boundbox = boundingRect(c);
            if (boundbox.height() > 10 && boundbox.height() < 24 && boundbox.width() > 5 && boundbox.width() < 24) {
                double aspectRatio = 1.0 * boundbox.height() / boundbox.width();
                if (aspectRatio >= 0.5 && aspectRatio < 3) {
                    rects.add(boundbox);
                    double area = contourArea(c);
                    araes.add(area);
                }
            }
        }
        if (!araes.isEmpty()) {
            bitwise_not(img, img);

            Double d = Collections.max(araes);
            res = img.apply(rects.get(araes.indexOf(d)));
            int w = res.size().width();
            int h = res.size().height();
            int max = Math.max(w,h);
            int leftBorder = (max - w) / 2;
            int upBorder = (max - h) / 2;
            int rigthBorder = max - w - leftBorder;
            int downBorder = max - h - upBorder;
            //cvtColor(res, res, CV_GRAY2RGB);
            copyMakeBorder(res, res, upBorder, downBorder, leftBorder, rigthBorder, BORDER_CONSTANT, Scalar.BLACK);
            //cvtColor(res, res, CV_RGB2GRAY);
            resize(res, res, new Size(20, 20));
            //cvtColor(res, res, CV_GRAY2RGB);
            copyMakeBorder(res, res, 4, 4, 4, 4, BORDER_CONSTANT, Scalar.BLACK);
            //cvtColor(res, res, CV_RGB2GRAY);
            return res;
        } else {
            return img;
        }
    }


    public static void main(String[] args) throws IOException {
        String[] FONTS = {
                "Arial",
                "Arial Black",
                "Comic Sans MS",
                "Courier New",
                "Georgia",
                "Impact",
                "Lucida Console",
                "Lucida Sans Unicode",
                "Palatino Linotype",
                "Tahoma",
                "Times New Roman",
                "Trebuchet MS",
                "Verdana",
                "MS Sans Serif",
                "MS Serif"
                };
        Font font;
        BufferedImage img;

        BufferedImage[] bgrnds = new BufferedImage[5];
        for (int i = 1; i <= bgrnds.length; i++) {
            bgrnds[i-1] = ImageIO.read(new File("background/" + i +".jpg"));
        }

        for (String data : DATA) {
            String path = PARENT_PATH + File.separator + data;
            new File(path).mkdirs();

            for (String fnt : FONTS) {
                for (int size = 18; size <= 28; size+=2) {
                    for(int zoom = 1; zoom <= 10; zoom++) {
                        for(int rotate = -10; rotate <=10; rotate+=2) {
                            font = new Font(fnt, Font.PLAIN, size);

                            FontRenderContext frc = new FontRenderContext(null, true, true);

                            //get the height and width of the text
                            Rectangle2D bounds = font.getStringBounds(data, frc);
                            double w = bounds.getWidth();
                            double h = bounds.getHeight();

                            //img = new BufferedImage(2 * max, 2 * max, BufferedImage.TYPE_BYTE_GRAY);
                            BufferedImage background = deepCopy(bgrnds[(int)(Math.random()*5)]);
                            background = background.getSubimage((int)(Math.random() * (background.getWidth() - 3 * w)), (int)(Math.random() * (background.getHeight() - 2 * h)), (int)(3*w), (int)(2*h));
                            Graphics2D g2d = background.createGraphics();
                            g2d.setFont(font);

                            g2d.setColor(Color.BLACK);
                            g2d.rotate(Math.toRadians(rotate));
                            g2d.drawString(data, (float)w, (float)h);
                            g2d.dispose();

                            img = background;

                            Mat mat = bufferedImageToMat(img);
                            Mat sourceGrey = new Mat(mat.size(), CV_8UC1);
                            cvtColor(mat, sourceGrey, COLOR_BGR2GRAY);

                            /*Apply Gaussian Filter*/
                            Mat blurimg = new Mat(mat.size(), CV_8UC1);
                            GaussianBlur(sourceGrey, blurimg, new opencv_core.Size(5, 5), 0);
                            /*Binarising Image*/
                            Mat binimg = new Mat(mat.size());
                            adaptiveThreshold(blurimg, binimg, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 25, 13);

                            Mat procimg = detectDigit(binimg);


                            if(procimg.size().width() == 28 && procimg.size().height() == 28)
                                imwrite(path + File.separator
                                        + fnt + "_" + data + "_" + size + "_" + zoom + "_" + rotate + ".jpg", procimg);

                        }
                    }
                }
            }
        }
        for (BufferedImage bgrnd : bgrnds) {
            try {
                ImageIO.write(bgrnd, "png", new File("test" + (int)(Math.random() * 1000) +".png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }


}
