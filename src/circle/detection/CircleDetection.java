/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package circle.detection;

import circle.detection.Pixel;
import ij.ImagePlus;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import image.edge.ImageEdge;
import image.edge.Image_Edge;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author FREAK
 */
public class CircleDetection {
    private BufferedImage image;
    private final int[] pixels;
    private final int whitish = new Color(210, 210, 210).getRGB();
    private final int whitish2 = new Color(150, 150, 150).getAlpha();
    private final int blackish = new Color(45, 45, 45).getRGB();
    private ImagePlus imagePlus;
    private Vector<Pixel> edgePixels;
    private Vector<Vector<Pixel>> circles;
    private Vector<Pixel> possibleCircle;
    private int Tf = 20000;              //failure threshold
    private int TMin = 400;             //minimum edge pixels threshold
    private int Ta = 15;                //minimum distance between any two random pixels
    private float Td = (float) 0.5;     //distance threshold
    private double Tr = 0.60;           //ratio threshold
    private int f;                      //number of failures
    private Pixel[] randomPixels;
    private Pixel[] agentPixels;
    private Pixel fourthPixel;
    private int a_;
    private int b_;
    private int r_;
    
    private boolean failed;
    
    public CircleDetection(BufferedImage img) throws IOException{
        ////System.out.println("whitish2 = " + whitish2);
        System.out.println("Roll like a buffalo");
        circles = new Vector();
        imagePlus = new ImagePlus("Buffalo", Toolkit.getDefaultToolkit().createImage(img.getSource()));
        
        //imagePlus = new ImagePlus("media/Test Images/onecoaster2.jpg");
        ImageConverter imageConverter = new ImageConverter(imagePlus);
        imageConverter.convertToGray8();
        //imageConverter.convertToRGB();
        ImageProcessor imageProcessor = imagePlus.getProcessor();
        imageProcessor.sharpen();
        //imageProcessor.smooth();
        //imageProcessor = ImageEdge.areaEdge(imageProcessor, 3.00, 1, 100, 90);
        //display(imageProcessor.getBufferedImage(), "Bad romance");
        
        //imageProcessor.findEdges();
        Image_Edge edgeFinder =  new Image_Edge();
        imagePlus = edgeFinder.EdgeDetection(imagePlus);
        
        imageProcessor = imagePlus.getProcessor();
        
        image = imagePlus.getBufferedImage();
        //display(image, "Get the pixels");
        
        
        
        byte[] bytePixels;
        
        Step_1:
        bytePixels = (byte[]) imageProcessor.getPixels();
        pixels = convertPixels(bytePixels);
        int i;
        
        //System.out.println("Size of array: " + pixels.length);
        //System.out.println("Dimension of image: " + (image.getHeight() * image.getWidth()));
        
        setEdgePixelsApart();
        
        //System.out.println("There are " + edgePixels.size() + " edge pixels in the image.");
        
        randomCircleDetection();
        imageConverter = new ImageConverter(imagePlus);
        
        imageConverter.convertToRGB();
        image = imagePlus.getBufferedImage();
        checkPixels();
        System.out.println("There are " + circles.size() + " circles detected.");
        //display(image, "detected");
    }
    
    public boolean isDetected(){
        if(circles.size() > 0){
            return true;
        }
        return false;
    }
    
