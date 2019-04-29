package simpa.annotation.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.mbari.swing.FancyComboBox;
import org.mbari.swing.SortedComboBoxModel;
import org.mbari.text.IgnoreCaseToStringComparator;

/**
 * A ComboBox with a ToggleButton. Meant to allow users to enter any
 * concept found in the knowlegebase.
 * @author brian
 */
public class ConceptWidget extends JPanel {

    private JToggleButton toggleButton;
    private JComboBox comboBox;
    /**
     * Create the panel
     */
    public ConceptWidget(Collection<String> names) {
        super();
        SortedComboBoxModel model = (SortedComboBoxModel) getComboBox().getModel();
        model.addAll(names);
        initialize();
        //
    }
    private void initialize() {
        setLayout(new BorderLayout());
        add(getComboBox(), BorderLayout.CENTER);
        add(getToggleButton(), BorderLayout.EAST);
    }
    /**
     * @return
     */
    protected JComboBox getComboBox() {
        if (comboBox == null) {
            comboBox = new FancyComboBox(new IgnoreCaseToStringComparator());
            comboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    final JToggleButton tb = getToggleButton();
                    tb.setEnabled(true);
                    tb.setText((String) comboBox.getSelectedItem());
                    tb.setSelected(true);
                }
            });
        }
        return comboBox;
    }
    /**
     * @return
     */
    protected JToggleButton getToggleButton() {
        if (toggleButton == null) {
            toggleButton = new JToggleButton();
            toggleButton.setText("    ");
            toggleButton.putClientProperty("JButton.buttonType", "textured");
            toggleButton.setEnabled(false);
        }
        return toggleButton;
    }


    /**
     *  Returns the selected conceptname.
     * @return
     */
    public String getSelectedConceptName() {
        return (String) getComboBox().getModel().getSelectedItem();
    }

}
