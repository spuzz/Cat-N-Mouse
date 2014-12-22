package org.GameObjects;

/*********************************************************
 * Base game image class for bitmapped game entities
 **********************************************************/
import java.awt.*;
import java.awt.geom.*;
import java.net.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImageEntity extends BaseGameEntity{
    //variables
    protected Image image;
    protected AffineTransform at;
    protected AffineTransform identity;
    //protected Graphics2D g2d;
    protected JPanel drawable;

    protected Point2D pos;
    protected Point2D vel;
    protected double rotRate;
    protected int width = 0;
    protected int height = 0;
    protected Point2D mapPos;
    
    
    //default constructor
    public ImageEntity(JPanel draw) {
        at = new AffineTransform();
        drawable = draw;
        setImage(null);
        setAlive(true);
        pos = new Point2D(0, 0);
        vel = new Point2D(0, 0);
        mapPos = new Point2D(0, 0);
        rotRate = 0.0;
    }

    public Image getImage() { return image; }

    public void setImage(Image image) {
        this.image = image;
    }

    public int width() {
        if (image != null)
            if(width == 0) {
                return drawable.getWidth();
            }
            else {
                return width;
            }
        else
            return 0;
    }
    public int height() {
        if (image != null)
            if(height == 0) {
                return drawable.getHeight();
            }
            else {
                return height;
            }
        else
            return 0;
    }
    
    public void setWidth(int w) {
        if (image != null)
            width = w;
        else
            width = 0;
    }
    
    public void setHeight(int h) {
        if (image != null)
            height = h;
        else
            height = 0;
    }
    
    public double getCenterX() {
        return getX() + width() / 2;
    }
    public double getCenterY() {
        return getY() + height() / 2;
    }


    public void load(String filename) {
        try {
            image= new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY);
            image=(BufferedImage)ImageIO.read(new File(filename));

        }
        catch(Exception e) {
        }

    }

    public void transform() {
        at.setToIdentity();
        at.translate(pos.X(),pos.Y());
    }

    public void draw(Graphics2D g2) {
        // Draw the background texture
        int w = width();
        int h = height();
        transform();
//        Rectangle imageRect =
//           new Rectangle((int)image.getWidth(),image.getHeight()), w, h);
        g2.drawImage(image,(int)pos.X(),(int)pos.Y(),w,h,drawable);
    }

    //bounding rectangle
    public Rectangle getBounds() {
        Rectangle r;
        r = new Rectangle((int)getX(), (int)getY(), width(), height());
        return r;
    }

}
