/*
 * File: Console.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 * ---------------------------
 * Edited From: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 *
 */

package proj12AhnSlager;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.StyleClassedTextArea;

/**
 * This class is used to support console functionality.
 * It can be used to write new lines of text to the console.
 * It can also be used to check whether user input been given,
 * and what the command string was.
 *
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou (Project 5)
 * @author  Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 1.0
 * @since   11-02-2018
 *
 */
public class Console extends StyleClassedTextArea {

    private int commandStartIndex;

    private String command;
    // Whether or not a user-input command has been received
    // Constructor, using StyleClassedTextArea default
    public Console(){
        super();
        this.commandStartIndex = -1;
        this.command = "";
        this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            this.handleKeyPressed(e);
        });
        this.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            this.handleKeyTyped(e);
        });
    }




    /**
     * Adds a new, separate line of text to this console.
     * Used in ToolbarController when printing to the console.
     * @param newString the string to add to the console
     */
    public void writeLine(String newString, String type){

        int fromIndex = this.getText().length();
        this.appendText(newString);

        //Style the texts differently base on their source provided
        int toIndex = this.getText().length();
        if(type.equals("INPUT")) {
            this.setStyleClass(fromIndex, toIndex, "inp");
        }
        else if(type.equals("ERROR")){
            this.setStyleClass(fromIndex, toIndex, "err");
        }
        else if(type.equals("CONS")){
            this.setStyleClass(fromIndex, toIndex, "cons");
        }
        this.moveCaretToEnd();
        this.setStyleClass(toIndex, toIndex, "normal");
    }

    /**
     * Consume all keyTyped event if it is before the commandstartindex
     * @param e the keyEvent
     */
    private void handleKeyTyped(KeyEvent e){
        if (this.getCaretPosition() < commandStartIndex) {
            e.consume();
        }
    }

    /**
     * Handles the keyPressed events in the console
     * Updates the content in the console and command stored in the field
     * The key press would not do anything if not pressed after the command start index
     * @param e the keyEvent
     */
    private void handleKeyPressed(KeyEvent e) {

        //If there is current command stored
        if (this.commandStartIndex != -1) {
            //Change the color of the user input text to default
            this.setStyleClass(commandStartIndex, this.getText().length(), ".default");
            this.command = this.getText().substring(commandStartIndex);
        }

        //If there are no command, update the start index of the command to the end of the current text
        else if (this.command.isEmpty()) {
            this.commandStartIndex = this.getText().length();
        }

        //If the user pressed Enter
        if (e.getCode() == KeyCode.ENTER) {
            e.consume();
            //If Enter was pressed in the middle of a command append a new line to the end
            if (this.getCaretPosition() >= commandStartIndex) {
                this.appendText("\n");
                this.requestFollowCaret();
            }
        }

        //If the user pressed back space.
        else if (e.getCode() == KeyCode.BACK_SPACE) {
            //If the keypress was before the start of the command, nothing would happen
            if (this.getCaretPosition() < commandStartIndex + 1) {
                e.consume();
            }
        }

        //If the user pressed delete key.
        else if (e.getCode() == KeyCode.DELETE) {
            //If the keypress was before the start of the command, nothing would happen
            if (this.getCaretPosition() < commandStartIndex){
                e.consume();
            }
        }
    }

    /**
     * Moves the caret to the end of the text and movee the scroll bar to the caret position
     */
    private void moveCaretToEnd(){
        int length = this.getText().length();
        this.moveTo(length);
        this.requestFollowCaret();
    }

}