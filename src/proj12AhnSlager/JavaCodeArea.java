package proj12AhnSlager;
/*
 * File: JavaCodeArea.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 */

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the controller for all of the toolbar functionality.
 * Specifically the compile, compile and run, and stop buttons
 *
 * @author Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 3.0
 * @since 11-20-2018
 */

public class JavaCodeArea extends CodeArea {

    private ContextMenuController contextMenuController;

    //ContextMenuController contextMenuController
    public JavaCodeArea(ContextMenuController contextMenuController) {
        super();
        this.subscribe();
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        this.contextMenuController = contextMenuController;
        this.contextMenuController.setupJavaCodeAreaContextMenuHandler(this);
        /**
         * Obtained from Douglas-Hanssen-MacDonald-Zhang
         * Used for closing braces and parentheses at the end of a code area
         */
        this.textProperty().addListener(new ChangeListener<String>() {
                                            @Override
                                            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                if (newValue.length() != 0) {
                                                    String lastChar = Character.toString(newValue.charAt(newValue.length() - 1));
                                                    if (lastChar.equalsIgnoreCase("(")) {
                                                        appendText(")");
                                                    } else if (lastChar.equalsIgnoreCase("{")) {
                                                        appendText("\n}");
                                                    }
                                                }
                                            }
                                        }
        );
    }


    /**
     * Method obtained from the RichTextFX Keywords Demo. Method allows
     * for syntax highlighting after a delay of 500ms after typing has ended.
     * This method was copied from JavaKeyWordsDemo
     * Original Author: Jordan Martinez
     */
    private void subscribe() {
        // recompute the syntax highlighting 500 ms after user stops editing area
        Subscription codeCheck = this

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> this.setStyleSpans(0, computeHighlighting(this.getText())));
    }

    // a list of strings that contain the keywords for the IDE to identify.
    private static final String[] KEYWORDS = new String[]{
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "var"
    };

    // the regex rules for the ide
    private static final String IDENTIFIER_PATTERN = "[a-zA-Z]+[a-zA-Z0-9_]*";
    private static final String FLOAT_PATTERN = "(\\d+\\.\\d+)";
    private static final String INTCONST_PATTERN = "\\d+";
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<FLOAT>" + FLOAT_PATTERN + ")"
                    + "|(?<INTCONST>" + INTCONST_PATTERN + ")"
                    + "|(?<IDENTIFIER>" + IDENTIFIER_PATTERN + ")"

    );

    /**
     * Method to highlight all of the regex rules and keywords.
     * Code obtained from the RichTextFX Demo from GitHub.
     *
     * @param text a string analyzed for proper syntax highlighting
     */
    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                            matcher.group("BRACE") != null ? "brace" :
                                    matcher.group("BRACKET") != null ? "bracket" :
                                            matcher.group("SEMICOLON") != null ? "semicolon" :
                                                    matcher.group("STRING") != null ? "string" :
                                                            matcher.group("COMMENT") != null ? "comment" :
                                                                    matcher.group("IDENTIFIER") != null ? "identifier" :
                                                                            matcher.group("INTCONST") != null ? "intconst" :
                                                                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


}

