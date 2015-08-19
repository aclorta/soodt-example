package org.dia.soodt.example;

// JavaCV Imports
import org.bytedeco.javacv.*;

// Apache Spark Imports
import org.apache.spark.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.oodt.cas.resource.structs.StreamingInstance;
import org.apache.oodt.cas.resource.structs.JobInput;
import org.apache.oodt.cas.resource.structs.exceptions.JobInputException;

// Helper classes
import org.dia.soodt.example.spark_cv_utils.CVGray;
import org.dia.soodt.example.spark_cv_utils.DisplayImage;
import org.dia.soodt.example.spark_cv_utils.CVSmooth;

/**
 * An example class for use with the Spark backend to the resource manager.
 * 
 * @author aclorta
 */
public class SimpleSparkCVApp implements StreamingInstance {
    SparkContext sc = null;
    JavaStreamingContext jssc = null; // Java-friendly version of Spark StreamingContext

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SparkConf conf = new SparkConf().setAppName("SimpleSparkCV").setMaster("local[*]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.milliseconds(500));
        
        SimpleSparkCVApp app = new SimpleSparkCVApp();
        app.setStreamingContext(jssc.ssc());
        app.execute(null);
        jssc.close();
    }
    /**
     * Set the Spark Context.
     */
    public void setSparkContext(SparkContext context) {
    }
    /**
     * Set Java Streaming Context for this job.
     */
    public void setStreamingContext(StreamingContext ssc) {
        this.jssc = new JavaStreamingContext(ssc);
    }
    /**
     * Execute this job.
     */
    public boolean execute(JobInput jobInput) throws JobInputException {
        int capture_device = 0; // Device ID of camera
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(capture_device);
        
        try {
            // Grab video settings needed for subsequent functions
            grabber.start();
            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            grabber.stop();
            
            // Image manipulation functions which are mapped onto
            // the incoming data stream of integer arrays (frames)
            CVGray cvGray = new CVGray(width, height);
            CVSmooth cvSmooth = new CVSmooth(width, height);
            DisplayImage displayImage = new DisplayImage(width, height, width);
            
            // Spark Streaming object which receives an integer array, representing frames, from
            // a custom Spark Streaming Receiver
            JavaDStream<int[]> customReceiverStream = this.jssc.receiverStream(new JavaCustomReceiver(capture_device));
            
            // Prepare image for motion detection
            // Grayscale (cvGray) -> Gaussian blur (cvSmooth) -> Image diff (previous frame vs current)
            // Note: The Image diff has not yet been implemented. We need to send two frames from the 
            // Receiver to our driver program
            JavaDStream<int[]> grayImage = customReceiverStream.map(cvGray);
            // cvSmooth causes a drastic slow-down in image processing. Commented out for testing
            // JavaDStream<int[]> smoothGrayImage = grayImage.map(cvSmooth);
            JavaDStream<Void> grayImageDisplay = grayImage.map(displayImage);
            
            // Output operation forcing the evaluation of our RDDs
            grayImageDisplay.print();
            // Start, wait for execution to stop, then close streams
            this.jssc.start();
            this.jssc.awaitTermination();
            this.jssc.close();
            // Closes CanvasFrame
            displayImage.close();
        } catch(Exception e) { System.out.println("Exception caught: " + e); }
        return true;
    }
}