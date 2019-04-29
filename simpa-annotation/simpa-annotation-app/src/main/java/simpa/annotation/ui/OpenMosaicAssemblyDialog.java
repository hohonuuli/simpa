/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class OpenMosaicAssemblyDialog extends JDialog {

    private final Logger log = LoggerFactory.getLogger(OpenTileFileDialog.class);
    private JButton cancelButton;
    private JButton okButton;
    private JTextField sessionIdentifierTextField;
    private JTextField cameraIdentifierTextField;
    private JLabel sessionIdentifierLabel;
    private JLabel cameraIdentifierLabel;
    private JButton browseButton;
    private JTextField urlTextField;
    private JLabel urlLabel;
    private ActionListener okActionListener;
    
    private String cameraIdentifier;
    private String sessionIdentifier;
    
    private final JFileChooser fileChooser = new JFileChooser();

    /**
     * Launch the application
     * @param args
     */
    public static void main(String args[]) {
        try {
            OpenMosaicAssemblyDialog dialog = new OpenMosaicAssemblyDialog();
            dialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog
     */
    public OpenMosaicAssemblyDialog() {
        super();
        try {
            initialize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    //
    }
    
    public OpenMosaicAssemblyDialog(JFrame parent) {
        super(parent);
        setLocationRelativeTo(parent);
        try {
            initialize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws Exception {
		final GroupLayout groupLayout = new GroupLayout((JComponent) getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.addContainerGap()
					.add(groupLayout.createParallelGroup(GroupLayout.LEADING)
						.add(groupLayout.createSequentialGroup()
							.add(groupLayout.createParallelGroup(GroupLayout.LEADING)
								.add(getCameraIdentifierLabel())
								.add(getUrlLabel()))
							.addPreferredGap(LayoutStyle.RELATED)
							.add(groupLayout.createParallelGroup(GroupLayout.LEADING)
								.add(getUrlTextField(), GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
								.add(getCameraIdentifierTextField(), GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)))
						.add(groupLayout.createSequentialGroup()
							.add(getSessionIdentifierLabel())
							.addPreferredGap(LayoutStyle.RELATED)
							.add(groupLayout.createParallelGroup(GroupLayout.LEADING)
								.add(GroupLayout.TRAILING, groupLayout.createSequentialGroup()
									.add(getCancelButton())
									.addPreferredGap(LayoutStyle.RELATED)
									.add(getOkButton()))
								.add(getSessionIdentifierTextField(), GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(getBrowseButton())
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.addContainerGap()
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getUrlLabel())
						.add(getBrowseButton())
						.add(getUrlTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getCameraIdentifierLabel())
						.add(getCameraIdentifierTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getSessionIdentifierLabel())
						.add(getSessionIdentifierTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getOkButton())
						.add(getCancelButton()))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
		pack();
	}

    /**
     * @return
     */
    protected JLabel getUrlLabel() {
        if (urlLabel == null) {
            urlLabel = new JLabel();
            urlLabel.setText("URL:");
        }
        return urlLabel;
    }

    /**
     * @return
     */
    protected JTextField getUrlTextField() {
        if (urlTextField == null) {
            urlTextField = new JTextField();
            urlTextField.setEnabled(false);
        }
        return urlTextField;
    }

    /**
     * @return
     */
    protected JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse");
            browseButton.setEnabled(false);
            
        }
        return browseButton;
    }

    /**
     * @return
     */
    protected JLabel getCameraIdentifierLabel() {
        if (cameraIdentifierLabel == null) {
            cameraIdentifierLabel = new JLabel();
            cameraIdentifierLabel.setText("Camera Identifier:");
        }
        return cameraIdentifierLabel;
    }

    /**
     * @return
     */
    protected JLabel getSessionIdentifierLabel() {
        if (sessionIdentifierLabel == null) {
            sessionIdentifierLabel = new JLabel();
            sessionIdentifierLabel.setText("Session Identifier:");
        }
        return sessionIdentifierLabel;
    }

    /**
     * @return
     */
    protected JTextField getCameraIdentifierTextField() {
        if (cameraIdentifierTextField == null) {
            cameraIdentifierTextField = new JTextField();
            cameraIdentifierTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    validateInputValues();
                }

                public void removeUpdate(DocumentEvent e) {
                    validateInputValues();
                }

                public void changedUpdate(DocumentEvent e) {
                    validateInputValues();
                }
            });
        }
        return cameraIdentifierTextField;
    }

    /**
     * @return
     */
    protected JTextField getSessionIdentifierTextField() {
        if (sessionIdentifierTextField == null) {
            sessionIdentifierTextField = new JTextField();
            sessionIdentifierTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    validateInputValues();
                }

                public void removeUpdate(DocumentEvent e) {
                    validateInputValues();
                }

                public void changedUpdate(DocumentEvent e) {
                    validateInputValues();
                }
            });
        }
        return sessionIdentifierTextField;
    }

    /**
     * @return
     */
    protected JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("OK");
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (validateInputValues()) {
                        cameraIdentifier = getCameraIdentifierTextField().getText();
                        sessionIdentifier = getSessionIdentifierTextField().getText();
                        setVisible(false);
                        okActionListener.actionPerformed(e);
                    }
                }
            });
        }
        return okButton;
    }

    /**
     * @return
     */
    protected JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    getCameraIdentifierTextField().setText("");
                    getSessionIdentifierTextField().setText("");
                    cameraIdentifier = null;
                    sessionIdentifier = null;
                    setVisible(false);
                }
            });
        }
        return cancelButton;
    }
    
    
    public String getCameraIdentifier() {
        return cameraIdentifier;
    }
    
    public String getSessionIdentifier() {
        return sessionIdentifier;
    }
    
    private boolean validateInputValues() {
        String sId = getSessionIdentifierTextField().getText();
        String cId = getCameraIdentifierTextField().getText();
        boolean isOK = (sId != null) && (cId != null) &&
                    !sId.equals("") && !cId.equals("");
        getOkButton().setEnabled(isOK);
        return isOK;
    }
    
    /**
     * Sets the actionListener that should be called when the OK button is pressed.
     * You don't need to deal with any UI issues here. They will be dealt with for
     * you. This actionListeners should only deal with buisness logic of what to
     * do with the parameters you've set in the UI. Make calls to getCameraIdentifier(),
     * getSessionIdentifier() and getUrl to get the parameters that were set.
     * 
     * @param actionListener
     */
    public void setOkActionListener(ActionListener actionListener) {
        okActionListener = actionListener;
    }
    
}

