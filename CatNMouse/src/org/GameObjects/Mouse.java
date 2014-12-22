/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.GameObjects;

import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.AI.Astar;
import org.AI.ChokePoint;
import org.AI.GameZone;

/**
 *
 * @author Spuz
 *
 * Main class for the mouse agent in Cat n Mouse
 */
public class Mouse extends Sprite {
    private MapObj map;
    private ArrayList<GameZone> visited;
    private ArrayList<GameZone> currentZones;
    private GameZone middleZone;
    private ChokePoint startChoke = null;
    private long timeTest;;

    // constructors
    public Mouse(JPanel drawable,MapObj map) {
        super(drawable);
        this.map = map;
        influence = 4;
        vision = 12;
        currentState = 1;
        setVisited();
        this.load("Images/mouse.png");
    }

    // getter and setter methods
    public void setVisited() {
        visited = new ArrayList<GameZone>();
    }

    // state machine
    public void updateState() {
        // enemy detected, retreat for 20 seconds then try going back to getting cheese
        if(enemyDetected && currentState != 2) {
            timeTest = System.currentTimeMillis();
            currentState = 2;
        }
        if(currentState == 2) {
            long endTest = System.currentTimeMillis();
            if((int)(endTest - timeTest) > 20000) {
                enemyDetected = false;
                currentState = 1;
            }
        }

        super.updateState();
        switch (currentState) {
            case 1:  getCheese();
                     break;
            case 2:  retreat();
                     break;
        }
    }

    // This state happens everytime a cat is detected, it will take the easiest exits out
    // of its current zone and will not return to that zone unless out of options
    public void retreat() {
        startChoke = null;
        currentZones = new ArrayList<GameZone>();
        Point2D curPos = map.getMapPos(pos.X(), pos.Y());
        // determine current game zone
        for(int a=0;a<map.getGameZones().size();a++) {
            if(map.getGameZones().get(a).inZone((int)curPos.X()/3, (int)curPos.Y()/3)) {
                currentZones.add(map.getGameZones().get(a));
            }
        }
        // if no game zones found then current point must be a choke point
        if(currentZones.size() == 0) {
            for(int a=0;a<map.getChokePoints().size();a++) {   
                if(map.getChokePoints().get(a).contains(new Point2D((int)curPos.X()/3, (int)curPos.Y()/3))) {
                    startChoke = map.getChokePoints().get(a);
                    for(int z=0;z<map.getChokePoints().get(a).getZones().size();z++) {
                        currentZones.add(map.getChokePoints().get(a).getZones().get(z));
                    }
                }
            }
        }
        // if mouse is in the middle of a game zome, keep track of it so it can
        // be removed from possibilities when the mouse moves out of this zone
        if(currentZones.size() == 1) {
            middleZone = currentZones.get(0);
        }
        setTarget(calcExit(currentZones));
    }

    // Gets the closest cheese on the map
    public void getCheese() {
        double distance = 2000000000;
        Point2D curTarget = new Point2D(0,0);
        for(int a=0;a<map.getCheeseList().size();a++) {
            double test = calcDistance(map.getCheeseList().get(a).position());
            if(test < distance) {
                distance = test;
                curTarget = map.getCheeseList().get(a).position();
            }
        }
        setTarget(curTarget);
    }

    // calculates distance between 2 points using pythagoras
    public double calcDistance(Point2D loc) {
        double x = Math.abs(pos.X() - loc.X());
        double y = Math.abs(pos.Y() - loc.Y());
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    // Main method for the retreat state to determine which exit to take
    public Point2D calcExit(ArrayList<GameZone> start) {
        ArrayList<ChokePoint> targets = new ArrayList<ChokePoint>();
        // if the mouse reaches a choke point, remove the previous zone from possibilities
        // by adding it to an array that will stop it being used
        if(currentZones.size() > 1) {
            boolean alreadyIn = false;
            for(int c=0;c<visited.size();c++) {
                if(visited.get(c) == middleZone) {
                    alreadyIn = true;
                }
            }
            if(alreadyIn == false) {
                visited.add(middleZone);
            }
        }

        // for each possible target choke point
        int curTotal = Integer.MAX_VALUE;
        for(int z=0;z<start.size();z++) {
            for(int a=0;a<map.getChokePoints().size();a++) {
                boolean check = true;
                // check it isnt the current choke point, if it is ignore it
                try {
                    if(map.getChokePoints().get(a).contains(startChoke.getPoints().get(0))) {
                        check = false;
                    }
                }
                catch(NullPointerException e) {

                }
                // check which choke point connects to the target
                if(map.getChokePoints().get(a).linksTo(start.get(z)) && check) {
                    int total = 0;
                    // if it has not been visited before calculate the total cat influence between
                    // the mouses current location and the target
                    if(!checkVisited(map.getChokePoints().get(a))) {
                        Point2D end = new Point2D(map.getChokePoints().get(a).getPoints().get(0).X()*3,map.getChokePoints().get(a).getPoints().get(0).Y()*3);
                        Astar test = new Astar(map.getFakeSprite(),pos,end,map,false,true);
                        for(int b=0;b<test.getPath().size();b++) {
                            total += map.getCatTileInfluence(test.getPath().get(b).getMapPos());
                        }
                        if(total < curTotal) {
                            targets = new ArrayList<ChokePoint>();
                            targets.add(map.getChokePoints().get(a));
                            curTotal = total;
                        }
                        else if(total == curTotal) {
                            targets.add(map.getChokePoints().get(a));
                        }
                    }
                }
            }
        }

        // if the current target is the same as any of the new targets then keep the same one
        for(int a=0;a<targets.size();a++) {
            Point2D targetPixel = new Point2D(targets.get(a).getPoints().get(0).X()*3*map.getWidthRatio(),targets.get(a).getPoints().get(0).Y()*3*map.getHeightRatio());
            if(targetPixel.compare(target)) {
                return target;
            }
        }
        Point2D targetPixel;
        Point2D estPixel;
        int targetZone = 0;
        // if there are no targets left to go to then reset the visted list and do it again
        if(targets.size() == 0) {
            setVisited();
            return calcExit(start);
        }
        else {
            // get the target with the lowest score and set that as the cats destination
            double max = Integer.MAX_VALUE;
            for(int a=0;a<targets.size();a++) {
                estPixel = new Point2D(targets.get(a).getPoints().get(0).X()*3*map.getWidthRatio(),targets.get(a).getPoints().get(0).Y()*3*map.getHeightRatio());
                double check = calcDistance(estPixel);
                if(check < max) {
                    targetZone = a;
                    max = check;
                }
            }
            targetPixel = new Point2D(targets.get(targetZone).getPoints().get(0).X()*3*map.getWidthRatio(),targets.get(targetZone).getPoints().get(0).Y()*3*map.getHeightRatio());
            return targetPixel;
        }

    }

    // checks visited list to see if a choke point is already there
    public boolean checkVisited(ChokePoint tar) {
        for(int b=0;b<visited.size();b++) {
            if(tar.linksTo(visited.get(b))) {
                return true;
            }
        }
        return false;
    }
}
