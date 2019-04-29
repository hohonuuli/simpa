/*
 * @(#)CameraPosition.java   2009.08.06 at 09:03:35 PDT
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

/**
 * Represents the POV of the camera. The position coordiantes are:
 * <ul>
 * <li>X - meters. +right/-left</li>
 * <li>Y - meters. +forward/ -backward</li>
 * <li>Z - meters. +down/-up. 0 is surface of ocean</li>
 * </ul>
 *
 * @author brian
 * @since Jun 5, 2008 3:07:10 PM
 */
public interface CameraPosition {

    /**
     * Relative heading in a 'geographic' notation. Geographic notation is
     * positive angles are clockwise. The initial heading here is the direction
     * of camera travel in the first tile. All angles in a mosaic are relative
     * to this initial heading. For converting between geographic angles and
     * math angles see the {@link org.mbari.gis.util.GISUtilities}.geoToMath
     * method in the mbarixj project.
     *
     * @return relative headings (radians)
     */
    double getHeading();

    /**
     *
     * @return pitch angle (radians)
     */
    double getPitch();

    /**
     *
     * @return roll angle (radians)
     */
    double getRoll();

    /**
     *
     * @return x position (positive right meters) relative to origin
     */
    Double getX();

    /**
     *
     * @return y position (positive forward (top)) relative to origin
     */
    Double getY();

    /**
     *
     * @return z position (positive down meters)
     */
    Double getZ();
}
