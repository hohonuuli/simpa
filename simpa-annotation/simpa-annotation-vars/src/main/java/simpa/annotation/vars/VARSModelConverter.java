/*
 * @(#)VARSModelConverter.java   2009.09.18 at 05:39:15 PDT
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

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.VideoTime;
import simpa.annotation.impl.VideoTimeImpl;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;
import vars.VideoMoment;
import vars.annotation.AnnotationFactory;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.09.18 at 05:35:47 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class VARSModelConverter {

    public static final int SAMPLERATE_MILLSEC = 15 * 1000;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Comparator<VideoFrame> videoFrameComparator = new VideoFrameTimecodeComparator();
    private final Comparator<VideoArchive> videoArchiveComparator = new VideoArchiveNameComparator();
    private final AnnotationFactory annotationFactory;

    /**
     * Constructs ...
     *
     * @param annoFactory
     * @param externalDataDAO
     */
    @Inject
    public VARSModelConverter(AnnotationFactory annoFactory, ExternalDataDAO externalDataDAO) {
        this.annotationFactory = annoFactory;
        this.externalDataDAO = externalDataDAO;
    }

    public MosaicAssembly<VARSVideoTile> convertToMosaicAssembly(String cameraIdentifier, String sessionIdentifier,
            Collection<? extends Tile> tiles) {

        /* *********************************************************************
         * The SIMPA model classes are a facade over come VARS classes. We need
         * to locate existing VARS data, if available. If not create new VARS
         * classes to put behind the SIMPA stuff
         ******************************************************************** */
        int seqNumber = Integer.parseInt(sessionIdentifier);

        // Create an empty VideoArchiveSet with the correct ids
        IVideoArchiveSet vas = annotationFactory.newVideoArchiveSet();
        vas.setPlatformName(cameraIdentifier);
        ICameraDeployment cpd = annotationFactory.newCameraDeployment();
        cpd.setSequenceNumber(seqNumber);
        vas.addCameraDeployment(cpd);

        /*
         * Find Create a VideoArchive. For simplicity we're just tacking all
         * SIMPA annotations onto the very first VideoArchive
         */
        List<IVideoArchive> videoArchives = new ArrayList<IVideoArchive>(vas.getVideoArchives());
        IVideoArchive videoArchive = null;

        if (videoArchives.size() > 0) {
            Collections.sort(videoArchives, videoArchiveComparator);
            videoArchive = videoArchives.get(0);
        }
        else {
            videoArchive = annotationFactory.newVideoArchive();
            String videoArchiveName = String.format("%c%04d-01", cameraIdentifier.charAt(0), seqNumber);
            videoArchive.setName(videoArchiveName);
            vas.addVideoArchive(videoArchive);
        }

        VARSMosaicAssembly mosiacAssembly = new VARSMosaicAssembly(videoArchive, annotationFactory);
        List<VARSVideoTile> videoTiles = mosiacAssembly.getTiles();
        List<IVideoFrame> videoFrames = new ArrayList<IVideoFrame>(vas.getVideoFrames());
        Collections.sort(videoFrames, videoFrameComparator);


        for (Tile tile : tiles) {

            log.debug("Processing " + tile);

            // Interpolate to time/timecode with the same value
            IVideoMoment dateTimecode = null;
            try {
                dateTimecode = externalDataDAO.interpolateTimecodeByDate(cameraIdentifier, tile.getDate(),
                        SAMPLERATE_MILLSEC, 29.97);
            }
            catch (Exception e) {
                log.warn("An error occured while attempting to interpolate timecode for " + tile, e);
            }

            if (dateTimecode == null) {
                log.warn("Unable to interpolate timecode for " + tile);

                continue;
            }

            // Search through the videoframes to find the one with the correct timecode
            IVideoFrame vf = annotationFactory.newVideoFrame();
            vf.setRecordedDate(dateTimecode.getRecordedDate());
            vf.setTimecode(dateTimecode.getAlternateTimecode()); // All SIMPA dives are shot in HD
            int i = Collections.binarySearch(videoFrames, vf, videoFrameComparator);

            VARSVideoTile videoTile = null;
            if (i > 0) {
                videoTile = new VARSVideoTile(videoFrames.get(i), annotationFactory);
            }
            else {
                IVideoFrame videoFrame = annotationFactory.newVideoFrame();
                videoArchive.addVideoFrame(videoFrame);
                videoTile = new VARSVideoTile(videoFrame, annotationFactory);

                // TODO figure out if we're always using alternateTimecode here
                VideoTime videoTime = new VideoTimeImpl(dateTimecode.getAlternateTimecode(),
                    dateTimecode.getRecordedDate());
                videoTile.setVideoTime(videoTime);
                videoTile.setCameraPosition(tile.getCameraPosition());
                videoTile.setDate(tile.getDate());
                videoTile.setHeightInMeters(tile.getHeightInMeters());
                videoTile.setTileIndex(tile.getTileIndex());
                videoTile.setUrl(tile.getUrl());
                videoTile.setWidthInMeters(tile.getWidthInMeters());

            }

            videoTiles.add(videoTile);

        }

        return mosiacAssembly;
    }
}
