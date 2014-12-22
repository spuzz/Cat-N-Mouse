/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.GameObjects;

import javax.swing.JPanel;

/**
 *
 * @author Spuz
 */
public class Grass extends Sprite {

    public Grass(JPanel drawable) {
        super(drawable);
        this.load("Images/grass.jpg");
    }
}
