/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.AI;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.GameObjects.MapObj;
import org.GameObjects.Point2D;
import org.GameObjects.Sprite;
import java.awt.geom.*;

/**
 *
 * @author Spuz
 *
 * Main Pathfinding class for cat and mouse, takes a current point in pixel values and a map tile 
 * point as a target, also 2 booleans, one to determine if the sprite using the A* is a mouse
 * and one to use the mouses speed even if the sprite is not a mouse
 * Returns an ArrayList of StarPoints containing the map points of the path
 */
public class Astar {
    public static int test = 0;
    protected ArrayList<StarPoint> openList;
    protected ArrayList<StarPoint> closedList;
    protected ArrayList<StarPoint> tmpStar;
    protected ArrayList<StarPoint> Apath;
    protected ArrayList<StarPoint> path;
    protected ArrayList<StarPoint> tmp;
    protected ArrayList<Integer> removeList;
    protected Point2D spritePos;
    protected Point2D mapTar;
    protected Point2D mapSprite;
    protected StarPoint curPoint;
    protected StarPoint defaultStar;
    protected MapObj map;
    protected int[] adjX = {-1,-1,-1,0,0,1,1,1};
    protected int[] adjY = {-1,0,1,-1,1,-1,0,1};
    protected Point2D[] velocity = {new Point2D(-4,-4),new Point2D(-4,0),new Point2D(-4,4),new Point2D(0,-4),
            new Point2D(0,4),new Point2D(4,-4),new Point2D(4,0),new Point2D(4,4) };
    protected Map<String, Point2D> pathVel = new HashMap<String,Point2D>();
    protected int curPath = 0;
    protected Sprite spr;
    protected Sprite tmpSpr;
    protected Sprite tmpSpr2;
    protected Point2D curVel = new Point2D(0,0);
    protected boolean mouse = false;
    private StarPoint testPoint = new StarPoint();
    protected int currentPath = 0;
    protected int speedVal;
    protected boolean speed;

    //Constructors

    // default for start up Astar without values
    public Astar(boolean speed) {
        if(speed == true) {
            this.speedVal = 3;
            setVelArr(3,3);
        }
        else {
            this.speedVal = 2;
            setVelArr(2,3);
        }
    }

    // Astar with no sprite defintion
    public Astar(Sprite spr,Point2D spritePos,Point2D tarPos, MapObj map) {
        setVelArr(2,2);
        run(spr,spritePos,tarPos,map);
    }

    // Astar with definition of sprite using the path
    public Astar(Sprite spr,Point2D spritePos,Point2D tarPos, MapObj map,boolean mouse,boolean speed) {
        this.mouse = mouse;
        if(speed == true) {
            this.speedVal = 3;
            setVelArr(3,3);
        }
        else {
            this.speedVal = 2;
            setVelArr(3,3);
        }
        run(spr,spritePos,tarPos,map);
    }      
    
    // Get and set methods
    public void setVelArr(int width, int height) {
        for(int a=0;a<velocity.length;a++) {
            velocity[a] = new Point2D(width,height);
        }
        for(int a=0;a<velocity.length;a++) {
            pathVel.put(Integer.toString(adjX[a]) + Integer.toString(adjY[a]),new Point2D(adjX[a],adjY[a]));
        }
    }
    
    public Point2D getCentre(Point2D pos) {
        double x = pos.X()*map.getWidthRatio() +(map.getWidthRatio()/ 2);
        double y = pos.Y()*map.getHeightRatio() +(map.getHeightRatio()/ 2);
        return (new Point2D(x,y));
    }
    
    public Point2D getVel() {
        return curVel;
    }
    
    public Point2D[] getVelArr() {
        return velocity;
    }
    public Point2D getTarget() {
        return mapTar;
    }
    
    public Point2D getStart() {
        return mapSprite;
    }
    public ArrayList<StarPoint> getPath() {
        return path;
    }


    public void resetCurrentPath() {
        currentPath = 0;
    }

    public int getSpeedVal() {
        return speedVal;
    }


