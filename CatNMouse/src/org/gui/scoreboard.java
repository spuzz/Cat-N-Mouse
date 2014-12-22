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
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author Spuz
 */
public class scoreboard extends JPanel {
        BufferedImage image;
        Minimap mini;
        PlayerStats stats;
	public scoreboard() {
            super();
            setOpaque(false);
            Border gap = BorderFactory.createEmptyBorder(30, 30, 30, 15);
            setBorder(gap);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            if(dim.height > 1000) {
                setMaximumSize(new Dimension(Integer.MAX_VALUE,dim.height - 580));
                setMinimumSize(new Dimension(Integer.MAX_VALUE,dim.height - 580));
                setPreferredSize(new Dimension(Integer.MAX_VALUE,dim.height - 580));
            }
            else {
                setMaximumSize(new Dimension(Integer.MAX_VALUE,dim.height - 430));
                setMinimumSize(new Dimension(Integer.MAX_VALUE,dim.height - 430));
                setPreferredSize(new Dimension(Integer.MAX_VALUE,dim.height - 430));
            }

            image= new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY);
            try {
                image=(BufferedImage)ImageIO.read(new File("wall4.png"));
            }
            catch(Exception e) {

            }
            mini = new Minimap();
            add(mini);
            stats = new PlayerStats();
            add(stats);
        }     
      @Override
      protected void paintComponent(Graphics g)
      {
        super.paintComponent(g); 
        if (image != null)
          g.drawImage(image, 0,0,this.getWidth(),this.getHeight(),this);
      }

      public Minimap getMiniMap() {
          return mini;
      }

      public PlayerStats getStats() {
          return stats;
      }
}
