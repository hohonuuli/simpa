/*
 * @(#)DataIngestorService.java   2009.08.06 at 09:03:35 PDT
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
import java.util.List;

/**
 * Interface definition for a service that reads tile information from a URL and
 * generates a SIMPA data objects.
 *
 *
 * @version        $date$, 2009.08.06 at 09:03:35 PDT
 * @author         Brian Schlining [brian@mbari.org]    
 */
public interface DataIngestorService {


    /**
     *
     * @param url The URL to read the tile information from
     * @return A list of Tile objects
     */
    List<Tile> loadTiles(URL url);
}
