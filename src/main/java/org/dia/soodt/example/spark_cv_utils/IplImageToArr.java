package org.dia.soodt.example.spark_cv_utils;

import java.awt.image.BufferedImage;

import org.apache.spark.api.java.function.Function;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 * Converts the IplImage representation of the image to type integer array
 * @author alorta
 */
public class IplImageToArr implements Function<IplImage, int[]> {
    private static final long serialVersionUID = 1L;
    Integer width = null;
    Integer height = null;
    static Java2DFrameConverter buffConverter = null;
    static OpenCVFrameConverter.ToIplImage converter = null;
    
    /**
     * @param width
     * @param height
     */
    public IplImageToArr(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        IplImageToArr.buffConverter = new Java2DFrameConverter();
        IplImageToArr.converter = new OpenCVFrameConverter.ToIplImage();
    }
    
    /* (non-Javadoc)
     * @see org.apache.spark.api.java.function.Function#call(java.lang.Object)
     */
    public int[] call(IplImage iplImage) {
        Frame frame = IplImageToArr.converter.convert(iplImage);
        BufferedImage buffImage = IplImageToArr.buffConverter.convert(frame);
        int[] rgbArray = new int[this.width * this.height];
        return buffImage.getRGB(0, 0, this.width, this.height, rgbArray, 0, this.width);
    }
}
