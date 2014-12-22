package org.GameObjects;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import org.AI.Astar;
import org.AI.GameZoneAstar;
import org.AI.InfluenceMap;
import org.AI.VisGrid;
import org.AI.GameZone;
import org.GameObjects.MapObj;

/* This is the super class for all objects in the game, it contains all the base information
* that all sprites need to function and some addition methods that are overwritten in the
* sub classes
*/
public class Sprite extends ImageEntity {
    protected int currentState;
    protected int vision = 5;
    protected long start, end, total;
    protected boolean runPath = false;
    protected int influence;
    protected Astar pathFind;
    protected GameZoneAstar tier2Path;
    protected Point2D[] velocity = {new Point2D(-5,-5),new Point2D(-5,0),new Point2D(-5,5),new Point2D(0,-5),
            new Point2D(0,5),new Point2D(5,-5),new Point2D(5,0),new Point2D(5,5) };
    protected int[] rotation = {0,45,90,135,180,225,270,315};
    protected VisGrid visionGrid;
    private Map<String, Integer> angle = new HashMap<String,Integer>();
    protected Point2D target = new Point2D(-1,-1);;
    protected boolean targetChange;
    protected GameZone zone;
    protected InfluenceMap catInfluence;
    protected MapObj map;
    protected boolean enemyDetected = false;
    protected boolean mouse = false;
    protected int targetSection;

    //constructor
    public Sprite(JPanel drawable) {
        super(drawable);
        this.setAlive(false);
        currentState = 0;
        targetChange = false;
        for(int a=0;a<velocity.length;a++) {
            angle.put(Integer.toString((int)velocity[a].X()) + Integer.toString((int)velocity[a].Y()),rotation[a]);
        }  
    }

    // getter and setter methods
    
    public VisGrid getVisionGrid() { return visionGrid; }
    public int getVision() { return vision; }
    public Point2D getTarget() { return target; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    
    public GameZone getGameZone() {
        return zone;
    }
    public int getTargetSection() {
        return 0;
    }

    public boolean getEnemyDetected() {
        return enemyDetected;
    }

    public int getInfluence() {
        return influence;
    }

    public boolean getTargetChange() { return targetChange; }
    public double getX() { return pos.X(); }
    public double getY() { return pos.Y(); }
    public double getVelX() { return vel.X(); }
    public double getVelY() { return vel.Y(); }

    public InfluenceMap getInfluenceMap() {
        return catInfluence;
    }


    public void setTargetSection(int sec) {
        targetSection = sec;
    }
    public void setInfluenceMap(InfluenceMap cat) {
        catInfluence = cat;
    }

    public void setInfluence(int inf) {
        this.influence = inf;
    }


    public void setVision(int width, int height,MapObj map) {
        visionGrid = new VisGrid(width,height,map);
    }
    public void setEnemyDetected(boolean result) {
        enemyDetected = result;
    }

    public void setTarget(Point2D tar) {
        if(!this.target.compare(tar)) {
            targetChange = true;
            this.target = tar;
        }
    }
    public void setGameZone(GameZone zone) {
        this.zone = zone;
    }

    public void setVelX(int x) { vel.setX(x); }
    public void setVelY(int y) { vel.setY(y); }
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }

    public int state() { return currentState; }
    public void setState(int state) {
        currentState = state;
    }

    public boolean beenSeen() {
        return true;
    }
    //sprite position
    public Point2D position() { return pos; }
    public void setPosition(Point2D pos) { this.pos = pos; }

    //sprite movement velocity
    public Point2D velocity() { return vel; }
    public void setVelocity(Point2D vel) { this.vel = vel; }

    //returns the center of the sprite as a Point2D
    public Point2D center() {
        return(new Point2D(this.getCenterX(),this.getCenterY()));
    }
    
    //check for collision with a rectangular shape
    public boolean collidesWith(Rectangle rect) {
        return (rect.intersects(getBounds()));
    }
    //check for collision with another sprite
    public boolean collidesWith(Sprite sprite) {
        return (getBounds().intersects(sprite.getBounds()));
    }
    //check for collision with a point
    public boolean collidesWith(Point2D point) {
        return (getBounds().contains(point.X(), point.Y()));
    }

    // load an image for the sprite to appear
    public void load(String filename) {
        super.load(filename);
    }

    //perform affine transformations
    public void transform() {
        this.setX(pos.X());
        this.setY(pos.Y());
        super.transform();
    }

    //draw the image
    public void draw(Graphics2D g2) {
        super.draw(g2);
    }

    //draw bounding rectangle around sprite
    public void drawBounds(Color c,Graphics2D g2) {
        g2.setColor(c);
        g2.draw(getBounds());
    }

    //update the position based on velocity
    public void updatePosition() {
        pos.setX(pos.X() + vel.X());
        pos.setY(pos.Y() + vel.Y());
        if(pos.X() > drawable.getWidth() - width()) {
            vel.setX(0);
            pos.setX(drawable.getWidth() - width());
        }
        else if(pos.X() < 0) {
            vel.setX(0);
            pos.setX(0);
        }
        if(pos.Y() > drawable.getHeight() - height()) {
            vel.setY(0);
            pos.setY(drawable.getHeight() - height());
        }
        else if(pos.Y() < 0) {
            vel.setY(0);
            pos.setY(0);
        }
    }

    // create a new Astar based on the sprites target and its current position
    public void newPath(MapObj map, Point2D targetPixel,boolean mouse) {
        runPath = true;
        this.mouse = mouse;
        start = System.currentTimeMillis();
        // make sure current path node is set to 0
        if(pathFind != null && targetChange == true) {
            pathFind.resetCurrentPath();
        }
        targetChange = false;
        Point2D pathTarget = map.getMapPos(targetPixel.X(),targetPixel.Y());
        Point2D posTest = map.getMapPos(pos.X(),pos.Y());
        // check the target isnt a wall as otherwise the game will crash in an infinite loop
        // as the Astar wont be able to find it
        if(map.isTileWall((int)pathTarget.X(),(int)pathTarget.Y())!= true && !posTest.compare(pathTarget)) {
            tier2Path = new GameZoneAstar(this,pos,pathTarget,map,mouse);
            end = System.currentTimeMillis();
            total = end - start;
        }
        
    }
    
    public void getPath() {
        try {
            // get the next Astar in the two tier Astar path
            pathFind = tier2Path.getNext().getPath();

            // update direction for next path node
            if(mouse == true ) {
                pathFind.checkVel(false,position(),3);
            }
            else {
                pathFind.checkVel(false,position(),2);
            }

            // set sprites new velocity using Astar
            vel.setX(pathFind.getVel().X()*tier2Path.getSpeedVal());
            vel.setY(pathFind.getVel().Y()*tier2Path.getSpeedVal());
        }
        catch(Exception e) {
            vel.setX(0);
            vel.setY(0);
        }
    }

    // deep copy
    public Sprite copy() {
        Sprite spr = new Sprite(drawable);
        spr.setPosition(pos);
        spr.setVelocity(vel);
        spr.width = (int)this.getWidth();
        spr.height = (int)this.getHeight();
        spr.catInfluence = this.catInfluence;
        //spr.load("mouse.png");
        return spr;
    }
    
    // overwrite methods
    public void updateState() {

    }

    public void checkVision() {

    }

    public void setMap(MapObj map) {

    }

    public void setTargetZone(GameZone tar) {
        
    }
}

