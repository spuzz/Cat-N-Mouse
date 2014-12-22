/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JMenu;

/**
 *
 * @author Spuz
 */
public class createMenu extends JMenu {
    int pos;
    public createMenu(String name,int pos) {
        super(name);
        this.setForeground(Color.red);
        this.setOpaque(false);
        this.pos = pos;
    }
    
    protected void paintComponent(Graphics g)
    {
            if(getParent() == null)
                    super.paintComponent(g);
            else
            {
                    //calculation is required... to find out exact x, y position
                    g.drawString(getText(), pos, 13);
            }
    }

    protected void paintBorder(Graphics g)
    {
            if(getParent() == null)
                    super.paintBorder(g);
    }
}
