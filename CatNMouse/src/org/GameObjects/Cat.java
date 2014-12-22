

package org.GameObjects;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.AI.GameZone;
import org.AI.TeamState;
/**
 *
 * @author Spuz
 *
 * Main class for the cat agent in Cat n Mouse
 */
public class Cat extends Sprite {
    MapObj map;
    Point2D targetPoint;
    int count;
    private TeamState teamState;
    private GameZone targetZone;

    // constructors
    public Cat(JPanel drawable,MapObj map) {
        super(drawable);
        this.map = map;
        count = 1;
        currentState = 5;
        vision = 12;
        influence = 4;
        this.load("Images/cat.png");
    }

    // getter and setter methods
    
    public void setTeamState(TeamState team) {
        this.teamState = team;
    }

    public TeamState getTeamState() {
        return teamState;
    }

    public void setTargetZone(GameZone tar) {
        targetZone = tar;
    }

    public void setTargetSection(int sec) {
        targetSection = sec;
    }

    public int getTargetSection() {
        return targetSection;
    }

    // Checks all visible points for the cat and if mouse influence is detected
    // update the team state with the position
    public void checkVision() {
        super.checkVision();
        int visionTest = 0;
        Point2D estTarget = new Point2D(0,0);
        for(int a=0;a<visionGrid.getVisable().size();a++) {
            if(map.getMouseTileInfluence(visionGrid.getVisable().get(a)) > visionTest) {
                estTarget = visionGrid.getVisable().get(a);
                visionTest = map.getMouseTileInfluence(visionGrid.getVisable().get(a));
            }
        }
        if(visionTest > 0) {
            teamState.setPosition(estTarget);
        }
    }


    // State machine
    public void updateState() {
        super.updateState();
        switch (currentState) {
            case 1:  chaseMouse();
                     break;
            case 2:  hangBack();
                     break;
            case 3:  cutOff();
                     break;
            case 4:  ambush();
                     break;
            case 5:  wander();
                     break;
            case 6:  search();
                     break;
        }
    }
    
    // Chases map coordinate of mouse, this could be adjusted to go for the next point (add velocity) to increase AI
    public void chaseMouse() {
        Point2D mapPos = new Point2D(map.getMapObjectPos(map.getMouseList().get(0).position().X(), map.getMouseList().get(0).position().Y()).X(),map.getMapObjectPos(map.getMouseList().get(0).position().X(), map.getMouseList().get(0).position().Y()).Y());
        setTarget(new Point2D(mapPos.X()*map.getObjectWidthRatio(),mapPos.Y()*map.getObjectHeightRatio()));
    }

    public void hangBack() {
        // Obselete
    }

    // takes the team target assigned to this cat, if the cat is within a certain distance of the
    // mouse, ignore this target and go for the mouse

    public void cutOff() {
        boolean close = false;
        Point2D catPos = map.getMapPos(position().X(), position().Y());
        Point2D catTarget = map.getMapPos(getTarget().X(), getTarget().Y());
        Point2D mousePos = map.getMapPos(map.getMouseList().get(0).position().X(), map.getMouseList().get(0).position().Y());
        if(calcDistance(catPos, mousePos) < 3) {
            close = true;
        }
        if(close == true) {
            chaseMouse();
        }
    }

    // takes the team target assigned to this cat, if the cat is within a certain distance of the
    // mouse, ignore this target and go for the mouse
    public void ambush() {
        boolean close = false;
        Point2D catPos = map.getMapPos(position().X(), position().Y());
        Point2D mousePos = map.getMapPos(map.getMouseList().get(0).position().X(), map.getMouseList().get(0).position().Y());
        if(calcDistance(catPos, mousePos) < 3) {
            close = true;
        }
        if(close == true) {
            chaseMouse();
        }
    }

    // Takes the target section given by the team state and selects and random tile from this
    // zone to search
    public void wander() {
        // determine all zones in target section
        ArrayList<GameZone> secZones = new ArrayList<GameZone>();
        for(int a=0;a<map.getGameZones().size();a++){
            if(map.getGameZones().get(a).getSection() == targetSection) {
                secZones.add(map.getGameZones().get(a));
            }
        }

        // if cat has reached current target, set a new random target in a random zone
        if(new Rectangle((int)(position().X()),(int)(position().Y()),(int)map.getObjectWidthRatio(), (int)map.getObjectHeightRatio()).contains(target.X(),target.Y())) {
            boolean cheese = false;
            for(int a=0;a<secZones.size();a++) {
                targetZone = secZones.get(a);
            }
            if(cheese == false) {
                targetZone = secZones.get((int)(Math.random()*secZones.size()));
            }
            targetPoint =  targetZone.getMapPoints().get((int)(Math.random()*targetZone.getMapPoints().size()));
            setTarget(new Point2D(targetPoint.X()*3*map.getWidthRatio(),targetPoint.Y()*3*map.getHeightRatio()));
        }
    }

    // searchs given target zone, once searched asks team state for another zone to search
    // if not more zones to search, reset to wander state
    public void search() {
        if(new Rectangle((int)(position().X()),(int)(position().Y()),(int)map.getObjectWidthRatio(), (int)map.getObjectHeightRatio()).contains(target.X(),target.Y())) {
            if(teamState.getSearchTargets().size() > 0) {
                 targetZone = teamState.getSearchTargets().get(0);
                 targetPoint =  targetZone.getMapPoints().get((int)(Math.random()*targetZone.getMapPoints().size()));
                 setTarget(new Point2D(targetPoint.X()*3*map.getWidthRatio(),targetPoint.Y()*3*map.getHeightRatio()));   
                 teamState.getSearchTargets().remove(0);
            }
            else {
                wander();
            }
        }
    }
    // calculates distance using which ever is bigger, x or y
    public double calcDistance(Point2D pos,Point2D loc) {
        double x = Math.abs(pos.X() - loc.X());
        double y = Math.abs(pos.Y() - loc.Y());
        if(x > y) {
            return x;
        }
        else {
            return y;
        }
    }
}
