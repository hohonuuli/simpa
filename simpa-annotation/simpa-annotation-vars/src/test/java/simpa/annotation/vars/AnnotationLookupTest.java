/*
 * @(#)AnnotationLookupTest.java   2009.09.21 at 10:35:04 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package simpa.annotation.vars;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.VideoTile;
import simpa.annotation.VideoTileDateComparator;
import simpa.annotation.VideoTileIndexComparator;
import simpa.annotation.VideoTileTimecodeComparator;
import simpa.core.DataIngestorService;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;

/**
 *
 * @author brian
 */
public class AnnotationLookupTest {

    private static final Logger log = LoggerFactory.getLogger(AnnotationLookupTest.class);
    private DataIngestorService ingest;
    private VARSModelConverter modelConverter;

    @Ignore
    @Test
    public void convertToMosaicAssembly() {

        VideoTileDateComparator dateComparator = new VideoTileDateComparator();
        VideoTileTimecodeComparator timecodeComparator = new VideoTileTimecodeComparator();
        VideoTileIndexComparator indexComparator = new VideoTileIndexComparator();

        // Read file
        String resource = "/data/Ventana/3240/20080730MosaicData-Short.csv";
        URL url = getClass().getResource(resource);
        if (url == null) {
            Assert.fail("Unable to find " + resource);
        }

        List<Tile> tiles = null;
        try {
            tiles = ingest.loadTiles(url);
        }
        catch (Exception ex) {
            log.error("Failed to read '" + url + "'", ex);
            Assert.fail("Unable to read data file");
        }

        // Convert to mosaic
        MosaicAssembly<VARSVideoTile> mosaicAssembly = modelConverter.convertToMosaicAssembly("Ventana", "3240", tiles);
        List<VARSVideoTile> videoTiles = new ArrayList<VARSVideoTile>(mosaicAssembly.getTiles());
        Collections.sort(videoTiles, indexComparator);    // Sort by TileIndex

        VideoTile lastVideoTile = null;
        for (VideoTile videoTile : videoTiles) {
            if (videoTile.getTileIndex() > 0) {
                if (lastVideoTile == null) {
                    lastVideoTile = videoTile;
                }
                else {
                    int t0 = timecodeComparator.compare(videoTile, lastVideoTile);
                    Assert.assertTrue("Tiles timecode are not in the expected sequence", t0 >= 0);
                    int d0 = dateComparator.compare(videoTile, lastVideoTile);
                    Assert.assertTrue("Tiles dates are not in the expected sequence", d0 >= 0);
                }
            }
        }
    }

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VARSModule());
        ingest = injector.getInstance(DataIngestorService.class);
        modelConverter = injector.getInstance(VARSModelConverter.class);
    }

}
