/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.GameObjects.Point2D;

/**
 *
 * @author Spuz
 *
 * Contains information about game zone areas (large open areas) in Cat n Mouse maps
 */
public class GameZone {
    ArrayList<Point2D> mapPoints;
    ArrayList<ChokePoint> chokePoints;
    private int gameZoneID;
    boolean cheese;
    private HashMap<Integer,Integer> connList;
    private int mapSection;


    // Constructors

    public GameZone(int id,ArrayList<ChokePoint> chokePoints) {
        mapPoints = new ArrayList<Point2D>();
        this.chokePoints = chokePoints;
        setID(id);
        cheese = false;
        connList = new HashMap<Integer,Integer>();
    }


    // Getters and setters
    public void giveCheese() {
        cheese = true;
    }
    
    public boolean hasCheese() {
        return cheese;
    }

    public void removeCheese() {
        cheese = false;
    }
    
    public void setID(int id) {
        gameZoneID = id;
    }

    public int getID() {
        return gameZoneID;
    }

    public void setConn(int x,int y) {
        connList.put(x, y);
    }
    
    public void setPoint(int x,int y) {
        mapPoints.add(new Point2D(x,y));
    }

    public ArrayList<Point2D> getMapPoints() {
        return mapPoints;
    }

    public int getSection() {
        return mapSection;
    }

    // Determine if a map point is in the zone
    public boolean inZone(int x, int y) {
        for(int a=0;a<mapPoints.size();a++) {
            if(mapPoints.get(a).X() == x && mapPoints.get(a).Y() == y) {
                return true;
            }
        }
        return false;
    }

    // determines if a map point is inside a choke point
    public boolean inAnyChoke(int x, int y) {
        for(int a=0;a<chokePoints.size();a++) {
            if(chokePoints.get(a).contains(new Point2D(x,y))) {
                return true;
            }
        }
        return false;
    }


    // Expansive search of image determining all points inside zone
    public void expand(int zone,int zoney, WritableRaster raster) {
        int rowStart,rowEnd,colStart,colEnd;
        boolean foundNew = true;
        int curChoke = 0;
        // keep looping till no more points are found
        while(foundNew == true) {
            foundNew = false;

            // for all map points currently in zone
            for(int a=0;a<mapPoints.size();a++) {
                rowStart = (int)mapPoints.get(a).X()-1;
                rowEnd = (int)mapPoints.get(a).X()+1;
                colStart = (int)mapPoints.get(a).Y()-1;
                colEnd = (int)mapPoints.get(a).Y()+1;
                // for all adjacant tiles 
                for (int x = rowStart; x <= rowEnd; x++) {
                    for (int y = colStart; y <= colEnd; y++) {
                        // miss centre (current point)
                        if(x != mapPoints.get(a).X() || y != mapPoints.get(a).Y()) {
                            // if point is red add and not in zone, add it
                            if(raster.getSample(x*2,y*2,0) == 255) {
                                if(!inZone(x,y)) {
                                    setPoint(x, y);
                                    foundNew = true;
                                }
                            }
                            /* if point is blue and not in a choke point, create new
                             * choke point and perform expansive search
                             */
                            else if(raster.getSample(x*2,y*2,2) == 255) {
                                if(!inAnyChoke(x,y)) {
                                    ChokePoint tmp = new ChokePoint();
                                    tmp.setPoint(new Point2D(x,y));
                                    expandChoke(x, y,raster,tmp);
                                    chokePoints.add(tmp);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Expansive search of image determining all points inside choke point
    public void expandChoke(int zone,int zoney, WritableRaster raster,ChokePoint tmp) {
        int rowStart,rowEnd,colStart,colEnd;
        boolean foundNew = true;
        // keep looping till no more points are found
        while(foundNew == true) {
            foundNew = false;
             // for all map points currently in choke point
            for(int a=0;a<tmp.getPoints().size();a++) {
                rowStart = (int)tmp.getPoints().get(a).X()-1;
                rowEnd = (int)tmp.getPoints().get(a).X()+1;
                colStart = (int)tmp.getPoints().get(a).Y()-1;
                colEnd = (int)tmp.getPoints().get(a).Y()+1;
                 // for all adjacant tiles 
                for (int x = rowStart; x <= rowEnd; x++) {
                    for (int y = colStart; y <= colEnd; y++) {
                        // miss centre (current point)
                        if(x != tmp.getPoints().get(a).X() || y != tmp.getPoints().get(a).Y()) {
                            /* if point is blue and not in a choke point, create new
                            * choke point and perform expansive search
                            */
                           if(raster.getSample(x*2,y*2,2) == 255) {
                                //inZone = false;
                                if(!tmp.contains(new Point2D(x,y))) {
                                    tmp.setPoint(new Point2D(x,y));
                                    foundNew = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Calculates which section (of 4 which the map is split into) the zones majority is in
    public void determineSection(int mapHeight,int mapWidth) {
        int[] sections = {0,0,0,0};
        int widthSec = 0;
        int heightSec = 0;
        /* for each map point determine which corner section it is in
         * and add one to the appropriate array value
         */
        for(int a=0;a<getMapPoints().size();a++) {
            Point2D point = getMapPoints().get(a);
            if(point.X() > mapWidth/2) {
                widthSec = 1;
            }
            if(point.Y() > mapHeight/2) {
                heightSec = 2;
            }
            if(heightSec == 0) {
                if(widthSec == 0) {
                    sections[0] += 1;
                }
                else {
                    sections[1] += 1;
                }
            }
            else {
                if(widthSec == 0) {
                    sections[2] += 1;
                }
                else {
                    sections[3] += 1;
                }
            }
        }
        int tmp = 0;
        // whichever section has the highest value in the array is the section
        for(int a=0;a<sections.length;a++) {
            if(sections[a] > tmp) {
                mapSection = a;
                tmp = sections[a];
            }
        }
    }

    // print functions for testing
    public void printZones() {
        for(int a=0;a<mapPoints.size();a++) {
            System.out.println(mapPoints.get(a).X() + " " + mapPoints.get(a).Y());
        }
    }


    public void printConns() {
        Iterator keys = connList.keySet().iterator();
        while ( keys.hasNext() ) {
            Object key = keys.next();
            System.out.println( key + " " + connList.get(key));
        }
    }
}
