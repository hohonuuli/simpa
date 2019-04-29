/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

/**
 * Encapsulates the login credentials of a user
 * @author brian
 */
public class LoginCredential {
    
    private final String login;
    private final String hostName;
    private final char[] password;

    public LoginCredential(String login, char[] password, String hostName) {
        this.login = login;
        this.password = password;
        this.hostName = hostName;
    }

    /**
     * 
     * @return The login name of the user
     */
    public String getLogin() {
        return login;
    }

    /**
     * 
     * @return The password used for login
     */
    public char[] getPassword() {
        return password;
    }
    
    /**
     * 
     * @return The specified hostName. For the simpa apps this will be the computer
     *      running the applications
     */
    public String getHostName() {
        return hostName;
    }

}
