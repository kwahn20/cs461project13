package proj12AhnSlager;
/*
 * File: FindAndReplaceWidget.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 */

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.NavigationActions;

import java.util.*;

/**
 * This is a widget deal with find and replace function.
 * @author Micheal Coyne
 * @author Danqing Zhao
 */
public class FindAndReplaceWidget {

    private Iterator<int[]> indices = new ArrayList<int[]>().iterator();
    private String target, textToSearch;
    private EditController editController;
    private Button findButton;
    private Button replaceAllButton;
    private TextField userEntryTextField;

    public FindAndReplaceWidget(EditController editController) {
        this.editController = editController;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTextToSearch() {
        return this.textToSearch;
    }

    public void setTextToSearch(String text) {
        this.textToSearch = text;
    }

    /**
     * @return a boolean that indicates whether or not the FindAndReplaceWidget's "indices" field, which
     * is an iterator, is empty or not.
     */
    public boolean isEmpty() {
        return !indices.hasNext();
    }

    /**
     * Set up the Find and Replace widget. Shows the dialog window for the widget, and
     * intializes the textToSearch, findButton, replaceAllButton, and userEntryTextField fields.
     * Once this method is run, the find and replace widget can be operated by the user.
     */
    public void setupWidget() {
        CodeArea currentCodeArea = this.editController.getCurJavaCodeArea();
        Stage popupWindow  = new Stage();
        GridPane layout    = new GridPane();
        Scene scene        = new Scene(layout);

        textToSearch       = currentCodeArea.getText();
        findButton         = new Button("Find next");
        replaceAllButton   = new Button("Replace all");
        userEntryTextField = new TextField();

        layout.add(findButton,         0, 1);
        layout.add(replaceAllButton,   1, 1);
        layout.add(userEntryTextField, 0, 0, 2, 1);

        setupButtons();

        popupWindow.setScene(scene);
        popupWindow.showAndWait();
    }

    /**
     * Binds events to the widget's "find" and "replace all" buttons.
     */
    private void setupButtons() {
        findButton.setOnAction(event -> selectNext());
        replaceAllButton.setOnAction(event -> replaceAll());
    }

    /**
     * Selects the next word in the CodeArea that matches the search that user
     * has entered in the TextField of the FindAndReplaceWidget.
     */
    private void selectNext() {
        CodeArea currentCodeArea   = editController.getCurJavaCodeArea();
        String   currentText       = currentCodeArea.getText();
        String   currentTarget     = userEntryTextField.getText();
        boolean  targetHasChanged  = !Objects.equals(this.getTarget(), currentTarget);
        boolean  srcTextHasChanged = !Objects.equals(this.getTextToSearch(), currentText);

        if(isEmpty() || targetHasChanged || srcTextHasChanged) {
            createIteratorFrom(currentText, currentTarget);
            setTarget(currentTarget);
            setTextToSearch(currentText);
        }
        int[] range = getNextRange();
        if(range != null) {
            // select the text in in the given range of the current code area
            currentCodeArea.moveTo(range[0]);
            currentCodeArea.moveTo(range[1], NavigationActions.SelectionPolicy.EXTEND);
        }
    }

    /**
     * replaces all substrings within the CodeArea that match the term the user
     * has entered in the TextField of the FindAndReplaceWidget with a replacement string.
     * The replacement string is obtained from a TextInputDialog which appears when this method
     * is called.
     */
    private void replaceAll() {
        String source = editController.getCurJavaCodeArea().getText();
        String target = userEntryTextField.getText();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText("Replace with: ");
        dialog.setTitle(      "Replace All");
        dialog.setHeaderText( "Enter the text you would like to replace your selection");

        Optional<String> userSelection = dialog.showAndWait();
        if(!userSelection.isPresent()) {
            return;
        }

        String replacement = userSelection.get();
        String newText     = source.replace(target, replacement);
        editController.handleSelectAll();
        editController.getCurJavaCodeArea().replaceSelection(newText);
    }

    /**
     * @param source the string that is being searched for a given substring
     * @param substring the substring being searched for in the source string
     * @return a two element int[] array that contains the start and end indices of the target string
     * in the source, where the first element of the array is the start location, and the second element
     * is the end location. If the given source string does not contain the given substring,
     * the returned array will contain the values [-1, -1]
     */
    private int[] indicesOf(String source, String substring) {
        int startOfSubstring = source.indexOf(substring);
        if(startOfSubstring == -1 || "".equals(substring)) {
            return new int[] {-1, -1};
        }
        return new int[] {startOfSubstring, startOfSubstring + substring.length()};
    }

    /**
     * A recursive helper method for the findAllIndices method. Returns a
     * @param idxAccumulator a List where the int[]s are accumulated
     * @param numPreviousChars the numbers of previous characters that have been examined
     *                         in search of the target substring
     * @param source the source that is being examined for the target substring
     * @param substring the target substring which is being searched for
     * @return a list of int arrays, where each int array represents the start and end location
     * of a target substring in the original source String
     */
    private List<int[]> findAllIndicesHelper(
            List<int[]> idxAccumulator, int numPreviousChars,
            String source, String substring) {

        int[] indicesOfSubstring = indicesOf(source, substring);
        int start = indicesOfSubstring[0];
        int end   = indicesOfSubstring[1];

        if (start == -1) {
            return idxAccumulator;
        }

        idxAccumulator.add(new int[] {start + numPreviousChars, end + numPreviousChars});
        String unsearchedText = source.substring(end, source.length());
        int numCharsSearched  = numPreviousChars + end;

        return findAllIndicesHelper(idxAccumulator, numCharsSearched, unsearchedText, substring);
    }

    /**
     * this method is used for finding all of the locations of a given substring in a given
     * source string
     * @param source the source text which is being examined for the given substring
     * @param substring the substring that is being searched for in the given source String
     * @return a list of int arrays, where each int array represents the start and end location
     *         of a target substring in the original source String
     */
    private List<int[]> findAllIndices(String source, String substring) {
        List<int[]> result = new ArrayList<>();
        return findAllIndicesHelper(result, 0, source, substring);
    }

    /**
     * helper method to create an iterator and store it in the indices field
     * @param source the source text that is being searched
     *               (usually all of the text contained in the current text area)
     * @param target the target string to be searched for
     */
    private void createIteratorFrom(String source, String target) {
        this.indices = findAllIndices(source, target).iterator();
    }

    /**
     * Helper method.
     * @return the next int[] in the object's "indices" iterator. Returns null if the iterator is empty.
     */
    private int[] getNextRange() {
        if(!this.isEmpty()) {
            return indices.next();
        }
        return null;
    }
}
