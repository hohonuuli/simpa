/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.mock;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import simpa.annotation.UserDefinedButtonInfo;
import simpa.annotation.UserLookupService;

/**
 *
 * @author brian
 */
public class UserLookupMock implements UserLookupService {

    public boolean isValidUser(String username, char[] password) {
        return true;
    }

    public List<UserDefinedButtonInfo> findButtonInformation(String username) {
        return new ArrayList<UserDefinedButtonInfo>();
    }

    public URL readImageTarget(String username, String hostname) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void writeImageTarget(String username, String hostname, URL targetURL) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URL readImageTargetMapping(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void writeImageTargetMapping(String username, URL targetURL) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
