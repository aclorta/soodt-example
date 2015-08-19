package org.dia.soodt.example.spark_cv_utils;

import java.awt.image.BufferedImage;

import org.apache.spark.api.java.function.Function;
import org.bytedeco.javacv.CanvasFrame;

import org.dia.soodt.example.spark_cv_utils.ArrToBufferedImage;

/**
 * Responsible for converting each frame into a BufferedImage, 
 * then displaying the image to screen. 
 * @author alorta
 */
public class DisplayImage implements Function<int[], Void> {
    private static final long serialVersionUID = 1L;
    static CanvasFrame canvasFrame = new CanvasFrame("Video Feed");
    ArrToBufferedImage arrToBufferedImage = null;
    
    /**
     * @param width
     * @param height
     * @param scansize
     */
    public DisplayImage (int width, int height, int scansize) {
        DisplayImage.canvasFrame.setCanvasSize(width, height);
        this.arrToBufferedImage = new ArrToBufferedImage(width, height, width);
    }
    /* (non-Javadoc)
     * @see org.apache.spark.api.java.function.Function#call(java.lang.Object)
     */
    public Void call(int[] rgbArray) {
        BufferedImage frame = this.arrToBufferedImage.call(rgbArray);
        DisplayImage.canvasFrame.showImage(frame);
        return null;
    }
    /**
     * Properly closes the canvas frame upon program termination
     */
    public void close() {
        DisplayImage.canvasFrame.dispose();
    }
}
