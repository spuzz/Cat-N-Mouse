/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import java.util.ArrayList;
import org.GameObjects.MapObj;
import org.GameObjects.Point2D;
import org.GameObjects.Sprite;

/**
 *
 * @author Spuz
 *
 * Vision class, takes a grid around the sprite and determines if each point is visable
 */
public class VisGrid {
    private ArrayList<ArrayList<Boolean>> vision;
    private MapObj map;
    private Point2D[][] grid;
    private int width,height;
    private ArrayList<Point2D> visablePoints;

    // constructors
    public VisGrid(int width, int height,MapObj map) {
        this.map = map;
        this.width = width;
        this.height = height;
        makeGrid();
    }

    // getter and setter methods
    public  ArrayList<ArrayList<Boolean>> getVision() {
        return vision;
    }
    public boolean getVisionPoint(int x,int y) {
        for(int a=0;a<visablePoints.size();a++) {
            if(visablePoints.get(a).X() == x && visablePoints.get(a).Y() == y) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Point2D> getVisionPoints() {
        return visablePoints;
    }

    public void resetPointList() {
        visablePoints = new ArrayList<Point2D>();
    }

    public ArrayList<Point2D> getVisable() {
        return visablePoints;
    }

    public void setVisPoint(Point2D point) {
        visablePoints.add(point);
    }

    // Checks to see if there is a wall between the sprite and the given grid point
    public boolean checkWall(Point2D mapLoc,Point2D gridLoc) {
        double max;
        // calculate max distance from sprite
        if(Math.abs(gridLoc.X()) > Math.abs(gridLoc.Y())) {
            max = Math.abs(gridLoc.X());
        }
        else {
            max = Math.abs(gridLoc.Y());
        }
        // determine which direction the point is
        int negativeX = -1;
        int negativeY = -1;
        if(gridLoc.X() < 0) {
            negativeX = 1;
        }
        if(gridLoc.Y() < 0) {
            negativeY = 1;
        }
        int count =  0;
        boolean visWall = false;
        boolean testVisable = false;
        //for every point between sprite and grid point 
        for(int a=0;a<max;a++) {
            //make sure x and y values only goes down to 0 (sprite location)
            double newX = gridLoc.X() + a*negativeX;
            if(negativeX == -1 && newX < 0) {
                newX = 0;
            }
            else if(negativeX == 1 && newX > 0) {
                newX = 0;
            }
            double newY = gridLoc.Y() + a*negativeY;
            if(negativeY == -1 && newY < 0) {
                newY = 0;
            }
            else if(negativeY == 1 && newY > 0) {
                newY = 0;
            }
            // if point is wall set visable to false, however if the only point that is a wall
            // is the grid point itself, reset it to visible as the closest wall should be visable.
            if(map.isTileWall((int)(mapLoc.X())+(int)newX,(int)(mapLoc.Y())+(int)newY)) {
                if(newX == gridLoc.X() && newY == gridLoc.Y()) {
                    visWall = true;
                }
                if(newX == gridLoc.X() && newY == gridLoc.Y() && gridLoc.Y() < -8) {
                    visWall = true;
                }
                testVisable = true;
                // if count is 1 and visable = false then set back to visible
                count += 1;
            }


        }
        if(visWall == true && count <4) {
            testVisable = false;
        }
        return testVisable;
    }

    // create grid containing points for visibility test
    public void makeGrid() {
        vision = new ArrayList<ArrayList<Boolean>>();
        for(int a=0;a<width;a++) {
            vision.add(new ArrayList<Boolean>());
            for(int b=0;b<height;b++) {
               vision.get(a).add(false);
            }
        }
    }
    
    public void update(Sprite spr) {
        spr.getVisionGrid().makeGrid();
        int vis = spr.getVision();
        visablePoints = new ArrayList<Point2D>();
        spr.getVisionGrid().resetPointList();
        Point2D pos = map.getMapPos(spr.getX(),spr.getY());

        // corners of grid for loops
        int Xstart = (int)pos.X() -vis;
        int Xend = (int)pos.X() + vis;
        int Ystart = (int)pos.Y() -vis;
        int Yend = (int)pos.Y() + vis;

        // make sure grid points are not outside of map
        if(Xstart < 0) { Xstart = 0; }
        if(Ystart < 0) { Ystart = 0; }
        if(Xend > vision.size() -1) { Xend = vision.size() -1; }
        if(Yend >  vision.get(0).size() -1) { Yend = vision.get(0).size() -1; }
        // for all grid points
        for(int a=Xstart; a<Xend;a++) {
            for(int b=Ystart; b<Yend;b++) {
                boolean visable;
                if(checkWall(pos,new Point2D(a - pos.X(),b - pos.Y()))) {
                    visable = false;
                }
                else {
                    visable = true;
                }
                // if vision isnt already true set it to new value
                if( vision.get(a).get(b) != true ) {
                    vision.get(a).set(b,visable);
                }
                //add ot 
                visablePoints.add(new Point2D(a,b));
                // update the sprite vision
                spr.getVisionGrid().getVision().get(a).set(b,visable);
                if(visable == true) {
                    spr.getVisionGrid().setVisPoint(new Point2D(a,b));
                }
            }
        }
    }

    // print methods for testing
    public void printGrid() {
        for(int a=0;a<vision.size();a++) {
            for(int b=0;b<vision.get(0).size();b++) {
               System.out.print(" " + vision.get(a).get(b));
            }
            System.out.println();
        }
    }
}
