/*
 * @(#)VARSAnnotationPersistenceService.java   2009.08.25 at 08:45:34 PDT
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.AnnotationPersistenceException;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.TileAnnotation;
import simpa.annotation.VideoTile;
import simpa.core.MosaicAssembly;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;

/**
 *
 * @author brian
 */
public class VARSAnnotationPersistenceService implements AnnotationPersistenceService {

    private final Logger log = LoggerFactory.getLogger(VARSAnnotationPersistenceService.class);
    private final AnnotationDAOFactory daoFactory;

    /**
     * Constructs ...
     *
     * @param daoFactory
     */
    @Inject
    public VARSAnnotationPersistenceService(AnnotationDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * @throws AnnotationPersistenceException If the {@link VideoArchiveDAO} call fails.
     */
    public void makePersistent(MosaicAssembly mosaicAssembly) {
        VARSMosaicAssembly vMosiacAssembly = (VARSMosaicAssembly) mosaicAssembly;
        VideoArchive videoArchive = (VideoArchive) vMosiacAssembly.getVideoArchive();

        try {
            daoFactory.newVideoArchiveSetDAO().makePersistent(videoArchive.getVideoArchiveSet());
        }
        catch (Exception e) {
            throw new AnnotationPersistenceException("Failed to persist " + mosaicAssembly + " to the data store", e, mosaicAssembly);
        }
    }

    /**
     * @throws AnnotationPersistenceException If the {@link vars.annotation.IVideoFrameDAO} call fails.
     */
    public void makePersistent(VideoTile videoTile) {
        VARSVideoTile vVideoTile = (VARSVideoTile) videoTile;
        VideoFrame videoFrame = vVideoTile.getVideoFrame();

        try {
            daoFactory.newVideoFrameDAO().makePersistent(videoFrame);
        }
        catch (Exception e) {
            throw new AnnotationPersistenceException("Failed to persist " + videoFrame + " to the data store", e, videoTile);
        }
    }

    /**
     * @throws PersistRuleException If one of the {@link PersistRule}s fails.
     * @throws AnnotationPersistenceException If the {@link ObservationDAO} call fails.
     */
    public void makePersistent(TileAnnotation tileAnnotation) {
        VARSTileAnnotation vTileAnnotation = (VARSTileAnnotation) tileAnnotation;

        Observation observation = vTileAnnotation.getObservation();
        ObservationDAO dao = daoFactory.newObservationDAO();

        try {
            dao.validateName(observation);
            dao.makePersistent(observation);
        }
        catch (Exception e) {
            throw new AnnotationPersistenceException("Failed to persist " + observation + " to the data store", e, tileAnnotation);
        }
    }

    public void makeTransient(MosaicAssembly mosaicAssembly) {
        VARSMosaicAssembly vMosiacAssembly = (VARSMosaicAssembly) mosaicAssembly;
        VideoArchive videoArchive = vMosiacAssembly.getVideoArchive();

        videoArchive.getVideoArchiveSet().removeVideoArchive(videoArchive);
        try {
            daoFactory.newVideoArchiveDAO().makeTransient(videoArchive);
        }
        catch (Exception ex) {
            throw new AnnotationPersistenceException("Failed to delete " + mosaicAssembly + " to the data store", ex, mosaicAssembly);
        }
    }

    public void makeTransient(VideoTile videoTile) {
        VARSVideoTile vVideoTile = (VARSVideoTile) videoTile;
        VideoFrame videoFrame = vVideoTile.getVideoFrame();
        videoFrame.getVideoArchive().removeVideoFrame(videoFrame);
        try {
            daoFactory.newVideoFrameDAO().makeTransient(videoFrame);
        }
        catch (Exception ex) {
            throw new AnnotationPersistenceException("Failed to delete " + videoTile + " to the data store", ex, videoTile);
        }

        
    }

    public void makeTransient(TileAnnotation tileAnnotation) {
        VARSTileAnnotation vTileAnnotation = (VARSTileAnnotation) tileAnnotation;
        Observation observation = vTileAnnotation.getObservation();
        observation.getVideoFrame().removeObservation(observation);
        try {
            daoFactory.newObservationDAO().makeTransient(observation);
        }
        catch (Exception e) {
            throw new AnnotationPersistenceException("Failed to delete " + 
                    tileAnnotation + " to the data store", e, tileAnnotation);
        }
    }
}
