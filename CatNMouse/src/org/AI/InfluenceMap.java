/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import java.util.ArrayList;
import org.GameObjects.MapObj;
import org.GameObjects.Point2D;
import org.GameObjects.Sprite;
import org.yourorghere.Grid;
import org.AI.Astar;
/**
 *
 * @author Spuz
 *
 * Creates a grid the size of the map, holds information on cats total influence on tiles
 * across the map
 */
public class InfluenceMap {
    protected int[][] mapInfluence;
    protected int[][] Iarray;
    protected boolean first = true;
    ArrayList<Sprite> sprites;
    ArrayList<ArrayList<Point2D>> spriteIgrid;
    protected MapObj map;
    protected int[] adjX = {-1,-1,-1,0,0,1,1,1};
    protected int[] adjY = {-1,0,1,-1,1,-1,0,1};


    // Constructor
    public <T> InfluenceMap(ArrayList<Sprite> sprites,MapObj map) {
        this.map = map;
        this.sprites = sprites;
        spriteIgrid = new ArrayList<ArrayList<Point2D>>();
        mapInfluence = new int[map.getTileList().size()][map.getTileList().get(0).size()];
        for(int a=0;a<sprites.size();a++) {
            spriteIgrid.add(Grid.createGrid(sprites.get(a).getInfluence()));
        }
        update();
//        for(int a=0;a<map.getMapList().get(0).size();a++) {
//            if(first == true) {
//                for(int b=0;b<map.getMapList().size();b++) {
//                    mapInfluence.add(new ArrayList<Double>());
//                }
//                first = false;              
//            }
//            for(int b=0;b<map.getMapList().size();b++) {
//                mapInfluence.get(b).add(0.0);
//            }
//        }
    }

    // getter and setter methods

    public int getTileInfluence(Point2D target,boolean aquire) {
        return mapInfluence[(int)target.X()][(int)target.Y()];
    }


    // reset map to default
    public void resetMap() {
        for(int i=0;i<mapInfluence[0].length;i++) {
            for(int j=0;j<mapInfluence.length;j++) {
                mapInfluence[j][i] = 0;
            }
        }
    }

    // Update influence map for new frame
    public void update() {
        for(int i=0;i<mapInfluence[0].length;i++) {
            for(int j=0;j<mapInfluence.length;j++) {
                mapInfluence[j][i] = 0;
            }
        }
        // for all sprites for current influence map, create a grid for their influence

        for(int a=0;a<sprites.size();a++) {
            Point2D mapLoc = map.getMapPos(sprites.get(a).position().X(),sprites.get(a).position().Y());
            ArrayList<Point2D> tmpGrid = spriteIgrid.get(a);

            // for each coordinate in grid, determine influence
            for(int b=0;b<tmpGrid.size();b++) {
                try {
                    int influence = mapInfluence[(int)(mapLoc.X())+(int)(tmpGrid.get(b).X())][(int)(mapLoc.Y())+(int)tmpGrid.get(b).Y()]; 
                    if(checkWallInf(mapLoc,tmpGrid.get(b))) {
                        influence = 0;
                    }
                    else {

                        influence = calcInfluence(tmpGrid.get(b),influence,sprites.get(a).getInfluence());
                    }
                    mapInfluence[(int)(mapLoc.X())+(int)(tmpGrid.get(b).X())][(int)(mapLoc.Y())+(int)tmpGrid.get(b).Y()] = influence;
                }
                catch(IndexOutOfBoundsException ex) {
                    //System.out.print("ERROR");
                }
            }
        }
    }

    // total influence based on distance from sprite location
    public int calcInfluence(Point2D gridLoc,int influence,int totalInf) {
        int Influence;
        if(Math.abs(gridLoc.X()) > Math.abs(gridLoc.Y())) {
            totalInf = totalInf + 1 - (int)Math.abs(gridLoc.X());
        }
        else {
            totalInf = totalInf + 1 - (int)Math.abs(gridLoc.Y());           
        }
        Influence = (int)(Math.pow(30.5,totalInf)*10);
        //int yInfluence = (int)(Math.pow(1.5,)*10);
        influence = (Influence);
        return influence;
    }

    /* Function to determine if point is a wall or blocked by a wall
     * For longer description see visGrid
     */
    public boolean checkWallInf(Point2D mapLoc,Point2D gridLoc) {
        double max;
        if(Math.abs(gridLoc.X()) > Math.abs(gridLoc.Y())) {
            max = Math.abs(gridLoc.X());
        }
        else {
            max = Math.abs(gridLoc.Y());
        }
        int negativeX = -1;
        int negativeY = -1;
        if(gridLoc.X() < 0) {
            negativeX = 1;
        }
        if(gridLoc.Y() < 0) {
            negativeY = 1;
        }
        for(int a=0;a<max;a++) {
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
            if(map.isTileWall((int)(mapLoc.X())+(int)newX,(int)(mapLoc.Y())+(int)newY)) {
                return true;
            }
            
        }
        return false;
    }


    // Deep copy
    public InfluenceMap copy() {
        InfluenceMap tmpInfluence = new InfluenceMap(sprites,map);
        for(int i=0;i<mapInfluence[0].length;i++) {
            for(int j=0;j<mapInfluence.length;j++) {
                tmpInfluence.mapInfluence[j][i] = this.mapInfluence[j][i];
            }
        }
        return tmpInfluence;
    }

    // Print functions for testing
    public void printMap() {
        for(int i=0;i<mapInfluence[0].length;i++) {
            for(int j=0;j<mapInfluence.length;j++) {
              //  if(mapInfluence[i][j] > 0) {
                System.out.print(mapInfluence[j][i] + " ");
                //}
            }
            System.out.println();
        }
        System.out.println();
    }
}
