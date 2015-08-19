package org.dia.soodt.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacpp.opencv_core.Buffer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class AdamImage implements Serializable {
    private static final long serialVersionUID = 1L;
    transient Frame frame;
    transient java.nio.Buffer[] image;
    transient java.nio.Buffer[] samples;
    
    
    //Frame properties expanded
    private int audioChannels;
//    private java.nio.Buffer[] image;
    private int imageChannels;
    private int imageDepth;
    private int imageHeight;
    private int imageStride;
    private int imageWidth;
    private boolean keyFrame;
    private Object opaque;
    private int sampleRate;
//    private java.nio.Buffer[] samples;
    
    //java.nio.Buffer image properties expanded
    private int length;
    
    //java.nio.Buffer samples properties expanded
    
    
    private void expandImageBuffer() {
        this.image.length = image.length;
    }
    
    private void collapseImageBuffer() {
        java.nio.Buffer image;
        image.length = 
    }
    
    private void expandFrame() {
        this.audioChannels = frame.audioChannels;
        this.image = frame.image;
        this.imageChannels = frame.imageChannels;
        this.imageDepth = frame.imageDepth;
        this.imageHeight = frame.imageHeight;
        this.imageStride = frame.imageStride;
        this.imageWidth = frame.imageWidth;
        this.keyFrame = frame.keyFrame;
        this.opaque = frame.opaque;
        this.sampleRate = frame.sampleRate;
        this.samples = frame.samples;
    }
    
    private void collapseFrame() {
        Frame frame = new Frame();
        frame.audioChannels = this.audioChannels;
        frame.image = this.image;
        frame.imageChannels = this.imageChannels;
        frame.imageDepth = this.imageDepth;
        frame.imageHeight = this.imageHeight;
        frame.imageStride = this.imageStride;
        frame.imageWidth = this.imageWidth;
        frame.keyFrame = this.keyFrame;
        frame.opaque = this.opaque;
        frame.sampleRate = this.sampleRate;
        frame.samples = this.samples;
        
        this.frame = frame;
        
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.collapseFrame();
    }
    private void writeObject(ObjectOutputStream out) throws IOException {
        this.expandFrame();
        out.defaultWriteObject();
    }
}
