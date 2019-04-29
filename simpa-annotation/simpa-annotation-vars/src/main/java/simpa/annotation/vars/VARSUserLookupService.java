/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import com.google.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import simpa.annotation.UserDefinedButtonInfo;
import simpa.annotation.UserLookupException;
import simpa.annotation.UserLookupService;
import simpa.core.CoreException;
import simpa.core.GlobalParameters;
import vars.UserAccount;
import vars.MiscDAOFactory;
import vars.jpa.VarsUserPreferencesFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.KnowledgebaseDAOFactory;

/**
 *
 * @author brian
 */
public class VARSUserLookupService implements UserLookupService {

    private static final String PROP_IMAGETARGET = "imageTarget";
    private static final String PROP_IMAGETARGETMAPPING = "imageTargetMapping";
    private static URL DEFAULT_IMAGETARGET;

    private final MiscDAOFactory daoFactory;
    private final VarsUserPreferencesFactory preferencesFactory;
    private final KnowledgebaseDAOFactory kbDaoFactory;

    @Inject
    public VARSUserLookupService(MiscDAOFactory daoFactory, VarsUserPreferencesFactory preferencesFactory, KnowledgebaseDAOFactory kbDaoFactory) {
        try {
            // Call toURI first to escape out illegal URL characters
            DEFAULT_IMAGETARGET = GlobalParameters.IMAGE_DIRECTORY.toURI().toURL();
        }
        catch (MalformedURLException ex) {
            // This should never get thrown... ;-)
            throw new UserLookupException("Failed to map default image target to a URL", ex);
        }
        this.daoFactory = daoFactory;
        this.preferencesFactory = preferencesFactory;
        this.kbDaoFactory = kbDaoFactory;
    }

    /**
     *
     * @param username
     * @param password
     * @throws CoreException Thrown if lookup fails
     * @return <b>true<b> if the user is validated
     */
    public boolean isValidUser(String username, char[] password) {

        boolean valid = false;

        UserAccount userAccount = null;
        try {
            userAccount = daoFactory.newUserAccountDAO().findByUserName(username);
        }
        catch (Exception ex) {
            throw new UserLookupException("Failed to lookup '" + username + "'", ex);
        }

        if (userAccount != null) {
            valid = userAccount.authenticate(new String(password));
        }

        return valid;

    }

    public List<UserDefinedButtonInfo> findButtonInformation(String username) {
        List<UserDefinedButtonInfo> buttonInfos = new ArrayList<UserDefinedButtonInfo>();

        // Get preferences for the specific user. (e.g. '/username/%')
        final Preferences userPreferences = preferencesFactory.userRoot(username);

        if (userPreferences != null) {

            // ConceptPanel preferences are stored under '/username/CP/%'
            final Preferences cpPreferences = userPreferences.node("CP");
            if (cpPreferences != null) {

                /*
                 * tabIds will be tab0, tab1, tab2, etc. (e.g.  '/username/CP/tab0/%' )
                 */
                String[] tabIds = null;
                try {
                    tabIds = cpPreferences.childrenNames();
                }
                catch (final BackingStoreException bse) {
                    throw new CoreException("Failed to lookup tabnames from saved preferences", bse);
                }

                /*
                 * The names of the buttons are like '/username/CP/tab0/grimpoteuthis/%'
                 */
                for (String tabId : tabIds) {
                    final Preferences tabPreferences = cpPreferences.node(tabId);
                    String tabName = tabPreferences.get("tabName", "Undefined");
                    // First grab all the button names from the tab userPrefs
                    String[] buttonNames = null;
                    try {
                        buttonNames = tabPreferences.childrenNames();
                    }
                    catch (final BackingStoreException bse) {
                        throw new CoreException("Unable to load button preferences from saved preferences", bse);
                    }

                    for (String btnName : buttonNames) {

                        Preferences btnPreferences = tabPreferences.node(btnName);
                        int btnOrder = btnPreferences.getInt("buttonOrder", 0);

                        IConcept concept = null;
                        try {
                            concept = kbDaoFactory.newConceptDAO().findByName(btnName);
                        }
                        catch (Exception e) {
                            throw new CoreException("A database error occured when looking up '" + btnName + "'", e);
                        }

                        boolean valid = concept != null && concept.getPrimaryConceptName().getName().equals(btnName);
                        UserDefinedButtonInfo btnInfo = new UserDefinedButtonInfo(btnName, tabName, valid, btnOrder);
                        buttonInfos.add(btnInfo);
                    }
                }
            }
        }

        return buttonInfos;
    }

