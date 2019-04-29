/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

/**
 *
 * @author brian
 */
public class AnnotationPersistenceException extends RuntimeException {

    final Object persistedObject;

    public AnnotationPersistenceException(Throwable cause, Object persistedObject) {
        super(cause);
        this.persistedObject = persistedObject;
    }

    public AnnotationPersistenceException(String message, Throwable cause, Object persistedObject) {
        super(message, cause);
        this.persistedObject = persistedObject;
    }

    public AnnotationPersistenceException(String message, Object persistedObject) {
        super(message);
        this.persistedObject = persistedObject;
    }

    public AnnotationPersistenceException(Object persistedObject) {
        this.persistedObject = persistedObject;
    }

    public Object getPersistedObject() {
        return persistedObject;
    }
    
}
