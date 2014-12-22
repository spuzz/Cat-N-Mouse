/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gui;

/**
 *
 * @author Spuz
 */
import java.awt.Graphics;
import java.awt.Image;
 
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;

//
public class ImageMenuBar extends JMenuBar {
 
	private Image bkImage;
	
	public ImageMenuBar() {
		super();
		getBkImage();
		setOpaque(false);
	}
	
	public void paintComponent(Graphics g) {	
		g.drawImage(bkImage, 0, 0, getWidth(), getHeight(), this);
		super.paintComponent(g);
	}
 
	private void getBkImage() {
		bkImage = (new ImageIcon("Images/menubar.png")).getImage();
	}
}