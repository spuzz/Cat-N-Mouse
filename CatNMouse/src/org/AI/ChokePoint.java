/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.GameObjects.Point2D;

/**
 *
 * @author Spuz
 *
 * Object containing information about choke points in Cat n Mouse map
 */
public class ChokePoint {
    private ArrayList<GameZone> zones;
    private ArrayList<Point2D> points;
    private HashMap<ChokePoint,Astar> pathList;
    public boolean attacked = false;

    // constructors
    public ChokePoint() {;
        points = new ArrayList<Point2D>();
        zones = new ArrayList<GameZone>();
        pathList = new HashMap<ChokePoint,Astar>();
    }

    // Getterss and setters

    public void setZone(GameZone zone) {
        zones.add(zone);
    }

    public void setPoint(Point2D point) {
        points.add(point);
    }

    public void setPath(ChokePoint point,Astar path) {
        if(attacked == true) {
            System.exit(0);
        }
        pathList.put(point, path);
    }

    public ArrayList<GameZone> getZones() {
        return zones;
    }
    
    public ArrayList<Point2D> getPoints() {
        return points;
    }

    public HashMap<ChokePoint,Astar> getPaths() {
        return pathList;
    }

    // determines if this chokepoint connects to given game zone
    public boolean linksTo(GameZone zone) {
        for(int a=0;a<zones.size();a++) {
            if(zones.get(a) == zone) {
                return true;
            }
        }
        return false;
    }

    // determines if a map point is in the choke point
    public boolean contains(Point2D point) {
        for(int a=0;a<points.size();a++) {
            if(points.get(a).compare(point)) {
                return true;
            }
        }
        return false;
    }

    // makes a deep copy of the choke point
    public void copy(ChokePoint prev) {
        Set set = prev.getPaths().keySet();
        Iterator it =  set.iterator();
        while(it.hasNext()) {
            ChokePoint tmpy = (ChokePoint)it.next();
            this.pathList.put(tmpy,prev.getPaths().get(tmpy));
        }

    }


    // print functions, for tezting
    public void printPoints() {
        for(int a=0;a<points.size();a++) {
            System.out.println(points.get(a).X() + " " + points.get(a).Y());
        }
    }

    public void printZones() {
        for(int a=0;a<zones.size();a++) {
            System.out.println(zones.get(a).getID());
        }
    }

    public void printPaths() {
        Set set = pathList.keySet();
        Iterator it =  set.iterator();
        System.out.print("CHOKE TEST");
        while(it.hasNext()) {
            pathList.get(it.next()).pathArr();
        }
    }
}
