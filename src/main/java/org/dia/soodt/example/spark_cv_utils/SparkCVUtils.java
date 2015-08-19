package org.dia.soodt.example.spark_cv_utils;

// JavaCV imports
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

// Java imports
import java.awt.Color;
import java.awt.image.BufferedImage;

// Apache Spark imports
import org.apache.spark.api.java.function.Function;
    
/**
 * Converts the integer array representation of the image to type BufferedImage
 * @author alorta
 */
class ArrToBufferedImage implements Function<int[], BufferedImage> {
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

/**
 * Converts the integer array representation of the image to type IplImage
 * int[] -> BufferedImage -> Frame -> IplImage
 * @author alorta
 */
class ArrToIplImage implements Function<int[], IplImage> {
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

/**
 * Converts the IplImage representation of the image to type integer array
 * @author alorta
 */
class IplImageToArr implements Function<IplImage, int[]> {
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

/**
 * Converts the incoming video stream to grayscale via bit shifts.
 * @author alorta
 */
class ArrToGrayArr implements Function<int[], int[]> {
    private static final long serialVersionUID = 1L;
    public int[] call(int[] rgbArray) {
        int[] newRGBArray = new int[rgbArray.length];
        for (int i = 0; i < rgbArray.length; i++) {
            int red = new Color(rgbArray[i]).getRed();
            int green = new Color(rgbArray[i]).getGreen();
            int blue = new Color(rgbArray[i]).getBlue();
            int alpha = new Color(rgbArray[i]).getAlpha();
            int newRGB = (((red + green + blue) / 3));
            newRGBArray[i] = ((((((alpha) << 8) | newRGB) << 8) | newRGB) << 8) | newRGB;
        }
        return newRGBArray;
    }
}

/**
 * Converts the incoming video stream to grayscale via the JavaCV library
 * @author alorta
 */
class CVGray implements Function<int[], int[]> {
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

/**
 * Transforms (Gaussian blur) the incoming video stream via the JavaCV library
 * @author alorta
 */
class CVSmooth implements Function<int[], int[]> {
    private static final long serialVersionUID = 1L;
    Integer width = null;
    Integer height = null;
    ArrToIplImage arrToIplImageCall = null;
    IplImageToArr iplImageToArrCall = null;
    
    /**
     * @param width
     * @param height
     */
    public CVSmooth(int width, int height) {
        this.width = width;
        this.height = height;
        this.arrToIplImageCall = new ArrToIplImage(this.width, this.height);
        this.iplImageToArrCall = new IplImageToArr(this.width, this.height);
    }
    
    /* (non-Javadoc)
     * @see org.apache.spark.api.java.function.Function#call(java.lang.Object)
     */
    public int[] call(int[] rgbArray) {
        IplImage frame = this.arrToIplImageCall.call(rgbArray);
        cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
        return this.iplImageToArrCall.call(frame);
    }
}
/**
 * Responsible for converting each frame into a BufferedImage, 
 * then displaying the image to screen. 
 * @author alorta
 */
class DisplayImage implements Function<int[], Void> {
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