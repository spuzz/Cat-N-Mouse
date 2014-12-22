package org.yourorghere;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.GameObjects.ImageEntity;

/**
 *
 * @author Spuz
 */
public class backGround extends ImageEntity {
    private BufferedImage bgImage;

    
    backGround(JPanel draw) {
        super(draw);
        load("fog.jpg");
        width = drawable.getWidth();
        height = drawable.getHeight();
    }
    public int width() {
        if (image != null)
            return width;
        else
            return 0;
    }
    
    public int height() {
        if (image != null)
            return height;
        else
            return 0;
    }
    
    public void draw(Graphics2D g2) {
        super.draw(g2);
    }
    
}
