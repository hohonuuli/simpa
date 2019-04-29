/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.UserLookupService;

/**
 *
 * @author brian
 */

public class ImagePreferencesPanelController {
    
    private final ImagePreferencesPanel imagePreferencesPanel;
    private final UserLookupService userLookupService;
    private LoginCredential loginCredential;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static final String PROPERTY_LOGIN_CREDENTIAL = "loginCredential";
    private final Logger log = LoggerFactory.getLogger(ImagePreferencesPanelController.class);
    
    ImagePreferencesPanelController(ImagePreferencesPanel imagePreferencesPanel, 
            UserLookupService userLookupService) {
        this.imagePreferencesPanel = imagePreferencesPanel;
        this.userLookupService = userLookupService;
    }

    public UserLookupService getUserLookupService() {
        return userLookupService;
    }

    public LoginCredential getLoginCredential() {
        return loginCredential;
    }

    public void setLoginCredential(LoginCredential loginCredential) {
        final LoginCredential oldLoginCredential = this.loginCredential;
        this.loginCredential = loginCredential;
        
        /*
         * Configure the UI with the new login credential
         */
        URL imageTarget = userLookupService.readImageTarget(loginCredential.getLogin(), loginCredential.getHostName());
        URL imageTargetMapping = userLookupService.readImageTargetMapping(loginCredential.getLogin());
        imagePreferencesPanel.getImageTargetTextField().setText(imageTarget.toExternalForm());
        imagePreferencesPanel.getImageTargetMappingTextField().setText(imageTargetMapping.toExternalForm());
        
        propertyChangeSupport.firePropertyChange(PROPERTY_LOGIN_CREDENTIAL, oldLoginCredential, loginCredential);
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }
    
    
    void updatePreferences() {
        final JTextField imageTargetTextField = imagePreferencesPanel.getImageTargetTextField();
        final JTextField imageTargetMappingTextField = imagePreferencesPanel.getImageTargetMappingTextField();
        
        URL imageTarget = null;
        try {
            imageTarget = new URL(imageTargetTextField.getText());
        }
        catch (MalformedURLException ex) {
            log.warn("The user specified and invalid URL as an imageTarget. The bogus URL is '" + 
                    imageTargetTextField.getText() + "'");
            // TODO notify user of bad URL
        }
        userLookupService.writeImageTarget(loginCredential.getLogin(), loginCredential.getHostName(), imageTarget);
        
        
        URL imageMappingTarget = null;
        try {
            imageMappingTarget = new URL(imageTargetMappingTextField.getText());
        }
        catch (MalformedURLException ex) {
            log.warn("The user specified and invalid URL as an imageTarget. The bogus URL is '" + 
                    imageTargetMappingTextField.getText() + "'");
            // TODO notify user of bad URL
        }
        userLookupService.writeImageTargetMapping(loginCredential.getLogin(), imageMappingTarget);
    }

}
