package simpa.annotation;

import java.net.URL;
import java.util.List;

public interface UserLookupService {

    /**
     * Checks to see if the username/password combo is valid.
     * 
     * @param username
     * @param password
     * @return
     */
    boolean isValidUser(String username, char[] password);

    /**
     * Looksup a users predefined buttons from the userpreferences.
     * @param username The name of the user to lookup
     * @return A List describing the button information needed.
     */
    List<UserDefinedButtonInfo> findButtonInformation(String username);
    
    /**
     * As a user preference, write the URL that the images should be written
     * to in the user preferences or data store. Normally this is a directory on 
     * a web server but other protocals may be implemented in the the future, 
     * such as scp, dav, ftp.
     * 
     * @param username The name of the user
     * @param hostname THe hostname of the computer
     * @return A URL used to upload the images
     */
    URL readImageTarget(String username, String hostname);
    
    /**
     * Read the user preference of the {@link URL} of where to write images to.
     * 
     * @param username THe name of the user
     * @param hostname The hostname of the computer
     * @param targetURL A URL used to upload the images.
     */
    void writeImageTarget(String username, String hostname, URL targetURL);
    
    /**
     * Read the user preference of that maps the imageTarget to an actual URL 
     * on a web server. For example the <i>imageTarget</i> might be 
     * <i>scp://host@/var/www/html/images</i> and the <i>imageTargetMapping</i>
     * might be <i>http://host.com/images</i>.
     * 
     * @param username The name of the user
     * @return The web accessable URL that corresponds the <i>imageTarget</i>.
     */
    URL readImageTargetMapping(String username);
    
    /**
     * Write the use preference that map the <i>imageTarget</i> to an actual
     * URL on a web werver.
     * 
     * @param username The name of the user
     * @param targetURL The web accessable URL that corresponds the <i>imageTarget</i>.
     */
    void writeImageTargetMapping(String username, URL targetURL);
}
