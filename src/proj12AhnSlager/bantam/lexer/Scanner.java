/*
 * File: Scanner.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 * ---------------------------
 * Edited From: Dale Skrien
 */

package proj12AhnSlager.bantam.lexer;

import proj12AhnSlager.bantam.util.CompilationException;
import proj12AhnSlager.bantam.util.ErrorHandler;

import java.io.*;
import java.util.Set;

import proj12AhnSlager.bantam.util.Error;


/**
 * This is the scanner class whose main responsibility is to
 * produce tokens from a Source File Object
 * as well as return
 *
 * @author Dale Skrien
 * @author Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 1.0
 * @since 11-20-2018
 */
public class Scanner
{
    private SourceFile sourceFile;
    private ErrorHandler errorHandler;
    private Character currentChar;

    private final Set<Character> charsEndingIdentifierOrKeyword =
            Set.of('"', '/', '+', '-', '>', '<', '=', '&', '{',
                    '}', '[', ']', '(', ')', ';', ':', '!', ' ',
                    '.', ',', '\r', '\n', '*', '%');


    /**
     *
     * @param handler an ErrorHandler
     */
    public Scanner(ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        sourceFile = null;
    }

    /**
     *
     * @param filename the name of the file that will be passed to the SourceFile
     * @param handler an ErrorHandler
     */
    public Scanner(String filename, ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        try {
            sourceFile = new SourceFile(filename);
        }
        catch (CompilationException e){
            throw e;

        }
    }

    /**
     *
     * @param reader a Reader linked to existing File to be passed to the SourceFile
     * @param handler an ErrorHandler
     */
    public Scanner(Reader reader, ErrorHandler handler) {
        errorHandler = handler;
        sourceFile = new SourceFile(reader);
    }


