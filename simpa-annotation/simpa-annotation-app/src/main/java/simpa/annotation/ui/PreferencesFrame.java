package simpa.annotation.ui;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import simpa.annotation.UserLookupService;

/**
 * Frame for allowing users to edit preferences.
 * 
 * @author brian
 */
public class PreferencesFrame extends JFrame {

    private final PreferencesFrameController controller;
    private final UserLookupService userLookupService;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel buttonPanel;
    private JTabbedPane tabbedPane;
    private ImagePreferencesPanel imagePreferencesPanel;
    private List<PreferenceUpdater> updaters = new ArrayList<PreferenceUpdater>();

    /**
     * Launch the application
     * @param args
     */
    public static void main(String args[]) {
        try {
            /*
             * Activate dependency injection
             */
            final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new AppModule());
            PreferencesFrame frame = injector.getInstance(PreferencesFrame.class);
            frame.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the frame
     */
    @Inject
    public PreferencesFrame(UserLookupService userLookupService) {
        super();
        this.controller = new PreferencesFrameController();
        this.userLookupService = userLookupService;
        setBounds(100, 100, 500, 375);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            initialize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws Exception {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        
        // Configure the updaters
        updaters.add(getImagePreferencesPanel());
    }

    /**
     * @return
     */
    protected JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Image Settings", getImagePreferencesPanel());
        }
        return tabbedPane;
    }

    /**
     * @return
     */
    protected JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(getCancelButton());
            buttonPanel.add(Box.createHorizontalStrut(10));
            buttonPanel.add(getOkButton());
        }
        return buttonPanel;
    }

    /**
     * @return
     */
    protected JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new CancelAction());
        }
        return cancelButton;
    }

    /**
     * @return
     */
    protected JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("OK");
            okButton.addActionListener(new OKAction());
        }
        return okButton;
    }

    public ImagePreferencesPanel getImagePreferencesPanel() {
        if (imagePreferencesPanel == null) {
            imagePreferencesPanel = new ImagePreferencesPanel(userLookupService);
            
            /*
             * Listen to changes to the loginCredential and relay them to the 
             * ImagePreferencePanel
             */
            final PropertyChangeSupport pcs = getController().getPropertyChangeSupport();
            pcs.addPropertyChangeListener(PreferencesFrameController.PROPERTY_LOGIN_CREDENTIAL, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    imagePreferencesPanel.getController().setLoginCredential((LoginCredential) evt.getNewValue());
                }
            });
        }
        return imagePreferencesPanel;
    }
    
    public PreferencesFrameController getController() {
        return controller;
    }
    
    
    private class OKAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            for (PreferenceUpdater preferenceUpdater : updaters) {
                preferenceUpdater.updatePreferences();
            }
            PreferencesFrame.this.setVisible(false);
        }
        
    }
    
    private class CancelAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PreferencesFrame.this.setVisible(false);
        }
        
    }
}
