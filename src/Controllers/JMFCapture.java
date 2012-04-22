/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

/**
 *
 * @author FREAK
 */

// JMFCapture.java
// Andrew Davison, May 2005, ad@fivedots.coe.psu.ac.th

/* The specified capture device is assigned a JMF player.

   For the assignment to work, the device should already be
   registered with JMF via its JMF Registry application.

   The user takes a snap by calling grabImage(), which
   returns the image as a BufferedImage object with dimensions 
   size * size. 
*/


import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.util.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;




public class JMFCapture implements ControllerListener
{
  // I obtained this information from JMF Registry, under its capture devices tab
  private static final String CAP_DEVICE = "vfw:Microsoft WDM Image Capture (Win32):0";
       // common name in WinXP
  // private static final String CAP_DEVICE = "vfw:Microsoft WDM Image Capture:0";
       // common name in Win98
  // private static final String CAP_DEVICE = "vfw:Logitech USB Video Camera:0";

  private static final String CAP_LOCATOR = "vfw://0";


  // used while waiting for the BufferToImage object to be initialized
  private static final int MAX_TRIES = 7;
  private static final int TRY_PERIOD = 5000;   // ms

  private int size;       // x/y- dimensions of final BufferedImage
  private double scaleFactor;  // snap --> final image scaling

  private Player p;
  private FrameGrabbingControl fg;
  private BufferToImage bufferToImage = null;
  private boolean closedDevice;

  //private static houghDemo hough;

  // used for waiting until the player has started
  private Object waitSync = new Object();
  private boolean stateTransitionOK = true;


  public JMFCapture()
  {
    size = 400;
    closedDevice = true;   // since device is not available yet

    bufferToImage = null;
    
    // link player to capture device
    try {
      MediaLocator ml = findMedia(CAP_DEVICE);
      // MediaLocator ml = new MediaLocator(CAP_LOCATOR);

	  p = Manager.createRealizedPlayer(ml);
      System.out.println("Created player");
    }
    catch (Exception e) {
      System.out.println("Failed to create player");
     //System.exit(0);
    }

    p.addControllerListener(this);

    // create the frame grabber
    fg =  (FrameGrabbingControl) p.getControl("javax.media.control.FrameGrabbingControl");
    if (fg == null) {
      System.out.println("Frame grabber could not be created");
      System.exit(0);
    }

    // wait until the player has started
    System.out.println("Starting the player...");
    p.start();
    if (!waitForStart()) {
      System.err.println("Failed to start the player.");
      System.exit(0);
    }

    waitForBufferToImage();
  }  // end of JMFCapture()


  private MediaLocator findMedia(String requireDeviceName)
  // return a media locator for the specified capture device
  {
    Vector devices = CaptureDeviceManager.getDeviceList(null);

    if (devices == null) {
      System.out.println("Devices list is null");
      System.exit(0);
    }
    if (devices.size() == 0) {
      System.out.println("No devices found");
      System.exit(0);
    }

    for (int i = 0; i < devices.size(); i++) {
      CaptureDeviceInfo devInfo = 
            (CaptureDeviceInfo) devices.elementAt(i);
      String devName = devInfo.getName();
      if (devName.equals(requireDeviceName)) {   // found device
        System.out.println("Found device: " + requireDeviceName);
        return devInfo.getLocator();   // this method may not work
      }
    }

    System.out.println("Device " + requireDeviceName + "not found");
    System.out.println("Using default media locator: " + CAP_LOCATOR);
    return new MediaLocator(CAP_LOCATOR);
  }  // end of findMedia()


  private boolean waitForStart()
  // wait for the player to enter its Started state
  { synchronized (waitSync) {
      try {
        while (p.getState() != Controller.Started && stateTransitionOK)
          waitSync.wait();
      }
      catch (Exception e) {
    	  
      }
    }
    return stateTransitionOK;
  } // end of waitForStart()


  private void waitForBufferToImage()
  /* Wait for the BufferToImage object to be initialized.
     May take several seconds to initialize this object, 
     so this method makes up to MAX_TRIES attempts.
  */
  {
    int tryCount = MAX_TRIES;
    System.out.println("Initializing BufferToImage...");
    while (tryCount > 0) {
      if (hasBufferToImage())   // initialization succeeded
        break;
      try {   // initialization failed so wait a while and try again
        System.out.println("Waiting...");
		Thread.sleep(TRY_PERIOD);
      }
      catch (InterruptedException e)
      {  System.out.println(e);  }
      tryCount--;
    }

    if (tryCount == 0) {
      System.out.println("Giving Up");
      System.exit(0);
    }

    closedDevice = false;   // device now available
  }  // end of waitForBufferToImage()


