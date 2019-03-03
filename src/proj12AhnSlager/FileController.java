/*
 * File: FileController.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 7
 * Date: November 2, 2018
 *
 * --------------------------------------
 *
 * Modified by Jackie Hang, Kyle Slager
 * Project 11
 * Date: February 13, 2019
 */

package proj12AhnSlager;

import javafx.event.Event;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import proj12AhnSlager.bantam.semant.MainMainVisitor;
import proj12AhnSlager.bantam.semant.NumLocalVarsVisitor;
import proj12AhnSlager.bantam.semant.SemanticAnalyzer;
import proj12AhnSlager.bantam.semant.StringConstantsVisitor;
import proj12AhnSlager.bantam.ast.Program;
import proj12AhnSlager.bantam.lexer.Scanner;
import proj12AhnSlager.bantam.lexer.Token;
import proj12AhnSlager.bantam.parser.Parser;
import proj12AhnSlager.bantam.treedrawer.Drawer;
import proj12AhnSlager.bantam.util.ClassTreeNode;
import proj12AhnSlager.bantam.util.CompilationException;
import proj12AhnSlager.bantam.util.Error;
import proj12AhnSlager.bantam.util.ErrorHandler;

/**
 * This class contains the handlers for each of the menu options in the IDE.
 *
 * Keeps track of the tab pane, the current tab, the index of the current tab
 * within the pane, and the File objects of the current tabs.
 *
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou (Project 5)
 * @author  Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 3.0
 * @since   11-2-2018
 */
public class FileController {

    private JavaTabPane javaTabPane;
    private HashMap<Tab, String> tabFilepathMap;
    private VBox vBox;

    /**
     * ContextMenuController handling context menu actions
     */
    private ContextMenuController contextMenuController;


    private Scanner scanner;
    private Parser parser;
    private ErrorHandler errorHandler;
    private ErrorHandler analysisErrors;

    /**
     * Constructor for the class. Intializes the save status
     * and the tabFilepathMap in a HashMap
     */
    public FileController(VBox vBox, JavaTabPane javaTabPane) {
        this.vBox = vBox;
        this.javaTabPane = javaTabPane;
        this.tabFilepathMap = new HashMap<>();
    }

    /**
     * Sets the contextMenuController.
     *
     * @param contextMenuController ContextMenuController handling context menu actions
     */
    public void setContextMenuController(ContextMenuController contextMenuController) {
        this.contextMenuController = contextMenuController;
    }

    /**
     * Returns the name of the file open in the current tab.
     * @return The name of the currently open file
     */
    protected String getFilePath(){
        Tab curTab = this.javaTabPane.getSelectionModel().getSelectedItem();
        return tabFilepathMap.get(curTab);
    }

