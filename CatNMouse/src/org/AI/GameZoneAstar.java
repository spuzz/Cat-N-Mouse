/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Set;
import org.GameObjects.MapObj;
import org.GameObjects.Point2D;
import org.GameObjects.Sprite;

/**
 *
 * @author as00022
 */
public class GameZoneAstar extends Astar {
    private ArrayList<ZoneStarPoint> openList;
    private ArrayList<ZoneStarPoint> closedList;
    private ArrayList<ZoneStarPoint> tmpStar;
    private ArrayList<ZoneStarPoint> Apath;
    private ArrayList<ZoneStarPoint> path;
    private ArrayList<ZoneStarPoint> tmp;
    private ArrayList<GameZone> gameZones;   
    private int gameZoneTarget;
    private int gameZoneStart;
    private ZoneStarPoint curPoint;
    private ZoneStarPoint testPoint = new ZoneStarPoint();

    // Second tier of A* algorithm, for comments on standard methods go to normal A*
    public GameZoneAstar(Sprite spr,Point2D spritePos,Point2D tarPos, MapObj map,boolean mouse) {
        super(mouse);
        this.mouse = mouse;
        speed = mouse;
        gameZones = map.getGameZones();
        run(spr,spritePos,tarPos,map);
    }
    
    private void run(Sprite spr,Point2D spritePos,Point2D tarPos, MapObj map)  {
        this.spr = spr;
        this.map = map;
        this.mapTar = tarPos;
        this.spritePos = spritePos;
        tmpSpr = spr.copy();
        tmpSpr2 = spr.copy();
        openList = new ArrayList<ZoneStarPoint>();
        closedList = new ArrayList<ZoneStarPoint>();
        Apath = new ArrayList<ZoneStarPoint>();
        tmpStar = new ArrayList<ZoneStarPoint>();
        mapSprite = map.getMapPos(this.spritePos.X(),this.spritePos.Y());
        curPoint = new ZoneStarPoint();
        curPoint.setMapPos(mapSprite);
        openList.add(curPoint.copyStar());
        calcPath();

    }


    // same as Astar using different objects
    public void sortPath() {
        path = new ArrayList<ZoneStarPoint>();
        if(Apath.size() > 0) {
            ZoneStarPoint tmp = Apath.get(Apath.size() - 1);

            while(tmp.getMapPos().X() != mapSprite.X() || tmp.getMapPos().Y() != mapSprite.Y()) {
                path.add(0,tmp);
                tmp = tmp.getParent();
            }

        }
    }

    // same as Astar using different objects
    public void moveClosed() {
        closedList.add(curPoint.copyStar());
        int index = 0;
        for(int a=0;a<openList.size();a++) {
            if(curPoint.getMapPos().X() == openList.get(a).getMapPos().X() &&
                curPoint.getMapPos().Y() == openList.get(a).getMapPos().Y() ) {
                index = a;
            }
        }
        try {

            openList.remove(index);
        }
        catch(Exception e) {
        }
    }

    /* calculate initial path using current sprite location. Determines current game zone
     * and then determines targets from choke points the game zone connects to
     */
    public void calcInt() {
        // if game zone could not be determined, it must be a choke point
        if(gameZoneStart == -1) {
            // for each choke point, check if the start point is in it
            for(int a=0;a<map.getChokePoints().size();a++) {
                if(map.getChokePoints().get(a).contains(new Point2D((int)mapSprite.X()/3,(int)mapSprite.Y()/3))) {
                    curPoint.setChoke(map.getChokePoints().get(a));
                    curPoint.setMapPos(mapSprite);
                    return;
                }
                else {
                    gameZoneStart = map.findZone(mapSprite);
                }
            }
        }
        Point2D end;
        ChokePoint point = new ChokePoint();
        point.setPoint(mapSprite);
        // determine choke points that link to start point and create Astar paths to each
        for(int a=0;a<map.getChokePoints().size();a++) {
            if(map.getChokePoints().get(a).linksTo(map.getGameZones().get(gameZoneStart))) {
                long timeTest = System.currentTimeMillis();
                end = new Point2D(map.getChokePoints().get(a).getPoints().get(0).X()*3,map.getChokePoints().get(a).getPoints().get(0).Y()*3);
                Astar newPath = new Astar(map.getFakeSprite(),new Point2D(mapSprite.X()*map.getWidthRatio(),mapSprite.Y()*map.getHeightRatio()),end,map,false,speed);
                point.setPath(map.getChokePoints().get(a), newPath);
                long endTest = System.currentTimeMillis();
            }
        }
        curPoint.setMapPos(mapSprite);
        curPoint.setChoke(point);
    }

