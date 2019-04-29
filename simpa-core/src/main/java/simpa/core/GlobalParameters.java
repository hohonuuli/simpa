/*
 * @(#)GlobalParameters.java   2009.08.06 at 09:03:35 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package simpa.core;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Bucket class for constants used by SIMPA classes and applications
 *
 * @author brian
 */
public class GlobalParameters {

    /**
     * Standard format for all Dates used in SIMPA. No timezone is displayed.
     * THe date will be formatted for the UTC timezone
     */
    public static final DateFormat DATE_FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Formats the dates using whatever the local timezone is. TImezone info
     * is displayed by this formatter
     */
    public static final DateFormat DATE_FORMAT_LOCAL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    /**
     * This is the sandbox directory used by simpa applications
     */
    public static final File PREFERENCES_DIRECTORY = new File(System.getProperty("user.home"), ".simpa");

    /**
     * Directory to write images to, if needed.
     */
    public static final File IMAGE_DIRECTORY = new File(PREFERENCES_DIRECTORY, "images");

    static {
        DATE_FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
}
