/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.ui;

import com.google.inject.Binder;
import com.google.inject.Module;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages Guice dependency injection here. This looks up the guice.module
 * property in The simpa-annotation.properties file for the class name of the Guice module
 * to bind at runtime.
 *
 * @author brian
 */
public class AppModule implements Module {

    public final Logger log = LoggerFactory.getLogger(getClass());

    public void configure(Binder binder) {

        /*
         * Lookup Guice module name then activate dependency injection. We'll set the
         * injector in the dispatcher so that other components can access it.
         */
        ResourceBundle bundle = ResourceBundle.getBundle(Lookup.RESOURCE_BUNDLE);
        final String moduleName = bundle.getString("guice.module");
        Module module = null;

        try {
            log.debug("Loading Guice Module: " + moduleName);
            module = (Module) Class.forName(moduleName).newInstance();
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to instantiate Guice module for dependency injection", ex);
        }

        binder.install(module);
    }

} 
