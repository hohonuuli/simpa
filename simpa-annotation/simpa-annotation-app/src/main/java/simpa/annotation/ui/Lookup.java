/*
 * @(#)Lookup.java   2009.09.03 at 04:54:58 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package simpa.annotation.ui;

import com.google.inject.Injector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.core.CoreException;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public class Lookup {

    private static final String KEY_APP_FRAME = "Application Frame";

    /**
     * Publish to this topic when setting the current user name (as a String)
     */
    public static final Object KEY_CURRENT_USER = "current user";
    public static final String RESOURCE_BUNDLE = "simpa-annotation";

    /**
     * Publish to this topic when deleting annotations. The data should be
     * a List<TileAnnotation> that will be deleted
     */
    public static final String TOPIC_DELETE_TILEANNOTATIONS = "Delete tile annotations";

    /**
     * Topic that should be called when you want to exit the app. Don't write
     * subscribers that do any work when this is called...there me be one that
     * calls System.exit() before yours gets handled. Better to add a
     * shutdown hook to the Runtime to handle exiting tasks.
     */
    public static final String TOPIC_EXIT = "exit application";

    /**
     * Subscribers to this topic will get and {@link Exception} as the data
     */
    public static final String TOPIC_FATAL_ERROR = "FATAL ERROR!!";

    /**
     * Publish to this topic when inserting annotations. The data should be
     * a List<TileAnnotation> that will be deleted
     */ 
    public static final String TOPIC_INSERT_TILEANNOTATIONS = "Insert tile annotations";

    /**
     * Subscribers to this topic will get a {@link String} as the data
     */
    public static final String TOPIC_NONFATAL_ERROR = "Non-fatal error";
    public static final Object KEY_CURRENT_GUICE_INJECTOR = Injector.class;

    /**
     * Publish to this topic when updating the entire MosaicAssembly.
     * The data should be a @{link MosaicAssembly} that will be persisted
     */
    public static final String TOPIC_UPDATE_MOSAIC_ASSEMBLY = "Update mosaic assembly";
    public static final String TOPIC_UPDATE_TILEANNOTATIONS = "Update tile annotations";
    private static final Logger log = LoggerFactory.getLogger(Lookup.class);

    /**
     * A subscriber thta logs all events on all topics on the {@link EventBus}
     */
    private static final EventTopicSubscriber LOGGING_SUBSCRIBER = new EventTopicSubscriber() {

        public void onEvent(String topic, Object data) {
            if (log.isDebugEnabled()) {
                log.debug("EventBus event published\n\tTOPIC: " + topic + "\n\tDATA: " + data);
            }
        }
    };

    /**
     * Publish to this topic when setting the current {@link Tile}.
     */
    private static final Object KEY_CURRENT_TILE = Tile.class;

    /**
     * Publish to this topic when setting the current {@link MosaicAssembly}.
     */
    private static final Object KEY_CURRENT_MOSIAC_ASSEMBLY = MosaicAssembly.class;

    static {

        /*
        * Add some constraints to our Dispatchers so we don't accidently set
        * the wrong type of object.
        */
        Dispatcher dispatcher;

        dispatcher = Dispatcher.getDispatcher(KEY_CURRENT_MOSIAC_ASSEMBLY);
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                Object obj = evt.getNewValue();

                if ((obj != null) && !(obj instanceof MosaicAssembly)) {
                    throw new CoreException(
                        "The current mosaic-assembly must be an instance of simpa.core.MosaicAssembly");
                }
            }

        });

        dispatcher = Dispatcher.getDispatcher(KEY_CURRENT_TILE);
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                Object obj = evt.getNewValue();

                if ((obj != null) && !(obj instanceof Tile)) {
                    throw new CoreException("The current tile must be an instance of simpa.core.Tile");
                }
            }

        });

        dispatcher = Dispatcher.getDispatcher(KEY_CURRENT_USER);
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                Object obj = evt.getNewValue();

                if ((obj != null) && !(obj instanceof String)) {
                    throw new CoreException("The current user must be an instance of java.lang.String");
                }
            }

        });

        EventBus.subscribe(TOPIC_EXIT, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_FATAL_ERROR, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_NONFATAL_ERROR, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_UPDATE_MOSAIC_ASSEMBLY, LOGGING_SUBSCRIBER);
    }

    public static Dispatcher applicationFrameDispatcher() {
        return Dispatcher.getDispatcher(KEY_APP_FRAME);
    }

    public static Dispatcher currentGuiceInjectorDispatcher() {
        return Dispatcher.getDispatcher(KEY_CURRENT_GUICE_INJECTOR);
    }

    public static Dispatcher currentMosaicAssemblyDispatcher() {
        return Dispatcher.getDispatcher(KEY_CURRENT_MOSIAC_ASSEMBLY);
    }

    public static Dispatcher currentTileDispatcher() {
        return Dispatcher.getDispatcher(KEY_CURRENT_TILE);
    }

    public static Dispatcher currentUserDispatcher() {
        return Dispatcher.getDispatcher(KEY_CURRENT_USER);
    }
}
