/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yourorghere;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.gui.ImageMenuBar;
import org.gui.MenuItem;
import org.gui.SidePanel;
import org.gui.createMenu;
import org.gui.scoreboard;
/**
 *
 * @author Spuz
 */
public class Skeleton extends JFrame implements ActionListener {
    private CatnMouse game;
    public Skeleton() {
        game = new CatnMouse();
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        scoreboard board = new scoreboard();
        game.setScoreBoard(board);
        SidePanel leftPanel = new SidePanel();
        SidePanel rightPanel = new SidePanel();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        ImageMenuBar menuBar = new ImageMenuBar();
        setJMenuBar(menuBar);
        createMenu file = new createMenu("File",6);
        createMenu control = new createMenu("Keyboard/Mouse",6);
        createMenu testing = new createMenu("Test",6);
        createMenu scenario = new createMenu("Scenarios",6);
        createMenu help = new createMenu("Help",6);

        MenuItem startMT = new MenuItem("Start New",this);
        MenuItem openMT = new MenuItem("Open",this);
        MenuItem exitMT = new MenuItem("Exit",this);
        MenuItem aboutMT = new MenuItem("About",this);

        MenuItem aiMT = new MenuItem("AI",this);
        MenuItem playerMT = new MenuItem("Player",this);
        MenuItem mouseVisionMT = new MenuItem("Mouse Vision",this);
        MenuItem catVisionMT = new MenuItem("Cat Vision",this);
        MenuItem AllVisiionMT = new MenuItem("All Vision",this);

        MenuItem scenario1MT = new MenuItem("Scenario 1",this);
        MenuItem scenario2MT = new MenuItem("Scenario 2",this);

        file.add(openMT);
        file.add(exitMT);
        file.add(startMT);

        help.add(aboutMT);

        testing.add(aiMT);
        testing.add(playerMT);
        testing.add(mouseVisionMT);
        testing.add(catVisionMT);
        testing.add(AllVisiionMT);

        scenario.add(scenario1MT);
        scenario.add(scenario2MT);
        
        menuBar.add(file);
        menuBar.add(control);
        menuBar.add(help);
        menuBar.add(testing);
        menuBar.add(scenario);

        mainPanel.add(leftPanel,BorderLayout.EAST);
        mainPanel.add(game,BorderLayout.CENTER);
        mainPanel.add(rightPanel,BorderLayout.CENTER);
//        contentPane.add(leftPanel,BorderLayout.CENTER);
//        contentPane.add(game,BorderLayout.CENTER);
        contentPane.add(mainPanel,BorderLayout.CENTER);
        contentPane.add(board);
        setTitle("Cat 'N' Mouse");
        setExtendedState(this.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setVisible(true);
        //setResizable(false);
        JOptionPane dialog= new JOptionPane();
        int result = dialog.showConfirmDialog(this,"Start Game?", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
        if(result == 0) {
            game.start();
        }
    }
    public static void main(String[] args) {
        //PassMap pmap = new PassMap();
        new Skeleton();
    }
    public static void waiting (int n){
        
        long t0, t1;

        t0 =  System.currentTimeMillis();

        do{
            t1 = System.currentTimeMillis();
        }
        while ((t1 - t0) < (n * 50));
    }

    public void actionPerformed( ActionEvent event )
	{
            if(event.getActionCommand() == "Start New") {
                JOptionPane dialog= new JOptionPane();
                int result = dialog.showConfirmDialog(this,"Start new game?", "",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
                if(result == 0) {
                    game.setScenario(0);
                    game.init();
                    game.start();
                }
            }
            else if(event.getActionCommand() == "AI") {
                game.setPlayer(false);
            }
            else if(event.getActionCommand() == "Player") {
                game.setPlayer(true);
            }
            else if(event.getActionCommand() == "Mouse Vision") {
                game.setMouseVision(true);
                game.setCatVision(false);
            }
            else if(event.getActionCommand() == "Cat Vision") {
                game.setMouseVision(false);
                game.setCatVision(true);
            }
            else if(event.getActionCommand() == "All Vision") {
                game.setMouseVision(true);
                game.setCatVision(true);
            }
            else if(event.getActionCommand() == "Scenario 1") {
                game.setScenario(1);
                game.init();
                game.start();
            }
            else if(event.getActionCommand() == "Scenario 2") {
                game.setScenario(2);
                game.init();
                game.start();
            }
            else if(event.getActionCommand() == "Exit") {
                System.exit(0);
            }
	}

}

