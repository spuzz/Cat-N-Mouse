/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import java.util.ArrayList;
import java.util.HashMap;
import org.GameObjects.MapObj;
import org.GameObjects.Point2D;
import org.GameObjects.Sprite;

/**
 *
 * @author as00022
 */
public class TeamState {
    private int state;
    private MapObj map;
    private boolean enemyPosKnown;
    private Point2D EstimatedEnemyPos;
    private ArrayList<GameZone> enemyGameZones;
    private ChokePoint guessedTarget;
    private ChokePoint previousTarget;
    private ArrayList<GameZone> searchTargets;
    private ArrayList<ChokePoint> targetZones;
    private HashMap<ChokePoint,Integer> targetZoneScore;
    private boolean retreating = false;
    private Point2D targetPoint;
    private long runTime;
    private boolean search = true;
    private boolean ambushSet = false;
    private ArrayList<Astar> targetZonePath;


    // constructors
    public TeamState(MapObj map) {
        state = 4;
        this.map = map;
        targetZones = new ArrayList<ChokePoint>();
        targetZoneScore = new HashMap<ChokePoint,Integer>();
        searchTargets = new ArrayList<GameZone>();
        enemyGameZones = new ArrayList<GameZone>();
    }

    // getter and setter methods
    public boolean posknown() {
        return enemyPosKnown;
    }

    public Point2D getEnemyPos() {
        return EstimatedEnemyPos;
    }

    public Point2D getGuessedPos() {
        return targetPoint;
    }

    public ArrayList<GameZone> getSearchTargets() {
        return searchTargets;
    }

    public void lostPosition() {
        enemyPosKnown = false;
    }

    public void resetPos() {
        EstimatedEnemyPos = null;
        enemyPosKnown = false;
    }

    // takes an enemy estimated or known position and determines which game zone it is in
    public void setEnemyZone() {
        if(enemyPosKnown) {
            for(int a=0;a<map.getGameZones().size();a++) {
                if(map.getGameZones().get(a).inZone((int)EstimatedEnemyPos.X()/3,(int)EstimatedEnemyPos.Y()/3)) {
                    enemyGameZones.add(map.getGameZones().get(a));
                }
            }
            // if no zone could be found it must be a choke point, determine which zones it links to
            if(enemyGameZones.size() == 0) {
                for(int a=0;a<map.getChokePoints().size();a++) {
                    if(map.getChokePoints().get(a).contains(new Point2D((int)EstimatedEnemyPos.X()/3,(int)EstimatedEnemyPos.Y()/3))) {
                        for(int z=0;z<map.getChokePoints().get(a).getZones().size();z++) {
                            enemyGameZones.add(map.getChokePoints().get(a).getZones().get(z));
                        }
                    }
                }
            }
        }
    }

    // determine all possible choke points target could go to
    public void estimateEnemyTarget() {
 
        for(int a=0;a<map.getChokePoints().size();a++) {
            for(int z=0;z<enemyGameZones.size();z++) {
                if(map.getChokePoints().get(a).linksTo(enemyGameZones.get(z))) {
                    targetZones.add(map.getChokePoints().get(a));
                }
            }

        }
    }

