package org.dia.soodt.example.spark_cv_utils;

import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import org.apache.spark.api.java.function.Function;
import org.bytedeco.javacpp.opencv_core.IplImage;

import org.dia.soodt.example.spark_cv_utils.ArrToIplImage;
import org.dia.soodt.example.spark_cv_utils.IplImageToArr;

/**
 * Transforms (Gaussian blur) the incoming video stream via the JavaCV library
 * @author alorta
 */
public class CVSmooth implements Function<int[], int[]> {
    private static final long serialVersionUID = 1L;
    Integer width = null;
    Integer height = null;
    ArrToIplImage arrToIplImage = null;
    IplImageToArr iplImageToArr = null;
    
    /**
     * @param width
     * @param height
     */
    public CVSmooth(int width, int height) {
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
        cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
        return this.iplImageToArr.call(frame);
    }
}
