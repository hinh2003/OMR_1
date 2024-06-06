package com.example.nckh8;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OMR2 {

    static {
        System.loadLibrary("opencv_java4");
    }

    public static int getX(Rect rect) {
        return rect.x;
    }

    public static int getY(Rect rect) {
        return rect.y;
    }

    public static int getH(Rect rect) {
        return rect.height;
    }

    public static int getXVer1(MatOfPoint contour) {
        Rect boundingRect = Imgproc.boundingRect(contour);
        return boundingRect.x * boundingRect.width;
    }

    public static List<Mat> cropImage(Mat img) {
        Mat imgGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imgGray, imgGray, new org.opencv.core.Size(5, 5), 0);

        // dò canh
        Mat edges = new Mat();
        Imgproc.Canny(imgGray, edges, 40, 150);
        // tìm đường viền
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Mat> ansBlocks = new ArrayList<>();
        int xOld = 0, yOld = 0, wOld = 0, hOld = 0;
        if (contours.size() > 0) {
            contours.sort(Comparator.comparingInt(OMR2::getXVer1));

            for (MatOfPoint contour : contours) {
                // Sử dụng MatOfPoint2f để lưu trữ các điểm 2D trong hình
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                // Tính toán chu vi đường viên
                double epsilon = 0.02 * Imgproc.arcLength(contour2f, true);
                Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);
                Rect rect = Imgproc.boundingRect(contour);
                int xCurr = rect.x;
                int yCurr = rect.y;
                int wCurr = rect.width;
                int hCurr = rect.height;

                if (approxCurve.total() == 4 && wCurr * hCurr >= 166000) {
                    int checkXYMin = xCurr * yCurr - xOld * yOld;
                    int checkXYMax = (xCurr + wCurr) * (yCurr + hCurr) - (xOld + wOld) * (yOld + hOld);

                    if (ansBlocks.isEmpty() || (checkXYMin > 10000 && checkXYMax > 10000)) {
                        Mat croppedImage = new Mat(img, new Rect(xCurr, yCurr, wCurr, hCurr));
                        ansBlocks.add(croppedImage);

                        xOld = xCurr;
                        yOld = yCurr;
                        wOld = wCurr;
                        hOld = hCurr;

                    }
                }

            }

        }
        return ansBlocks;
    }

    public static List<Mat> processAnsBlocks(List<Mat> ansBlocks) {
        List<Mat> listAnswers = new ArrayList<>();

        for (Mat ansBlock : ansBlocks) {
            int offset1 = (int) Math.ceil(ansBlock.rows() / 6);

            for (int i = 0; i < 6; i++) {
                Mat boxImg = new Mat(ansBlock, new Rect(0, i * offset1, ansBlock.cols(), offset1));
                int heightBox = boxImg.rows();


                boxImg = boxImg.submat(5, heightBox , 0, boxImg.cols());
//                boxImg = boxImg.submat(0, heightBox, 5, boxImg.cols()-5);

                int offset2 = (int) Math.ceil(boxImg.rows() / 5);

                for (int j = 0; j < 5; j++) {
                    listAnswers.add(boxImg.submat(j * offset2, (j + 1) * offset2, 0, boxImg.cols()));
                }
            }

        }

        return listAnswers;
    }
    public static Mat cropImageBorders(Mat image, int borderSize) {
        // Lấy kích thước ảnh
        int height = image.rows();
        int width = image.cols();

        // Tạo một hình chữ nhật mới, loại bỏ các viền
        Rect roi = new Rect(borderSize, borderSize, width - 2 * borderSize, height - 2 * borderSize);

        // Trả về phần cắt của ảnh
        return new Mat(image, roi);
    }




    public static List<Mat> processListAns(List<Mat> listAnswers) {
        List<Mat> listChoices = new ArrayList<>();
        int offset = 35;
        int start = 50;

        for (Mat answerImg : listAnswers) {
            for (int i = 0; i < 4; i++) {
                Mat bubbleChoice = new Mat(answerImg,
                        new Rect(start + i * offset, 0, offset, answerImg.rows()));


                // Convert to grayscale
                Imgproc.cvtColor(bubbleChoice, bubbleChoice, Imgproc.COLOR_BGR2GRAY);

                Imgproc.threshold(bubbleChoice, bubbleChoice, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
                Imgproc.resize(bubbleChoice, bubbleChoice, new org.opencv.core.Size(28, 28));
                bubbleChoice = bubbleChoice.reshape(1); // Reshape to a single channel Mat
                Mat croppedAnswer = cropImageBorders(bubbleChoice, 4); // Bớt 4 viền
                listChoices.add(croppedAnswer);
            }

        }


        if (listChoices.size() != 480) {
            throw new IllegalArgumentException("Length of listChoices must be 480");
        }

        return listChoices;
    }




    public static String mapAnswer(int idx) {
        String answerCircle;
        if (idx % 4 == 0) {
            answerCircle = "A";
        } else if (idx % 4 == 1) {
            answerCircle = "B";
        } else if (idx % 4 == 2) {
            answerCircle = "C";
        } else {
            answerCircle = "D";
        }
        return answerCircle;
    }


}

