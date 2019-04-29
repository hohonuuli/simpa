package simpa.map.ui;

import simpa.map.core.AnnotatedAssembly;
import simpa.map.core.impl.AnnotatedAssemblyImpl;
import simpa.map.core.impl.DataIngestorServiceImpl04;
import simpa.core.DataIngestorService;
import simpa.core.Tile;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 15, 2009
 * Time: 3:27:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestUtilities {

    public static final Logger log = LoggerFactory.getLogger(TestUtilities.class);

    public static final String TEST_HARNESS_DIR = "/Users/brian/workspace/simpa-test-harness/dives/Ventana/3240/";
    public static final String TEST_HARNESS_DATA = TEST_HARNESS_DIR + "V3240-2.txt";
    public static final String TEST_HARNESS_IMAGE_REFS = TEST_HARNESS_DIR + "images-reference";
    public static final String TEST_HARNESS_IMAGE_HD = TEST_HARNESS_DIR + "images-hd";

    public static AnnotatedAssembly loadAssembly() {
        
        URL dataUrl;
        try {
            dataUrl = (new File(TEST_HARNESS_DATA)).toURI().toURL();
        }
        catch (MalformedURLException e) {
            throw new TException(e);
        }

        // Read in a mosaic assembly
        log.debug("Reading data from " + dataUrl.toExternalForm());
        DataIngestorService ingest = new DataIngestorServiceImpl04(new File(TEST_HARNESS_IMAGE_REFS));
        List<Tile> tiles = ingest.loadTiles(dataUrl);
        log.debug("Data has been read. " + tiles.size() + " tiles were read.");

        AnnotatedAssembly assembly = new AnnotatedAssemblyImpl();
        assembly.getTiles().addAll(tiles);
        assembly.setCameraIdentifier("TEST-Camera");
        assembly.setSessionIdentifier("TEST");

        return assembly;
    }

    @Test
    @Ignore
    public void dummyTest() {

    }

}
