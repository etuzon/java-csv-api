package io.github.etuzon.java.csv.exceptions;

import io.github.etuzon.projects.core.expections.EtuzonExceptionBase;

/**************************************************
 * Exception for illegal operation.
 * 
 * @author Eyal Tuzon
 *
 */
public class CsvOperationException extends EtuzonExceptionBase {

    private static final long serialVersionUID = 1L;
    
    /**************************************************
     * Constructor.
     * 
     */
    public CsvOperationException() {
        super();
    }
    
    /**************************************************
     * Constructor.
     * 
     * @param message Exception message.
     */
    public CsvOperationException(String message) {
        super(message);
    }
    
    /**************************************************
     * Constructor.
     * 
     * Convert input exception as exception message and exception stacktrace.
     * 
     * @param e Input exception object that will be converted to exception message and stacktrace.
     */
    public CsvOperationException(Exception e) {
        super(e);
    }
}