package com.apostal.nonogramsolver.service;

import com.apostal.nonogramsolver.util.Const;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.apostal.nonogramsolver.util.ImageUtils.img2Mat;
import static com.apostal.nonogramsolver.util.ImageUtils.mat2Img;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.opencv.core.Core.FONT_HERSHEY_PLAIN;
import static org.opencv.core.Core.bitwise_not;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageProcessingService {
    private final DigitRecognitionService digitRecognitionService;

    private List<Mat> procImages;
    private int currentImageIndex = 0;
    private int[][] resultArray;

    public void setImage(BufferedImage image){
        BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        convertedImg.getGraphics().drawImage(image, 0, 0, null);
        Mat mat = img2Mat(convertedImg);
        setImage(mat);
    }

    private void setImage(Mat srcImage){
        long startTime = System.currentTimeMillis();
        procImages = new ArrayList<>();
        procImages.add(srcImage);
        currentImageIndex = 0;
        Mat sourceGrey = coloredToGray(srcImage);
        procImages.add(sourceGrey);
        Mat blur = grayToBlur(sourceGrey);
        procImages.add(blur);
        Mat binImg = blurToBinarise(blur);
        procImages.add(binImg);
        Mat rect = warpPerspectivePuzzle(binImg);
        procImages.add(rect);
        List<Line> lines = findLines(rect);
        Mat withLines = putLinesToMat(rect, lines);
        procImages.add(withLines);
        Point[][] points = getPoint(lines);
        Mat withPoints = putPointsToMat(withLines, points);
        procImages.add(withPoints);
        Mat withDigits = getMatrixOfDigits(rect, points);
        procImages.add(withDigits);
        log.info(Const.Pattern.LOG, Const.Messages.IMAGE_PROCESSING_DONE_INFO, (System.currentTimeMillis() - startTime) + " ms.");
    }

    private Mat getMatrixOfDigits(Mat src, Point[][] points){
        Mat res = src.clone();
        bitwise_not(res, res);
        bitwise_not(src, src);
        cvtColor(res, res, COLOR_GRAY2RGB);
        int[][] result = new int[points.length-1][points[0].length-1];
        for(int i=0;i<points.length-1;i++){
            for(int j=0;j<points[i].length-1;j++){
                Point corner1 = points[i][j];
                Point corner2 = points[i+1][j+1];
                Mat rect = src.submat(new Rect(corner1,corner2));
                result[i][j] = digitRecognitionService.detectNumber(rect);
                if(result[i][j] > -1){
                    putText(res, result[i][j] + "", new Point(corner1.x+2, corner1.y+13),
                            FONT_HERSHEY_PLAIN, 1.1, new Scalar(200, 25, 25, 0), 2, 2, false);
                }
            }
        }
        resultArray = result;
        return res;
    }

    private Mat putPointsToMat(Mat src, Point[][] points){
        Mat dest = src.clone();
        for (Point[] point : points) {
            for (Point point1 : point) {
                circle(dest, point1, 10, new Scalar(0, 0, 0, 255), -1, 8, 0);
            }
        }
        return dest;
    }

    public BufferedImage next(){
        currentImageIndex++;
        currentImageIndex = currentImageIndex % procImages.size();
        return mat2Img(procImages.get(currentImageIndex));
    }

    public BufferedImage prev(){
        currentImageIndex--;
        currentImageIndex = (currentImageIndex + procImages.size()) % procImages.size();
        return mat2Img(procImages.get(currentImageIndex));
    }

    private Mat coloredToGray(Mat src){
        Mat sourceGrey = new Mat(src.size(), CV_8UC1);
        cvtColor(src, sourceGrey, COLOR_BGR2GRAY);
        return sourceGrey;
    }

    private Mat grayToBlur(Mat src){
        Mat blurimg = new Mat(src.size(), CV_8UC1);
        GaussianBlur(src, blurimg, new Size(5, 5), 0);
        return blurimg;
    }

    private Mat blurToBinarise(Mat src){
        Mat binimg = new Mat(src.size(), CV_8UC1);
        adaptiveThreshold(src, binimg, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 25, 13);
        return binimg;
    }

    private Mat putLinesToMat(Mat src, List<Line> lines){
        Mat dest = src.clone();
        bitwise_not(src, dest);
        cvtColor(dest, dest, COLOR_GRAY2RGB);
        lines.forEach(l -> {
            double cosTheta = Math.cos(l.theta);
            double sinTheta = Math.sin(l.theta);
            double x0 = cosTheta * l.rho;
            double y0 = sinTheta * l.rho;
            Point pt1 = new Point(x0 + 10000 * (-sinTheta), y0 + 10000 * cosTheta);
            Point pt2 = new Point(x0 - 10000 * (-sinTheta), y0 - 10000 * cosTheta);
            Imgproc.line(dest, pt1, pt2, new Scalar(0, 255, 0), 3);
        });
        return dest;
    }

    private List<Line> findLines(Mat src){
        Mat dest = src.clone();
        bitwise_not(src, dest);
        Mat canimg = dest.clone();
        Canny(dest, canimg, 30, 90);
        Mat lines = new Mat();
        HoughLines(canimg, lines, 1, Math.PI / 2, 150);

        List<Line> vLines = new ArrayList<>();
        List<Line> hLines = new ArrayList<>();

        for (int i = 0; i < lines.rows(); i++) {
            double data[] = lines.get(i, 0);
            double rho1 = data[0];
            double theta1 = data[1];
            double cosTheta = Math.cos(theta1);
            double sinTheta = Math.sin(theta1);
            if(cosTheta > 0.99999) {
                vLines.add(new Line(rho1, theta1));
            } else if (sinTheta > 0.99999) {
                hLines.add(new Line(rho1, theta1));
            }
        }
        vLines = getFilteredLines(vLines);
        hLines = getFilteredLines(hLines);
        vLines.addAll(hLines);
        return vLines;
    }

    private List<Line> getFilteredLines(List<Line> lines){
        lines.sort(Comparator.comparingDouble(Line::getRho).reversed());
        List<Double> differences = new ArrayList<>();
        for(int i=1;i<lines.size();i++)
            differences.add(lines.get(i-1).rho-lines.get(i).rho);
        differences.sort(Comparator.comparingDouble(Double::doubleValue).reversed());
        double prev = differences.get(0);
        double count = 0;
        for (Double diff : differences) {
            if (diff / prev > 0.93) {
                count++;
            } else {
                count = 1;
            }
            prev = diff;
            if (count >= 7) break;
        }
        double actualSize = prev;
        prev = lines.get(0).rho;
        count = 0;
        for (Line l : lines) {
            double diff = prev - l.rho;
            if(0.75 * actualSize > diff) continue;
            if(diff < 1.5 * actualSize) {
                count++;
            } else {
                count += 2;
            }
            prev = l.rho;
            if (count >= 10) break;
        }
        double realSize = (lines.get(0).rho - prev) / count;
        int realCount = (int)Math.round((lines.get(0).rho - lines.get(lines.size()-1).rho) / realSize);
        realSize = (lines.get(0).rho - lines.get(lines.size()-1).rho - 2) / realCount;
        List<Line> result = new ArrayList<>();
        result.add(lines.get(lines.size()-1));
        result.get(0).setRho(result.get(0).getRho()+1);
        for(int i=0;i<realCount;i++){
            double rho = result.get(0).rho+realSize*(i+1);
            double theta = result.get(0).theta;
            result.add(new Line(rho, theta));
        }
        return result;
    }

    public int[][] getData() {
        return resultArray;
    }

    @Data
    private class Line{
        double rho;
        double theta;

        Line(double rho, double theta) {
            this.rho = rho;
            this.theta = theta;
        }
    }

    private Mat warpPerspectivePuzzle(Mat image) {
        MatOfPoint rect = getLargestRect(image);
        if(rect == null) throw new NullPointerException();
        MatOfPoint2f largestRect = new MatOfPoint2f(rect.toArray());
        MatOfPoint2f approx = new MatOfPoint2f();
        approxPolyDP(largestRect, approx, 5, true);

        //calculate the center of mass of our contour image using moments
        Moments moment = Imgproc.moments(approx);
        int x = (int) (moment.get_m10() / moment.get_m00());
        int y = (int) (moment.get_m01() / moment.get_m00());

        //SORT POINTS RELATIVE TO CENTER OF MASS
        Point[] approxPoints = approx.toArray();
        Point[] sortedPoints = new Point[4];

        Point data;
        int count = 0;
        for (Point approxPoint : approxPoints) {
            data = approxPoint;
            double dataX = data.x;
            double dataY = data.y;
            if (dataX < x && dataY < y) {
                sortedPoints[0] = new Point(dataX, dataY);
                count++;
            } else if (dataX > x && dataY < y) {
                sortedPoints[1] = new Point(dataX, dataY);
                count++;
            } else if (dataX < x && dataY > y) {
                sortedPoints[2] = new Point(dataX, dataY);
                count++;
            } else if (dataX > x && dataY > y) {
                sortedPoints[3] = new Point(dataX, dataY);
                count++;
            }
        }
        if(count != 4) {
            throw new UnsupportedOperationException("Не удалось найти прямоугольник с кроссвордом");
        }

        float width = (float)getDistance(sortedPoints[0].x, sortedPoints[0].y, sortedPoints[1].x, sortedPoints[1].y);
        float height = (float)getDistance(sortedPoints[0].x, sortedPoints[0].y, sortedPoints[2].x, sortedPoints[2].y);

        double max = Math.max(width, height);
        double resize = Math.max(max / 950, 1);

        width /= resize;
        height /= resize;


        MatOfPoint2f src = new MatOfPoint2f(
                sortedPoints[0],
                sortedPoints[1],
                sortedPoints[2],
                sortedPoints[3]);

        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0, 0),
                new Point(width-1,0),
                new Point(0,height-1),
                new Point(width-1,height-1)
        );

        Mat warpMat = Imgproc.getPerspectiveTransform(src,dst);
        Mat destImage = new Mat(new Size((int)width, (int)height), image.type());
        Imgproc.warpPerspective(image, destImage, warpMat, new Size((int)width, (int)height));
        return destImage;
    }

    private static double getDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    private static MatOfPoint getLargestRect(Mat img) {
        List<MatOfPoint> contours = new ArrayList<>();
        List<Double> areas = new ArrayList<>();
        List<MatOfPoint> cntrs = new ArrayList<>();
        findContours(img, contours, new Mat(), 3, 2, new Point(0, 0));
        for (MatOfPoint c : contours) {
            double area = contourArea(c);
            areas.add(area);
            cntrs.add(c);
        }
        if (areas.isEmpty() || Collections.max(areas) < 4000) {
            return null;
        } else {
            Double d = Collections.max(areas);
            return cntrs.get(areas.indexOf(d));
        }
    }

    private static Point[][] getPoint(List<Line> lines){
        List<Line> vLines = new ArrayList<>();
        List<Line> hLines = new ArrayList<>();
        lines.sort(Comparator.comparingDouble(Line::getRho));
        lines.forEach(l -> {
            if(cos(l.theta) > 0.99999) {
                vLines.add(l);
            } else if (sin(l.theta) > 0.99999) {
                hLines.add(l);
            }
        });
        Point[][] points = new Point[vLines.size()][hLines.size()];
        for (int i = 0; i < hLines.size(); i++) {
            Line line = hLines.get(i);
            double r1 = line.rho;
            double t1 = line.theta;
            for (int j = 0; j < vLines.size(); j++) {
                Line line1 = vLines.get(j);
                double r2 = line1.rho;
                double t2 = line1.theta;
                Point o = parametricIntersect(r1, t1, r2, t2);
                if (o.y != -1 & o.x != -1) {
                    points[j][i] = o;
                }
            }
        }
        return points;
    }

    private static Point parametricIntersect(Double r1, Double t1, Double r2, Double t2) {
        double ct1 = cos(t1);     //matrix element a
        double st1 = sin(t1);     //b
        double ct2 = cos(t2);     //c
        double st2 = sin(t2);     //d
        double d = ct1 * st2 - st1 * ct2;//determinative (rearranged matrix for inverse)
        if (d != 0.0f) {
            int x = (int) ((st2 * r1 - st1 * r2) / d);
            int y = (int) ((-ct2 * r1 + ct1 * r2) / d);
            return new Point(x, y);
        } else { //lines are parallel and will NEVER intersect!
            return new Point(-1, -1);
        }
    }


}
