package org.dia.soodt.example;

// JavaCV Imports
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

// Apache Spark Imports
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

// Java Imports
import java.awt.image.*;

/**
 * <p>
 * Custom Apache Spark Receiver which uses JavaCV to capture webcam
 * Frames, converts them to integer arrays (serializable objects), and
 * sends them over to Spark Streaming via the JavaDStream object.
 * </p>
 * @author alorta
 */
public class JavaCustomReceiver extends Receiver<int[]> {
    private static final long serialVersionUID = 1L;
    int source = 0;
    BufferedImage frame = null;
    static OpenCVFrameGrabber grabber = null;
    static Java2DFrameConverter converter = null;
    static int width = 0;
    static int height = 0;

    /**
     * @param source
     *          The webcam's device ID. Defaults to 0.
     */
    public JavaCustomReceiver(int source) {
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.source = source;
        JavaCustomReceiver.grabber = new OpenCVFrameGrabber(this.source);
        JavaCustomReceiver.converter = new Java2DFrameConverter();
    }
    /* (non-Javadoc)
     * @see org.apache.spark.streaming.receiver.Receiver#onStart()
     */
    public void onStart() {
        try {
            JavaCustomReceiver.grabber.start();
            JavaCustomReceiver.width = JavaCustomReceiver.grabber.getImageWidth();
            JavaCustomReceiver.height = JavaCustomReceiver.grabber.getImageHeight();
        } catch(Exception e) { System.out.println("Exception caught: " + e); }
        
        // Start the thread that receives data 
        new Thread()  {
            @Override 
            public void run() {
                receive();
            }
        }.start();
    }
    /* (non-Javadoc)
     * @see org.apache.spark.streaming.receiver.Receiver#onStop()
     */
    public void onStop() {
    }
    /**
     * Captures a BufferedImage (frame) from the indicated source, converts it to an Integer array,
     * (rgbArray - serializable object) and stores it in Spark Streaming's memory
     */
    private void receive() {
        try {
            while ((this.frame = JavaCustomReceiver.converter.convert(JavaCustomReceiver.grabber.grab())) != null) {
                int[] rgbArray = new int[JavaCustomReceiver.width * JavaCustomReceiver.height];
                rgbArray = this.frame.getRGB(0, 0, JavaCustomReceiver.width, JavaCustomReceiver.height, rgbArray, 0, JavaCustomReceiver.width);
                store(rgbArray);
            }
            JavaCustomReceiver.grabber.stop();
        } catch(Exception e) { System.out.println("Exception caught: " + e); }
    }
}
