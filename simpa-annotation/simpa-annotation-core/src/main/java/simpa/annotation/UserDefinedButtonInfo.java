package simpa.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents metadata about predefined buttons used in the annotation Application. 
 * Currently these buttons are created only in the VARS annotation app but are
 * imported into the simpa application.
 * 
 * @author brian
 *
 */
public class UserDefinedButtonInfo {

    private final String conceptName;
    private final String tabName;
    private final boolean valid;
    private final Integer order;

    public UserDefinedButtonInfo(String conceptName, String tabName, boolean valid, Integer order) {
        this.conceptName = conceptName;
        this.tabName = tabName;
        this.valid = valid;
        this.order = order;
    }

    /**
     * 
     * @return The concept Name that the button should represent
     */
    public String getConceptName() {
        return conceptName;
    }

    /**
     * 
     * @return The name of the group that a button belongs to.
     */
    public String getTabName() {
        return tabName;
    }

    /**
     * 
     * @return <b>true</b> if the conceptName thsi button represents was found
     *      is valid and can be used. <b>false</b> otherwise. If false the button
     *      that uses this button info should be disabled.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * 
     * @return The order that the button should appear on a tab.
     */
    public Integer getOrder() {
        return order;
    }
    
    public static Set<String> extractTabNames(Collection<UserDefinedButtonInfo> buttonInfos) {
        Set<String> tabNames = new HashSet<String>();
        for (UserDefinedButtonInfo bi : buttonInfos) {
            tabNames.add(bi.getTabName());
        }
        return tabNames;
    }
    
    /**
     * Returns a list of UserDefinedButtonInfo objects in a collection that 
     * correspond to a particular tabName. The info objects will be sorted in the
     * List by the order they should appear in the tabged panel
     * 
     * @param buttonInfos The buttonInfo objects to process
     * @param tabName The name of the tab we're nterested in.
     * @return A List of objects that correpond to a particular tab sorted in
     *      the order that they should appear in.
     */
     
    public static List<UserDefinedButtonInfo> extractButtons(Collection<UserDefinedButtonInfo> buttonInfos, String tabName) {
        List<UserDefinedButtonInfo> buttons = new ArrayList<UserDefinedButtonInfo>();
        for (UserDefinedButtonInfo bi : buttonInfos) {
            if (bi.getTabName().equals(tabName)) {
                buttons.add(bi);
            }
        }

        Collections.sort(buttons, new Comparator<UserDefinedButtonInfo>() {
            public int compare(UserDefinedButtonInfo o1, UserDefinedButtonInfo o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });
        
        return buttons;
    }
}
