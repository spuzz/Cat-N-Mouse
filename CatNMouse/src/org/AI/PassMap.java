/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.GameObjects.MapObj;
import org.GameObjects.Point2D;
import org.GameObjects.Sprite;

/**
 *
 * @author Spuz
 *
 * Main class for determining the passability map, image analysis
 */
public class PassMap {
    BufferedImage bimage;
    private ArrayList<ArrayList<Double>> mapList;
    private BufferedReader reader;
    private boolean first = true;
    private int pixExpand = 1;
    private ArrayList<GameZone> gameZones;
    private ArrayList<ChokePoint> chokePoints;

    // constructors
    public PassMap() {
        loadMap("Images/map.txt");
        calcBaseImage();
        expand(false);
        expand(true);
        colourImage(0,"red");
        saveImage();
        calcGameZones();
        calcGameZoneConn();
    }

    // getter and setter methods
    public ArrayList<ChokePoint> getChokePoints() {
        return chokePoints;
    }

    public ArrayList<GameZone> getGameZones() {
        return gameZones;
    }

    // simple load of the map text tile for analysis
    public void loadMap(String file) {
        // array containing all the tile information
        mapList = new ArrayList<ArrayList<Double>>();
        try {
            reader = new BufferedReader(new FileReader(file));
                    String line = null; //not declared within while loop

            while (( line = reader.readLine()) != null){
                if(first == true) {
                    for(int a=0;a<line.length();a++) {
                        mapList.add(new ArrayList<Double>());
                    }
                    first = false;
                }
                for(int a=0;a<line.length();a++) {
                    int val = Integer.parseInt(Character.toString(line.charAt(a)));
                    mapList.get(a).add((double)val);
                }
            System.out.println();
            }
        }  catch(IOException ex) {
            System.out.print("ERROR IN MAP LOAD");
        }
    }

    // calculates the base colour image for the passablity map, settings walls to black and
    // empty space to white
    public void calcBaseImage() {
        // make image two pixels per tile
        bimage = new BufferedImage(mapList.size()*2, mapList.get(0).size()*2, BufferedImage.TYPE_INT_RGB);
        int w = mapList.size(); //assume that they all have the same dimensions
        int h = mapList.get(0).size();
        float red;
        float green;
        float blue;
        for (int a = 0; a < w; a++) {
           for (int b = 0; b < h; b++) {
             if(mapList.get(a).get(b) == 0) {
                red = 255;
                green = 255;
                blue = 255;
             }
             else {
                red = 0;
                green = 0;
                blue = 0;
             }
             for(int x=a*2;x<a*2 + 2;x++) {
                 int y = b*2;
                 bimage.getRaster().setSample(x,y, 0, red);
                 bimage.getRaster().setSample(x,y, 1, green);
                 bimage.getRaster().setSample(x,y, 2, blue);
                 bimage.getRaster().setSample(x,y + 1, 0, red);
                 bimage.getRaster().setSample(x,y + 1, 1, green);
                 bimage.getRaster().setSample(x,y + 1, 2, blue);
             }
           }
        }

    }

