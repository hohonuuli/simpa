/*
 * @(#)Tile.java   2009.08.06 at 09:03:34 PDT
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

import java.net.URL;
import java.util.Date;

/**
 * A Tile represents a rectangular image as viewed from a particular camera view point.
 *
 * @author brian
 * @since Jun 5, 2008 3:06:27 PM
 */
public interface Tile {

    /**
     * @return The position of the camera when the image for this tile was
     *      captured.
     */
    CameraPosition getCameraPosition();

    /**
     * Dates are all caputred as GMT
     * @return The date when the tile was captured.
     */
    Date getDate();

    /**
     * @return The height (y-axis) of the tile in meters
     */
    double getHeightInMeters();

    /**
     *
     * @return The index of the tile in a sequence of tiles
     */
    int getTileIndex();

    /**
     *
     * @return The url to the image referenced by this tile.
     */
    URL getUrl();

    /**
     *
     * @return The width (x-axis) of the tile in meters
     */
    double getWidthInMeters();

    void setCameraPosition(CameraPosition cameraPosition);

    void setDate(Date date);

    void setHeightInMeters(double height);

    void setTileIndex(int tileIndex);

    void setUrl(URL url);

    void setWidthInMeters(double width);
}
