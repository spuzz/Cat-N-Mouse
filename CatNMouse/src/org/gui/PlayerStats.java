/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author Spuz
 */
public class PlayerStats extends JPanel{
        BufferedImage image;
        JLabel time;
        JLabel score;
        int scoreVal = 0;
        public PlayerStats() {
                super();
                Border gap = BorderFactory.createEmptyBorder(30, 30, 30, 15);
                setBorder(gap);
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setMaximumSize(new Dimension(1000,227));
                setMinimumSize(new Dimension(227,227));
                image= new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY);
                try {
                    image=(BufferedImage)ImageIO.read(new File("wall4.png"));
                }
                catch(Exception e) {
                }
                time = new JLabel("Time ");
                time.setFont(new Font("Serif", Font.BOLD, 36));
                time.setForeground(Color.red);
                add(time);
                score = new JLabel("Score: " + scoreVal);
                score.setFont(new Font("Serif", Font.BOLD, 36));
                score.setForeground(Color.red);
                add(score);
            }
      protected void paintComponent(Graphics g)
      {
        super.paintComponent(g);
        if (image != null)
          g.drawImage(image, 0,0,this.getWidth(),this.getHeight(),this);
      }

      public void setTime(String time) {
          this.time.setText("Time:  " + time);
      }

      public void scored() {
          scoreVal += 1;
          this.score.setText("Score: " + scoreVal);
      }

      public void setScore() {
          this.score.setText("Score: " + scoreVal);
      }
      
      public void resetScore() {
          scoreVal = 0;
      }
}

