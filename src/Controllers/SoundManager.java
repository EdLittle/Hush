/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import sun.audio.*;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author FREAK
 */

public class SoundManager {
    public InputStream in;
    public AudioStream as;
    
    public AudioInputStream toggle;
    public AudioInputStream stapler;
    public AudioInputStream switch_;
    public AudioInputStream click;
    public AudioInputStream click2;
    public AudioInputStream clickOff;
    public AudioInputStream button;
    
    
    public Clip toggleClip;
    public Clip switchClip;
    public Clip staplerClip;
    public Clip clickClip;
    public Clip click2Clip;
    public Clip clickOffClip;
    public Clip buttonClip;
    
    public SoundManager() throws FileNotFoundException, IOException, UnsupportedAudioFileException, LineUnavailableException{
        //String currentDir = new File(".").getAbsolutePath();
        //System.out.println(currentDir);
        //in = new FileInputStream("sounds/toggle.wav");
        //as = new AudioStream(in);
        toggle = AudioSystem.getAudioInputStream(new File("sounds/button.wav").getAbsoluteFile());
        stapler = AudioSystem.getAudioInputStream(new File("sounds/stapling paper.wav").getAbsoluteFile());
        switch_ = AudioSystem.getAudioInputStream(new File("sounds/switch.wav").getAbsoluteFile());
        click = AudioSystem.getAudioInputStream(new File("sounds/click.wav").getAbsoluteFile());
        click2 = AudioSystem.getAudioInputStream(new File("sounds/click2.wav").getAbsoluteFile());
        clickOff = AudioSystem.getAudioInputStream(new File("sounds/click off.wav").getAbsoluteFile());
        button = AudioSystem.getAudioInputStream(new File("sounds/button.wav").getAbsoluteFile());
        
        toggleClip = AudioSystem.getClip();
        toggleClip.open(toggle);
        
        staplerClip = AudioSystem.getClip();
        staplerClip.open(stapler);
        
        switchClip = AudioSystem.getClip();
        switchClip.open(switch_);
        
        clickClip = AudioSystem.getClip();
        clickClip.open(click);
        
        click2Clip = AudioSystem.getClip();
        click2Clip.open(click2);
        
        clickOffClip = AudioSystem.getClip();
        clickOffClip.open(clickOff);
        
        buttonClip = AudioSystem.getClip();
        buttonClip.open(button);
    }
    
    public void playToggle(){
        //AudioPlayer.player.start(as);
        //AudioPlayer.player.stop(in);
        toggleClip.start();
        //System.out.println("" + clip.isRunning());
        toggleClip.setFramePosition(0);
    }
    
    public void playSwitch(){
        switchClip.start();
        switchClip.setFramePosition(0);
    }
    
    public void playStapler(){
        staplerClip.start();
        staplerClip.setFramePosition(0);
    }
    
    public void playClick(){
        clickClip.start();
        clickClip.setFramePosition(0);
    }
    
    public void playClick2(){
        click2Clip.start();
        click2Clip.setFramePosition(0);
    }
    
    public void playClickOff(){
        clickOffClip.start();
        clickOffClip.setFramePosition(0);
    }
    
    public void playButton(){
        buttonClip.start();
        buttonClip.setFramePosition(0);
    }
}
