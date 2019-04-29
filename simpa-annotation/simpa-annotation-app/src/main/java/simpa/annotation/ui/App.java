package simpa.annotation.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launcher for the Simpa Annotation Application
 *
 */
public class App {

    /**
     * To loosely couple our components, I'm using an event bus to
     * monitor for shutdown messages. We need to hang on to the reference
     * to the EventTopicSubscriber so that it doesn't get garbage collected.
     *
     * This subscriber handles shutdown.
     */
    private final EventTopicSubscriber exitSubscriber = new EventTopicSubscriber() {

        public void onEvent(String topic, Object data) {
            System.exit(0);
        }
    };
    /**
     * This subscriber displays and logs non-fatal errors
     */
    private final EventTopicSubscriber nonFatalErrorSubscriber = new EventTopicSubscriber() {

        public void onEvent(String topic, Object error) {

            String msg = "An error occurred. Refer to details for more information.";
            String details = null;
            Throwable data = null;
            if (error instanceof Throwable) {
                data = (Throwable) error;
                details = formatStackTraceForDialogs(data, true);
            }
            else {
                details = error.toString();
            }


            /*
             * Create an error pane to display the error stuff
             */
            JXErrorPane errorPane = new JXErrorPane();
            Icon errorIcon = new ImageIcon(getClass().getResource("/images/yellow-smile.jpg"));
            ErrorInfo errorInfo = new ErrorInfo("VARS - Something exceptional occured (and we don't like that)", msg, details, null, data, ErrorLevel.WARNING, null);
            errorPane.setIcon(errorIcon);
            errorPane.setErrorInfo(errorInfo);
            JXErrorPane.showDialog((JFrame) Lookup.applicationFrameDispatcher().getValueObject(), errorPane);
        }
    };

    /**
     * This subscriber should display a warning message on a fatal error. When
     * the OK button is clicked a notificaiton to the EXIT_TOPIC should be sent
     */
    private final EventTopicSubscriber fatalErrorSubscriber = new EventTopicSubscriber<Exception>() {

        public void onEvent(String topic, Exception data) {

            String msg = randomHaiku();
            String details = formatStackTraceForDialogs(data, false);

            /*
             * Create an error pane to display the error stuff
             */
            JXErrorPane errorPane = new JXErrorPane();
            Icon errorIcon = new ImageIcon(getClass().getResource("/images/red-frown_small.png"));
            ErrorInfo errorInfo = new ErrorInfo("VARS - Fatal Error", msg, details, null, data, ErrorLevel.FATAL, null);
            errorPane.setIcon(errorIcon);
            errorPane.setErrorInfo(errorInfo);
            JXErrorPane.showDialog((JFrame) Lookup.applicationFrameDispatcher().getValueObject(), errorPane);

        }

        String randomHaiku() {
            final List<String> haikus = new ArrayList<String>() {

                {
                    add("Chaos reigns within.\nReflect, repent, and restart.\nOrder shall return.");
                    add("Errors have occurred.\nWe won't tell you where or why.\nLazy programmers.");
                    add("A crash reduces\nyour expensive computer\nto a simple stone.");
                    add("There is a chasm\nof carbon and silicon\nthe software can't bridge");
                    add("Yesterday it worked\nToday it is not working\nSoftware is like that");
                    add("To have no errors\nWould be life without meaning\nNo struggle, no joy");
                    add("Error messages\ncannot completely convey.\nWe now know shared loss.");
                    add("The code was willing,\nIt considered your request,\nBut the chips were weak.");
                    add("wind catches lily\nscatt'ring petals to the wind:\napplication dies");
                    add("Three things are certain:\nDeath, taxes and lost data.\nGuess which has occurred.");
                    add("Rather than a beep\nOr a rude error message,These words: \"Restart now.\"");
                }
            };

            return haikus.get((int) Math.floor(Math.random() * haikus.size()));

        }
    };

    public App() {

        final Logger log = LoggerFactory.getLogger(App.class);

        log.debug("Starting SIMPA annotations application using " + getClass().getName());

        /*
         * Subscribe to all our favorite topics
         */
        EventBus.subscribe(Lookup.TOPIC_EXIT, exitSubscriber);
        EventBus.subscribe(Lookup.TOPIC_FATAL_ERROR, fatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_NONFATAL_ERROR, nonFatalErrorSubscriber);

        /*
         * Lookup Guice module name then activate dependency injection. We'll set the
         * injector in the dispatcher so that other components can access it.
         */

        /*
         * Activate dependency injection
         */
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AppModule());
        Lookup.currentGuiceInjectorDispatcher().setValueObject(injector); // Store injector in case we need it else where

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                try {
                    /*
                     * MUST instantiate the root class using Guice so that it can
                     * walk the transitive Injection points and inject our
                     * dependencies.
                     */
                    JFrame f = injector.getInstance(AppFrame.class);
                    f.setPreferredSize(new Dimension(400, 400));
                    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    f.setLocationRelativeTo(null);
                    f.setVisible(true);
                }
                catch (Exception e) {
                     log.error("Failure occured on startup", e);
                     EventBus.publish(Lookup.TOPIC_FATAL_ERROR, e);
                }
            }
        });
    }

    /**
     * Defines a custom format for the stack trace as String.
     */
    String formatStackTraceForDialogs(Throwable throwable, boolean isCause) {

        //add the class name and any message passed to constructor
        final StringBuilder result = new StringBuilder();
        result.append("<h2>");
        if (isCause) {
            result.append("Caused by: ");
        }

        result.append(throwable.toString()).append("</h2>");
        final String newLine = "<br/>";

        //add each element of the stack trace
        for (StackTraceElement element : throwable.getStackTrace()) {
            result.append(element);
            result.append(newLine);
        }

        final Throwable cause = throwable.getCause();
        if (cause != null) {
            result.append(formatStackTraceForDialogs(cause, true));
        }

        return result.toString();
    }

    public static void main(String[] args) {

        /**
         * We like to do all database transaction in the UTC timezone
         */
        System.setProperty("user.timezone", "UTC");


        /*
         * Make it pretty on Macs
         */
        if (SystemUtilities.isMacOS()) {
            SystemUtilities.configureMacOSApplication("SIMPA Annotation");
        }

        /*
         * Create an application settings directory if needed
         */
        String home = System.getProperty("user.home");
        File settingsDirectory = new File(home, ".simpa");
        if (!settingsDirectory.exists()) {
            settingsDirectory.mkdir();
        }
        File logDirectory = new File(settingsDirectory, "logs");
        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }

        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            LoggerFactory.getLogger(App.class).warn("Failed to set system look and feel", e);
        }

        try {
            new App();
        }
        catch (Throwable e) {
            LoggerFactory.getLogger(App.class).warn("An error occurred on startup", e);
        }
    }
}
