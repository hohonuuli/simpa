package simpa.annotation.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.UserLookupService;

public class ImagePreferencesPanel extends JPanel implements PreferenceUpdater {
    
    private final ImagePreferencesPanelController controller;

    private JComboBox imageFormatComboBox;
    private JLabel imageFormatLabel;
    private JTextField imageTargetMappingTextField;
    private JLabel imageWebAccessLabel;
    private JButton browseButton;
    private JTextField imageTargetTextField;
    private JLabel saveImagesToLabel;
    
    private final Logger log = LoggerFactory.getLogger(ImagePreferencesPanel.class);

    /**
     * Create the panel
     */
    public ImagePreferencesPanel(UserLookupService userLookupService) {
        super();
        this.controller = new ImagePreferencesPanelController(this, userLookupService);
        try {
            initialize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private void initialize() throws Exception {
		final GroupLayout groupLayout = new GroupLayout((JComponent) this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.addContainerGap()
					.add(groupLayout.createParallelGroup(GroupLayout.LEADING)
						.add(getImageWebAccessLabel())
						.add(getSaveImagesToLabel())
						.add(getImageFormatLabel()))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupLayout.createParallelGroup(GroupLayout.LEADING)
						.add(groupLayout.createSequentialGroup()
							.add(getImageTargetTextField(), GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
							.addPreferredGap(LayoutStyle.RELATED)
							.add(getBrowseButton()))
						.add(getImageTargetMappingTextField(), GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
						.add(getImageFormatComboBox(), 0, 347, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.addContainerGap()
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getSaveImagesToLabel())
						.add(getBrowseButton())
						.add(getImageTargetTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getImageWebAccessLabel())
						.add(getImageTargetMappingTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getImageFormatLabel())
						.add(getImageFormatComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(292, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}

    /**
     * @return
     */
    protected JLabel getSaveImagesToLabel() {
        if (saveImagesToLabel == null) {
            saveImagesToLabel = new JLabel();
            saveImagesToLabel.setText("Save Images to:");
        }
        return saveImagesToLabel;
    }

    /**
     * @return
     */
    protected JTextField getImageTargetTextField() {
        if (imageTargetTextField == null) {
            imageTargetTextField = new JTextField();
        }
        return imageTargetTextField;
    }

    /**
     * @return
     */
    protected JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse");
            browseButton.addActionListener(new BrowseAction());
        }
        return browseButton;
    }

    /**
     * @return
     */
    protected JLabel getImageWebAccessLabel() {
        if (imageWebAccessLabel == null) {
            imageWebAccessLabel = new JLabel();
            imageWebAccessLabel.setText("Image Web Access:");
        }
        return imageWebAccessLabel;
    }

    /**
     * @return
     */
    protected JTextField getImageTargetMappingTextField() {
        if (imageTargetMappingTextField == null) {
            imageTargetMappingTextField = new JTextField();
        }
        return imageTargetMappingTextField;
    }

    /**
     * @return
     */
    protected JLabel getImageFormatLabel() {
        if (imageFormatLabel == null) {
            imageFormatLabel = new JLabel();
            imageFormatLabel.setText("Image Format:");
        }
        return imageFormatLabel;
    }

    /**
     * @return
     */
    protected JComboBox getImageFormatComboBox() {
        if (imageFormatComboBox == null) {
            imageFormatComboBox = new JComboBox();
            imageFormatComboBox.setEnabled(false);
        }
        return imageFormatComboBox;
    }


    public void updatePreferences() {
        controller.updatePreferences();
    }
    
    protected ImagePreferencesPanelController getController() {
        return controller;
    }
    
    /**
     * Action for browsing for a directory to save images into.
     */
    private class BrowseAction implements ActionListener {
        
        final JFileChooser chooser;

        public BrowseAction() {
            chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        public void actionPerformed(ActionEvent e) {
            int response = chooser.showOpenDialog(ImagePreferencesPanel.this);
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                getImageTargetTextField().setText(file.getAbsolutePath());
            } 
        }
        
    }
    
}
