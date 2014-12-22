/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.GameObjects;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JPanel;
import org.AI.ChokePoint;
import org.AI.GameZone;
import org.AI.InfluenceMap;
import org.AI.MainInfluenceMap;
import org.AI.VisGrid;
import org.gui.Minimap;

/**
 *
 * @author Spuz
 *
 * Main data class for every frame of the animation, contains all data relating to
 * the sprites and objects in the game as well as data gaiend from offline methods
 * Most important class for AI to use as it contains everything needed.
 */
public class MapObj {
    private ArrayList<ArrayList<Double>> mapList;
    private ArrayList<ArrayList<Double>> tileList;
    private BufferedReader reader;
    private boolean first = true; 
    private double width = 1;
    private double height = 1;
    private double objectWidth = 1;
    private double objectHeight = 1;
    private double heightRatio;
    private double widthRatio;
    private double objectHeightRatio;
    private double objectWidthRatio;
    private int totalWalls;
    private int totalCheese = 0;
    private BufferedImage image;
    private Minimap mini;
    private InfluenceMap catInfluence;
    private InfluenceMap mouseInfluence;
    private ArrayList<Wall> walls; 
    private ArrayList<Sprite> cats;
    private ArrayList<Sprite> mouse;
    private HashMap wallMap;
    private ArrayList<GameZone> gameZones;
    private ArrayList<ChokePoint> chokePoints;
    private Cat fake;
    private VisGrid visionGrid;
    private ArrayList<Cheese> cheeseList;
    private HashMap<String,Boolean> wallTest;


    // constructors
    public MapObj(String file) {
        wallTest = new HashMap<String,Boolean>();
        mapList = new ArrayList<ArrayList<Double>>();
        tileList = new ArrayList<ArrayList<Double>>();
        // load map text file into 1 array  with 1 value for each point for the map coords
        // and 1 array with 3 values for each point for the movement tiles
        try {
            reader = new BufferedReader(new FileReader(file));  
                    String line = null; //not declared within while loop
                    
            while (( line = reader.readLine()) != null){
                if(first == true) {
                    for(int a=0;a<line.length();a++) {
                        mapList.add(new ArrayList<Double>());
                        for(int b=0;b<3;b++) {
                            tileList.add(new ArrayList<Double>());
                        }
                    }
                    first = false;              
                }
                for(int a=0;a<line.length();a++) {
                    int val = Integer.parseInt(Character.toString(line.charAt(a)));
                    mapList.get(a).add((double)val);
                    for(int b=0;b<3;b++) {
                        for(int c=0;c<3;c++) {
                            tileList.get(a*3+b).add((double)val);
                        }
                    }
                }
            System.out.println();
            }
        }  catch(IOException ex) {
            System.out.print("ERROR IN MAP LOAD");
        }
        setObjectWidth(mapList.size());
        setObjectHeight(mapList.get(0).size());
        setWidth(tileList.size());
        setHeight(tileList.get(0).size());
        visionGrid = new VisGrid(tileList.size(),tileList.get(0).size(),this);
    }


    // getter and setter methods
    
    public void setGameZones(ArrayList<GameZone> zones) {
        gameZones = zones;
    }

    public void setMiniMap(Minimap mini) {
        this.mini = mini;
    }
    public void setMouse(ArrayList<Sprite> mouse) {
        this.mouse = mouse;
    }

    public void setWalls(ArrayList<Wall> walls) {
        this.walls = walls;
    }

    public void setWallMap(HashMap walls) {
        this.wallMap = walls;
    }

    public void setWidth(int size) {
        width = size;
    }

    public void setHeight(int size) {
        height = size;
    }

    public void setObjectWidth(int size) {
        objectWidth = size;
    }

    public void setObjectHeight(int size) {
        objectHeight = size;
    }

    public void setChokePoints(ArrayList<ChokePoint> points) {
        chokePoints = points;
    }