    public void calcPath() {
        StarPoint extraPoint;
        Point2D endPoint;
        Point2D startPoint;

        // determine game zone
        gameZoneTarget = map.findZone(mapTar) - 1 ;
        gameZoneStart = map.findZone(mapSprite) - 1;
        ChokePoint targetChoke = new ChokePoint();
        if(gameZoneTarget == -1 ) {
            for(int a=0;a<map.getChokePoints().size();a++) {
                if(map.getChokePoints().get(a).contains(new Point2D((int)mapTar.X()/3,(int)mapTar.Y()/3))) {
                    targetChoke = map.getChokePoints().get(a);
                }
            }
        }
        // if target is in same zone as start, create normal path
        if(gameZoneTarget == gameZoneStart) {
            normPath();
            return;
        }
        // start is a choke point and links to target game zone then create normal path
        if(gameZoneStart == -1) {
            for(int a=0;a<map.getChokePoints().size();a++) {
                if(map.getChokePoints().get(a).contains(new Point2D((int)mapSprite.X()/3,(int)mapSprite.Y()/3))) {
                    if(map.getChokePoints().get(a).linksTo(map.getGameZones().get(gameZoneTarget))) {
                        normPath();
                        return;
                    }
                }
            }
        }
        calcInt();
        boolean loop = false;
        // while curPoint isnt the last target, loop through algorithm
        while(!loop) {
            moveClosed();
            tmpStar = checkAdj(curPoint);
            checkOpen();
            curPoint = getSmallest();
            Apath.add(curPoint);
            if(gameZoneTarget == -1) {
                loop = targetChoke == curPoint.getChoke();
            }
            else {
                loop = curPoint.getChoke().linksTo(map.getGameZones().get(gameZoneTarget));
            }
        }
        // add additional path from last choke point to target tile
        Astar last = new Astar(map.getFakeSprite(),new Point2D(curPoint.getMapPos().X()*map.getWidthRatio(),curPoint.getMapPos().Y()*map.getHeightRatio()),mapTar,map,false,speed);
        ZoneStarPoint tmpEnd = new ZoneStarPoint();
        tmpEnd.setMapPos(mapTar);
        tmpEnd.setPath(last);
        tmpEnd.setParent(curPoint);
        Apath.add(tmpEnd);
        sortPath();

    }

    // checks all choke points that current choke point links to
    public ArrayList<ZoneStarPoint> checkAdj(ZoneStarPoint star) {
        Set set = star.getChoke().getPaths().keySet();
        Iterator it =  set.iterator();
        while(it.hasNext()) {
            ZoneStarPoint newStar = new ZoneStarPoint();
            ChokePoint key = (ChokePoint)it.next();
            newStar.setParent(star);
            newStar.setChoke(key);
            newStar.setMapPos(new Point2D(key.getPoints().get(0).X()*3,key.getPoints().get(0).Y()*3));
            newStar.setPath(star.getChoke().getPaths().get(key));
            calcVal(newStar,star.getChoke().getPaths().get(key));
            tmpStar.add(newStar);
        }
        return tmpStar;
    }

    // Similar to Astar method except using whole paths rather than single points
    public void calcVal(ZoneStarPoint star,Astar path) {
        star.setG(calcG(path,star));
        star.setH(calcH(path));
        if(mouse == true) {
            int total = 0;
            int tmpInt =0;
            for(int b=0;b<path.path.size();b++) {
                if(mapSprite.X() == (int)path.path.get(b).getMapPos().X() && mapSprite.Y() == (int)path.path.get(b).getMapPos().Y()) {
                    tmpInt = b;
                }
            }
            // calculate total cat influence in path
            if(path.path.size() > 0) {
                double x = Math.abs(path.path.get(tmpInt).getMapPos().X() - curPoint.getMapPos().X());
                double y = Math.abs(path.path.get(tmpInt).getMapPos().Y() - curPoint.getMapPos().Y());
                if( x > 0 || y > 0) {
                    Point2D tmpPoint = new Point2D(curPoint.getMapPos().X()*map.getWidthRatio(),curPoint.getMapPos().Y()*map.getHeightRatio());
                    Astar only = new Astar(map.getFakeSprite(),tmpPoint,path.path.get(tmpInt).getMapPos(),map,false,speed);
                    for(int a=tmpInt;a<only.path.size();a++) {
                        if(map.getMouseList().get(0).getVisionGrid().getVision().get((int)(only.path.get(a).getMapPos().X())).get((int)(only.path.get(a).getMapPos().Y()))) {
                            total +=map.getCatTileInfluence(only.path.get(a).getMapPos());
                        }
                    }
                }
            }

            for(int a=tmpInt;a<path.path.size();a++) {
                        for(int b=tmpInt;b<path.path.size();b++) {
                            total +=map.getCatTileInfluence(path.path.get(b).getMapPos());
                        }
                    break;
            }
            star.setC(total);
        }
        star.calcF();
    }

