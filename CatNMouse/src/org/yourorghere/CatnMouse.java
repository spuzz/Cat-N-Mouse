package org.yourorghere;

import org.GameObjects.Point2D;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import org.AI.GameZone;
import org.AI.PassMap;
import org.AI.TeamState;
import org.GameObjects.Mouse;
import org.GameObjects.Cat;
import org.GameObjects.Cheese;
import org.GameObjects.Grass;
import org.GameObjects.MapObj;
import org.GameObjects.Sprite;
import org.GameObjects.Wall;
import org.gui.scoreboard;

/**
 * CatnMouse.java <BR>
 *
 * Swing JFrame containing graphics and animation of the Cat n Mouse game
**/

public class CatnMouse extends JPanel implements Runnable,MouseListener,KeyListener {
    
    private int width,height,clickX,clickY;
    private float catX,catY,moveX,moveY;
    private long startTime;
    private long runTime;
    private backGround backG;
    private Cat cat;
    private Mouse mouse;
    private ArrayList<Wall> walls;
    private Wall wall;
    private Grass grass;
    private ArrayList<Cheese> cheeses;
    private ArrayList<Grass> grassList;
    private ArrayList<Sprite> cats,mice;
    private MapObj map;
    private boolean collision = false;
    private boolean runPath = true;
    private boolean clicked = false;
    private boolean start = true;
    private Thread animator = null;
    private boolean ingame = true;
    private int count;
    private scoreboard board;
    private PassMap pmap;
    private boolean mouseVision = false;
    private boolean catVision = false;
    private ArrayList<ArrayList<Boolean>> visGrid;
    private ArrayList<Point2D> visPoints;
    private HashMap wallMap;
    private TeamState team;
    private Graphics2D g2;
    private boolean paused = false;
    private boolean player = true;
    private int mouseSpeed = 3;
    private long totalRuntime;
    private int scenario = 0;


    // constructor

