/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

/**
 *
 * @author Spuz
 *
 * Each individual point of an Astar path list, object to hold information on a
 * point in the path for easy access
 */
public class ZoneStarPoint extends StarPoint{
    private ChokePoint location;
    private ZoneStarPoint parent;
    private Astar path;


    // getter and setter methods
    public ChokePoint getChoke() {
        return location;
    }

    public void setChoke(ChokePoint point) {
        location = point;
    }

    public void setPath(Astar p) {
        path = p;
    }

    public Astar getPath() {
        return path;
    }

    public ZoneStarPoint getParent() { return parent; }
    public void setParent(ZoneStarPoint parent) { this.parent = parent; }
    
    // deep copy
    public ZoneStarPoint copyStar() {
        ZoneStarPoint tmp = new ZoneStarPoint();
        tmp.setParent(this.getParent());
        tmp.setMapPos(this.getMapPos());
        tmp.setF(this.getF());
        tmp.setG(this.getG());
        tmp.setH(this.getH());
        tmp.setChoke(this.getChoke());
        tmp.setPath(this.path);
        return tmp;
    }

}
