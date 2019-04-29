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
public class VideoControlException extends CoreException {

    public VideoControlException() {
    }

    public VideoControlException(String message) {
        super(message);
    }

    public VideoControlException(String message, Throwable cause) {
        super(message, cause);
    }

    public VideoControlException(Throwable cause) {
        super(cause);
    }

}