    public CatnMouse() {
        this.addMouseListener(this);
        this.addKeyListener(this);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if(dim.height > 1000) {
            setMaximumSize(new Dimension(768,624));
            setMinimumSize(new Dimension(768,624));
            setPreferredSize(new Dimension(768,624));
        }
        else {
            setMaximumSize(new Dimension(576,468));
            setMinimumSize(new Dimension(576,468));
            setPreferredSize(new Dimension(576,468));
        }
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.DARK_GRAY);
        // Setup the drawing area and shading mode
    }

    public void pause() {
            paused = true;
        }

    public void setPlayer(boolean bool) {
        player = bool;
        if(bool == true) {
            mice.get(0).setVelX(0);
            mice.get(0).setVelY(0);
        }
    }

    public void setScenario(int sce) {
        scenario = sce;
    }

    public void setMouseVision(boolean bool) {
        mouseVision = bool;
    }

    public void setCatVision(boolean bool) {
        catVision = bool;
    }
    
    public void setScoreBoard(scoreboard board) {
        this.board = board;
    }

    // sets the main animation going
    public void run() {
        //waiting(1000);
        while (true)  {
            try {
                //animator.sleep(1000 / 2);  // delay and yield to other threads
            }
            catch(Exception e) {

            }
            repaint();
        }

    }

    // called on start up of the class, calls all the pre animation AI classes
    // and sets up all the sprites and objects ready to be drawn and updated
    public void init() {
       
        // create passability map
        pmap = new PassMap();
        height = this.getHeight();
        width = this.getWidth();
        backG = new backGround(this);
        
        // create main map object
        map = new MapObj("Images/map.txt");
        map.adjustRatio(this);
        map.setMiniMap(board.getMiniMap());
        map.setGameZones(pmap.getGameZones());
        map.setFakeSprite(this);
        
        // create non moving sprite arrays
        walls = new ArrayList<Wall>();
        grassList = new ArrayList<Grass>();
        cheeses = new ArrayList<Cheese>();
        wallMap = new HashMap();
        backG.setWidth((int)(map.getTileList().size()*(map.getWidthRatio())));
        backG.setHeight((int)(map.getTileList().get(0).size()*(map.getHeightRatio())));

        // sets up the cheese
        checkCheese();

        // set up the walls and grass tiles
        for(int i=0;i<map.getMapList().size();i++) {
            for(int j=0;j<map.getMapList().get(i).size();j++) {
                if(map.isWall(i,j)) {
                    setUpWall(i,j);
                }
                else {
                    setUpGrass(i,j);
                }
            }
        }
        map.setWalls(walls);
        map.setWallMap(wallMap);
        map.setUpWallColl();
        // calculate passability map paths and map sections
        pmap.calcPaths(map.getFakeSprite(),map);
        pmap.calcSections(map.getMapList().size(),map.getMapList().get(0).size());
        map.setChokePoints(pmap.getChokePoints());

        // create moving sprite arrays and team state for cats
        mice = new ArrayList<Sprite>();
        team = new TeamState(map);
        cats = new ArrayList<Sprite>();

        // Set up Moving sprites according to scenario selected
        // Standard
        switch(scenario) {
            case 0:
                setUpMouse(7, 6);
                setUpCat(28, 6);
                setUpCat(27, 15);
                setUpCat(2, 14);
                break;
            case 1:
                setUpMouse(13, 24);
                setUpCat(8, 23);
                setUpCat(12, 16);
                setUpCat(2, 14);
              break;
            case 2:
                setUpMouse(20, 14);
                setUpCat(25, 19);
                setUpCat(18, 18);
                setUpCat(20, 11);
                break;
        }

        // set up vision and influence in map
        mice.get(0).setVision(map.getTileList().size(),map.getTileList().get(0).size(),map);
        map.setLists(cats,mice);
        map.setCheeseList(cheeses);

        // begin the animation
        if (animator == null) {
            animator = new Thread(this);
            animator.start();
        }
        count = 0;
        System.out.println("FINISHED INIT");
        startTime = System.currentTimeMillis();
        paused = true;
    }

    public void paint(Graphics g) {
        if(start == true) {
            //waiting(3);
            init();
            start = false;
        }
        // if it from a player perspective only show mouse vision
        if(player == true) {
            mouseVision = true;
        }
        runTime = System.currentTimeMillis();
        super.paint(g);
        checkCheese();
        // update the java graphics
        g2 = updateGraphics(g);
        // update the main vision grid
        map.updateVisionGrid();

        // if game running update the cats and mouse otherwise show appropriate message on screen
        if(ingame == false) {
            g2.setPaint(Color.white);
            g2.setFont(new Font("Purisa", Font.PLAIN, 16));
            g2.drawString("GAME OVER", 340, 300);
        }
        else if(paused == true) {
            g2.setPaint(Color.white);
            g2.setFont(new Font("Purisa", Font.PLAIN, 16));
            g2.drawString("PAUSED", 340, 300);
        }
        else {
            updateCats(g2);
            updateMouse(g2);
        }

        // update the vision (drawing all the objects)
        updateVision(g2);

        // determine if end game conditions have been met
        for(int i=0;i<cats.size();i++) {
            if(mice.get(0).collidesWith(cats.get(i))) {
                ingame = false;
            }
        }

        // determine if mouse has scored by catching a cheese
        for(int i=0;i<cheeses.size();i++) {
            if(mice.get(0).collidesWith(cheeses.get(i))) {
                cheeses.get(i).getGameZone().removeCheese();
                cheeses.remove(i);
                board.getStats().scored();
                map.decCheese();
            }
        }

        // Update the influence maps and mini map
        map.getCatInfluence().update();
        map.getMouseInfluence().update();
        map.updateMiniMap(player);
        updateTime();
        board.repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    // updates the timer on the score board, also checks that the frame rate is at
    // a maximum of 20 to allow a sensible game speed
    public void updateTime() {
        long end = System.currentTimeMillis();
        long frameTime;
        if((end - runTime) <50) {
            waiting(50 -(int)(end - runTime));
            frameTime = 50;
        }
        else {
            frameTime = end - runTime;
        }
        if(paused == false && ingame == true) {
            totalRuntime += (int)frameTime;
        }
        long total = totalRuntime/1000;
        int minutes = 0;
        String seconds;
        while(total>59) {
            minutes += 1;
            total -= 60;
        }
        seconds = Long.toString(total);
        if(total < 10) {
            seconds = "0" + seconds ;
        }
        board.getStats().setTime(minutes + ":" + seconds);
    }


    // The following set up grass, walls, cats, mice and cheese to their positions and
    // correct size at the start of the animation


    public void setUpGrass(int i, int j) {
        grass = new Grass(this);
        String tmp = Double.toString(i) + Double.toString(j);
        grass.setPosition(new Point2D(i*map.getObjectWidthRatio(),j*map.getObjectHeightRatio()));
        grass.setHeight((int)map.getObjectHeightRatio());
        grass.setWidth((int)map.getObjectWidthRatio());
        grassList.add(grass);
    }

    public void setUpWall(int i,int j) {
        map.incWalls();
        wall = new Wall(this);
        String tmp = Double.toString(i) + Double.toString(j);
        wall.setPosition(new Point2D(i*map.getObjectWidthRatio(),j*map.getObjectHeightRatio()));
        wall.setHeight((int)map.getObjectHeightRatio());
        wall.setWidth((int)map.getObjectWidthRatio());
        wallMap.put(tmp,wall);
        walls.add(wall);
    }

    public void setUpMouse(int x,int y) {
        mouse = new Mouse(this,map);
        mouse.setHeight((int)12);
        mouse.setWidth((int)12);
        mouse.setPosition(new Point2D(x*map.getObjectWidthRatio(),y*map.getObjectHeightRatio()));
        mouse.setVelocity(new Point2D(0,0));
        mice.add(mouse);
    }
    public void setUpCat(int x,int y) {
        cat = new Cat(this,map);
        cat.setHeight((int)18);
        cat.setWidth((int)18);
        cat.setPosition(new Point2D(x*map.getObjectWidthRatio(),y*map.getObjectHeightRatio()));
        cat.setVelocity(new Point2D(0,0.1));
        cat.setTarget(new Point2D(cat.position().X(), cat.position().Y()));
        //cat.setTarget(mouse.position());
        cat.setVision(map.getTileList().size(),map.getTileList().get(0).size(),map);
        cat.setTeamState(team);
        cat.setTargetSection(calcSection(x, y));
        cats.add(cat);
    }

    public void checkCheese() {
        while(map.cheese() < 6) {
            int tmp = (int)(Math.random()*map.getGameZones().size());
            GameZone zone = map.getGameZones().get(tmp);
            if(zone.hasCheese() == false) {
                zone.giveCheese();
                int tmpLoc = (int)(Math.random()*zone.getMapPoints().size());
                Cheese cheese = new Cheese(this);
                cheese.setPosition(new Point2D(zone.getMapPoints().get(tmpLoc).X()*map.getObjectWidthRatio(),zone.getMapPoints().get(tmpLoc).Y()*map.getObjectHeightRatio()));
                cheese.setHeight((int)map.getObjectHeightRatio());
                cheese.setWidth((int)map.getObjectWidthRatio());
                cheese.setGameZone(map.getGameZones().get(tmp));
                cheeses.add(cheese);
                map.incCheese();
            }
            try {
                mice.get(0).setTarget(cheeses.get(0).position());
            }
            catch(NullPointerException e) {

            }
        }
    }

    public void updateCats(Graphics2D g2) {
        for(int i=0;i<cats.size();i++) {
            cats.get(i).checkVision();
        }
        team.updateState();
        for(int i=0;i<cats.size();i++) {
            
            cats.get(i).updateState();
            cats.get(i).newPath(map,cats.get(i).getTarget(),false);
            cats.get(i).getPath();
            cats.get(i).updatePosition();

        }
    }

    // The following are the update methods that are called

    public void updateVision(Graphics2D g2) {
        if(mouseVision == true) {
            visGrid = mice.get(0).getVisionGrid().getVision();
        }
        else if(catVision == true) {
            visGrid = cats.get(0).getVisionGrid().getVision();
        }
        if(mouseVision == true && catVision == true) {
            visGrid = map.getVisGrid().getVision();
        }
        for(int i=0;i<walls.size();i++) {
            if(visGrid.get((int)(walls.get(i).getX()/map.getWidthRatio())).get((int)(walls.get(i).getY()/map.getHeightRatio())) && paused == false && ingame == true) {
                walls.get(i).draw(g2);
            }
            else if(walls.get(i).position().X() == 0 || walls.get(i).position().Y() == 0) {
                walls.get(i).draw(g2);
            }
            else if(walls.get(i).position().X() == (map.getMapList().size()-1)*map.getObjectWidthRatio() || walls.get(i).position().Y() == (map.getMapList().get(0).size()-1)*map.getObjectHeightRatio()) {
                walls.get(i).draw(g2);
            }
            else {
                Composite originalComposite = g2.getComposite();
                g2.setComposite(makeComposite(0.3f));
                walls.get(i).draw(g2);
                g2.setComposite(originalComposite);
            }
        }

        for(int i=0;i<grassList.size();i++) {
            if(visGrid.get((int)(grassList.get(i).getX()/map.getWidthRatio())).get((int)(grassList.get(i).getY()/map.getHeightRatio())) && paused == false && ingame == true) {
                grassList.get(i).draw(g2);
            }
            else {
                Composite originalComposite = g2.getComposite();
                g2.setComposite(makeComposite(0.3f));
                grassList.get(i).draw(g2);
                g2.setComposite(originalComposite);
            }

        }
        
        for(int i=0;i<cheeses.size();i++) {
            cheeses.get(i).draw(g2);
        }
        if(catVision == true) {
            if(visGrid.get((int)(mice.get(0).getX()/map.getWidthRatio())).get((int)(mice.get(0).getY()/map.getHeightRatio()))) {
                mice.get(0).draw(g2);
            }
        }
        else {
            mice.get(0).draw(g2);
        }

        for(int i=0;i<cats.size();i++) {
            if(mouseVision == true) {
                if(visGrid.get((int)(cats.get(i).getX()/map.getWidthRatio())).get((int)(cats.get(i).getY()/map.getHeightRatio()))) {
                    cats.get(i).draw(g2);
                }
            }
            else {
                cats.get(i).draw(g2);
            }
        }
    }
    
    public void updateMouse(Graphics2D g2) {
//        for(int a=0;a<mice.get(0).getVisionGrid().getVisionPoints().size();a++) {
//            for(int b=0;b<cats.size();b++) {
//                mice.get(0).getVisionGrid().getVisionPoints().get(a);
//            }
//        }
        if(player == false) {
            mice.get(0).updateState();
            mice.get(0).newPath(map,mice.get(0).getTarget(),true);
            mice.get(0).getPath();
        }

        long mouseEnd = System.currentTimeMillis();
        collision = map.playerWallCollision(mice.get(0));
        if(collision == false) {
            mice.get(0).updatePosition();
        }
        else {
            Point2D tmpVel = new Point2D(mice.get(0).getVelX(),mice.get(0).getVelY());
            mice.get(0).setVelX(0);
            collision = map.playerWallCollision(mice.get(0));
            if(collision == false) {
                mice.get(0).updatePosition();
            }
            else {
                mice.get(0).setVelX((int)tmpVel.X());
                mice.get(0).setVelY(0);
                collision = map.playerWallCollision(mice.get(0));
                if(collision == false) {
                    mice.get(0).updatePosition();
                }
                else {
                    System.out.println("COLLISION ");
                }
            }
        }

    }
    
    public Graphics2D updateGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        
        RenderingHints rh =
                new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                 RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHints(rh);
        return g2;
    }

    public int calcSection(int x, int y) {
        Point2D point = new Point2D(x,y);
        int mapWidth = map.getMapList().size();
        int mapHeight = map.getMapList().get(0).size();
        int widthSec = 0;
        int heightSec = 0;
        int targetSec;
        if(point.X() > mapWidth/2) {
            widthSec = 1;
        }
        if(point.Y() > mapHeight/2) {
            heightSec = 2;
        }
        if(heightSec == 0) {
            if(widthSec == 0) {
                targetSec = 0;
            }
            else {
                targetSec = 1;
            }
        }
        else {
            if(widthSec == 0) {
                targetSec = 2;
            }
            else {
                targetSec = 3;
            }
        }
        return targetSec;
    }
    
    public static void waiting (int n){
        
        long t0, t1;

        t0 =  System.currentTimeMillis();

        do{
            t1 = System.currentTimeMillis();
        }
        while ((t1 - t0) < (n * 1));
    }

    public void start() {
        //ingame = true;
        board.getStats().resetScore();
        totalRuntime = 0;
        paused = false;
        ingame = true;
        board.getStats().setScore();
        //run();
    }
    
    public void mouseClicked(MouseEvent e) {
        clicked = true;
        clickX = e.getX();
        clickY = e.getY();
    }

    public void mouseExited(MouseEvent e) {
        
    }
    
    public void mouseEntered(MouseEvent e) {
        
    }
    
    public void mouseReleased(MouseEvent e) {
        
    }
    
    public void mousePressed(MouseEvent e) {
        
    }

    public void keyTyped(KeyEvent e) {

    }

    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 36 || e.getKeyChar() == 'q' || e.getKeyChar() == 'Q' || e.getKeyChar() == '7')
        {
            mice.get(0).velocity().setX(-mouseSpeed);
            mice.get(0).velocity().setY(-mouseSpeed);
        }
        else if (e.getKeyCode() == 38  || e.getKeyChar() == 'w' || e.getKeyChar() == 'W' || e.getKeyChar() == '8')
        {
            mice.get(0).velocity().setX(0);
            mice.get(0).velocity().setY(-mouseSpeed);
        }
        else if (e.getKeyCode()  == 33 || e.getKeyChar() == 'e'|| e.getKeyChar() == 'E' || e.getKeyChar() == '9')
        {
            mice.get(0).velocity().setX(mouseSpeed);
            mice.get(0).velocity().setY(-mouseSpeed);
        }
        else if (e.getKeyCode() == 37 || e.getKeyChar() == 'a' || e.getKeyChar() == 'A' || e.getKeyChar() == '4')
        {
            mice.get(0).velocity().setX(-mouseSpeed);
            mice.get(0).velocity().setY(0);
        }
        else if (e.getKeyCode() == 39 || e.getKeyChar() == 'd' || e.getKeyChar() == 'D' || e.getKeyChar() == '6')
        {
            mice.get(0).velocity().setX(mouseSpeed);
            mice.get(0).velocity().setY(0);
        }
        else if (e.getKeyCode() == 35 || e.getKeyChar() == '\\' || e.getKeyChar() == '1')
        {
            mice.get(0).velocity().setX(-mouseSpeed);
            mice.get(0).velocity().setY(mouseSpeed);
        }
        else if (e.getKeyCode() == 40 || e.getKeyChar() == 'z' || e.getKeyChar() == 'Z' || e.getKeyChar() == '2')
        {
            mice.get(0).velocity().setX(0);
            mice.get(0).velocity().setY(mouseSpeed);
        }
        else if (e.getKeyCode() == 34 || e.getKeyChar() == 'x' || e.getKeyChar() == 'X' || e.getKeyChar() == '3')
        {
            mice.get(0).velocity().setX(mouseSpeed);
            mice.get(0).velocity().setY(mouseSpeed);
        }
        else if(e.getKeyChar() == 'p') {
            paused = true;
        }
        else if(e.getKeyChar() == 'r') {
            paused = false;
        }
        else if(e.getKeyChar() == 27) {
            JOptionPane dialog= new JOptionPane();
            int result = dialog.showConfirmDialog(this,"Really Exit?", "",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
            if(result == 0) {
                System.exit(0);
            }
        }
    }

    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
//        mice.get(0).velocity().setX(0);
//        mice.get(0).velocity().setY(0);
    }

    /** Handle the button click. */
    public void actionPerformed(ActionEvent e) {
    }

    private AlphaComposite makeComposite(float alpha) {
      int type = AlphaComposite.SRC_OVER;
      return(AlphaComposite.getInstance(type, alpha));
     }
}

