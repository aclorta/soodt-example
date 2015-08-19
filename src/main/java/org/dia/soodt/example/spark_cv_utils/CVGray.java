package org.dia.soodt.example.spark_cv_utils;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

import org.apache.spark.api.java.function.Function;
import org.bytedeco.javacpp.opencv_core.IplImage;

import org.dia.soodt.example.spark_cv_utils.ArrToIplImage;
import org.dia.soodt.example.spark_cv_utils.IplImageToArr;

/**
 * Converts the incoming video stream to grayscale via the JavaCV library
 * @author alorta
 */
public class CVGray implements Function<int[], int[]> {
    private static final long serialVersionUID = 1L;
    Integer width = null;
    Integer height = null;
    ArrToIplImage arrToIplImage = null;
    IplImageToArr iplImageToArr = null;
    
    /**
     * @param width
     * @param height
     */
    public CVGray(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        this.arrToIplImage = new ArrToIplImage(this.width, this.height);
        this.iplImageToArr = new IplImageToArr(this.width, this.height);
    }
    
    /* (non-Javadoc)
     * @see org.apache.spark.api.java.function.Function#call(java.lang.Object) 
     */
    public int[] call(int[] rgbArray) {
        IplImage frame = this.arrToIplImage.call(rgbArray);
        IplImage gray = cvCreateImage(cvSize(this.width, this.height), 8, 1);
        cvCvtColor(frame, gray, CV_BGR2GRAY);
        return this.iplImageToArr.call(gray);
    }
}