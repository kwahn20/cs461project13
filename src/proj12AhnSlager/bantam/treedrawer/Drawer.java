/*
 * @(#)Drawer.java                        2.0 1999/08/11
 *
 * Copyright (C) 1999 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 *
 * Modified by Dale Skrien to work with Bantam Java
 * January, 2014
 */

package proj12AhnSlager.bantam.treedrawer;

import proj12AhnSlager.bantam.ast.Program;

import java.awt.*;

public class Drawer
{

    /**
     * Displays a Swing window with a drawing of the AST
     * @param sourceName the name of the file containing the program parsed into the AST
     * @param AST The AST created when parsing the file with the given name.
     */
    public void draw(String sourceName, Program AST)
    {
        DrawerPanel panel = new DrawerPanel();
        DrawerFrame frame = new DrawerFrame(sourceName, panel);

        Font font = new Font("SansSerif", Font.PLAIN, 12);
        frame.setFont(font);

        FontMetrics fontMetrics = frame.getFontMetrics(font);

        proj12AhnSlager.bantam.treedrawer.LayoutVisitor layout = new LayoutVisitor(fontMetrics);
        proj12AhnSlager.bantam.treedrawer.DrawingTree theDrawing = (DrawingTree) AST.accept(layout);
        theDrawing.position(new Point(2048, 10));
        panel.setDrawing(theDrawing);

        frame.setVisible(true);
    }

}
