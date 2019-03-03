/*
 * File: CompilationException.java
 * Author: djskrien
 * Date: Fall, 2018
 */
package proj12AhnSlager.bantam.util;

/**
 * This class represents an error in a Bantam Java program that is severe enough
 * that the compilation process cannot continue.
 * Examples of such situations:
 *      the file to be compiled cannot be found or cannot be read
 *      the parser cannot continue because an ERROR token was found by the scanner
 *      the code generator cannot run because the semantic analyzer found errors.
 * The compiler throws this exception whenever it finds such an error.
 */
public class CompilationException extends RuntimeException
{
    /**
     * creates an exception with a message
     * @param message The message telling the reason the exception was thrown
     */
    public CompilationException(String message) {
        super(message);
    }
}