    public ArrayList<ChokePoint> getChokePoints() {
        ArrayList<ChokePoint> tmp = new ArrayList<ChokePoint>();
        for(int a=0;a<chokePoints.size();a++) {
            ChokePoint tmpChoke = new ChokePoint();
            tmpChoke.copy(chokePoints.get(a));
            tmp.add(chokePoints.get(a));
        }
        return tmp;
    }

    public ArrayList<Cheese> getCheeseList() {
        return cheeseList;
    }

    public ArrayList<Sprite> getCatList() {
        return cats;
    }

    public ArrayList<Sprite> getMouseList() {
        return mouse;
    }

    public InfluenceMap getCatInfluence() {
        return catInfluence;
    }
    
    public Point2D getMapPos(double x,double y) {
        Point2D sprite = new Point2D((int)Math.round(x/widthRatio),(int)Math.round(y/heightRatio));
        return sprite;
    }

    public Point2D getMapObjectPos(double x,double y) {
        Point2D sprite = new Point2D((int)(x/objectWidthRatio),(int)(y/objectHeightRatio));
        return sprite;
    }


    public ArrayList<GameZone> getGameZones() {
        return gameZones;
    }
    public int getCatTileInfluence(Point2D target) {
        return catInfluence.getTileInfluence(target,false);
    }

    public InfluenceMap getMouseInfluence() {
        return mouseInfluence;
    }

    public int getMouseTileInfluence(Point2D target) {
        return mouseInfluence.getTileInfluence(target,true);
    }

    public double getObjectWidthRatio() {
        return objectWidthRatio;
    }

    public double getObjectHeightRatio() {
        return objectHeightRatio;
    }

    public double getWidthRatio() {
        return widthRatio;
    }

    public double getHeightRatio() {
        return heightRatio;
    }

    public VisGrid getVisGrid() {
        return visionGrid;
    }

    public void setFakeSprite(JPanel draw) {
        fake = new Cat(draw,this);
        fake.setHeight((int)12);
        fake.setWidth((int)12);
    }

    public Sprite getFakeSprite() {
        return fake;
    }

    public ArrayList<ArrayList<Double>> getMapList() {
        return mapList;
    }

    public ArrayList<ArrayList<Double>> getTileList() {
        return tileList;
    }
    public void incWalls() {
        totalWalls += 1;
    }

    public int getTotalWalls() {
        return totalWalls;
    }

    public boolean isWall(int x,int y) {
        if(mapList.get(x).get(y) == 1) {
            return true;
        }else {
            return false;
        }
    }

    public boolean isTileWall(int x,int y) {
        if(tileList.get(x).get(y) == 1) {
            return true;
        }else {
            return false;
        }
    }
    public void incCheese() {
        totalCheese += 1;
    }

    public void decCheese() {
        totalCheese -= 1;
    }

    public int cheese() {
        return totalCheese;
    }

    public void setCheeseList(ArrayList<Cheese> cheese) {
        cheeseList = cheese;
    }


    public int findZone(Point2D point) {
        for(int a=0;a<gameZones.size();a++) {
            if(gameZones.get(a).inZone((int)point.X()/3,(int)point.Y()/3)) {
                return gameZones.get(a).getID();
            }
        }
        return 0;
    }

    // updates the main visionGrid with the latest information
    public void updateVisionGrid() {
        visionGrid.makeGrid();
        for(int a=0;a<mouse.size();a++ ) {
            visionGrid.update(mouse.get(a));
        }
        for(int a=0;a<cats.size();a++ ) {
            visionGrid.update(cats.get(a));
        }
    }

