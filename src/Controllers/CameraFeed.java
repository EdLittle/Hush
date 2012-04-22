/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import GUI.ImagePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import javax.media.*;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JButton;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author Administrator
 */
public class CameraFeed extends Panel{
    public static Player player;
    public CaptureDeviceInfo di = null;
    public MediaLocator ml=null;
    //public JButton capture=null;
    public Buffer buf=null;
    public Image img=null;
    public VideoFormat vf=null;
    public BufferToImage btoi=null;
    public ImagePanel imgpanel=null;
    private Future gameTask; 
    //public int capturenumber=0;
    private ScheduledExecutorService executor;
    
    public CameraFeed(){
        
        setLayout(new BorderLayout());
        setSize(320, 550);
        String str = "vfw:Microsoft WDM Image Capture (Win32):0";
        di = CaptureDeviceManager.getDevice(str);
        ml = new MediaLocator(str);
        executor = Executors.newScheduledThreadPool(5);
        imgpanel = new ImagePanel();
        add(imgpanel, BorderLayout.CENTER);
        try{
            player = Manager.createRealizedPlayer(ml);
            player.start();
        }
        catch(Exception e){
<<<<<<< HEAD
            
=======
>>>>>>> eee3728485b9f5008a5d065726f45464bc93057f
            e.printStackTrace();
        }
    }
    
    public static void playerclose(){
        player.close();
        player.deallocate();
    }
    
    public void startFeed(){
        imgpanel.setBackground(Color.CYAN);
        executor.scheduleAtFixedRate(new Runnable(){
            
                public void run(){
                    BufferedImage feed = getFeed();
                    imgpanel.setImage(feed);
                }
            }, 500, 83, TimeUnit.MILLISECONDS);
    }
    
    public BufferedImage grabImage(){
        FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
        buf  = fgc.grabFrame();
        btoi = new BufferToImage((VideoFormat)buf.getFormat());
        img = btoi.createImage(buf);
        BufferedImage bf = (BufferedImage)img;
        bf.getSubimage(80, 80, 350, 200);
        return bf;
    }
    
    public BufferedImage getFeed(){
        FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
        buf  = fgc.grabFrame();
        btoi = new BufferToImage((VideoFormat)buf.getFormat());
        img = btoi.createImage(buf);
        BufferedImage bf = (BufferedImage)img;
        return bf;
    }
    
    
}
