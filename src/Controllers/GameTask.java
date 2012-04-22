/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import GUI.DecoyPlay;
import GUI.Hush;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author Administrator
 */
public class GameTask implements Runnable{
    private String taskType;
    private Hush hush;
    private JLabel stopLight1;
    private JLabel stopLight2;
    private JLabel displayLabel;
    private Timer timer;
    private String[] randomColors;
    private int roundNumber = -1;
    private boolean correct;
    private boolean finished;
    
    public GameTask(String type, String[] randomColors){
        taskType = type;
        hush = Hush.getHush();
        DecoyPlay decoyPlay = hush.getDecoyPlay();
        stopLight1 = decoyPlay.getStopLight1();
        stopLight2 = decoyPlay.getStopLight2();
        displayLabel = decoyPlay.getJLabel1();
        this.randomColors = randomColors;
        finished = false;
        correct = false;
    }
    
    public GameTask(String type){
        taskType = type;
        hush = Hush.getHush();
        DecoyPlay decoyPlay = hush.getDecoyPlay();
        stopLight1 = decoyPlay.getStopLight1();
        stopLight2 = decoyPlay.getStopLight2();
        displayLabel = decoyPlay.getJLabel1();
        correct = false;
    }
    /*
    public GameTask(String type, Timer timer){
        taskType = type;
        this.timer = timer;
    }
    */
    @Override
    public void run() {
        
        if(this.taskType.equals("Red")){
            setReady();
        }
        else if (this.taskType.equals("Yellow")){
            setGetSet();
        }
        else if (this.taskType.equals("Green")){
            setGo();
        }
        else if (this.taskType.equals("Cancel")){
            detectColor();
        }
        else if (this.taskType.equals("Color Level")){
            try {
                singleColorRound();
            } catch (InterruptedException ex) {
                Logger.getLogger(GameTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (this.taskType.equals("Ask")){
            askColor();
        }
    }
    
    public boolean isCorrect(){
        return correct;
    }
    
    public boolean isFinished(){
        return finished;
    }
    
    public void setTaskType(String taskType){
        this.taskType = taskType;
    }
    
    private void setReady(){
        displayLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/ready.png")));
        stopLight1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-red.png")));
        stopLight2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-red.png")));
        setTaskType("Yellow");
    }
    
    private void setGetSet(){
        displayLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/get set.png")));
        stopLight1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-orange.png")));
        stopLight2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-orange.png")));
        setTaskType("Green");
        this.roundNumber++;
    }
    
    private void setGo(){
        displayLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/go.png")));
        stopLight1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-green.png")));
        stopLight2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/trafficlight-green.png")));
        setTaskType("Ask");
    }
    
    private void askColor(){
        displayLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med/" + randomColors[roundNumber])));
        setTaskType("Red");
    }
    
    private void singleColorRound() throws InterruptedException{
        System.out.println("D.O.");
        for(int i = 0; i < 16; i++){
            
            int rand = (int)(Math.random() * 100) % 5;
            System.out.println(i + " " + rand);
            //simulate randomness
            if (rand == 0){
                
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println("If I were a boy");
        finished = true;
    }
    
    private void detectColor(){
        System.out.println("BaekHyun");
        
    }
    
    public void setFinished(boolean isFinished){
        finished = isFinished;
    }
    
    
}

