/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.AI.InfluenceMap;
import org.GameObjects.Point2D;

/**
 *
 * @author Spuz
 */
public class Minimap extends JPanel{
        BufferedImage image;
        public Minimap() {
                super();
                Border gap = BorderFactory.createEmptyBorder(15, 15, 15, 15);
                setBorder(gap);
                setMaximumSize(new Dimension(180,180));
                //setMinimumSize(new Dimension(300,300));
                image= new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);
                try {
                    image=(BufferedImage)ImageIO.read(new File("Images/wall2.jpg"));
                }
                catch(Exception e) {
                }
        }
      protected void paintComponent(Graphics g)
      {
        super.paintComponent(g);
        if (image != null)
          g.drawImage(image, 0,0,this.getWidth(),this.getHeight(),this);
      }

      public void updateMap(int[][] raster,InfluenceMap influence,InfluenceMap mouse) {
        int w = image.getWidth(); //assume that they all have the same dimensions
        int h = image.getHeight();
        float red;
        float green;
        float blue;
        for (int x = 0; x < w; x++)
           for (int y = 0; y < h; y++) {
              //creates a new float to contain the new colour for the pixel
              if(raster[x][y] == 2) {
                  red = 100;
                  green = 100;
                  blue = 100;
              }
              else if(raster[x][y] == 1) {
                  red = 160;
                  green = 82;
                  blue = 45;
              }
              else {
                  red = 0;
                  green = 100;
                  blue = 0;
              }
              if(influence.getTileInfluence(new Point2D(x/2,y/2),false) > 0 && raster[x][y] != 2 ) {
                  red = 255;
              }
              if(mouse.getTileInfluence(new Point2D(x/2,y/2),false) > 0 && raster[x][y] != 2 ) {
                  blue = 255;
              }
             // Makes the pixels colour equal to the new value contained in difference
             image.getRaster().setSample(x,y, 0, red);
             image.getRaster().setSample(x,y, 1, green);
             image.getRaster().setSample(x,y, 2, blue);
             // Render into the BufferedImage graphics to create the texture
         }
      }

      public BufferedImage getImage() {
          return image;
      }

      public void setImage(BufferedImage image) {
          this.image = image;
      }
}
