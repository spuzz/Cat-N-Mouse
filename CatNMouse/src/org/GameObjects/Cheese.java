/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.GameObjects;

import javax.swing.JPanel;
import org.AI.GameZone;
/**
 *
 * @author Spuz
 */
public class Cheese extends Sprite {

    public Cheese(JPanel drawable) {
        super(drawable);
        vision = 5;
        influence = 7;
        this.load("Images/slice-cheese.jpg");
    }

}