    // expands the current selection (walls or empty space) by 1 pixel
    public void expand(boolean invert) {
        int[][] raster = getRasterArray();
        int w = raster.length;
        int h = raster[0].length;
        int rowStart,rowEnd,colStart,colEnd,red,green,blue,condition;
        // determines which pixels to expand 
        if(invert == false) {
            condition = 1;
        }
        else {
            condition = 0;
        }

        boolean wall;
        // for every adjacant pixel
        for (int a = 0; a < w; a++) {
            for (int b = 0; b < h; b++) {
                rowStart = a-pixExpand;
                rowEnd = a+pixExpand;
                colStart = b-pixExpand;
                colEnd = b+pixExpand;
                if(rowStart < 0) { rowStart = 0; }
                if(rowEnd > w-1) { rowEnd = w-1; }
                if(colStart < 0) { colStart = 0; }
                if(colEnd > h-1) { colEnd = h-1; }
                wall = false;
                if(raster[a][b] == condition) {
                    if(invert == false) {
                        red = 0;
                        green = 0;
                        blue = 0;
                    }
                    else {
                        red = 255;
                        green = 255;
                        blue = 255;
                    }
                    bimage.getRaster().setSample(a,b, 0, red);
                    bimage.getRaster().setSample(a,b, 1, green);
                    bimage.getRaster().setSample(a,b, 2, blue);
                }
                else {
                    if(invert == false) {
                        red = 0;
                        green = 0;
                        blue = 255;
                    }
                    else {
                        red = 255;
                        green = 255;
                        blue = 255;
                    }
                    for (int x = rowStart; x <= rowEnd; x++) {
                        for (int y = colStart; y <= colEnd; y++) {
                            if(raster[x][y] == condition) {
                                bimage.getRaster().setSample(a,b, 0, red);
                                bimage.getRaster().setSample(a,b, 1, green);
                                bimage.getRaster().setSample(a,b, 2, blue);
                                wall = true;
                                break;
                            }
                        }
                        if(wall == true) {
                            break;
                        }
                    }
                }
            }
        }
    }

    // Method that takes a tile tpye (wall/empty space) and colours it given colour
    public void colourImage(int condition, String colour) {
        int[][] raster = getRasterArray();
        int w = raster.length;
        int h = raster[0].length;
        int red,green,blue;
        if(colour.equals("red")) {
            red = 255;
            green = 0;
            blue = 0;
        }
        else if(colour.equals("green")) {
            red = 0;
            green = 255;
            blue = 0;
        }
        else {
            red = 0;
            green = 0;
            blue = 255;
        }
        for (int a = 0; a < w; a++) {
            for (int b = 0; b < h; b++) {
                if(raster[a][b] == condition) {
                    red = 255;
                    green = 0;
                    blue = 0;
                    bimage.getRaster().setSample(a,b, 0, red);
                    bimage.getRaster().setSample(a,b, 1, green);
                    bimage.getRaster().setSample(a,b, 2, blue);
                }
            }
        }
    }

    // returns image pixel 2D array
    public int[][] getRasterArray() {
        int[][] imageRaster = new int[bimage.getWidth()][bimage.getHeight()];
        WritableRaster raster = bimage.getRaster();
        int w = raster.getWidth();
        int h = raster.getHeight();
        for (int a = 0; a < w; a++) {
            for (int b = 0; b < h; b++) {
                if(raster.getSample(a, b, 0) == 0) {
                   imageRaster[a][b] = 1;
                }
                else {
                   imageRaster[a][b] = 0;
                }
            }
        }
        return imageRaster;
    }

    // Save the current image into a file for us in the main program
    public void saveImage() {
        try {
            File outputfile = new File("saved.png");
            ImageIO.write(bimage, "png", outputfile);
        }
        catch(IOException e) {
        }
    }
    // for each tile in saved image, determine if it is red and create a game zone using it
    // the game zone then uses its own methods to calculate all its tiles.
    public void calcGameZones() {
        boolean inZone;
        chokePoints = new ArrayList<ChokePoint>();
        gameZones = new ArrayList<GameZone>();
        WritableRaster raster = bimage.getRaster();
        for(int zone=0;zone<mapList.size();zone++) {
            for(int zoney=0;zoney<mapList.get(0).size();zoney++) {
                if(raster.getSample(zone*2,zoney*2,0) == 255) {
                    inZone = false;
                    // check if tile is already in a game zone
                    for(int a=0;a<gameZones.size();a++) {
                        if(gameZones.get(a).inZone(zone, zoney)) {
                            inZone = true;
                        }
                    }
                    if(inZone == false) {
                        GameZone newZone = new GameZone(gameZones.size() + 1,chokePoints);
                        newZone.setPoint(zone, zoney);
                        newZone.expand(zone,zoney,raster);
                        gameZones.add(newZone);
                    }
                }
            }
        }
    }

