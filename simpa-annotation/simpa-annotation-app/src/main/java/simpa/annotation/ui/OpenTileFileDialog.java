package simpa.annotation.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for setting the values needed to open and correctly resolve a Tile File.
 * These parameters are <b>cameraIdentifier</b>, <b>sessionIdentifier</b>, 
 * <b>url</b>. This class does nothing by itself. You will need to add an
 * actionlistener via the <b>setOkActionListener</b> method to perform any 
 * non-UI related processing of the parameters.
 * 
 * @author brian
 */
public class OpenTileFileDialog extends JDialog {

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
    private URL url;
    
    private final JFileChooser fileChooser = new JFileChooser();

    /**
     * Launch the application
     * @param args
     */
    public static void main(String args[]) {
        try {
            OpenTileFileDialog dialog = new OpenTileFileDialog();
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
    public OpenTileFileDialog() {
        super();
        try {
            initialize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    //
    }
    
    public OpenTileFileDialog(JFrame parent) {
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
            urlTextField.getDocument().addDocumentListener(new DocumentListener() {

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
        return urlTextField;
    }

    /**
     * @return
     */
    protected JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse");
            browseButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    int option = fileChooser.showOpenDialog(OpenTileFileDialog.this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File tileFile = fileChooser.getSelectedFile();
                        try {
                            getUrlTextField().setText(tileFile.toURL().toExternalForm());
                        } catch (MalformedURLException ex) {
                            log.error("Failed to parse " + tileFile.getAbsolutePath(), ex);
                            getUrlTextField().setText("");
                        }
                    }
                }
            });
            
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
                        try {
                            url = new URL(getUrlTextField().getText());
                        } catch (MalformedURLException ex) {
                            log.error("Invalid URL was specified", ex);
                        }
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
                    getUrlTextField().setText("");
                    cameraIdentifier = null;
                    sessionIdentifier = null;
                    url = null;
                            
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
    
    public URL getUrl() {
        return url;
    }
    
    private boolean validateInputValues() {
        boolean isOK = false;
        Color urlLabelColor = Color.BLACK;
        
        String sId = getSessionIdentifierTextField().getText();
        String cId = getCameraIdentifierTextField().getText();
        String urlText = getUrlTextField().getText();
        
        if (urlText != null && !urlText.equals("")) {
            try {
                url = new URL(getUrlTextField().getText());
                isOK = (url != null) && (sId != null) && (cId != null) &&
                    !sId.equals("") && !cId.equals("");
            } catch (MalformedURLException ex) {
                urlLabelColor = Color.RED;
            }
        }
        
        getOkButton().setEnabled(isOK);
        getUrlLabel().setForeground(urlLabelColor);
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
