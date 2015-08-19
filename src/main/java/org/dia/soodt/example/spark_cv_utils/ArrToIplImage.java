package org.dia.soodt.example.spark_cv_utils;

import org.dia.soodt.example.spark_cv_utils.ArrToBufferedImage;
import org.dia.soodt.example.spark_cv_utils.ArrToIplImage;

import java.awt.image.BufferedImage;

import org.apache.spark.api.java.function.Function;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 * Converts the integer array representation of the image to type IplImage
 * int[] -> BufferedImage -> Frame -> IplImage
 * @author alorta
 */
public class ArrToIplImage implements Function<int[], IplImage> {
    private static final long serialVersionUID = 1L;
    Integer width = null;
    Integer height = null;
    static Java2DFrameConverter buffConverter = null;
    static OpenCVFrameConverter.ToIplImage converter = null;
    
    /**
     * @param width
     * @param height
     */
    public ArrToIplImage(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        ArrToIplImage.buffConverter = new Java2DFrameConverter();
        ArrToIplImage.converter = new OpenCVFrameConverter.ToIplImage();
    }
    
    /* (non-Javadoc)
     * @see org.apache.spark.api.java.function.Function#call(java.lang.Object)
     */
    public IplImage call(int[] rgbArray) {
        ArrToBufferedImage toBufferedImageCall = new ArrToBufferedImage(this.width, this.height, this.width);
//            ArrToBufferedImage toBufferedImageCall = ArrToBufferedImage(this.width, this.height, this.width);
        BufferedImage bufferedFrame = toBufferedImageCall.call(rgbArray);
        Frame frame = ArrToIplImage.buffConverter.convert(bufferedFrame);
        IplImage iplFrame = ArrToIplImage.converter.convert(frame);
        return iplFrame;
    }
}
