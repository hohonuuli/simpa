/*
 * @(#)AnchorPoint.java   2009.06.30 at 11:04:36 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simpa.annotation;

import org.mbari.geometry.Point2D;

/**
 *
 * @author brian
 */
public class AnchorPoint {

    private String name;
    private Point2D<Double> point;

    public String getName() {
        return name;
    }

    public Point2D<Double> getPoint() {
        return point;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoint(Point2D<Double> point) {
        this.point = point;
    }
}


