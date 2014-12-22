/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author as00022
 */
public class SidePanel extends JPanel{
    BufferedImage image;
    
    public SidePanel() {
        super();
        setOpaque(false);
        Border gap = BorderFactory.createEmptyBorder(30, 30, 30, 15);
        setBorder(gap);
        //setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if(dim.height > 1000) {
            setMaximumSize(new Dimension(((int)(dim.getWidth() - 576)/2),624));
        }
        else {
            setMaximumSize(new Dimension(((int)(dim.getWidth() - 576)/2),468));
        }
        //setBackground(Color.red);
        //setMinimumSize(new Dimension(Integer.MAX_VALUE/8,20600));
        image= new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
        try {
            image=(BufferedImage)ImageIO.read(new File("wall4.png"));
        }
        catch(Exception e) {

        }
    }
      @Override
      protected void paintComponent(Graphics g)
      {
        super.paintComponent(g);
        if (image != null)
          g.drawImage(image, 0,0,this);
      }
}
