package com.apostal.nonogramsolver.util;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;

public class ImageUtils {
    public static BufferedImage mat2Img(Mat in) {
        BufferedImage out;
        int rows = in.rows();
        int cols = in.cols();
        byte[] data = new byte[cols * rows * (int)in.elemSize()];
        int type;
        in.get(0, 0, data);

        if(in.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;

        out = new BufferedImage(cols, rows, type);

        out.getRaster().setDataElements(0, 0, cols, rows, data);
        return out;
    }

    public static Mat img2Mat(BufferedImage in)
    {
        Mat out;
        byte[] data;
        int r, g, b;

        int rows = in.getHeight();
        int cols = in.getWidth();

        if(in.getType() == BufferedImage.TYPE_INT_RGB)
        {
            out = new Mat(rows, cols, CV_8UC3);
            data = new byte[cols * rows * (int)out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, cols, rows, null, 0, cols);
            for(int i = 0; i < dataBuff.length; i++)
            {
                data[i*3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i*3 + 2] = (byte) ((dataBuff[i]) & 0xFF);
            }
        }
        else
        {
            out = new Mat(rows, cols, CV_8UC1);
            data = new byte[cols * rows * (int)out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, cols, rows, null, 0, cols);
            for(int i = 0; i < dataBuff.length; i++)
            {
                r = (byte) ((dataBuff[i] >> 16) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i]) & 0xFF);
                data[i] = (byte)((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
            }
        }
        out.put(0, 0, data);
        return out;
    }


}