  private boolean hasBufferToImage()
  /*  The BufferToImage object is initialized here, so that when 
      grabImage() is called later, the snap can be quickly changed to 
      an image.

      The object is initialized by taking a snap, which
      may be an actual picture or be 'empty'.

      An 'empty' snap is a Buffer object with no video information,
      as detected by examining its component VideoFormat data. 

      An 'empty' snap is caused by the delay in the player, which 
      although in its started state may still take several seconds to 
      start capturing.
 
      The dimensions of the snap are used to calculate the scale
      factor from the original image size to size*size.
  */
  {
    Buffer buf = fg.grabFrame();     // take a snap
    if (buf == null) {
      System.out.println("No grabbed frame");
      return false;
    }
    
    // there is a buffer, but check if it's empty or not
    VideoFormat vf = (VideoFormat) buf.getFormat();
    if (vf == null) {
      System.out.println("No video format");
      return false;
    }

    System.out.println("Video format: " + vf);
    int width = vf.getSize().width;     // the image's dimensions
    int height = vf.getSize().height;
    if (width > height)
      scaleFactor = ((double) size ) / width;
    else
      scaleFactor = ((double) size) / height;

    // initialize bufferToImage with the video format info.
    bufferToImage = new BufferToImage(vf);
    return true;
  }  // end of hasBufferToImage()



  synchronized public BufferedImage grabImage()
  /* Capture an image/frame.
     The frame is converted from Buffer object to Image,
     and finally to BufferedImage. 
  */
  {
    if (closedDevice)
      return null;
    
    // grab the current frame as a buffer object
    Buffer buf = fg.grabFrame();
    if (buf == null) {
      System.out.println("No grabbed buffer");
      return null;
    }
    
    // convert buffer to image
    Image im = bufferToImage.createImage(buf);
    if (im == null) {
      System.out.println("No grabbed image");
      return null;
    }

    return makeBIM(im);
  }  // end of grabImage()



  private BufferedImage makeBIM(Image im)
  /* Make a BufferedImage copy of im, assuming no alpha channel.
     Resize it to fit into size dimensions. */
  {
    BufferedImage copy = new BufferedImage(size, size, 
                                        BufferedImage.TYPE_INT_RGB);
                                        // BufferedImage.TYPE_3BYTE_BGR);
      // TYPE_3BYTE_BGR is a BufferedImage format that can support 
      // dynamic texturing in OpenGL v.1.2 (and above) and D3D

    // create a graphics context
    Graphics2D g2d = copy.createGraphics();
/*
    try{
    	File f = new File("capturedImage.jpg");
    	JimiRasterImage jrf = Jimi.createRasterImage(image.getSource());
    	   Jimi.putImage("image/jpeg",jrf,new FileOutputStream(f));
    	   }
    	catch (JimiException je) {
    	   je.printStackTrace();}
*/

    
//    Image convertedImage;
 ///   hough = new houghDemo();
//    hough.init(im);
    
    // image --> resized BufferedImage
    g2d.scale(scaleFactor, scaleFactor);
         /* Scale the image according to the scaleFactor value, by
            scaling the graphics context instead. */
    g2d.drawImage(im,0,0,null);
    g2d.dispose();
    return copy;
  }  // end of makeBIM()


  synchronized public void close()
  /* close() and grabImage() are synchronized so that it's not
     possible to close down the player while a frame is being
     snapped. */
  { 
	  p.close();  
	  closedDevice = true;
  } 


  public boolean isClosed()
  {  return closedDevice;  }


  public void controllerUpdate(ControllerEvent evt)
  // respond to events
  {
    if (evt instanceof StartEvent) {   // the player has started
      synchronized (waitSync) {
        stateTransitionOK = true;
        waitSync.notifyAll();
      }
    }
    else if (evt instanceof ResourceUnavailableEvent) {  
      synchronized (waitSync) {  // there was a problem getting a player resource
        stateTransitionOK = false;
        waitSync.notifyAll();
      }
    }
  } // end of controllerUpdate()


} // end of JMFCapture class
