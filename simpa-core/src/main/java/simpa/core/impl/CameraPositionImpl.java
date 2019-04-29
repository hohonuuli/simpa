/*
 * @(#)CameraPositionImpl.java   2009.08.06 at 09:03:36 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package simpa.core.impl;

import org.mbari.geometry.Point3D;
import simpa.core.CameraPosition;

/**
 * The position of the camera. This is a generic version that could be
 * used by most implementations of the SIMPA data interfaces.
 */
public class CameraPositionImpl extends Point3D<Double> implements CameraPosition {

    private final double heading;
    private final double pitch;
    private final double roll;

    /**
     * Constructs ...
     *
     * @param x
     * @param y
     * @param z
     * @param pitch
     * @param roll
     * @param heading
     */
    public CameraPositionImpl(double x, double y, double z, double pitch, double roll, double heading) {
        super(x, y, z);
        this.pitch = pitch;
        this.roll = roll;
        this.heading = heading;
    }

    /**
     * TODO what is this heading relative to?
     * @return relative headings (radians)
     */
    public double getHeading() {
        return heading;
    }

    /**
     *
     * @return pitch angle (radians)
     */
    public double getPitch() {
        return pitch;
    }

    /**
     *
     * @return roll angle (radians)
     */
    public double getRoll() {
        return roll;
    }
}
