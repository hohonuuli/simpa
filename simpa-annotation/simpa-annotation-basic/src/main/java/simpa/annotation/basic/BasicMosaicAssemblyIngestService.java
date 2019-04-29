package simpa.annotation.basic;

import simpa.annotation.MosaicAssemblyIngestService;
import simpa.core.MosaicAssembly;

import javax.swing.*;
import java.net.URL;
import java.io.File;

/**
 * @author brian
 * @since Nov 9, 2008 8:05:05 PM
 */
public class BasicMosaicAssemblyIngestService implements MosaicAssemblyIngestService {

    private final BasicMosaicAssemblyIngestServiceImpl impl = new BasicMosaicAssemblyIngestServiceImpl();

    public MosaicAssembly load(Object... args) {
        return impl.load((URL) args[0], (File) args[1]);
    }

    public JDialog getLoadDialog() {
        return null;
    }
}
