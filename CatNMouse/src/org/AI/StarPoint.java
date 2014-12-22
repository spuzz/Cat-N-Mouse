/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import org.GameObjects.Point2D;

/**
 *
 * @author Spuz
 *
 * Each individual point of an Astar path list, object to hold information on a
 * point in the path for easy access
 */
public class StarPoint {
    private int fCost = 0;
    private int hCost = 0;
    private int gCost = 0;
    private int cCost = 0;
    private StarPoint parent = null;
    private Point2D mapPos = new Point2D(-1,-1);

    // getter and setter methods
    public void setMapPos(Point2D point) {mapPos = point; }
    public Point2D getMapPos() { return mapPos; }
    
    public StarPoint getParent() { return parent; }
    public void setParent(StarPoint parent) { this.parent = parent; }

    public int getF() { return fCost; }
    public void addF(int f) { this.fCost += f; }
    public void setF(int f) { this.fCost = f; }
    public void calcF() { fCost = gCost + hCost + cCost; }
    
    public int getH() { return hCost; }
    public void addH(int h) { this.hCost += h; }
    public void setH(int h) { this.hCost = h; }
    
    public int getG() { return gCost; }
    public void addG(int g) { this.gCost += g; }
    public void setG(int g) { this.gCost = g; }
    
    public int getC() { return cCost; }
    public void addC(int c) { this.cCost += c; }
    public void setC(int c) { this.cCost = c; }


    // deep copy
    public StarPoint copyStar() {
        StarPoint tmp = new StarPoint();
        tmp.setParent(this.getParent());
        tmp.setMapPos(this.getMapPos());
        tmp.setF(this.getF());
        tmp.setG(this.getG());
        tmp.setH(this.getH());
        return tmp;
    }
}