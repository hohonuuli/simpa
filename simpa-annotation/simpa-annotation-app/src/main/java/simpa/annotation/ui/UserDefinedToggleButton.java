/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import javax.swing.JToggleButton;
import simpa.annotation.UserDefinedButtonInfo;

/**
 *
 * @author brian
 */
public class UserDefinedToggleButton extends JToggleButton {
    
    private final UserDefinedButtonInfo buttonInfo;

    public UserDefinedToggleButton(UserDefinedButtonInfo buttonInfo) {
        this.buttonInfo = buttonInfo;
        setText(buttonInfo.getConceptName());
        setEnabled(buttonInfo.isValid());
        // This defines a button look on Mac OS X. It's ignored on other platforms
        putClientProperty("JButton.buttonType", "textured");
    }

    public UserDefinedButtonInfo getButtonInfo() {
        return buttonInfo;
    }
    
        

}