    /**
     * @param args the command line arguments
     *
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        final long startTime = System.nanoTime();
        final long endTime;
        try {
            CircleDetection circleDetection = new CircleDetection();
        } finally {
            endTime = System.nanoTime();
        }
        final long duration = endTime - startTime;
        System.out.println(duration);
        
    }
    */
    private void display(BufferedImage image, String title){
        JLabel label = new JLabel(new ImageIcon(image));
        JFrame frame = new JFrame(title);
        frame.setVisible(true);
        frame.setSize(image.getWidth(), image.getHeight());
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void display(BufferedImage image){
        JLabel label = new JLabel(new ImageIcon(image));
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setSize(image.getWidth(), image.getHeight());
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void setEdgePixelsApart(){
        int i;
        int[] edgePixels2 = null;
        int xTemp = 0;
        int yTemp = 0;
        int width = image.getWidth();
        ////System.out.println("There are " + pixels.length + " pixels");
        edgePixels = new Vector();
        
        for(i = 0; i < pixels.length; i++){
            ////System.out.println(xTemp + " " + yTemp);
            ////System.out.println(pixels[i]);
            if(pixels[i] >= whitish2){
                ////System.out.println(pixels[i]);
                edgePixels.add(new Pixel(pixels[i], xTemp, yTemp));
                ////System.out.println(xTemp + " " + yTemp);
                //pixels[i] = Color.cyan.getRGB();
            }
            xTemp++;
            
            if (xTemp >= width){
                xTemp = 0;
                yTemp++;
            }
            
        }
        ////System.out.println(edgePixels.size());
        
        image = imagePlus.getBufferedImage();
        //display(image);
        //System.out.println(image.getWidth() + " AEGIS " + image.getHeight());
    }
    
    private void randomCircleDetection(){
        randomPixels = new Pixel[4];
        int numberOfPixels;
        int randomNumber;
        
        Initialize_Threshold_Values:
        f = 0;
        
        while(true){
            
            numberOfPixels = edgePixels.size();
            
            Step_2:
            if((numberOfPixels < TMin) || (f >= Tf)){
                ////System.out.println("Ayoko na");
                if(numberOfPixels < TMin)
                    System.out.println("Rihanna");
                else if (f >= Tf){
                    System.out.println("Beyonce");
                }
                
                break;
            }
            else{
                
                Step_3:
                getRandomPoints();
                if(validatePoints()){
                    //System.out.println("Should I rewrite it?");
                    
                    Step_4:
                    checkRestOfPixels();
                    
                    Step_5:
                    if(checkIfDetectedCircleIsValid()){
                        //System.out.println("We found love in a hopeless place...");
                        f = 0;
                        circles.add(possibleCircle);
                    }
                    else{
                        returnFailedCircle();
                        continue;
                    }
                    ////System.out.println("Yellow Diamonds");
                    //break;
                }
                else{
                    returnFailedPoints();
                    continue;
                }
            }
        }
        //System.out.println("Yeah");
    
    }
    
    public void detectCircle(){
        int numberOfPixels;
        int randomNumber;
        f = 0;
        randomPixels = new Pixel[4];
        
        //while(true){
        for(int ctr = 0; ctr < 1; ctr ++){
            numberOfPixels = edgePixels.size();
            
            if ((numberOfPixels < TMin) || (f == Tf))
                break;           
            //Step 3
            checkForPossibleCircles();
            
            if(failed){
                //System.out.println("There are no circles detected. g");
            }
            
            checkRestOfPixels();
            checkIfDetectedCircleIsValid();
            
        }
    }
    
    private void returnFailedCircle(){
        int size = possibleCircle.size();
        int i;
        
        for(i = 0; i < size; i++){
            edgePixels.add(possibleCircle.elementAt(i));
            
        }
        possibleCircle.removeAllElements();
        possibleCircle = new Vector();
    }
    
    private void getRandomPoints(){
        int i;
        int randomNumber;
        int numberOfPixels;
        ////System.out.println("There are " + numberOfPixels + "edge pixels");
        
        for(i = 0; i < 4; i++){
            numberOfPixels = edgePixels.size();
            randomNumber = (int) ((Math.random() * 1000000) % numberOfPixels);
            ////System.out.println("Random Number = " + randomNumber);
            
            randomPixels[i] = edgePixels.remove(randomNumber);
        }

    }
    
    private boolean validatePoints(){
        Vector<int[]> combinations = new Vector<int[]>();
        int x1, x2, x3, x4;
        int y1, y2, y3, y4;
        int i, d, a, b, r, combination;
        int[] arrangement;
        
        combinations.add(new int[]{0, 1, 2});
        combinations.add(new int[]{0, 1, 3});
        combinations.add(new int[]{0, 2, 3});
        combinations.add(new int[]{1, 2, 3});
        combination = 0;
        ////System.out.println("");
        for(i = 3; i > -1; i--){
            x4 = randomPixels[i].getX();
            y4 = randomPixels[i].getY();
            ////System.out.println("WE FELL IN LOVE IN A HOPELESS FACE");
            arrangement = combinations.elementAt(combination);
            x1 = randomPixels[arrangement[0]].getX();
            y1 = randomPixels[arrangement[0]].getY();
            x2 = randomPixels[arrangement[1]].getX();
            y2 = randomPixels[arrangement[1]].getY();
            x3 = randomPixels[arrangement[2]].getX();
            y3 = randomPixels[arrangement[2]].getY();
            
            
            if(checkAgentPixelDistance(x1, y1, x2, y2, x3, y3)){
                a = getXCenter(x1, y1, x2, y2, x3, y3);
                b = getYCenter(x1, y1, x2, y2, x3, y3);
                
                if((a == -55) || (b == -55)){
                    continue;
                }
                
                r = getRadius(a, b, x1, y1);
                d = getDistanceFromBoundary(a, b, r, x4, y4);
                
                if(d <= Td){
                    ////System.out.println("\n\n4thpixel:" + x4 + " " + y4);
                    ////System.out.println("Agent pixels: \n" + x1 + " " + y1);
                    ////System.out.println(x2 + " " + y2);
                    ////System.out.println(x3 + " " + y3);
                    ////System.out.println("Center is " + a + ", " + b);
                    ////System.out.println("Radius is " + r);
                    ////System.out.println("Distance from boundary is: " + d);
                    ////System.out.println("Agent Pixels are: ");
                    fourthPixel = new Pixel(x4, y4);
                    agentPixels = new Pixel[]{new Pixel(x1, y1), new Pixel(x2, y2), new Pixel(x3, y3)};
                    a_ = a;
                    b_ = b;
                    r_ = r;

                    return true;
                }
                else{
                    f++;
                    
                }
            }
            else{
                f++;
                
            }
            combination++;
        }
        return false;
    }
    
    
    private void checkForPossibleCircles(){
        Vector<int[]> combinations = new Vector<int[]>();
        int i;
        int d;
        int a;
        int b;
        int r;
        int x1, x2, x3, x4;
        int y1, y2, y3, y4;
        int combination = 0;
        int[] arrangement;
        
        
        combinations.add(new int[]{0, 1, 2});
        combinations.add(new int[]{0, 1, 3});
        combinations.add(new int[]{0, 2, 3});
        combinations.add(new int[]{1, 2, 3});
        
        RandomCircleFitting:
        while(true){
        
            if(!(f >= Tf)){
                getRandomPoints();
                if(failed){
                    //System.out.println("There are no circles detected");
                    break RandomCircleFitting;
                
                }
                combination = 0;
        
                for(i = 3; i > -1; i--){
                    //System.out.println("\nPokerface" + i);
                    x4 = randomPixels[i].getX();
                    y4 = randomPixels[i].getY();

                    arrangement = combinations.elementAt(combination);
                    x1 = randomPixels[arrangement[0]].getX();
                    y1 = randomPixels[arrangement[0]].getY();
                    x2 = randomPixels[arrangement[1]].getX();
                    y2 = randomPixels[arrangement[1]].getY();
                    x3 = randomPixels[arrangement[2]].getX();
                    y3 = randomPixels[arrangement[2]].getY();

                //Get center
                if(checkAgentPixelDistance(x1, y1, x2, y2, x3, y3)){
                    a = getXCenter(x1, y1, x2, y2, x3, y3);
                    b = getYCenter(x1, y1, x2, y2, x3, y3);
                    
                    if((a == -55) || (b == -55)){
                        f++;
                        returnFailedPoints();
                        continue;
                    }
                    
                    r = getRadius(a, b, x1, y1);
                    d = getDistanceFromBoundary(a, b, r, x4, y4);

                    if (d <= Td){

                        //System.out.println("\n\n4thpixel:" + x4 + " " + y4);
                        //System.out.println("Agent pixels: \n" + x1 + " " + y1);
                        //System.out.println(x2 + " " + y2);
                        //System.out.println(x3 + " " + y3);
                        //System.out.println("Center is " + a + ", " + b);
                        //System.out.println("Radius is " + r);
                        //System.out.println("Distance from boundary is: " + d);
                        //System.out.println("Agent Pixels are: ");
                        fourthPixel = new Pixel(x4, y4);
                        agentPixels = new Pixel[]{new Pixel(x1, y1), new Pixel(x2, y2), new Pixel(x3, y3)};
                        a_ = a;
                        b_ = b;
                        r_ = r;
                        failed = false;
                        break RandomCircleFitting;
                    }
                    else{
                        f++;
                        returnFailedPoints();
                    }
                }
                else{
                    f++;
                    returnFailedPoints();
                }
                
            
                combination++;
                }
            }
            else{
                failed = true;
                break RandomCircleFitting;
            }
        }
    }
    
    private void checkRestOfPixels(){
        int c = 0;
        int i = 0;
        int size = 0;
        int x = 0;
        int y = 0;
        int d = 0;
        Pixel pixel;
        
        possibleCircle = new Vector();
        possibleCircle.add(agentPixels[0]);
        possibleCircle.add(agentPixels[1]);
        possibleCircle.add(agentPixels[2]);
        possibleCircle.add(fourthPixel);
        
        for(;;){
            size = edgePixels.size();
            if ((i >= size) || (size == 0)){
                break;
            }
            pixel = edgePixels.elementAt(i);
            x = pixel.getX();
            y = pixel.getY();

            d = getDistanceFromBoundary(a_, b_, r_, x, y);
            
            if(d <= Td){
                ////System.out.println("Add");
                c++;
                possibleCircle.add(edgePixels.remove(i));
            }
            
            i++;
            
        }
        
        ////System.out.println("");
    }
    
    private int getXCenter(int x1, int y1, int x2, int y2, int x3, int y3){
        int a = 0;
        int denominator = 4 * ((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
        
        if(denominator == 0){
            return -55;
        }
        int numeratorFirst = (int)((Math.pow(x2, 2) + Math.pow(y2, 2) - (Math.pow(x1, 2) + Math.pow(y1, 2))) * (2 * (y3 - y1)));
        int numeratorSecond = (int)((Math.pow(x3, 2) + Math.pow(y3, 2) - (Math.pow(x1, 2) + Math.pow(y1, 2))) * (2 * (y2 - y1)));
        
        a = (numeratorFirst - numeratorSecond)/denominator;
        
        return a;
    
    }
    
    private int getYCenter(int x1, int y1, int x2, int y2, int x3, int y3){
        int b = 0;
        int denominator = 4 * ((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
        
        if (denominator == 0){
            return -55;
        }
        
        int numeratorFirst = (int)((2 * (x2 - x1)) * (Math.pow(x3 , 2) + Math.pow(y3, 2) - (Math.pow(x1, 2) + Math.pow(y1 , 2))));
        int numeratorSecond = (int)((2 * (x3 - x1)) * (Math.pow(x2 , 2) + Math.pow(y2, 2) - (Math.pow(x1, 2) + Math.pow(y1 , 2))));
        
        b = (numeratorFirst - numeratorSecond)/denominator;
        
        return b;
    }
    
    private int getRadius(int a, int b, int x, int y){
        int radius = 0;
        
        radius = (int) Math.sqrt(Math.pow((x - a), 2) + Math.pow((y - b), 2));
        return radius;
    }
    
    public boolean checkAgentPixelDistance(int x1, int y1, int x2, int y2, int x3, int y3){
        int x, y;
        int i;
        double distance;
        
        distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        if(distance < Ta)
            return false;
        
        distance = Math.sqrt(Math.pow((x3 - x1), 2) + Math.pow((y3 - y1), 2));
        if(distance < Ta)
            return false;
        
        distance = Math.sqrt(Math.pow((x3 - x2), 2) + Math.pow((y3 - y2), 2));
        if(distance < Ta)
            return false;
        
        return true;
    }
    
    public boolean checkDistances(){
        int x1, x2;
        int y1, y2;
        int i, j;
        for(i = 0; i < 4; i++){
            for(j = i; j < 4; j++){
                if(i != j){
                    x1 = randomPixels[i].getX();
                    y1 = randomPixels[i].getY();
                    x2 = randomPixels[j].getX();
                    y2 = randomPixels[j].getY();
                    
                    
                    ////System.out.println("Comparing distance of" + x1 + " " + x2 + " " + y1 + " " + y2);
                    double distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
                    if(distance < Ta)
                        return false;
                }
            }
        }
        return true;
    }

    private void returnFailedPoints() {
        int i;
        
        for(i = 0; i < 4; i++){
            edgePixels.add(randomPixels[i]);
        }
    }
    
    private int getDistanceFromBoundary(int a, int b, int r, int x, int y){
        int d = 0;
        
        d = (int) Math.abs((Math.sqrt( Math.pow((x - a), 2) + Math.pow((y - b), 2)) - r));
        
        return d;
    }
    
    private void checkPixels(){
        int lim;
        int i, j;
        int numCircles = circles.size();
        Vector<Pixel> currentCircle;
        ImageProcessor imageProcessor = imagePlus.getProcessor();
        
        for(i = 0; i < numCircles; i++){
            currentCircle = circles.elementAt(i);
            lim = currentCircle.size();
            ////System.out.println("Yo " + lim);
            for(j = 0; j < lim; j++){
                image.setRGB(currentCircle.elementAt(j).getX(), currentCircle.elementAt(j).getY(), Color.orange.getRGB());
                //imageProcessor.putPixel(currentCircle.elementAt(j).getX(), currentCircle.elementAt(j).getY(), Color.gray.getAlpha());
            }
        }
        //display(image);
    }

    private boolean checkIfDetectedCircleIsValid() {
        double sizeOfPossibleCircle = (double) possibleCircle.size();
        double idealNumber = 2 * Math.PI * r_ * Tr;
        System.out.println("The ideal number is: " + idealNumber);
        System.out.println("There are " + sizeOfPossibleCircle + " detected.");
        //System.out.println(edgePixels.size());
        
        if (sizeOfPossibleCircle >= idealNumber){
            System.out.println("We have a freaking circle");
            return true;
        }
        else{
            System.out.println("Fuck it. We didn't meet the quota");
            return false;            
        }
        
    }
    
    private int[] convertPixels(byte[] oldPixels){
        int[] pixels = new int[oldPixels.length];
        int value;
        int i;
        ////System.out.println("Converting");
        for(i = 0; i < oldPixels.length; i++){
            value = 0xff & oldPixels[i];
            pixels[i] = value;
            ////System.out.println(value);
        }        
        
        return pixels;
    }
}
