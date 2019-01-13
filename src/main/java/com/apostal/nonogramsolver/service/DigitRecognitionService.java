package com.apostal.nonogramsolver.service;

import com.apostal.nonogramsolver.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMax;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.opencv.core.Core.*;
import static org.opencv.imgproc.Imgproc.*;

@Slf4j
@Component
@RequiredArgsConstructor
class DigitRecognitionService {
    private final MultiLayerNetwork NETWORK;

    private final Scalar SCALAR_WHITE = new Scalar(255,255,255,0);

    int detectNumber(Mat img){
        img = img.clone();
        List<Mat> dig = detectDigit(img);
        if (dig == null) return -1;
        int result = 0;
        for (Mat mat : dig) {
            result = 10 * result + recogniseDigit(mat);
        }
        return result;
    }

    private List<Mat> detectDigit(Mat img) {
        Mat res;
        List<MatOfPoint> contours = new ArrayList<>();
        List<Rect> rects = new ArrayList<>();
        List<Double> areas = new ArrayList<>();
        bitwise_not(img, img);
        int height = (int)(img.size().height);
        int width = (int)(img.size().width);
        findContours(img, contours, new Mat(), RETR_TREE, CHAIN_APPROX_SIMPLE, new Point(0, 0));
        for (MatOfPoint contour : contours) {
            Rect boundBox = boundingRect(contour);
            if (boundBox.height > 0.3 * height && boundBox.height < 0.8 * height && boundBox.width > 0.2 * width && boundBox.width < 0.8 * width) {
                double aspectRatio = 1.0 * boundBox.height / boundBox.width;
                if (aspectRatio >= 0.5 && aspectRatio < 3) {
                    rects.add(boundBox);
                    double area = contourArea(contour);
                    areas.add(area);
                }
            }
        }
        if(areas.size() > 2) return null;
        if(areas.isEmpty()) return null;
        rects.sort(Comparator.comparingInt(r -> r.x));
        List<Mat> result = new ArrayList<>();
        bitwise_not(img, img);
        for (Rect rect : rects) {
            res = img.submat(rect);
            int w = (int) res.size().width;
            int h = (int) res.size().height;
            int max = Math.max(w, h);
            int leftBorder = (max - w) / 2;
            int upBorder = (max - h) / 2;
            int rigthBorder = max - w - leftBorder;
            int downBorder = max - h - upBorder;
            cvtColor(res, res, COLOR_GRAY2RGB);
            copyMakeBorder(res, res, upBorder, downBorder, leftBorder, rigthBorder, BORDER_CONSTANT, SCALAR_WHITE);
            cvtColor(res, res, COLOR_RGB2GRAY);
            resize(res, res, new Size(20, 20));
            cvtColor(res, res, COLOR_GRAY2RGB);
            copyMakeBorder(res, res, 4, 4, 4, 4, BORDER_CONSTANT, SCALAR_WHITE);
            cvtColor(res, res, COLOR_RGB2GRAY);
            result.add(res);
        }
        return result;
    }

    private int recogniseDigit(Mat digit) {
        int idx = 0;
        try {
            NativeImageLoader loader = new NativeImageLoader(28, 28, 1);
            bitwise_not(digit, digit);
            INDArray dig = loader.asMatrix(ImageUtils.mat2Img(digit));
            INDArray flatten = dig.reshape(new int[]{1, 784});
            flatten = flatten.div(255);
            INDArray output = NETWORK.output(flatten);
            idx = Nd4j.getExecutioner().execAndReturn(new IAMax(output)).getFinalResult();
            digit.release();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return idx;
    }
}
