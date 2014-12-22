/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gui;

import javax.swing.JMenuItem;
import org.yourorghere.Skeleton;

/**
 *
 * @author Spuz
 */
public class MenuItem extends JMenuItem{

    public MenuItem(String name, Skeleton frame) {
        super(name);
        addActionListener(frame);
    }
}
