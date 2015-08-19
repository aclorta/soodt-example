package org.dia.soodt.example.spark_cv_utils;

import java.awt.image.BufferedImage;

import org.apache.spark.api.java.function.Function;

/**
 * Converts the integer array representation of the image to type BufferedImage
 * @author alorta
 */
public class ArrToBufferedImage implements Function<int[], BufferedImage> {
    private static final long serialVersionUID = 1L;
    Integer width = null;
    Integer height = null;
    Integer scansize = null;
    
    /**
     * @param width
     * @param height
     * @param scansize
     */
    public ArrToBufferedImage(Integer width, Integer height, Integer scansize) {
        this.width = width;
        this.height = height;
        this.scansize = scansize;
    }
    
    /* (non-Javadoc)
     * @see org.apache.spark.api.java.function.Function#call(java.lang.Object)
     */
    public BufferedImage call(int[] rgbArray) {
        BufferedImage result = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, this.width, this.height, rgbArray, 0, this.scansize);
        return result;
    }
}
