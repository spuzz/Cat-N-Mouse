package org.GameObjects;

import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Spuz
 */
public class Wall extends Sprite {
    
    public Wall(JPanel drawable) {
        super(drawable);
        this.load("Images/wall5.jpg");
    }
}
