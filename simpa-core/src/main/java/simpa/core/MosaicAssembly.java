/*
 * @(#)MosaicAssembly.java   2009.08.06 at 09:03:35 PDT
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

import java.util.List;

/**
 * A container for all the parts of a single mosaic.
 *
 * @author brian
 * @since Jun 5, 2008 4:00:45 PM
 *
 * @param <T>
 */
public interface MosaicAssembly<T extends Tile> {

    /**
     *
     * @return An identifier to determine the camera source. For example, it might be the
     *      name of an ROV used to collect the video
     */
    String getCameraIdentifier();

    /**
     *
     * @return A unique identifier used to determing the video session. For example,
     *      it would be the dive number of an ROV used to collect the video
     */
    String getSessionIdentifier();

    /**
     *
     * @return A List of Tiles contained in this mosaic assembly
     */
    List<T> getTiles();

    /**
     * Set the name of the camera source.
     * @param cameraIdentifier
     */
    void setCameraIdentifier(String cameraIdentifier);

    /**
     * Set the unique session identifier
     * @param sessionIdentier
     */
    void setSessionIdentifier(String sessionIdentier);
}