    // Main function
    private void run(Sprite spr,Point2D spritePos,Point2D tarPos, MapObj map)  {
        this.spr = spr;
        this.map = map;
        this.mapTar = tarPos;
        this.spritePos = spritePos;
        tmpSpr = spr.copy();
        tmpSpr2 = spr.copy();
        openList = new ArrayList<StarPoint>();
        closedList = new ArrayList<StarPoint>();
        Apath = new ArrayList<StarPoint>();
        tmpStar = new ArrayList<StarPoint>();
        mapSprite = map.getMapPos(this.spritePos.X(),this.spritePos.Y());
        curPoint = new StarPoint();
        curPoint.setMapPos(mapSprite);
        openList.add(curPoint.copyStar());
        calcPath();
        sortPath();
    }


    /* Sorts the path into first to last order by using each star points parent point starting
    *  from the target point
    */
    public void sortPath() {
        path = new ArrayList<StarPoint>();
        if(Apath.size() > 0) {
            StarPoint tmp = Apath.get(Apath.size() - 1);

            while(tmp.getMapPos().X() != getStart().X() || tmp.getMapPos().Y() != getStart().Y()) {
                path.add(0,tmp);
                tmp = tmp.getParent();
            }
            double x = path.get(0).getMapPos().X() - mapSprite.X();
            double y = path.get(0).getMapPos().Y() - mapSprite.Y();
            curVel = pathVel.get(Integer.toString((int)(x)) + Integer.toString((int)y)); 
            spr.setVelocity(curVel);
        }
    }
    

    /* Loops through the algorithm until the current point is the target point
     */
    public void calcPath() {
        Rectangle r;
        long endTest = 0;
        r = new Rectangle((int)(curPoint.getMapPos().X()*map.getWidthRatio()),(int)(curPoint.getMapPos().Y()*map.getHeightRatio()),(int)map.getWidthRatio(), (int)map.getHeightRatio());
        while(!curPoint.getMapPos().compare(mapTar)) {
            moveClosed();
            long timeTest = System.currentTimeMillis();
            tmpStar = checkAdj(curPoint);
            checkOpen();
            endTest += (System.currentTimeMillis() - timeTest);
            curPoint = getSmallest();
            Apath.add(curPoint);
        }
//        StarPoint newStar = new StarPoint();
//        newStar.setParent(curPoint);
//        newStar.setMapPos(mapTar);
//        Apath.add(newStar);
    }

    // Checks to see the index the current point is in the open list and removes it
    public void moveClosed() {
        closedList.add(curPoint.copyStar());
        int index = 0;
        for(int a=0;a<openList.size();a++) {
            if(curPoint.getMapPos().X() == openList.get(a).getMapPos().X() &&
                   curPoint.getMapPos().Y() == openList.get(a).getMapPos().Y() ) {
                index = a;
            }
        }
        try {
            openList.remove(index);
        }
        catch(Exception e) {
        }
    }

