package edu.byu.minecraft.cat.dataaccess;

/**
 * An exception that provides information on a database access errors or other related errors.
 */
public class DataAccessException extends Exception {

    /**
     * Constructs a new DataAccessException with a given message
     *
     * @param message a description of the exception
     */
    public DataAccessException(String message) {
        super(message);
    }


    /**
     * Constructs a new DataAccessException with a given message and cause
     *
     * @param message a description of the exception
     * @param cause   an underlying cause of this exception
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * Constructs a new DataAccessException with a given cause
     *
     * @param cause an underlying cause of this exception
     */
    public DataAccessException(Throwable cause) {
        super(cause);
    }

}
