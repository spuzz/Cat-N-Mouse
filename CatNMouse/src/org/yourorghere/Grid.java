/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yourorghere;

import java.util.ArrayList;
import org.GameObjects.Point2D;

/**
 *
 * @author Spuz
 */
public abstract class Grid {
    
    public static ArrayList<Point2D> createGrid(int size) {
        ArrayList<Point2D> grid = new ArrayList<Point2D>();
        for(int a=0;a<( size*2+1);a++) {
            for(int b=0;b<( size*2+1);b++) {
                grid.add(new Point2D(( -size) + a,(-size) + b));
            }
        }
        return grid;
    }
}
