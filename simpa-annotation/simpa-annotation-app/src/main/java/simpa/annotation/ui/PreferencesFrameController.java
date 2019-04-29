/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import java.beans.PropertyChangeSupport;

/**
 *
 * @author brian
 */
public class PreferencesFrameController {
    
    private LoginCredential loginCredential;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static final String PROPERTY_LOGIN_CREDENTIAL = "loginCredential";

    public LoginCredential getLoginCredential() {
        return loginCredential;
    }

    public void setLoginCredential(LoginCredential loginCredential) {
        final LoginCredential oldLoginCredential = this.loginCredential;
        this.loginCredential = loginCredential;
        propertyChangeSupport.firePropertyChange(PROPERTY_LOGIN_CREDENTIAL, oldLoginCredential, loginCredential);
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }
    
}