    // updates the mini map with the latest information
    public void updateMiniMap(boolean player) {
        image = mini.getImage();
        int tmp;
        int[][] imageRaster = new int[image.getWidth()][image.getHeight()];
        for(int a=0;a<mapList.size();a++) {
            for(int b=0;b<mapList.get(0).size();b++) {
                boolean booltmp = true;
                // determines whether a point on the minimap will be, fog, wall or green
                if(player == true) {
                    if(!mouse.get(0).getVisionGrid().getVision().get(a*3).get(b*3)) {
                        tmp = 2;
                    }
                    else if(mapList.get(a).get(b) == 1) {
                        tmp = 1;
                    }
                    else {
                        tmp = 0;
                    }
                }
                else {
                    if(!getVisGrid().getVision().get(a*3).get(b*3)) {
                        tmp = 2;
                    }
                    else if(mapList.get(a).get(b) == 1) {
                        tmp = 1;
                    }
                    else {
                        tmp = 0;
                    }
                }

                // set a 6x6 grid for each tile on the mini map
                for(int x=a*6;x<a*6 +6;x++) {
                    int y = b*6;
                    imageRaster[x][y] = tmp;
                    imageRaster[x][y + 1] = tmp;
                    imageRaster[x][y + 2] = tmp;
                    imageRaster[x][y + 3] = tmp;
                    imageRaster[x][y + 4] = tmp;
                    imageRaster[x][y + 5] = tmp;
                }
            }
        }
        // update the gui
        mini.updateMap(imageRaster,catInfluence,mouseInfluence);
    }

    public void gameZonePath(Sprite tmp) {
        for(int a=0;a<gameZones.size();a++) {
            //gameZones.get(a).setUpPaths(this, tmp);
        }
    }

    // Sets up the influence maps for both the cats and the mice on startup
    public void setLists(ArrayList<Sprite> cats,ArrayList<Sprite> mouse) {
        this.cats = cats;
        this.mouse = mouse;
        catInfluence = new MainInfluenceMap(cats,this,this.mouse);
        for(int a=0;a<cats.size();a++) {
            ArrayList<Sprite> tmp = new ArrayList<Sprite>();
            tmp.add(cats.get(a));
            cats.get(a).setInfluenceMap(new InfluenceMap(tmp,this));
        }
        mouseInfluence = new InfluenceMap(mouse,this);
        for(int a=0;a<mouse.size();a++) {
            ArrayList<Sprite> tmp = new ArrayList<Sprite>();
            tmp.add(mouse.get(a));
            mouse.get(a).setInfluenceMap(new InfluenceMap(tmp,this));
        }
    }
    

    // for each tile it will calculate whether there is a wall collision. this is done
    // before the game begins
    public void setUpWallColl() {
        for(int a=0;a<tileList.size();a++) {
            for(int b=0;b<tileList.get(0).size();b++) {
                Sprite tmp = getFakeSprite();
                tmp.setPosition(new Point2D(a*getWidthRatio(),b*getHeightRatio()));
                if(setWallCollision(tmp,false)) {
                    String tmpPoint = Integer.toString(a) + " " +  Integer.toString(b);
                    wallTest.put(tmpPoint, true);
                }
                else {
                    String tmpPoint = Integer.toString(a) + " " +  Integer.toString(b);
                    wallTest.put(tmpPoint, false);
                }
            }
        }

    }

    // takes a sprite and determines if its next move would be a collision using the offline
    // hash map created above.
    public boolean checkWallCollision(Sprite catTest,boolean test) {
        Point2D catPoint = getMapPos(catTest.getX() + (int)catTest.getVelX(), catTest.getY() + (int)catTest.getVelY());
        String tmpPoint = Integer.toString((int)catPoint.X()) + " " + Integer.toString((int)catPoint.Y());
        try{
            if(wallTest.get(tmpPoint) != null) {
                if(wallTest.get(tmpPoint) == true) {
                    if(test == true) {
                    }
                    return true;
                }
                else {
                    if(test == true) {
                    }
                    return false;
                }
            }
            System.out.println("SHOULDNT HAPPEN");
            return false;
        }
        catch(NullPointerException e) {
            System.out.print("BREAK");
            return false;
        }
    }