    /**
     * Read the URL used to write images to.
     *
     * @param username
     * @param hostname
     * @return The Base URL where images should be written into
     */
    public URL readImageTarget(String username, String hostname) {
        Preferences preferences = hostPrefs(username, hostname);
        String value = preferences.get(PROP_IMAGETARGET, DEFAULT_IMAGETARGET.toExternalForm());
        URL imageTarget = null;
        try {
            imageTarget = new URL(value);
        }
        catch (MalformedURLException ex) {
            throw new UserLookupException("Failed to convert '" + value + "' to a URL", ex);
        }
        return imageTarget;
    }

    /**
     * Write the URL used to write images to.
     *
     * @param username The username
     * @param hostname The current hostname fo the platform that SIMPA is running on
     * @param targetURL The URL to write images to.
     */
    public void writeImageTarget(String username, String hostname, URL targetURL) {
        Preferences preferences = hostPrefs(username, hostname);
        preferences.put(PROP_IMAGETARGET, targetURL.toExternalForm());
    }

    /**
     * Read the URL used to read images from a web server that were written to
     * <i>imageTarget</i>.
     *
     * @param username The name of the user as logged into SIMPA
     * @return The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public URL readImageTargetMapping(String username) {
        Preferences preferences = userPrefs(username);
        String value = preferences.get(PROP_IMAGETARGETMAPPING, DEFAULT_IMAGETARGET.toExternalForm());
        URL imageTarget = null;
        try {
            imageTarget = new URL(value);
        }
        catch (MalformedURLException ex) {
            throw new UserLookupException("Failed to convert '" + value + "' to a URL", ex);
        }
        return imageTarget;
    }

    /**
     * Write the URL used to read images from a web server that were written to
     * <i>imageTarget</i>.
     *
     * @param username The name of the user as logged into SIMPA
     * @param targetMappingURL The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public void writeImageTargetMapping(String username, URL targetMappingURL) {
        Preferences preferences = userPrefs(username);
        preferences.put(PROP_IMAGETARGETMAPPING, targetMappingURL.toExternalForm());
    }

    /**
     * Looks up the node that contains the preferences for the UserLookupService
     * given a username and hostname. THe node key is something like:
     * /[username]/simpa/annotation/UserLookupService/[hostname]
     *
     * @param username The name of the user as logged into SIMPA
     * @param hostname The host name of the computer running SIMPA
     * @return The preference node like /[username]/simpa/annotation/UserLookupService/[hostname]
     */
    private Preferences hostPrefs(String username, String hostname) {
        final Preferences lookupPreferences = userPrefs(username);
        return lookupPreferences.node(hostname);
    }

    /**
     * Looks up the node that contains the preferences for the UserLookupService
     * given a username and hostname. THe node key is something like:
     * /[username]/simpa/annotation/UserLookupService
     *
     * @param username The name of the user as logged into SIMPA
     * @param hostname The host name of the computer running SIMPA
     * @return The preference node like /[username]/simpa/annotation/UserLookupService
     */
    private Preferences userPrefs(String username) {
        final Preferences userPreferences = preferencesFactory.userRoot(username);
        String node = UserLookupService.class.getCanonicalName().replace(".", "/");
        return userPreferences.node(node);
    }

}
