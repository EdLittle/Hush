/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.BufferedImage;

/**
 *
 * @author Administrator
 */
public class ImagePanel extends javax.swing.JPanel {
    public BufferedImage image = null;
    /**
     * Creates new form ImagePanel
     */
    public ImagePanel(){
            //setLayout(null);
            setSize(320,240);
        }
        public void setImage(BufferedImage img){
            this.image = img;
            repaint();
        }
        @Override
        public void paint(Graphics g){
            int h, w;
            if(image != null){
                h = image.getHeight();
                w = image.getWidth();
                g.drawImage(image, 0, 0, w, h, w, 0, 0, h, null);
            }
        }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
