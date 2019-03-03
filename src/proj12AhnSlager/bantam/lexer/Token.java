 /*
  * @(#)Token.java                        2.0 1999/08/11
  *
  * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
  * and School of Computer and Math Sciences, The Robert Gordon University,
  * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
  * All rights reserved.
  *
  * This software is provided free for educational use only. It may
  * not be used for commercial purposes without the prior written permission
  * of the authors.
  *
  * Modified by Dale Skrien, Fall 2018
  */

 package proj12AhnSlager.bantam.lexer;

 import java.util.Set;

 public class Token
 {
     //instance variables
     public Kind kind;
     public String spelling; // the actual sequence of chars in the token
     public int position; // the line number where the token was found

     public String getSpelling() {
         return spelling;
     }


     //constructor
     Token(Kind kind, String spelling, int position) {
         this.spelling = spelling;
         this.position = position;

         // patch the kind in the case of boolean constants and keywords
         if (kind == Kind.IDENTIFIER && (spelling.equals("true") || spelling.equals("false"))) {
             this.kind = Kind.BOOLEAN;
         }
         else if (kind == Kind.IDENTIFIER && reservedWords.contains(spelling)) {
             this.kind = Enum.valueOf(Kind.class, spelling.toUpperCase());
         }
         else {
             this.kind = kind;
         }
     }

     public String toString() {
         return "Token: Kind=" + kind.name() + ", spelling=" + spelling + ", " +
                 "position=" + position;
     }


     public enum Kind
     {
         // literals, identifiers...
         INTCONST, STRCONST, BOOLEAN, IDENTIFIER,

         // operators...
         BINARYLOGIC, PLUSMINUS, MULDIV, COMPARE, UNARYINCR, UNARYDECR, ASSIGN,
         UNARYNOT,

         // punctuation...
         DOT, COLON, SEMICOLON, COMMA,

         // brackets...
         LPAREN, RPAREN, LBRACKET, RBRACKET, LCURLY, RCURLY,

         // special tokens...
         COMMENT, ERROR, EOF, //end of file token

         // reserved words
         BREAK, CAST, CLASS, VAR, ELSE, EXTENDS, FOR, IF, INSTANCEOF, NEW,
         RETURN, WHILE
     }

     private static Set<String> reservedWords = Set.of("break", "cast", "class", "var",
             "else", "extends", "for", "if", "instanceof", "new", "return", "while");

 }