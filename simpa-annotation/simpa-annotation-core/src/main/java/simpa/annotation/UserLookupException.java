/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import simpa.core.CoreException;

/**
 * Exception that can be thrown from the UserLookupService methods
 * 
 * @author brian
 */
public class UserLookupException extends CoreException {

    public UserLookupException() {
    }

    public UserLookupException(String message) {
        super(message);
    }

    public UserLookupException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserLookupException(Throwable cause) {
        super(cause);
    }

}
