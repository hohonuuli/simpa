/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import simpa.core.CoreException;

/**
 *
 * @author brian
 */
public class AnnotationLookupException extends CoreException {

    public AnnotationLookupException() {
    }

    public AnnotationLookupException(String message) {
        super(message);
    }

    public AnnotationLookupException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnnotationLookupException(Throwable cause) {
        super(cause);
    }
    
}