    // Weight each choke point target
    public void valueTargets(Point2D enemyPosVal) {
        targetZonePath = new ArrayList<Astar>();
        for(int a=0;a<targetZones.size();a++) {
            ChokePoint tmp = targetZones.get(a);
            int score = 0;
            int total = 0;

            // calculate cat influence in path to choke point
            Point2D end = new Point2D(targetZones.get(a).getPoints().get(0).X()*3,targetZones.get(a).getPoints().get(0).Y()*3);
            Astar test = new Astar(map.getFakeSprite(),new Point2D((int)(enemyPosVal.X()/3)*3*map.getWidthRatio(),(int)(enemyPosVal.Y()/3)*3*map.getHeightRatio()),end,map,false,true);
            targetZonePath.add(test);
            for(int b=0;b<test.getPath().size();b++) {
                total += map.getCatTileInfluence(test.getPath().get(b).getMapPos());
            }
            if(total > 0) {
                // divided by 1000 so scores dont overwhelm the rest
                score += total/1000;
            }

            // Take away some score if the mouse is heading in that direction, done for both
            // x and y directions
            double xval = targetZones.get(a).getPoints().get(0).X()*3*map.getWidthRatio() - map.getMouseList().get(0).position().X();
            double yval = targetZones.get(a).getPoints().get(0).Y()*3*map.getHeightRatio()  - map.getMouseList().get(0).position().Y();
            if((map.getMouseList().get(0).getVelX()*xval) > 0) {
                score -= 100;
            }
            if((map.getMouseList().get(0).getVelY()*yval) > 0) {
                score -= 100;
            }
            targetZoneScore.put(targetZones.get(a),score);
        }
    }
    public void calcTarget() {
        int totalCatTargets = 0;
        ArrayList<Integer> cats = new ArrayList<Integer>();
        ArrayList<ChokePoint> zoneList = new ArrayList<ChokePoint>();
        int currentScore = Integer.MAX_VALUE;
        ChokePoint tmpTarget = null;
        // Determine most likely target point
        for(int a=0;a<targetZones.size();a++) {
            if(targetZoneScore.get(targetZones.get(a)) < currentScore) {
                guessedTarget = targetZones.get(a);
                currentScore = targetZoneScore.get(targetZones.get(a));
            }
        }
        targetPoint = new Point2D(guessedTarget.getPoints().get(0).X()*3*map.getWidthRatio(),guessedTarget.getPoints().get(0).Y()*3*map.getHeightRatio());
        Sprite tmpCat = null;
        int catID = 10;
        int currentTargetZone = 0;
        // for each cat
        while(totalCatTargets < map.getCatList().size()) {
            int curDistance = Integer.MAX_VALUE;
            currentScore = Integer.MAX_VALUE;
            // determine lowest scoring target that has not already been taken
            for(int a=0;a<targetZones.size();a++) {
                if(targetZoneScore.get(targetZones.get(a)) < currentScore && !inZoneList(zoneList, targetZones.get(a))) {
                    tmpTarget = targetZones.get(a);
                    currentScore = targetZoneScore.get(targetZones.get(a));
                    currentTargetZone = a;
                }
            }
            // add target to taken list
            zoneList.add(tmpTarget);

            // for all cats excluding ones with targets already, determine closest to target
            for(int b=0;b<map.getCatList().size();b++) {
                double tmp = calcDistance(map.getCatList().get(b).position(),new Point2D(tmpTarget.getPoints().get(0).X()*3*map.getWidthRatio(),tmpTarget.getPoints().get(0).Y()*3*map.getHeightRatio()));
                if(tmp < curDistance && !inList(cats,b)) {
                    curDistance = (int)tmp;
                    tmpCat = map.getCatList().get(b);
                    catID = b;
                    
                }
            }
            // add cat to list of cats with targets
            cats.add(catID);
            
            double currentDistance = -Double.MAX_VALUE;
            int target = -1;
            // calculate the best point to cut off the mouse
            for(int a=0;a<targetZonePath.get(currentTargetZone).getPath().size();a++) {

                    Point2D catPos = new Point2D(tmpCat.position().X()/map.getWidthRatio(),tmpCat.position().Y()/map.getHeightRatio());
                    double catDistance = Math.abs(speedDistance(catPos,targetZonePath.get(currentTargetZone).getPath().get(a).getMapPos()));
                    Point2D mousePos = new Point2D(map.getMouseList().get(0).position().X()/map.getWidthRatio(),map.getMouseList().get(0).position().Y()/map.getHeightRatio());
                    double mouseDistance = Math.abs(speedDistance(mousePos,targetZonePath.get(currentTargetZone).getPath().get(a).getMapPos()));
                    double tmpDistance = catDistance - mouseDistance;
                    // take point cloest point to the cat that will still be able to cut him off
                    // from the target point
                    if(tmpDistance > currentDistance && tmpDistance < 0) {
                        target = a;
                        currentDistance = tmpDistance;
                }
            }
            // if cannot reach target before mouse, go for the choke point
            if(target != -1) {
                tmpCat.setTarget(new Point2D(targetZonePath.get(currentTargetZone).getPath().get(target).getMapPos().X()*map.getWidthRatio(),targetZonePath.get(currentTargetZone).getPath().get(target).getMapPos().Y()*map.getHeightRatio()));
            }
            else {
                tmpCat.setTarget(new Point2D(tmpTarget.getPoints().get(0).X()*3*map.getWidthRatio(),tmpTarget.getPoints().get(0).Y()*3*map.getHeightRatio()));
            }

            totalCatTargets += 1;
        }
    }

