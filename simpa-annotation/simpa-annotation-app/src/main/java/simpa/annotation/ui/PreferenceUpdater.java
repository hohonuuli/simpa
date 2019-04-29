/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

/**
 * Marker interface for components that can update user preferences
 * @author brian
 */
public interface PreferenceUpdater {
    
    /**
     * Update the preferences to the persistent data store.
     */
    void updatePreferences();

}