    // similar to Astar except calculate value for each point in path
    public int calcG(Astar path,ZoneStarPoint star) {
        return path.Apath.size()*10 + star.getParent().getG();
    }
    
    // Same again
    public int calcH(Astar path) {
        int h = 0;
            int x = Math.abs((int)(mapTar.X() - (path.Apath.get(path.Apath.size()-1).getMapPos().X())))*10;
            int y = Math.abs((int)(mapTar.Y() - (path.Apath.get(path.Apath.size()-1).getMapPos().Y())))*10;
            h += (x + y);
        return h;
    }

    // same as Astar
    public void checkOpen() {
        removeList = new ArrayList<Integer>();
        for(int a=0;a<tmpStar.size();a++) {
            for(int b=0;b<openList.size();b++) {
                boolean x = tmpStar.get(a).getMapPos().X() == openList.get(b).getMapPos().X();
                boolean y = tmpStar.get(a).getMapPos().Y() == openList.get(b).getMapPos().Y();
                if(x && y) {
                    if(tmpStar.get(a).getF() < openList.get(b).getF()) {

                        openList.remove(b);
                        openList.add(tmpStar.get(a).copyStar());
                    }
                    removeList.add(a);
                }
            }
        }

        tmp = new ArrayList<ZoneStarPoint>();
        for(int i=0;i<tmpStar.size();i++) {
            boolean remove = false;
            for(int a=0;a<removeList.size();a++) {
                if(i == removeList.get(a)) {
                    remove = true;
                }
            }
            if(remove == false) {
                tmp.add(tmpStar.get(i));
            }
        }
        tmpStar = tmp;
        removeList = new ArrayList<Integer>();
        for(int a=0;a<tmpStar.size();a++) {
            for(int b=0;b<closedList.size();b++) {
                boolean x = tmpStar.get(a).getMapPos().X() == closedList.get(b).getMapPos().X();
                boolean y = tmpStar.get(a).getMapPos().Y() == closedList.get(b).getMapPos().Y();
                if(x && y) {
                    removeList.add(a);
                }
            }
        }

        tmp = new ArrayList<ZoneStarPoint>();
        for(int i=0;i<tmpStar.size();i++) {
            boolean remove = false;
            for(int a=0;a<removeList.size();a++) {
                if(i == removeList.get(a)) {
                    remove = true;
                }
            }
            if(remove == false) {
                tmp.add(tmpStar.get(i));
            }
        }
        tmpStar = tmp;
        for(int a=0;a<tmpStar.size();a++) {
            openList.add(tmpStar.get(a).copyStar());
        }
    }

    // Same as Astar with slightly different objects
    public ZoneStarPoint getSmallest() {
        testPoint = new ZoneStarPoint();
        testPoint.setMapPos(new Point2D(-1,-1));
        for(int a=0; a<openList.size();a++) {
            if(testPoint.getMapPos().X() == -1) {
                testPoint = openList.get(a).copyStar();
            }
            else if(testPoint.getF() > openList.get(a).getF()) {
                testPoint = openList.get(a);
            }
        }
        return testPoint;
    }

    // gets next Astar path from GameZoneStar path list
    public ZoneStarPoint getNext() {
        if (path.size() == 0) {
            throw new EmptyStackException();
        }
        if(path.get(0).getPath().currentPath == path.get(0).getPath().path.size()) {
            path.get(0).getPath().currentPath = 0;
            path.remove(0);
        }
        return path.get(0);
    }

    // creates a standard A* path
    public void normPath() {
        path = new ArrayList<ZoneStarPoint>();
        Astar only;
        if(mouse == true) {
            only = new Astar(map.getFakeSprite(),spritePos,mapTar,map,true,speed);
        }
        else {
            only = new Astar(map.getFakeSprite(),spritePos,mapTar,map);
        }
        curPoint = new ZoneStarPoint();
        curPoint.setPath(only);
        curPoint.setMapPos(mapSprite);
        path.add(curPoint);
    }

}
