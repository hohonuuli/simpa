package simpa.annotation.basic

import simpa.core.MosaicAssembly

/**
 *
 *
 * @author brian
 * @since Nov 8, 2008 5:25:46 PM
 */

public class BasicMosaicAssembly implements MosaicAssembly<BasicVideoTile> {

    String cameraIdentifier
    String sessionIdentifier
    List<BasicVideoTile> tiles = new ArrayList<BasicVideoTile>()

}