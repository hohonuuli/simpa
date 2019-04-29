/*
 * @(#)TileImpl.java   2009.08.06 at 09:03:35 PDT
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

import java.net.URL;
import java.util.Date;
import simpa.core.CameraPosition;
import simpa.core.GlobalParameters;
import simpa.core.Tile;

/**
 * Representation of a single source image in the mosaic
 */
public class TileImpl implements Tile {

    private CameraPosition cameraPosition;
    private Date date;
    private double height;
    private int tileIndex;
    private URL url;
    private double width;

    /**
     * Constructs ...
     */
    public TileImpl() {
        super();
    }

    /**
     * Constructs ...
     *
     * @param tileIndex
     * @param width
     * @param height
     * @param url
     * @param cameraPosition
     * @param date
     */
    public TileImpl(int tileIndex, double width, double height, URL url, CameraPosition cameraPosition, Date date) {
        this.tileIndex = tileIndex;
        this.width = width;
        this.height = height;
        this.url = url;
        this.cameraPosition = cameraPosition;
        this.date = date;
    }

    /**
     * @return The position of the camera when the image for this tile was
     *      captured.
     */
    public CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    /**
     * Dates are all caputred as GMT
     * @return The date when the tile was captured.
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return The height (y-axis) of the tile in meters
     */
    public double getHeightInMeters() {
        return height;
    }

    /**
     *
     * @return The index of the tile in a sequence of tiles
     */
    public int getTileIndex() {
        return tileIndex;
    }

    /**
     *
     * @return The url to the image referenced by this tile.
     */
    public URL getUrl() {
        return url;
    }

    /**
     *
     * @return The width (x-axis) of the tile in meters
     */
    public double getWidthInMeters() {
        return width;
    }

    public void setCameraPosition(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHeightInMeters(double height) {
        this.height = height;
    }

    public void setTileIndex(int tileIndex) {
        this.tileIndex = tileIndex;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setWidthInMeters(double width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "<TileImpl date=\"" + GlobalParameters.DATE_FORMAT_UTC.format(date) + "\" x=\"" +
               cameraPosition.getX() + "\" y=\"" + cameraPosition.getY() + "\" />";
    }
}