    // Method called from cat who detects mouse position
    public void setPosition(Point2D loc) {
        targetZones = new ArrayList<ChokePoint>();
        targetZoneScore = new HashMap<ChokePoint,Integer>();
        enemyGameZones = new ArrayList<GameZone>();
        runTime = System.currentTimeMillis();
        EstimatedEnemyPos = loc;
        enemyPosKnown = true;
        setEnemyZone();
        estimateEnemyTarget();
        valueTargets(EstimatedEnemyPos);
        calcTarget();
    }

    // State machine
    public void updateState() {
        if(enemyPosKnown) {
            long end = System.currentTimeMillis();
            long total = (int)(end -runTime);
            // determines when information is to out dates and starts a search
            if(total > 7000 && total < 20000) {
                //resetPos();
                if(search == true) {
                    search();
                    search = false;
                }
                state = 5;
            }
            // determines if mouse is completely lost, if so go back to wonder
            else if(total > 20000) {
                System.out.println("LOST HIM");
                resetPos();
                search = true;
                state = 4;
            }
            else {
                if(closer() == true) {
                    state = 2;
                }
                else {
                    state = 3;
                }
                search = true;

            }
        }
        else {
            search = true;
            state = 4;
        }
        switch (state) {
            case 1: attack();
                    break;
            case 2: cutOff();
                    break;
            case 3: ambush();
                    break;
            case 4: wander();
                    break;
            case 5: wander();
                    break;
            case 6: lure();
                    break;
        }
        previousTarget = guessedTarget;
    }

    // sets all cats to attack state
    public void attack() {
        for(int a=0;a<map.getCatList().size();a++) {
            map.getCatList().get(a).setState(1);
        }
    }

    // sets all cats to cut off state
    public void cutOff() {
        for(int a=0;a<map.getCatList().size();a++) {
            map.getCatList().get(a).setState(3);
        }
    }

    // recalculates targets based on the mouses targets next game zone rather than current
    public void ambush() {
        if(ambushSet == false || !previousTarget.contains(guessedTarget.getPoints().get(0)) ) {
            GameZone curZone;
            GameZone tarZone = map.getGameZones().get(0);
            targetZones = new ArrayList<ChokePoint>();
            for(int a=0;a<guessedTarget.getZones().size();a++) {
                curZone = guessedTarget.getZones().get(a);
                if(!curZone.inZone((int)(map.getMouseList().get(0).position().X()/(3*map.getWidthRatio())),(int)(map.getMouseList().get(0).position().Y()/(3*map.getHeightRatio())))) {
                    tarZone = curZone;
                }
            }
            for(int a=0;a<map.getChokePoints().size();a++) {
                if(map.getChokePoints().get(a).linksTo(tarZone)) {
                    targetZones.add(map.getChokePoints().get(a));
                }
            }
            valueTargets(new Point2D(guessedTarget.getPoints().get(0).X()*3,guessedTarget.getPoints().get(0).Y()*3));
            calcTarget();
            for(int a=0;a<map.getCatList().size();a++) {
                map.getCatList().get(a).setState(4);
            }
            ambushSet = true;
        }
    }

    // Sets all cats to a wander state and gives them a section to wander in
    public void wander() {
        for(int a=0;a<map.getCatList().size();a++) {
            map.getCatList().get(a).setState(5);
        }
        // for each section determine if more than 1 cat is searching there
        // if it is, give the mouse another random section to explore
        for(int z=0;z<4;z++) {
            int total = 0;
            for(int a=0;a<map.getCatList().size();a++) {
                if(map.getCatList().get(a).getTargetSection() == z) {
                    total += 1;
                    if(total > 1) {
                        boolean gotZone = true;
                        while(gotZone) {
                            gotZone = false;
                            int zone = (int)(Math.random()*4);
                            for(int b=0;b<map.getCatList().size();b++) {
                                if(map.getCatList().get(b).getTargetSection() == zone) {
                                    gotZone = true;
                                }
                            }
                            if(gotZone == false) {

                                map.getCatList().get(a).setTargetSection(zone);
                            }
                       }
                    }
               }
            }
        }
    }

