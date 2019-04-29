package simpa.annotation.jni;

import foxtrot.Job;
import foxtrot.Worker;
import gnu.io.CommPortIdentifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import simpa.annotation.ui.GlobalUIParameters;

public class RS422VideoControlServiceDialog extends JDialog {

    private JButton cancelButton;
    private JButton okButton;
    private JTextField textField;
    private JButton rescanPortsButton;
    private JComboBox comboBox;
    private JLabel frameRateLabel;
    private JLabel commPortLabel;
    private final RS422VideoControlService videoControlService;

    /**
     * Launch the application
     * @param args
     */
    public static void main(String args[]) {


        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    RS422VideoControlServiceDialog dialog = new RS422VideoControlServiceDialog(new RS422VideoControlService());
                    dialog.addWindowListener(new WindowAdapter() {

                        @Override
                        public void windowClosing(WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the dialog
     */
    protected RS422VideoControlServiceDialog(RS422VideoControlService videoControlService) {
        super();
        this.videoControlService = videoControlService;
        try {
            initialize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        scanForOpenPortsAndUpdateUI();

    //
    }

    private void initialize() throws Exception {
		final GroupLayout groupLayout = new GroupLayout((JComponent) getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(GroupLayout.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.add(groupLayout.createParallelGroup(GroupLayout.TRAILING)
						.add(groupLayout.createSequentialGroup()
							.add(getCancelButton())
							.addPreferredGap(LayoutStyle.RELATED)
							.add(getOkButton()))
						.add(GroupLayout.LEADING, groupLayout.createSequentialGroup()
							.add(getCommPortLabel())
							.addPreferredGap(LayoutStyle.RELATED)
							.add(getComboBox(), 0, 272, Short.MAX_VALUE))
						.add(GroupLayout.LEADING, groupLayout.createSequentialGroup()
							.add(getFrameRateLabel())
							.addPreferredGap(LayoutStyle.RELATED)
							.add(getTextField(), GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(getRescanPortsButton())
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.addContainerGap()
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getCommPortLabel())
						.add(getRescanPortsButton())
						.add(getComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getFrameRateLabel())
						.add(getTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(getOkButton())
						.add(getCancelButton()))
					.addContainerGap(13, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
		pack();
	}

    /**
     * @return
     */
    protected JLabel getCommPortLabel() {
        if (commPortLabel == null) {
            commPortLabel = new JLabel();
            commPortLabel.setText("Comm Port:");
        }
        return commPortLabel;
    }

    /**
     * @return
     */
    protected JLabel getFrameRateLabel() {
        if (frameRateLabel == null) {
            frameRateLabel = new JLabel();
            frameRateLabel.setText("Frame Rate:");
        }
        return frameRateLabel;
    }

    /**
     * @return
     */
    protected JComboBox getComboBox() {
        if (comboBox == null) {
            comboBox = new JComboBox();
            comboBox.setPreferredSize(new java.awt.Dimension(200, 25));
        }
        return comboBox;
    }

    /**
     * @return
     */
    protected JButton getRescanPortsButton() {
        if (rescanPortsButton == null) {
            rescanPortsButton = new JButton();
            rescanPortsButton.setText("Rescan Ports");
            rescanPortsButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    scanForOpenPortsAndUpdateUI();
                }
            });
        }
        return rescanPortsButton;
    }

    /**
     * @return
     */
    protected JTextField getTextField() {
        if (textField == null) {
            textField = new JFormattedTextField(new DecimalFormat("##.##"));
            textField.setText("29.97");
        }
        return textField;
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
                    Double frameRate = Double.parseDouble(getTextField().getText());
                    String portName = (String) getComboBox().getSelectedItem();
                    try {
                        videoControlService.connect(portName, frameRate);
                        setVisible(false);
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                "Failed to set up video controller",
                                "SIMPA: Connection Failure", JOptionPane.ERROR_MESSAGE);
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
                    setVisible(false);
                }
            });
        }
        return cancelButton;
    }

    private void scanForOpenPortsAndUpdateUI() {

        // Close open ports
        videoControlService.disconnect();

        JComboBox cb = getComboBox();
        DefaultComboBoxModel model = (DefaultComboBoxModel) cb.getModel();
        model.removeAllElements();

        WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(this.getRootPane(),
                    "Scanning for Serial Ports", GlobalUIParameters.WAITINDICATOR_FONT);
        List<CommPortIdentifier> ports = (List<CommPortIdentifier>) Worker.post(new Job() {

            @Override
            public Object run() {
                // Return a list of CommPortIdentifiers sorted by name
                List<CommPortIdentifier> p = new ArrayList<CommPortIdentifier>(videoControlService.findAvailableCommPorts());
                Collections.sort(p, new CommPortIdentifierComparator());
                return p;
            }

        });
        waitIndicator.dispose();

        for (CommPortIdentifier commPortIdentifier : ports) {
            model.addElement(commPortIdentifier.getName());
        }
    }

    /**
     * Used for sorting commportidentifiers by name
     */
    private class CommPortIdentifierComparator implements Comparator<CommPortIdentifier> {

        public int compare(CommPortIdentifier o1, CommPortIdentifier o2) {
            return o1.getName().compareTo(o2.getName());
        }

    }
}