    /**
     * Handler for the "About" menu item in the "File" menu.
     * Creates an Information alert dialog to display author and information of this program
     */
    public void handleAbout() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("About");
        dialog.setHeaderText(null);
        dialog.setContentText("V3 Authors: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager\n" +
                "Version 2 Authors: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou\n" +
                "Version 1 Authors: Kevin Ahn, Matt Jones, Jackie Hang, Kevin Zhou\n" +
                "This application is a basic IDE with syntax highlighting.");
        dialog.showAndWait();
    }

    /**
     * Handler for the "New" menu item in the "File" menu.
     * Adds a new Tab to the TabPane, adds null to the tabFilepathMap HashMap,
     * and false to the saveStatus HashMap
     */
    public void handleNew(File file) {
        this.javaTabPane.createNewTab(this, contextMenuController, file);
        JavaTab t = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();

        if (file == null) this.tabFilepathMap.put(t, null);
        else {
            this.tabFilepathMap.put(t, file.getPath());

        }

    }

    /**
     * Handler for the "Open" menu item in the "File" menu.
     * Creates a FileChooser to select a file
     * Use scanner to read the file and write it into a new tab.
     */
    public void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        Window stage = this.vBox.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null){
            handleNew(file);
        }

    }


    /**
     * Handler for the "Close" menu item in the "File" menu.
     * Checks to see if the file has been changed since the last save.
     * If changes have been made, redirect to askSaveAndClose and then close the tab.
     * Otherwise, just close the tab.
     */
    public void handleClose(Event event) {

        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();

        if (tabFilepathMap.get(curTab) != null) {
            // check if any changes were made
            if (this.javaTabPane.tabIsSaved(curTab))
                this.closeTab();
            else
                this.askSaveAndClose(event);
        } else {
            if(!tabFilepathMap.isEmpty()) {
                this.askSaveAndClose(event);
            }
        }
    }

    /**
     * Handler for the "Save" menu item in the "File" menu.
     * If the current tab has been saved before, writes out the content to its corresponding
     * file in storage.
     * Else if the file has never been saved, opens a pop-up window that allows the user to
     * choose a filename and directory and then store the content of the tab to storage.
     */
    public boolean handleSave() {
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();

        if (tabFilepathMap.get(curTab) != null){
            File file = new File(tabFilepathMap.get(curTab));    // this is what gets the path
            writeFile(file);
            this.javaTabPane.updateTabSavedStatus(curTab, true);
            return true;
        }
        else
            return this.handleSaveAs();
    }

    /**
     * Handler for the "Save as..." menu item in the "File" menu.
     * Opens a pop-up window that allows the user to choose a filename and directory.
     * Calls writeFile to save the file to memory.
     * Changes the name of the current tab to match the newly saved file's name.
     */
    public boolean handleSaveAs() {
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as...");
        Window stage = this.vBox.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file == null){
            return false;
        }
        else{
            writeFile(file);
            tabFilepathMap.replace(curTab,file.getPath());
            this.javaTabPane.updateTabSavedStatus(curTab, true);
        }
        curTab.setText(file.getName());
        return true;
    }

    /**
     * Handler for the "Exit" menu item in the "File" menu.
     * Closes all the tabs using handleClose()
     * Returns when the user cancels exiting any tab.
     */
    public void handleExit(Event event) {
        int numTabs = tabFilepathMap.size();
        // Close each tab using handleClose()
        // Check if current number of tabs decreased by one to know if the user cancelled.
        for (int i = 0; i < numTabs; i++ ) {
            this.handleClose(event);
            if (tabFilepathMap.size() == (numTabs - i)) return;

        }
        Platform.exit();
    }

    /**
     * Creates a pop-up window which allows the user to select whether they wish to save
     * the current file or not.
     * Used by handleClose.
     *
     * @param event the tab closing event that may be consumed
     */
    private void askSaveAndClose(Event event) {
        ShowSaveOptionAlert saveOptions = new ShowSaveOptionAlert();
        Optional<ButtonType> result = saveOptions.getUserSaveDecision();

        if (result.isPresent()) {
            if (result.get() == saveOptions.getCancelButton()) {
                event.consume();
                return;
            } else if (result.get() == saveOptions.getYesButton()) {

                boolean isNotCancelled = this.handleSave();

                if(isNotCancelled) {
                    this.closeTab();
                } else {
                    event.consume();
                }
                return;
            }
            this.closeTab();
        }
    }

    /**
     * Creates a pop-up window which allows the user to select whether they wish to save
     * the current file or not.
     * Used by handleClose.
     *
     * @param event the tab closing event that may be consumed
     */
    private String askSaveAndScan(Event event) {
        ShowSaveOptionAlert saveOptions = new ShowSaveOptionAlert();
        Optional<ButtonType> result = saveOptions.getUserSaveDecision();

        if (result.isPresent()) {
            if (result.get() == saveOptions.getCancelButton()) {
                event.consume();
                return "cancel";
            }
            else if (result.get() == saveOptions.getYesButton()){
                boolean saved = this.handleSave();
                if (saved) return "yes";
                event.consume();
                return null;
            }
            else {
                event.consume();
                return "no";
            }
        }
        return null;
    }


    /**
     * Saves the text present in the current tab to a given filename.
     * Used by handleSave, handleSaveAs.
     *
     * @param file The file object to which the text is written to.
     */
    private void writeFile(File file) {
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane<CodeArea> scrollPane = (VirtualizedScrollPane<CodeArea>) curTab.getContent();
        CodeArea codeArea = scrollPane.getContent();
        String text = codeArea.getText();

        // use a BufferedWriter object to write out the string to a file
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(text);
            writer.close();
        }
        catch (IOException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("File Error");
            alert.setContentText("Cannot find file or file is Read-only. Please select a new file.");
            alert.showAndWait();
            return;
        }

        this.javaTabPane.updateTabSavedStatus(curTab, true);
    }


    /**
     * this method is called when the Scan button is pressed
     * if the file is not saved it prompts the user to save before scanning
     * it will scan the file and display the tokens in a new tab
     * @param event press of the Scan button triggering the handleScan method
     */
    public void handleScan(Event event) {
        scanOrParseHelper(event, "SCAN_ONLY" );

    }

    /**
     * this method is called when the Scan&Parse button is pressed
     * if the file is not saved it prompts the user to save before scanning
     * it will scan and parse the file and display an AST if parse
     * was successful
     * @param event press of the Scan button triggering the handleScan method
     */
    public void handleScanAndParse (Event event) {

        scanOrParseHelper(event, "SCAN_AND_PARSE" );


    }

    /**
     * Assists with calling just scan or scanning and parsing the
     * file
     * @param event press of the Scan button triggering the handleScan and Parse method
     * @param scanOrParse string "SCAN_ONLY" or "SCAN_AND_PARSE" or "PARSE_NO_TREE_DRAWN"
     */
    public Program scanOrParseHelper(Event event, String scanOrParse ){
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();


        if (this.javaTabPane.tabIsSaved(curTab)) {
            String filename = this.tabFilepathMap.get(curTab);
            try {
                this.errorHandler = new ErrorHandler();
                if(scanOrParse.equals("SCAN_ONLY")) {
                    this.scanner = new Scanner(filename, this.errorHandler);
                }
                else{
                    this.parser = new Parser(this.errorHandler);
                }

            }
            catch(CompilationException e){
                throw e;
            }

            if(scanOrParse.equals("SCAN_ONLY")) {
                this.handleNew(null);
                curTab = (JavaTab) this.javaTabPane.getSelectionModel().getSelectedItem();
                Token nextToken;
                while ( (nextToken = scanner.scan()).kind != Token.Kind.EOF) {
                    curTab.getCodeArea().appendText(nextToken.toString()+"\n");
                }
                return null;
            }

            else{
                Program root = this.parser.parse(filename);
                if(scanOrParse.equals("SCAN_AND_PARSE")) {
                    Drawer drawer = new Drawer();
                    drawer.draw(filename, root);
                }
                return root;
            }

        }

        String saveStatus = this.askSaveAndScan(event);
        if (saveStatus == "cancel") {
            this.scanner = null;
            return null;
        }
        else if (saveStatus == "no") {
            if (tabFilepathMap.get(curTab) == null) {
                return null;
            }
        }
        else if (saveStatus == "yes"){
            scanOrParseHelper(event, scanOrParse);
        }
        return null;
    }

    /**
     * Parses the program and checks if there is a
     * main method and a Main class
     * @param event
     * @return boolean of statement above
     */
    public Boolean handleMainCheck(Event event){
        Program program;
        try {
            program = scanOrParseHelper(event, "PARSE_NO_TREE_DRAWN");
        }
        catch(CompilationException e){
            throw e;

        }
        MainMainVisitor mainVisitor = new MainMainVisitor();
        return mainVisitor.hasMain(program);

    }

    /**
     * Scans and Parses and then checks the program using the semantic analyzer
     * @param event
     * @return
     */
    public ClassTreeNode handleAnalyze(Event event){
        Program program;
        try{
            program = scanOrParseHelper(event, "SCAN_AND_PARSE");
        }
        catch(CompilationException e){
            throw e;
        }
        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
        ClassTreeNode analysis = analyzer.analyze(program);
        analysisErrors = analyzer.getErrorHandler();
        return analysis;
    }

    public List<Error> getAnalysisErrors(){
        return analysisErrors.getErrorList();
    }

    /**
     * Parses the program and returns a map
     * of the String constants in the program
     * @param event
     * @return Map<String, String> where the key is STRING_CONST + NUMBER
     */
    public Map<String, String> handleStrConstCheck(Event event){
        Program program;
        try {
            program = scanOrParseHelper(event, "PARSE_NO_TREE_DRAWN");
        }
        catch(CompilationException e){
            throw e;
        }

        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();
        return stringConstantsVisitor.getStringConstants(program);
    }

    /**
     *
     * Parses the program and returns a map of the
     * number of local variables in every method in the
     * program
     * @param event
     * @return
     */
    public Map<String,Integer> handleNumLocVarCheck(Event event){
        Program program;
        try {
            program = scanOrParseHelper(event, "PARSE_NO_TREE_DRAWN");
        }
        catch(CompilationException e){
            throw e;
        }

        NumLocalVarsVisitor numLocalVarsVisitor = new NumLocalVarsVisitor();
        return numLocalVarsVisitor.getNumLocalVars(program);
    }



    /**
     *
     * @return the list of errors from the most recent scan performed on a file
     * return value will be null if there is no valid file open to scan
     */
    public List<Error> getErrors() {
        if (this.scanner == null) return null;
        return this.errorHandler.getErrorList();
    }

    /**
     * Executes process for when a tab is closed, which is to remove the filename and saveStatus at
     * the corresponding HashMaps, and then remove the Tab object from TabPane
     *
     */
    private void closeTab() {
        //NOTE: the following three lines has to be in this order removing the tab first would
        //result in calling handleUpdateCurrentTab() because the currently selected tab will
        //change, and thus the wrong File will be removed from the HashMaps
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();
        tabFilepathMap.remove(curTab);
        javaTabPane.removeTab(curTab);
    }

}