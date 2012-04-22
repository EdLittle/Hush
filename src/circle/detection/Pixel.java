/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package circle.detection;

import java.awt.Color;

/**
 *
 * @author FREAK
 */
public class Pixel {
    private int x;
    private int y;
    private float color;
    
    public Pixel(float value, int indexX, int indexY){
        color = value;
        x = indexX;
        y = indexY;
    }
    
    public Pixel(int indexX, int indexY){
        x = indexX;
        y = indexY;
        color = Color.white.getRGB();
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
}