    /* This is called for every tile, each tile then has a rectangle for a sprite
    * checked against every adjacant square as well as itself. If any of these are a
    * wall this point is permenetatly set as a wall for the game and the sprite
    * cannot go into it
    */
    public boolean setWallCollision(Sprite cat,boolean test) {
        long start,end,total;
        start = System.currentTimeMillis();
        boolean tmp = false;
        int grid = 1;
        String tmpPoint;
        int y = (int)cat.getY() + (int)cat.getVelY();
        int x = (int)(cat.getX() + (int)cat.getVelX());
        Point2D loc = getMapObjectPos(x, y);
        double xmax = loc.X() + 1;
        double ymax = loc.Y() + 1;
        double xmin = loc.X() - 1;
        double ymin = loc.Y() - 1;
        Point2D locTest = getMapPos(x, y);
        // for every adjacant point and itself
        for(double i=xmin; i<=xmax;i++) {
            for(double j=ymin; j<=ymax; j++) {
                tmpPoint = Double.toString(i) + Double.toString(j);
                try {
                    Wall point = (Wall)wallMap.get(tmpPoint);
                    Rectangle rect = new Rectangle(x,y,18,18);
                    tmp = rect.intersects(point.getBounds());
                    if(tmp == true) {
                        return true;
                    }
                }
                catch(Exception e) {
                }
            }
        }
        end = System.currentTimeMillis();
        total = end - start;
        return tmp;
    }

    /* does the same as the above collision detection except that it is done on a
    * pixel value. This is only ever called on a normal collision detection, once
    * per sprite in a frame as it is to computationally expensive to be used in the
    * Astar
    */
    public boolean playerWallCollision(Sprite cat) {
        long start,end,total;
        start = System.currentTimeMillis();
        boolean tmp = false;
        int grid = 1;
        String tmpPoint;
        int y = (int)cat.getY() + (int)cat.getVelY();
        int x = (int)(cat.getX() + (int)cat.getVelX());
        Point2D loc = getMapObjectPos(x, y);
        double xmax = loc.X() + 1;
        double ymax = loc.Y() + 1;
        double xmin = loc.X() - 1;
        double ymin = loc.Y() - 1;
        Point2D locTest = getMapPos(x, y);
        for(double i=xmin; i<=xmax;i++) {
            for(double j=ymin; j<=ymax; j++) {
                tmpPoint = Double.toString(i) + Double.toString(j);
                try {
                    Wall point = (Wall)wallMap.get(tmpPoint);
                    Rectangle rect = new Rectangle(x,y,12,12);
                    tmp = rect.intersects(point.getBounds());
                    if(tmp == true) {
                        return true;
                    }
                }
                catch(Exception e) {
                }
            }
        }
        end = System.currentTimeMillis();
        total = end - start;
        return tmp;
    }

    // sets up the dimensions for the tiles on the screen
    public void adjustRatio(JPanel drawable) {
        if(Toolkit.getDefaultToolkit().getScreenSize().height > 1000) {
            widthRatio = 8;
            heightRatio = 8;
            objectWidthRatio = 24;
            objectHeightRatio =24;
        }
        else {
            widthRatio = 6;
            heightRatio = 6;
            objectWidthRatio = 18;
            objectHeightRatio =18;
        }
        System.out.println("RATIOS " + objectWidthRatio + " " + objectHeightRatio + " " + widthRatio + " " + heightRatio );
    }
    

    // Test function to make sure values calculate before game start dont change
    public void checkChange() {
        for(int a=0;a<getChokePoints().size();a++) {
            Set set = getChokePoints().get(a).getPaths().keySet();
            Iterator it =  set.iterator();
            while(it.hasNext()) {
                ChokePoint key = (ChokePoint)it.next();
                if(getChokePoints().get(a).getPaths().get(key).getPath().size() == 0) {
                    System.out.println("CHANGED");
                }
            }
        }
    }
}