    // set all cats state to search
    public void searching() {
        for(int a=0;a<map.getCatList().size();a++) {
            map.getCatList().get(a).setState(6);
        }
    }
    // sets 1 cats state to attack and the other two to ambush
    public void lure() {
        int mouseSection = 0;
        int catSection = 0;
        Point2D pos = map.getMapPos(map.getMouseList().get(0).position().X(), map.getMouseList().get(0).position().Y());
        for(int z=0;z<map.getGameZones().size();z++) {
            if(map.getGameZones().get(z).inZone((int)pos.X(), (int)pos.Y())) {
                mouseSection = map.getGameZones().get(z).getSection();
                break;
            }
        }
        for(int a=0;a<map.getCatList().size();a++) {
            pos = map.getMapPos(map.getCatList().get(a).position().X(), map.getCatList().get(a).position().Y());
            for(int z=0;z<map.getGameZones().size();z++) {
                if(map.getGameZones().get(z).inZone((int)pos.X(), (int)pos.Y())) {
                    catSection = map.getGameZones().get(z).getSection();
                    break;
                }
            }
        }
    }

    // Takes targets last game zone and searchs all neighbouring zones
    public void search() {
        int rounds = 0;
        int zone;
        for(int a=0;a<guessedTarget.getZones().size();a++) {
            searchTargets.add(guessedTarget.getZones().get(a));
        }
        for(int a=0;a < guessedTarget.getZones().size();a++) {
            for(int b=0;b<guessedTarget.getZones().get(a).chokePoints.size();b++) {
                for(int c=0;c<guessedTarget.getZones().get(a).chokePoints.get(b).getZones().size();c++) {
                    searchTargets.add(guessedTarget.getZones().get(a).chokePoints.get(b).getZones().get(c));
                }
            }
        }
        
        for(int z=0;z<map.getCatList().size();z++) {
            zone = z - (rounds*guessedTarget.getZones().size());
            if(zone >= guessedTarget.getZones().size() - 1) {
                rounds += 1;   
            }
            map.getCatList().get(z).setTargetZone(guessedTarget.getZones().get(zone));
            targetPoint =  guessedTarget.getZones().get(zone).getMapPoints().get((int)(Math.random()*guessedTarget.getZones().get(zone).getMapPoints().size()));
            map.getCatList().get(z).setTarget(new Point2D(targetPoint.X()*3*map.getWidthRatio(),targetPoint.Y()*3*map.getHeightRatio()));   
            map.getCatList().get(z).setTargetSection(guessedTarget.getZones().get(zone).getSection());
        }
        for(int z=0;z<map.getCatList().size();z++) {
            if(searchTargets.size() > 0) {
                searchTargets.remove(0);
            }
        }
        
    }

    // determines if mouse is closer to target exit than cats, if not return true
    public boolean closer() {
        ArrayList<Sprite> test = new ArrayList<Sprite>();
        for(int z=0;z<map.getCatList().size();z++) {
            for(int a=0;a<guessedTarget.getZones().size();a++) {
                if(guessedTarget.getZones().get(a).inZone((int)(map.getCatList().get(z).position().X()/(3*map.getWidthRatio())),(int)(map.getCatList().get(z).position().Y()/(3*map.getWidthRatio())))) {
                    test.add(map.getCatList().get(z));
                    break;
                }
            }
        }
        Point2D tmpPoint = new Point2D(guessedTarget.getPoints().get(0).X()*3*map.getWidthRatio(),guessedTarget.getPoints().get(0).Y()*3*map.getHeightRatio());
        double mouseDistance = calcDistance(map.getMouseList().get(0).position(),tmpPoint);
        for(int z=0;z<test.size();z++) {
            double catDistance = calcDistance(test.get(z).position(),tmpPoint);
            if(catDistance < mouseDistance) {
                return true;
            }   
        }
        return false;
    }

    // determine if cat has already got target
    public boolean inList(ArrayList<Integer> cats,int id) {
        for(int a=0;a<cats.size();a++) {
            if(cats.get(a) == id) {
                return true;
            }
        }
        return false;
    }

    // determine if choke point is in list of already taken targets
    public boolean inZoneList(ArrayList<ChokePoint> targets,ChokePoint point) {
        for(int a=0;a<targets.size();a++) {
            if(point.contains(targets.get(a).getPoints().get(0))) {
                return true;
            }
        }
        return false;
    }

    // calculate quickest direct distance betwween 2 points
    public double calcDistance(Point2D pos,Point2D loc) {
        double x = Math.abs(pos.X() - loc.X());
        double y = Math.abs(pos.Y() - loc.Y());

        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    // calculate pythagoras thereom distance between 2 points
    public double speedDistance(Point2D pos,Point2D loc) {
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
