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
public class ImageCaptureException extends CoreException {

    public ImageCaptureException() {
    }

    public ImageCaptureException(String message) {
        super(message);
    }

    public ImageCaptureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageCaptureException(Throwable cause) {
        super(cause);
    }

}
