/*
 * File: JavaTabPane.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 */

package proj12AhnSlager;

import javafx.scene.control.TabPane;

import java.io.File;
import java.util.HashMap;

/**
 * This is the JavaTabPane class which stores JavaTabs
 *
 * @author Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 1.0
 * @since 11-20-2018
 */

public class JavaTabPane extends TabPane {

    HashMap<JavaTab, Boolean> tabSavedStatusMap;

    public JavaTabPane() {
        super();
        this.tabSavedStatusMap = new HashMap<>();
    }

    /**
     * Creates a new tab and adds the tab to the pane
     *
     * @param fileController
     * @param contextMenuController
     * @param file
     */
    public void createNewTab(FileController fileController,
                             ContextMenuController contextMenuController, File file) {

        // determine file name
        String filename;
        String content = "";
        if (file == null) {
            filename = "Untitled-".concat( Integer.toString(this.getTabs().size()) );
        }
        else {
            filename = file.getName();
        }
        // create the new tab
        JavaTab newTab = new JavaTab(fileController, contextMenuController,
                this, filename, file);

        // add to the list of tabs
        this.getTabs().add(0, newTab);
        // focus on the new tab
        this.getSelectionModel().select(newTab);
        // add it to the map indicating that it has never been saved

        if (file == null) this.tabSavedStatusMap.put(newTab, false);
        else this.tabSavedStatusMap.put(newTab, true);

    }

    /**
     * Changes the status of whether or not the file has been
     * saved
     *
     * @param t
     * @param newStatus
     */
    public void updateTabSavedStatus(JavaTab t, Boolean newStatus) {

        if (this.tabSavedStatusMap.containsKey(t)) {
            this.tabSavedStatusMap.replace(t, newStatus);
        }
    }

    /**
     * Getter for the save status of a tab in the pane
     * @param t
     * @return boolean
     */
    public boolean getTabSavedStatus(JavaTab t) {
        return this.tabSavedStatusMap.get(t);
    }

    /**
     *Getter for the save status of a tab in the pane
     * @param t
     * @return
     */
    public boolean tabIsSaved(JavaTab t) {
        return this.tabSavedStatusMap.get(t);
    }

    /**
     * Removes a JavaTab from the JavaTabPane
     */
    public void removeTab(JavaTab t) {
        this.getTabs().remove(t);
        this.tabSavedStatusMap.remove(t);
    }

}