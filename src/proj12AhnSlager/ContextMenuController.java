/*
 * File: ContextMenuController.java
 * F18 CS361 Project 6
 * Names: Liwei Jiang, Chris Marcello, Tracy Quan
 * Date: 10/23/2018
 * This file contains the ContextMenuController class, handling context menu related actions.
 */

/*
 * Edited by Lucas DeGraw 11/1/18
 * removed setupConsoleContextMenuHandler method,
 * changed variables names as appropriate to work with AhnDeGrawHangSlager project 7
 */

package proj12AhnSlager;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.Node;
import javafx.scene.control.SeparatorMenuItem;

import java.io.File;

/**
 * ContextMenu Controller, handling context menu related actions.
 */
public class ContextMenuController {

    /**
     * FileMenuController handling File menu actions
     */
    private FileController fileMenuController;
    /**
     * EditMenuController handling Edit menu actions
     */
    private EditController editMenuController;


    /**
     * Sets file menu controller.
     *
     * @param fileMenuController FileMenuController initialized in main Controller.
     */
    public void setFileMenuController(FileController fileMenuController) { this.fileMenuController = fileMenuController; }

    /**
     * Sets the edit menu controller.
     *
     * @param editMenuController EditMenuController initialized in main Controller.
     */
    public void setEditMenuController(EditController editMenuController) { this.editMenuController = editMenuController; }

    /**
     * Helper method to set on shown action.
     * Will be called when a context menu node shows up.
     * By clicking on the left mouse button, the context menu will disappear.
     * By clicking on the right mouse button, the context menu moves to the clicked position.
     */
    private void setOnShown(ContextMenu rightClickMenu, final Node node) {
        rightClickMenu.setOnShown(onShowEvent -> {
            node.setOnMouseClicked(e -> {
                // clicking on the left button hides the context menu
                if (e.getButton() == MouseButton.PRIMARY) {
                    rightClickMenu.hide();
                }
                // clicking on the right button moves the context menu to the clicked position
                else if (e.getButton() == MouseButton.SECONDARY) {
                    rightClickMenu.show(node, e.getScreenX(), e.getScreenY());
                }
            });
        });
    }


    /**
     * Handles the right click context menu action for a Tab.
     * Pops up a context menu when right-clicking on the specified Tab.
     * The context menu contains Save, SaveAs, New, Open, Close items.
     * After the context menu pops up, left mouse click will make the context menu disappear;
     * right mouse click will move the context menu to the location of the mouse click.
     *
     * @param tab Tab being clicked on
     */
    public void setupTabContextMenuHandler(Tab tab) {
        ContextMenu rightClickMenu = new ContextMenu();
        rightClickMenu.getStyleClass().add("contextMenu");

        MenuItem SaveItem = new MenuItem("Save");
        SaveItem.setOnAction(e -> this.fileMenuController.handleSave());

        MenuItem SaveAsItem = new MenuItem("SaveAs");
        SaveAsItem.setOnAction(e -> this.fileMenuController.handleSaveAs());

        MenuItem NewItem = new MenuItem("New");
        NewItem.setOnAction(e -> this.fileMenuController.handleNew( new File("")));

        MenuItem OpenItem = new MenuItem("Open");
        OpenItem.setOnAction(e -> this.fileMenuController.handleOpen());

        MenuItem CloseItem = new MenuItem("Close");
        CloseItem.setOnAction(e -> this.fileMenuController.handleClose(e));

        rightClickMenu.getItems().addAll(SaveItem, SaveAsItem, new SeparatorMenuItem(),
                NewItem, OpenItem, new SeparatorMenuItem(), CloseItem);
        tab.setContextMenu(rightClickMenu);
    }


    /**
     *
     * Handles the right click context menu action for a StyledJavaCodeArea.
     * Pops up a context menu when right-clicking on the specified StyledJavaCodeArea.
     * The context menu contains Undo, Redo, Cut, Copy, Paste, SelectAll,
     * Toggle Comment, Toggle Block Comment, Indent, Unindent items.
     * After the context menu pops up, left mouse click will make the context menu disappear;
     * right mouse click will move the context menu to the location of the mouse click.
     *
     * @param styledJavaCodeArea StyledJavaCodeArea being clicked on
     */
    public void setupJavaCodeAreaContextMenuHandler(JavaCodeArea styledJavaCodeArea) {
        ContextMenu rightClickMenu = new ContextMenu();

        MenuItem UndoItem = new MenuItem("Undo");
        UndoItem.setOnAction(e -> styledJavaCodeArea.undo());

        MenuItem RedoItem = new MenuItem("Redo");
        RedoItem.setOnAction(e -> styledJavaCodeArea.redo());

        MenuItem CutItem = new MenuItem("Cut");
        CutItem.setOnAction(e -> styledJavaCodeArea.cut());

        MenuItem CopyItem = new MenuItem("Copy");
        CopyItem.setOnAction(e -> styledJavaCodeArea.copy());

        MenuItem PasteItem = new MenuItem("Paste");
        PasteItem.setOnAction(e -> styledJavaCodeArea.paste());

        MenuItem SelectAllItem = new MenuItem("SelectAll");
        SelectAllItem.setOnAction(e -> styledJavaCodeArea.selectAll());

        MenuItem ToggleCommentItem = new MenuItem("Toggle Comment");
        ToggleCommentItem.setOnAction(e -> this.editMenuController.handleCommenting());

        MenuItem indentItem = new MenuItem("Indent");
        indentItem.setOnAction(e -> this.editMenuController.handleTabbing());

        MenuItem unindentItem = new MenuItem("Unindent");
        unindentItem.setOnAction(e -> this.editMenuController.handleUnTabbing());

        rightClickMenu.getItems().addAll(UndoItem, RedoItem, CutItem, CopyItem, PasteItem, SelectAllItem,
                new SeparatorMenuItem(), ToggleCommentItem,
                new SeparatorMenuItem(), indentItem, unindentItem);

        styledJavaCodeArea.setOnContextMenuRequested(event -> {
            rightClickMenu.show(styledJavaCodeArea, event.getScreenX(), event.getSceneY());
        });
        this.setOnShown(rightClickMenu, styledJavaCodeArea);
    }

}