    // Determine which game zones all the choke point connect to using another expansive search
    public void calcGameZoneConn() {
        WritableRaster raster = bimage.getRaster();
        for(int c=0;c<chokePoints.size();c++) {
            int rowStart,rowEnd,colStart,colEnd;
            ChokePoint tmp = chokePoints.get(c);
            boolean foundNew = true;
            // until no more tiles are found
            while(foundNew == true) {
                foundNew = false;
                for(int a=0;a<tmp.getPoints().size();a++) {
                    rowStart = (int)tmp.getPoints().get(a).X()-1;
                    rowEnd = (int)tmp.getPoints().get(a).X()+1;
                    colStart = (int)tmp.getPoints().get(a).Y()-1;
                    colEnd = (int)tmp.getPoints().get(a).Y()+1;
                    // for all adjacant
                    for (int x = rowStart; x <= rowEnd; x++) {
                        for (int y = colStart; y <= colEnd; y++) {
                            if(x != tmp.getPoints().get(a).X() || y != tmp.getPoints().get(a).Y()) {
                               // if adjacant tile is red, determine which game zone it is and add it to
                               // links if not already in
                               if(raster.getSample(x*2,y*2,0) == 255) {
                                   for(int z=0;z<gameZones.size();z++) {
                                       if(gameZones.get(z).inZone(x, y)) {
                                           if(!(tmp.linksTo(gameZones.get(z)))) {
                                               tmp.setZone(gameZones.get(z));
                                               foundNew = true;
                                           }
                                       }
                                   }

                                }
                            }
                        }
                    }
                }
            }
        }

    }

    // calculate all paths between game zones
    public void calcPaths(Sprite spr,MapObj map) {
        Point2D start;
        Point2D end;
        Sprite tmp = spr.copy();
        // for each choke point, determine all the zones it connects to
        for(int a=0;a<chokePoints.size();a++) {
            for(int b=0;b<chokePoints.get(a).getZones().size();b++) {
                ArrayList<Astar> tmpPath = new ArrayList<Astar>();
                start = new Point2D(chokePoints.get(a).getPoints().get(0).X()*map.getObjectWidthRatio(),chokePoints.get(a).getPoints().get(0).Y()*map.getObjectHeightRatio());
                tmp.setPosition(new Point2D(chokePoints.get(a).getPoints().get(0).X()*3,chokePoints.get(a).getPoints().get(0).Y()*3));
                // for each zone the choke connects calcualate a path to all of that zones chokes
                for(int c=0;c<chokePoints.size();c++) {
                    if(c != a && chokePoints.get(c).linksTo(chokePoints.get(a).getZones().get(b))) {
                        end = new Point2D(chokePoints.get(c).getPoints().get(0).X()*3,chokePoints.get(c).getPoints().get(0).Y()*3);
                        Astar path = new Astar(tmp,start,end,map);
                        chokePoints.get(a).setPath(chokePoints.get(c), path);
                    }
                }
            }
        }
    }

    // Helper method to determine if two choke points are the same
    public boolean isSame(ArrayList<Point2D> choke1, ArrayList<Point2D> choke2) {
        for(int a=0;a<choke1.size();a++) {
            boolean same = false;
            for(int b=0;b<choke2.size();b++) {
                if(choke1.get(a).compare(choke2.get(b))) {
                    same = true;
                    break;
                }
            }
            if(same == false) {
                return false;
            }
        }
        return true;
    }

    // As name implies
    public void calcSections(int width,int height) {
        for(int a=0;a<gameZones.size();a++) {
            gameZones.get(a).determineSection(height, width);
        }
    }


    // print methods for testing
    public void printChokes() {
        System.out.println("Array Size " + chokePoints.size());
        for(int a=0;a<chokePoints.size();a++) {
            System.out.println("Choke Point " + a);
            chokePoints.get(a).printPoints();
        }
    }

    public void printZones() {
        System.out.println("Array Size " + chokePoints.size());
        for(int a=0;a<gameZones.size();a++) {
            System.out.println("GameZone " + a);
            gameZones.get(a).printZones();
        }
    }

    public void printPaths() {
        System.out.println("Array Size " + chokePoints.size());
        for(int a=0;a<chokePoints.size();a++) {
            System.out.println("Choke Point " + a);
            chokePoints.get(a).printPaths();
        }
    }
}