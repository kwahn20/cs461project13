/*
 * File: MatchBracketOrParen.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 */

package proj12AhnSlager;

import javafx.scene.control.IndexRange;
import java.util.Stack;

/**
 * This is the MatchBracketOrParen Class that takes in an edit controller
 * and matches the bracket of the file
 *
 * @author Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 1.0
 * @since 11-20-2018
 */
public class MatchBracketOrParen {

    private EditController editController;

    public MatchBracketOrParen(EditController editController) {
        this.editController = editController;
    }

    /**
     * if a single "{", "}", "[", "]", "(", ")" is highlighted, this will attempt to find
     * the matching opening or closing character and if successful, will highlight the
     * text in between the matching set of {}, [], or (),
     * otherwise will display an appropriate error message
     */
    public void handleBracketOrParenMatching() {
        // get in-focus code area
        JavaCodeArea curJavaCodeArea = this.editController.getCurJavaCodeArea();

        // get any highlighted text in the code area
        String highlightedText = curJavaCodeArea.getSelectedText();

        if (highlightedText.isEmpty()) {
            editController.showAlert("Please Highlight a Bracket!");
            return;
        } else if (highlightedText.length() == 1) {

            // true if matching a closing character to an opening character,
            // false if matching an opening character to a closing character
            Boolean findClosingCharacter;

            if (highlightedText.equals("{") || highlightedText.equals("[")
                    || highlightedText.equals("(")) {
                findClosingCharacter = true;
            } else if (highlightedText.equals("}") || highlightedText.equals("]")
                    || highlightedText.equals(")")) {
                findClosingCharacter = false;
            } else {
                editController.showAlert("VALID CHARACTER NOT HIGHLIGHTED\n" +
                        "VALID CHARACTERS ARE '{', '}', '[', ']', '(' or ')'");
                return;
            }

            // save length of whole file
            int fileTextLength = curJavaCodeArea.getLength();

            // this stack holds only opening "[","(","{" or closing "]",")","}" characters
            // depending which type was initially highlighted to match against
            // start with initial highlighted bracket/parenthesis/brace on the stack
            Stack<String> charStack = new Stack<>();
            charStack.push(highlightedText);

            // get the indices of the highlighted character within the file
            IndexRange highlightedCharRange = curJavaCodeArea.getSelection();

            if (findClosingCharacter) {

                String openingMatchCharacter;

                // search forward through file
                int idxAfterCharToMatch = highlightedCharRange.getEnd();
                for (int i = idxAfterCharToMatch; i < fileTextLength; i++) {

                    // get the opening char on top of stack
                    openingMatchCharacter = charStack.peek();

                    // current character being checked for a closing bracket match
                    String curChar = curJavaCodeArea.getText(i, i + 1);

                    // check that the character is not not written as a string "(" or '('
                    try {
                        if (curJavaCodeArea.getText(i - 1, i).equals("\"")
                                && curJavaCodeArea.getText(i + 1, i + 2).equals("\"")
                                || curJavaCodeArea.getText(i - 1, i).equals("'")
                                && curJavaCodeArea.getText(i + 1, i + 2).equals("'")) {
                            continue;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        throw(e);
                    }
                    /* pop the top opening char off the stack if its closing match is found,
                     * otherwise push the newly found opening char onto the stack */
                    switch (curChar) {
                        case ("]"):
                            if (openingMatchCharacter.equals("[")) charStack.pop();
                            break;
                        case (")"):
                            if (openingMatchCharacter.equals("(")) charStack.pop();
                            break;
                        case ("}"):
                            if (openingMatchCharacter.equals("{")) charStack.pop();
                            break;
                        case ("["):
                            charStack.push(curChar);
                            break;
                        case ("("):
                            charStack.push(curChar);
                            break;
                        case ("{"):
                            charStack.push(curChar);
                            break;
                        default:
                            break;
                    }
                    // stack is empty if the originally highlighted character has been
                    /// matched with the current character
                    if (charStack.isEmpty()) {
                        // highlight between matching characters ({}, () or [])
                        curJavaCodeArea.selectRange(idxAfterCharToMatch, i);
                        return;
                    }
                }
                editController.showAlert("MATCHING CLOSING CHARACTER NOT FOUND");
                return;
            } else {
                String closingMatchCharacter;
                int idxBeforeCharToMatch = highlightedCharRange.getStart();
                // search backward through file
                for (int i = idxBeforeCharToMatch; i > 0; i--) {

                    // get closing character on top of the stack
                    closingMatchCharacter = charStack.peek();

                    // check that the character is not not written as a string "(" or '('
                    try {
                        int textLen = curJavaCodeArea.getText().length();
                        if(i > textLen) {
                            if (curJavaCodeArea.getText(i - 2, i - 1).equals("\"")
                                    && curJavaCodeArea.getText(i, i + 1).equals("\"")
                                    || curJavaCodeArea.getText(i - 2, i - 1).equals("'")
                                    && curJavaCodeArea.getText(i, i + 1).equals("'")) {
                                continue;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        throw(e);
                    }

                    // pop the top opening char off the stack if its closing match is found,
                    // otherwise push the newly found opening char onto the stack
                    // current character being checked for a closing bracket match
                    String curChar = curJavaCodeArea.getText(i - 1, i);

                    switch (curChar) {
                        case ("["):
                            if (closingMatchCharacter.equals("]")) charStack.pop();
                            break;
                        case ("("):
                            if (closingMatchCharacter.equals(")")) charStack.pop();
                            break;
                        case ("{"):
                            if (closingMatchCharacter.equals("}")) charStack.pop();
                            break;
                        case ("]"):
                            charStack.push(curChar);
                            break;
                        case (")"):
                            charStack.push(curChar);
                            break;
                        case ("}"):
                            charStack.push(curChar);
                            break;
                        default:
                            break;
                    }
                    // stack is empty if the originally highlighted character has been
                    /// matched with the current character in the file
                    if (charStack.isEmpty()) {
                        // highlight between matching characters ({}, () or [])
                        curJavaCodeArea.selectRange(i, idxBeforeCharToMatch);
                        return;
                    }
                }
                editController.showAlert("MATCHING OPENING CHARACTER NOT FOUND");
                return;
            }
        } else {
            editController.showAlert("VALID CHARACTERS ARE A SINGLE '{', '}', '[', ']', '(' or ')'");
        }

    }
}
