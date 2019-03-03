/*
 * File: Error.java
 * Author: djskrien
 * Date: Fall 2018
 */

package proj12AhnSlager.bantam.util;

/**
 * Class for representing errors
 */
public class Error {

    public enum Kind
    {  LEX_ERROR, PARSE_ERROR, CODEGEN_ERROR, SEMANT_ERROR }


    /**
     * Type of an error (lex, parse, semantic, code generation)
     */
    private Kind kind;
    /**
     * File name where the error occurred
     */
    private String filename;
    /**
     * Line number in the source file where the error occurred
     */
    private int lineNum;
    /**
     * Error message
     */
    private String message;

    /**
     * Error constructor
     *
     * @param kind     the type of error (lex, parse, semantic)
     * @param filename file name where the error occurred
     * @param lineNum  line number where the error occurred
     * @param message  error message
     */
    public Error(Kind kind, String filename, int lineNum, String message) {
        this.kind = kind;
        this.filename = filename;
        this.lineNum = lineNum;
        this.message = message;
    }

    /**
     * Get the type of error (lex, parse, semantic)
     *
     * @return the type of error
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Get the file name where the error occurred
     *
     * @return the file name
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the line number in the source file where the error occurred
     *
     * @return the line number
     */
    public int getLineNum() {
        return lineNum;
    }

    /**
     * Get the error message
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * return a string with the error message
     */
    public String toString() {
        if (getFilename() == null) {
            return "Error: " + getTypeString(getKind()) + getMessage();
        }
        else {
            return getFilename() + ":" + getLineNum() + ":" +
                    getTypeString(getKind()) + getMessage();
        }
    }

    /**
     * Get the type string (lex, parse, semantic, code generation, none of these)
     *
     * @return string representing the type of error
     */
    private String getTypeString(Kind kind) {
        if (kind == Kind.LEX_ERROR) {
            return "lexical error: ";
        }
        else if (kind == Kind.PARSE_ERROR) {
            return "syntactic error: ";
        }
        else if (kind == Kind.SEMANT_ERROR) {
            return "semantic error: ";
        }
        else if (kind == Kind.CODEGEN_ERROR) {
            return "code generation error: ";
        }
        else {
            return "";
        }
    }

}