    // First checks current point to determine if it is a wall, then checks surrounding points
    // the wall collision check uses the offline wall collision, returns an
    public ArrayList<StarPoint> checkAdj(StarPoint star) {
        double x = star.getMapPos().X(); 
        double y = star.getMapPos().Y();
        double xPixel = x*map.getWidthRatio();
        double yPixel = y*map.getHeightRatio();
        ArrayList<StarPoint> tmpStar = new ArrayList<StarPoint>();
        for(int a = 0;a<adjX.length;a++) {
            boolean tmp = false;
            boolean openListCheck = false;
            // Check if already in open list
            for(int b=0;b<openList.size();b++) {
                if(new Point2D((int)x + adjX[a],(int)y + adjY[a]).compare(openList.get(b).getMapPos())) {
                    openListCheck = true;
                    break;
                }
                else {
                }
            }
            if(!openListCheck) {
                try {
                    tmpSpr.setPosition(new Point2D((int)xPixel,(int)yPixel));
                    tmpSpr.setVelocity(new Point2D(adjX[a]*map.getWidthRatio(),adjY[a]*map.getHeightRatio()));
                    if(((int)tmpSpr.getY() + (int)tmpSpr.getVelY()) < 0) {continue;};
                    if(((int)tmpSpr.getX() + (int)tmpSpr.getVelX()) < 0) {continue;};
                    // check initial point
                    tmp = map.checkWallCollision(tmpSpr,false);
                    if(tmp != true) {
                        // Check all adjacent 
                        if(a == 0) {
                            tmpSpr.setPosition(new Point2D((int)xPixel,(int)yPixel));
                            tmpSpr.setVelocity(new Point2D(adjX[1]*map.getWidthRatio(),adjY[1]*map.getHeightRatio()));
                            tmpSpr2.setPosition(new Point2D((int)xPixel,(int)yPixel));
                            tmpSpr2.setVelocity(new Point2D(adjX[3]*map.getWidthRatio(),adjY[3]*map.getHeightRatio()));
                            if(map.checkWallCollision(tmpSpr,false)|| map.checkWallCollision(tmpSpr2,false)){
                                continue;
                            }
                        }
                        if(a == 2) {
                            tmpSpr.setPosition(new Point2D((int)xPixel,(int)yPixel));
                            tmpSpr.setVelocity(new Point2D(adjX[1]*map.getWidthRatio(),adjY[1]*map.getHeightRatio()));
                            tmpSpr2.setPosition(new Point2D((int)xPixel,(int)yPixel));
                            tmpSpr2.setVelocity(new Point2D(adjX[4]*map.getWidthRatio(),adjY[4]*map.getHeightRatio()));
                            if(map.checkWallCollision(tmpSpr,false)|| map.checkWallCollision(tmpSpr2,false)){
                                continue;
                            }
                        }
                        if(a == 5) {
                            tmpSpr.setPosition(new Point2D((int)xPixel,(int)yPixel));
                            tmpSpr.setVelocity(new Point2D(adjX[3]*map.getWidthRatio(),adjY[3]*map.getHeightRatio()));
                            tmpSpr2.setPosition(new Point2D((int)xPixel,(int)yPixel));
                            tmpSpr2.setVelocity(new Point2D(adjX[6]*map.getWidthRatio(),adjY[6]*map.getHeightRatio()));
                            if(map.checkWallCollision(tmpSpr,false)|| map.checkWallCollision(tmpSpr2,false)){
                                continue;
                            }
                        }
                        if(a == 7) {
                            tmpSpr.setPosition(new Point2D((int)xPixel,(int)yPixel));
                            tmpSpr.setVelocity(new Point2D(adjX[4]*map.getWidthRatio(),adjY[4]*map.getHeightRatio()));
                            tmpSpr2.setPosition(new Point2D((int)xPixel,(int)yPixel));
                            tmpSpr2.setVelocity(new Point2D(adjX[6]*map.getWidthRatio(),adjY[6]*map.getHeightRatio()));
                            if(map.checkWallCollision(tmpSpr,false)|| map.checkWallCollision(tmpSpr2,false)){
                                continue;
                            }
                        }
                    }
                }
                catch(ArrayIndexOutOfBoundsException ex) {
                    tmp = true;
                }
                catch(IndexOutOfBoundsException ex) {
                    tmp = true;
                }
            }
            // if not a wall, check score and add to array
            if(tmp != true) {
                StarPoint newStar = new StarPoint();
                newStar.setParent(star);
                newStar.setMapPos(new Point2D((int)x + adjX[a],(int)y + adjY[a])); 
                calcVal(newStar,a);
                tmpStar.add(newStar);
            }
        }
        return tmpStar;
    }
    
    public void calcVal(StarPoint star, int index) {
        star.setG(calcG(star,index));
        star.setH(calcH(star.getMapPos()));
        if(mouse == true) {
                if(map.getCatTileInfluence(star.getMapPos()) > 0) {
                }
                star.setC(map.getCatTileInfluence(star.getMapPos()));
        }
        star.calcF();
    }

    // Calculate distance score
    public int calcG(StarPoint star,int index) {
        int g = 0;
        // add previous points score
        if(star.getParent() != null) {
            g = star.getParent().getG();   
        }
        // if diagonal add 14 else add 10
        if(index == 0 || index == 2 || index == 5 || index == 7) {
            g += 14;
        } else {
            g += 10;
        }
        return g;
    }

    // calculate heuristic cost using target position and current position
    public int calcH(Point2D mapPos) {
        int h = 0;
        int x = Math.abs((int)(mapTar.X() - mapPos.X()))*10;
        int y = Math.abs((int)(mapTar.Y() - mapPos.Y()))*10;
        h += (x + y);
        return h;
    }

