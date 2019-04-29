package simpa.annotation.basic

import simpa.annotation.UserLookupService
import simpa.annotation.UserDefinedButtonInfo

/**
 *
 *
 * @author brian
 * @since Nov 8, 2008 5:24:38 PM
 */

public class BasicUserLookupService implements UserLookupService {

    public boolean isValidUser(String username, char[] password) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<UserDefinedButtonInfo> findButtonInformation(String username) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URL readImageTarget(String username, String hostname) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeImageTarget(String username, String hostname, URL targetURL) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public URL readImageTargetMapping(String username) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeImageTargetMapping(String username, URL targetURL) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}