    /** Each call of this method builds the next Token from the contents
     * of the file being scanned and returns it. When it reaches the end of the
     * file, any calls to scan() result in a Token of kind EOF.
     */
    public Token scan() {
        Character tempChar = currentChar;

        if (currentChar.equals(SourceFile.eof)) return new Token(Token.Kind.EOF,
                currentChar.toString(), this.sourceFile.getCurrentLineNumber());

        //gets rid of whitespace
        else {
            while(currentChar.equals('\t') || currentChar.equals('\r')
                    || currentChar.equals('\n') || currentChar.equals('\f') || currentChar.equals(' ')) {

                currentChar = this.sourceFile.getNextChar();
                tempChar = currentChar;
            }
        }


        switch(tempChar) {

            case(SourceFile.eof):
                return new Token(Token.Kind.EOF,
                        currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('*'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.MULDIV, tempChar.toString(),
                        this.sourceFile.getCurrentLineNumber());

            case('"'): return this.getStringConstToken();

            case('/'): return this.getCommentOrMulDivToken();

            case('+'): return this.getPlusToken();

            case('-'): return this.getMinusToken();

            case('>'): return this.getCompareToken();

            case('<'): return this.getCompareToken();

            case('='): return this.getEqualsToken();

            case('&'): return getBinaryLogicToken();

            case('|'): return getBinaryLogicToken();

            case('%'): return getCommentOrMulDivToken();

            case('{'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.LCURLY,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('}'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.RCURLY,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('['):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.LBRACKET,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(']'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.RBRACKET,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('('):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.LPAREN,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(')'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.RPAREN,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(';'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.SEMICOLON,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(':'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.COLON,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('!'): return this.getUnaryNotOrCompareToken();

            case('.'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.DOT,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(','):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.COMMA,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            default:

                if (Character.isDigit(currentChar)) return getIntConstToken();
                else if (Character.isLetter(currentChar)) return getIdentifierOrKeywordToken();
                else {
                    currentChar = sourceFile.getNextChar();
                    this.errorHandler.register(Error.Kind.LEX_ERROR,
                            this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                            "TOKEN ERROR");
                    return new Token(Token.Kind.ERROR, tempChar.toString(),
                            this.sourceFile.getCurrentLineNumber());
                }
         }
    }

    /**
     *
     * @return a token of Kind.COMMENT, Kind.MULDIV or Kind.ERROR
     */
    private Token getCommentOrMulDivToken() {
        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();
        switch(currentChar) {

            case('/'): return this.getSingleLineCommentToken();

            case('*'): return this.getBlockCommentToken(prevChar);

            default:
                return new Token(Token.Kind.MULDIV, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Creates and returns a single line comment token
     * @return a token of Kind.COMMENT
     */
    private Token getSingleLineCommentToken() {

        String commentBody = "//";
        currentChar = this.sourceFile.getNextChar();    // move to first char after //
        while (!( currentChar.equals(SourceFile.eol) ||
                currentChar.equals(SourceFile.eof) )) {

            commentBody = commentBody.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        if(currentChar.equals(SourceFile.eol)){currentChar = sourceFile.getNextChar();}

        return new Token(Token.Kind.COMMENT, commentBody,
                this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Creates and returns a multi-line comment token
     * @return a token of Kind.COMMENT or Kind.ERROR if it was unclosed
     */
    private Token getBlockCommentToken(Character prevChar) {

        String commentBody = "/*";

        // move prevChar and currentChar past the "/*"
        for (int i = 0; i < 2; i++) {
            prevChar = currentChar;
            currentChar = this.sourceFile.getNextChar();
        }

        boolean commentTerminated = false;

        while (!commentTerminated) {

            commentBody = commentBody.concat(prevChar.toString());
            if (currentChar.equals(SourceFile.eof)) {

                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(),
                        this.sourceFile.getCurrentLineNumber(),
                        "UNTERMINATED BLOCK COMMENT");

                return new Token(Token.Kind.ERROR,
                        commentBody.concat(currentChar.toString()),
                        this.sourceFile.getCurrentLineNumber());
            }

            else if (prevChar.equals('*') && currentChar.equals('/'))
                commentTerminated = true;


            prevChar = currentChar;
            currentChar = this.sourceFile.getNextChar();
        }
        currentChar = sourceFile.getNextChar();
        return new Token(Token.Kind.COMMENT, commentBody.concat(prevChar.toString()),
                this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Creates and returns a token of Kind.BINARYLOGIC (|| or &&)
     * or Kind.ERROR if neither are found
     *
     * @return a token of Kind.BINARYLOGIC (|| or &&) or Kind.ERROR if neither found
     */
    private Token getBinaryLogicToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals(prevChar)) {
            Character temp = currentChar;
            currentChar = sourceFile.getNextChar();

            String spelling = prevChar.toString().concat(temp.toString());
            return new Token(Token.Kind.BINARYLOGIC, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
        else {
            this.errorHandler.register(Error.Kind.LEX_ERROR,
                    this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                    "BINARY LOGIC ERROR");
            return new Token(Token.Kind.ERROR, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Creates and returns a Compare token
     *
     * @return a token of Kind.COMPARE, could be >, >=, <, <=
     */
    private Token getCompareToken() {
        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals('=')) {
            String tokenSpelling = prevChar.toString().concat(currentChar.toString());
            currentChar = sourceFile.getNextChar();
            return new Token(Token.Kind.COMPARE, tokenSpelling, this.sourceFile.getCurrentLineNumber());
        }
        else {
            return new Token(Token.Kind.COMPARE, prevChar.toString(), this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Creates and returns a COMPARE or UNARYNOT token
     *
     * @return a token of Kind.COMPARE (if !=) or Kind.UNARYNOT (if just !)
     */
    private Token getUnaryNotOrCompareToken(){
        currentChar = this.sourceFile.getNextChar();
        if (currentChar.equals('=')){
            currentChar = sourceFile.getNextChar();
            return new Token(Token.Kind.COMPARE,
                    "!=", this.sourceFile.getCurrentLineNumber());
        }
        else {
            return new Token(Token.Kind.UNARYNOT,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Creates and returns an ASSIGN or COMPARE token
     *
     * @return a token of Kind.ASSIGN(=) or Kind.COMPARE(==)
     */
    private Token getEqualsToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals(prevChar)) {

            String spelling = prevChar.toString().concat(currentChar.toString());
            currentChar = sourceFile.getNextChar();
            return new Token(Token.Kind.COMPARE, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
        else {

            return new Token(Token.Kind.ASSIGN, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Creates a PLUSMINUS or UNARYINCR token
     *
     * @return a token of Kind.PLUSMINUS(+) or Kind.UNARYINCR(++)
     */
    private Token getPlusToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals(prevChar)) {

            String spelling = prevChar.toString().concat(currentChar.toString());
            currentChar = sourceFile.getNextChar();
            return new Token(Token.Kind.UNARYINCR, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
        else {
            return new Token(Token.Kind.PLUSMINUS, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Creates and returns a minus token or a unary decrement token
     *
     * @return a token of Kind.PLUSMINUS(-) or UNARYDECR(---)
     */
    private Token getMinusToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals(prevChar)) {

            String spelling = prevChar.toString().concat(currentChar.toString());
            currentChar = sourceFile.getNextChar();
            return new Token(Token.Kind.UNARYDECR, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
        else {
            return new Token(Token.Kind.PLUSMINUS, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Returns an integer constant token, where the integer
     * value does not exceed (2^31 - 1)
     * @return token of Kind.INTCONST or Kind.ERROR
     */
    private Token getIntConstToken() {
        String spelling = "";
        while(Character.isDigit(currentChar)){
            spelling = spelling.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        try {
                Integer.parseInt(spelling);
                return new Token(Token.Kind.INTCONST, spelling, this.sourceFile.getCurrentLineNumber());
        }
        catch (NumberFormatException e){
            this.errorHandler.register(Error.Kind.LEX_ERROR,
                    this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                    "INVALID INTEGER CONSTANT");
            return new Token(Token.Kind.ERROR, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Returns a identifier or keyword token
     * if it should be a keyword, it will be converted to the appropriate Kind in the
     * Token constructor
     *
     * @return a token of Kind.IDENTIFIER or Kind.ERROR if its an invalid character
     */
    private Token getIdentifierOrKeywordToken() {
        String spelling = "";
        while(!charsEndingIdentifierOrKeyword.contains(currentChar)){

            if(Character.isLetterOrDigit(currentChar) || currentChar.equals('_')) {
                spelling = spelling.concat(currentChar.toString());
                currentChar = this.sourceFile.getNextChar();
            }
            else{
                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                        "UNSUPPORTED IDENTIFIER CHARACTER");

                spelling= spelling.concat(currentChar.toString());
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.ERROR, spelling,
                        this.sourceFile.getCurrentLineNumber());
            }
        }


        return new Token(Token.Kind.IDENTIFIER, spelling, this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Returns a string constant token ensuring that
     * no strings are over 5000 characters
     *
     * @return string constant token
     */
    private Token getStringConstToken() {

        String spelling = "";
        spelling = spelling.concat(currentChar.toString());
        currentChar = this.sourceFile.getNextChar();

        //while the quote is unmatched continue getting chars
        while(!currentChar.equals('"')){

            //if you've reached an eof or a new line in a string, throws error
            if(currentChar.equals(SourceFile.eof) || currentChar.equals('\n')){
                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                        "UNCLOSED QUOTE");
                return createErrorToken(spelling);
            }
            else if (currentChar.equals('\\')){
                spelling = spelling.concat(currentChar.toString());
                currentChar = this.sourceFile.getNextChar();
            }
            //otherwise add on to the string
            spelling = spelling.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        //add on end quote
        spelling = spelling.concat(currentChar.toString());
        currentChar = sourceFile.getNextChar();

        //makes sure the string is less than 5000 chars
        if(spelling.length()<5000) {
            return new Token(Token.Kind.STRCONST, spelling, this.sourceFile.getCurrentLineNumber());
        }
        else{
            this.errorHandler.register(Error.Kind.LEX_ERROR,
                    this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                    "STRING EXCEEDS MAX CHAR LENGTH 5000");
            return createErrorToken(spelling);
        }
    }

    public Token createErrorToken(String spelling){
        return new Token(Token.Kind.ERROR, spelling,
                this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Tester Method for the Scanner class.
     * Prints all the tokens of a file
     *
     * @param args command line file arguments
     */
    public static void main (String[] args){
        if(args.length > 0){
            for(int i = 0; i< args.length; i ++){
                Scanner scanner;
                ErrorHandler errorHandler = new ErrorHandler();
                try {

                    scanner = new Scanner(args[i], errorHandler);

                }
                catch(CompilationException e){
                    System.out.println(e);
                    continue;
                }

                Token nextToken;
                while ( (nextToken = scanner.scan()).kind != Token.Kind.EOF) {
                    System.out.println(nextToken);
                }

                if(errorHandler.getErrorList().size() > 0){
                    System.out.println("Scanning of " + args[i] + " was not successful. "+
                            errorHandler.getErrorList().size() +" errors were found.\n\n");
                }
                else{

                    System.out.println("Scanning of " + args[i] + " was successful. " +
                            "No errors were found.\n\n");
                }

            }
        }

    }

    public String getFilename(){
        return sourceFile.getFilename();
    }

}