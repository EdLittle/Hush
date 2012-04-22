/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import GUI.DecoyPlay;
import GUI.Hush;
import GUI.ImagePanel;
import circle.detection.CircleDetection;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class GameManager {
    private Hush hush;
    private DecoyPlay decoyPlay;
    private JLabel stopLight1;
    private JLabel stopLight2;
    private JLabel displayLabel;
    private ScheduledExecutorService executor;
    //private String colors[] = {"black", "blue", "green", "orange", "yellow", "red", "white"};
    private String colors[] = {"black", "black", "black", "black", "black", "black", "black"};
    private String randomColors[] = {"", "", "", "", "" , "", ""};
    private boolean running;
    private int round;
    private int level;
    //private boolean roundFinished;
    private Future gameTask;
    //private CameraCapture camera;
    private CameraFeed camera;
    private BandsAnalyzer bandsAnalyzer;
    private CircleDetection circleDetector;
    private final int NUMBER_OF_TRIES = 15;
    
    public GameManager(){
        hush = Hush.hush;
        decoyPlay = hush.getDecoyPlay();
        stopLight1 = decoyPlay.getStopLight1();
        stopLight2 = decoyPlay.getStopLight2();
        displayLabel = decoyPlay.getJLabel1();
        executor = Executors.newScheduledThreadPool(15);
        running = false;
        round = 0;
        level = 0;
        bandsAnalyzer = new BandsAnalyzer();
        
        //roundFinished = false;
        camera = decoyPlay.getCamera();
        setRandomColors();
    }
    
    public void startGame() throws InterruptedException{
        int i, j, k;
        running = true;
        
        //for level
        for(k = 0; k < 1; k++){
        
            //for round
            /*startTime = System.currentTimeMillis();
            long oneSecondLater = startTime + 1000;
            long twoSecondsLater = startTime + 2000;
            long threeSecondsLater = startTime + 3000;
            */
            
            executor.schedule(new Runnable(){

                @Override
                public void run() {
                    stopLight1.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-red.png")));
                    stopLight2.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-red.png")));
                    displayLabel.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/ready.png")));
                }
            }, 1, TimeUnit.SECONDS);
            
            executor.schedule(new Runnable(){

                @Override
                public void run() {
                    stopLight1.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-orange.png")));
                    stopLight2.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-orange.png")));
                    displayLabel.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/get set.png")));
                }
                
            }, 2, TimeUnit.SECONDS);
            
            executor.schedule(new Runnable(){

                @Override
                public void run() {
                    stopLight1.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-green.png")));
                    stopLight2.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-green.png")));
                    displayLabel.setIcon( new javax.swing.ImageIcon(getClass().getResource("/med/go.png")));
                
                }
            
            }, 3, TimeUnit.SECONDS);
            if(level == 0){
                executor.schedule(new Runnable(){
                    public void run(){

                        displayLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/" + randomColors[round] + ".png")));
                        System.out.println("Asking for " + randomColors[round]);
                    }
                }, 5, TimeUnit.SECONDS);

                        

            }
            else if(level == 1){
                executor.schedule(new Runnable(){
                    public void run(){
                        displayLabel.setIcon(null);
                        displayLabel.setText("Circle");
                    }
                }, 5, TimeUnit.SECONDS);

            }
            
            if(level == 0){
                
                gameTask = executor.scheduleAtFixedRate(new Runnable(){
                    int tries = 1;
                    int random;
                    @Override
                    public void run(){
                        try {
                            
                            BufferedImage image = camera.grabImage();
                            String detectedColor = bandsAnalyzer.analyzeImage(image);
                            System.out.println("The shit detected" + detectedColor + " " + round);
                            
                            boolean correct = detectedColor.equals(randomColors[round]);
                            

                            if((correct) || (tries == NUMBER_OF_TRIES)){
                                cancelFuture();
                                
                                if(correct){
                                    ScoreManager.addScore();
                                }
                                
                                startGame();
                                /*
                                if(round < 6){                                    
                                    startGame();
                                }
                                else if (round == 6){

                                    System.out.println("Next level please!");
                                    round = 0;
                                    level++;
                                    startGame();
                                }
                                * 
                                */
                                //roundFinished = true;
                            }
                            else{
                                tries++;
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }, 6, 1, TimeUnit.SECONDS);

            }
            
            
            else if(level == 1){
            gameTask = executor.scheduleAtFixedRate(new Runnable(){
                    int tries = 1;
                    int random;
                    @Override
                    public void run(){
                        try {
                            random = (int) (Math.random() * 100) % 5;
                            BufferedImage image = camera.grabImage();
                            circleDetector = new CircleDetection(image);
                            boolean correct = circleDetector.isDetected();
                            
                            if(correct)
                                System.out.println("Correct!");

                            if((random == 0) || (tries == NUMBER_OF_TRIES)){
                                getFuture().cancel(true);


                                if(round < 7){
                                //System.out.println("Grab image here and analise" + round);
                                //BufferedImage image = camera.grabImage();
                                //JFrame frame = new JFrame();
                                //ImagePanel panel = new ImagePanel();
                                //panel.setImage(image);
                                //frame.add(panel);
                                //frame.setSize(450, 450);
                                //frame.setVisible(true);
                                //round++;
                                //System.out.println("Exo");
                                startGame();
                                }
                                else if (round == 7){
                                    System.out.println("Next level please!");
                                    round = 0;
                                    level++;
                                    startGame();
                                }
                                //roundFinished = true;
                            }
                            else{
                                tries++;
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }, 6, 1, TimeUnit.SECONDS);

            }
            /*while(!roundFinished){
                //System.out.println("Boom");
            }
            /*for(j = 0; j < 7; j++){
                
                
                while(true){
                    
                    Thread.sleep(1000);
                }
            }*/
        }
        
    }
    
    private Future<?> getFuture(){
        return gameTask;
    }
    
    private void cancelFuture(){
        
        getFuture().cancel(true);
        round++;
        if(round == 7){
            ScoreManager.revokeStars();
            round = 0;
            level++;
        }
        
        
    }
    
    private void colorRound() throws InterruptedException{
        int i;
        int random;
        for(i = 0; i < 15; i++){
            random = (int)( Math.random() * 100 ) % 5;
            System.out.println("random " + random);
            if(random == 0){
                System.out.println("Correct!");
                //roundFinished = true;
                break;
            }
            Thread.sleep(1000);
            
        }
        
        //roundFinished = true;;
    }
    
    public boolean isRunning(){
        return running;
    }
    
    private void setRandomColors(){
        Vector randomPermutation = getRandomPermutation();
        Integer[] order = {10, 10, 10, 10, 10, 10, 10};

        
        order = (Integer[])randomPermutation.toArray(new Integer[0]);
        for (int i = 0; i < 7; i++){
            randomColors[i] = colors[(int)order[i]];
            //randomColors[i] = colors[randomPermutation.elementAt(i)];
            //System.out.println(randomColors[i]);
        }
    }
    
    private Vector getRandomPermutation(){
        Vector randomPermutation = new Vector();
        int i = 0;
        int rand;
        
        for(i = 0; i < 7; i++){
            do{
                rand = (int)(Math.random()*100) % 7;
            }while(randomPermutation.contains(rand));
            randomPermutation.add((int)rand);
            //System.out.println("Random Number " + rand);
        }
        
        return randomPermutation;
    }
}