    // Check open List for each new point, if it is already determine if new path is quicker
    public void checkOpen() {
        long start = System.currentTimeMillis();
        removeList = new ArrayList<Integer>();

        // Check open list first
        for(int a=0;a<tmpStar.size();a++) {
            for(int b=0;b<openList.size();b++) {
                boolean x = tmpStar.get(a).getMapPos().X() == openList.get(b).getMapPos().X();
                boolean y = tmpStar.get(a).getMapPos().Y() == openList.get(b).getMapPos().Y();
                if(x && y) {
                    if(tmpStar.get(a).getF() < openList.get(b).getF()) {

                        openList.remove(b);
                        openList.add(tmpStar.get(a).copyStar());
                    }
                    removeList.add(a);
                }
            }
        }

        tmp = new ArrayList<StarPoint>();
        for(int i=0;i<tmpStar.size();i++) {
            boolean remove = false;
            for(int a=0;a<removeList.size();a++) {
                if(i == removeList.get(a)) {
                    remove = true;
                }
            }
            if(remove == false) {
                tmp.add(tmpStar.get(i));
            }
        }

        // check closed list as well
        tmpStar = tmp;
        removeList = new ArrayList<Integer>();
        for(int a=0;a<tmpStar.size();a++) {
            for(int b=0;b<closedList.size();b++) {
                boolean x = tmpStar.get(a).getMapPos().X() == closedList.get(b).getMapPos().X();
                boolean y = tmpStar.get(a).getMapPos().Y() == closedList.get(b).getMapPos().Y();
                if(x && y) {
                    removeList.add(a);
                }             
            }
        }

        tmp = new ArrayList<StarPoint>();
        for(int i=0;i<tmpStar.size();i++) {
            boolean remove = false;
            for(int a=0;a<removeList.size();a++) {
                if(i == removeList.get(a)) {
                    remove = true;
                }
            }
            if(remove == false) {
                tmp.add(tmpStar.get(i));
            }
        }

        // add remainder left to open list
        tmpStar = tmp;
        for(int a=0;a<tmpStar.size();a++) {
            openList.add(tmpStar.get(a).copyStar());
        }
    }

    // For each point in the open list determine which has smallest score
    public StarPoint getSmallest() {
        testPoint = new StarPoint();
        testPoint.setMapPos(new Point2D(-1,-1));
        for(int a=0; a<openList.size();a++) {
            if(testPoint.getMapPos().X() == -1) {
                testPoint = openList.get(a).copyStar();
            }
            else if(testPoint.getF() > openList.get(a).getF()) {
                testPoint = openList.get(a);
            }
        }
        return testPoint;
    }

    // take current point sprite point and determine which point in the path it is, return velocity
    // depending on speed passed in and direction needed for next point in path.
    public boolean checkVel(boolean found,Point2D spritePosi,int spriteSpeed) {
        this.speedVal = spriteSpeed;
        mapSprite = map.getMapPos(spritePosi.X(),spritePosi.Y());
        // Check if start point is further down a precalculated path and start from there if it is
        if(currentPath == 0) {
            for(int a=0;a<path.size();a++) {
                if(mapSprite.X() == path.get(a).getMapPos().X() && mapSprite.Y() == path.get(a).getMapPos().Y()) {
                    currentPath = a ;
                }
            }
        }

        // if last point in path set velocity to 0
        if(path.size() == currentPath) {
            double x = path.get(currentPath).getMapPos().X()*map.getWidthRatio() - spritePosi.X();
            double y = path.get(currentPath).getMapPos().Y()*map.getHeightRatio() - spritePosi.Y();
            if(Math.abs(x) < speedVal && Math.abs(y) < speedVal) {
                curVel = new Point2D(0,0);
                return false;
            }
        }else {
            double x = path.get(currentPath).getMapPos().X()*map.getWidthRatio() - spritePosi.X();
            double y = path.get(currentPath).getMapPos().Y()*map.getHeightRatio() - spritePosi.Y();
            if(Math.abs(x) < speedVal && Math.abs(y) < speedVal) {
                    currentPath += 1;
                    //path.remove(0);
                    checkVel(true,spritePosi,spriteSpeed);
             }
             if(found == true || currentPath == 0) {
                if(x > 1) {x = 1;}
                if(y > 1) {y = 1;}
                if(x < -1) {x = -1;}
                if(y < -1) {y = -1;}
                if(curVel != pathVel.get(Integer.toString((int)(x)) + Integer.toString((int)y))) {
                    curVel = pathVel.get(Integer.toString((int)(x)) + Integer.toString((int)y));
                }
             }
        }
        return true;
    }

    // printing methods for testing
    public void pathArr() {
        System.out.println("PRINTING PATH");
        for(int a=0;a<path.size();a++) {
            System.out.println(path.get(a).getMapPos().X() + " " + path.get(a).getMapPos().Y());
        }
    }

}
