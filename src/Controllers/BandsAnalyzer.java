/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.JMFCapture;
import GUI.BandsPanel;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author FREAK
 */
public class BandsAnalyzer extends Thread{
    //private BandsPanel bandsPanel;
    //private JMFCapture camera;
    private BandManager bandManagers[];
    
    private final Color RED = new Color(180, 20, 23);
    private final Color ORANGE = new Color(200, 80, 40);
    private final Color YELLOW = new Color(200, 200, 85);
    private final Color GREEN = new Color(30, 110, 80);
    private final Color BLUE = new Color(25, 127, 190);
    private final Color WHITE = new Color(180, 180, 180);
    private final Color BLACK = new Color(0, 0, 0);
    private final int NUMBER_OF_BAND_MANAGERS = 7;
    
    private int bandIdx, blobIdx;
    
    public BandsAnalyzer(){
        //this.bandsPanel = bandsPanel;
        bandManagers = new BandManager[7];
        
        bandManagers[0] = new BandManager("red", RED);
        bandManagers[1] = new BandManager("orange", ORANGE);
        bandManagers[2] = new BandManager("yellow", YELLOW);
        bandManagers[3] = new BandManager("green", GREEN);
        bandManagers[4] = new BandManager("blue", BLUE);
        bandManagers[5] = new BandManager("white", WHITE);
        bandManagers[6] = new BandManager("black", BLACK);
        
    }
    
    /*public void run(){
        camera = new JMFCapture();
        BufferedImage im = null;
        
        im = camera.grabImage();
        analyzeImage(im);
        //applyBigBlob();
    }*/
    
    public String analyzeImage(BufferedImage im){
        if (im == null){
            System.out.println("There is no image");
            return "";
        }
        
        int width = im.getWidth();
        int height = im.getHeight();
        int[] pixels = new int[width * height];
        
        im.getRGB(0, 0, width, height, pixels, 0, width);
        
        boolean madeBigBlob = false;
        int i = 0;
        int bandID;
        
        int xc = 0;
        int yc= 0;
        
        while((i < pixels.length) && (!madeBigBlob)){
            bandID = isBandColor(pixels[i]);
            
            if(bandID != -1){
                madeBigBlob = addPointToBand(bandID, xc, yc);
            }
            xc++;
            if(xc == width){
                xc = 0;
                yc++;
            }
            i++;
        }
        if(bandIdx != -1){
            BandManager bm = bandManagers[bandIdx];
            //Point pt = bm.getBlobCenter(blobIdx);
            //String name =
            return bm.getName();
        }
        return "";
    }
    
    private boolean addPointToBand(int bandID, int x, int y){
        boolean madeBigBlob = false;
        
        int blobID = bandManagers[bandID].addPoint(x, y);
        if(blobID != -1){
            madeBigBlob = true;
            bandIdx = bandID;
            blobIdx = blobID;
        }
        
        return madeBigBlob;
    }
    
    private int isBandColor(int pixel){
        int red = (pixel>>16)&255;
        int green = (pixel>>8)&255;
        int blue = pixel&255;
        int i;
        
        for(i = 0; i < NUMBER_OF_BAND_MANAGERS; i++){
            if(bandManagers[i].isBandColour(red, green, blue))
                return i;
        }
        return -1;
    }
    
}
    
