/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.AbstractAnnotationLookupService;
import simpa.annotation.AnnotationLookupException;
import simpa.annotation.VideoTime;
import simpa.core.CoreException;
import simpa.core.MosaicAssembly;
import simpa.core.Tile;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.KnowledgebaseDAOFactory;

/**
 *
 * @author brian
 */
public class VARSAnnotationLookupService extends AbstractAnnotationLookupService {

    public static final int SAMPLERATE_MILLSEC = 15 * 1000;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final Calendar CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

    private final Comparator<VideoFrame> videoFrameComparator = new VideoFrameTimecodeComparator();
    private final Comparator<VideoArchive> videoArchiveComparator = new VideoArchiveNameComparator();


    private final AnnotationFactory annotationFactory;
    private final AnnotationDAOFactory annoDaoFactory;
    private final KnowledgebaseDAOFactory kbDaoFactory;

    @Inject
    public VARSAnnotationLookupService(AnnotationFactory annotationFactory, AnnotationDAOFactory daoFactory, KnowledgebaseDAOFactory kbDaoFactory) {
        this.annotationFactory = annotationFactory;
        this.annoDaoFactory = daoFactory;
        this.kbDaoFactory = kbDaoFactory;
    }

    public VideoArchiveSet findVideoArchiveSetByPlatformAndDiveNumber(String platform, int diveNumber) {
        VideoArchiveSet vas = null;

        try {
            Collection<VideoArchiveSet> vasSet = annoDaoFactory.newVideoArchiveSetDAO().findAllByPlatformAndSequenceNumber(platform, diveNumber);
            if (vasSet.size() > 0) {
                vas = vasSet.iterator().next();
            }
        }
        catch (Exception ex) {
            throw new CoreException("Failed to lookup VideoArchiveSet for " +
                    platform + " #" + diveNumber, ex);
        }
        return vas;
    }

    public VideoArchive findVideoArchiveByName(String name) {
        VideoArchive videoArchive = null;
        try {
            videoArchive = annoDaoFactory.newVideoArchiveDAO().findByName(name);
        }
        catch (Exception ex) {
            throw new CoreException("Failed to lookup VideoArchive named " +
                    name, ex);
        }
        return videoArchive;
    }


//    public List<VideoTime> findAllTimecodesByDate(String platform, Date date, int millisecTolerance) {
//
//        /*
//         * Retrive HDTimecode and GMT from the EXPD Database and store in a
//         * map for us to work with later.
//         */
//        Map<Date, String> map = new HashMap<Date, String>();
//        String table = platform + "CamlogData";
//        Date startDate = new Date(date.getTime() - millisecTolerance);
//        Date endDate = new Date(date.getTime() + millisecTolerance);
//
//        String sql =
//                "SELECT DateTimeGMT, hdTimecode  " +
//                "FROM " + table + " " +
//                "WHERE DateTimeGMT BETWEEN '" + GlobalParameters.DATE_FORMAT_UTC.format(startDate) +
//                "' AND '" + GlobalParameters.DATE_FORMAT_UTC.format(endDate) + "' AND " +
//                "hdTimecode IS NOT NULL " +
//                "ORDER BY DateTimeGMT";
//        Connection connection = null;
//        try {
//            connection = getConnection();
//            Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//            if (log.isDebugEnabled()) {
//                log.debug("Executing query: " + sql);
//            }
//            ResultSet resultSet = statement.executeQuery(sql);
//            while (resultSet.next()) {
//                Date rovDate = resultSet.getTimestamp(1, CALENDAR);
//                String timecode = resultSet.getString(2);
//                if (log.isDebugEnabled()) {
//                    log.debug("Found record: \n\tLocal Date: " + GlobalParameters.DATE_FORMAT_LOCAL.format(rovDate) +
//                            "\n\tUTC Date:   " + GlobalParameters.DATE_FORMAT_UTC.format(rovDate) +
//                            "\n\tTimecode:   " + timecode);
//                }
//                map.put(rovDate, timecode);
//            }
//            resultSet.close();
//            statement.close();
//        }
//        catch (SQLException e) {
//            if (connection != null) {
//                log.error("Failed to execute the following SQL on EXPD:\n" + sql, e);
//                try {
//                    connection.close();
//                }
//                catch (SQLException ex) {
//                    log.error("Failed to close database connection", ex);
//                }
//            }
//            throw new CoreException("Failed to execute the following SQL on EXPD: " + sql, e);
//        }
//
//        List<VideoTime> tapeTimes = new ArrayList<VideoTime>();
//        for (Date d: map.keySet()) {
//            tapeTimes.add(new VideoTimeImpl(map.get(d), d));
//        }
//
//        return tapeTimes;
//
//    }

    /**
     * @return A {@link Connection} to the EXPD database. The connection should
     *      be closed when you're done with it.
     */
//    private Connection getConnection() throws SQLException {
//        Connection connection = connections.get();
//        if (connection == null || connection.isClosed()) {
//            connection = DriverManager.getConnection("jdbc:jtds:sqlserver://solstice.shore.mbari.org:1433/EXPD",
//                "everyone", "guest");
//            connections.set(connection);
//        }
//        return connection;
//    }


//    public VideoTile convertToVideoTile(String cameraIdentifier,
//            String sessionIdentifier, Tile tile) {
//        // Interpolate to time/timecode based on EXPD
//        VideoTime tapeTime = interpolateTimecodeToDate(cameraIdentifier, tile.getDate(), SAMPLERATE_MILLSEC,
//                VideoStandard.NTSC.getFramesPerSecond());
//
//        // Lookup any existing frames with the same value
//        IVideoArchiveSet vas = findVideoArchiveSetByPlatformAndDiveNumber(cameraIdentifier, Integer.parseInt(sessionIdentifier));
//        vas.getVideoFrames();
//
//        // Instantiate VideoTile and return it
//        VideoTile videoTile = new VARSVideoTile(annotationFactory.newVideoFrame(), annotationFactory);
//        videoTile.setVideoTime(tapeTime);
//
//        return videoTile;
//
//    }

//    public MosaicAssembly<VARSVideoTile> convertToMosaicAssembly(String cameraIdentifier, String sessionIdentifier, Collection<? extends Tile> tiles) {
//
//        /* *********************************************************************
//         * The SIMPA model classes are a facade over come VARS classes. We need
//         * to locate existing VARS data, if available. If not create new VARS
//         * classes to put behind the SIMPA stuff
//         ******************************************************************** */
//
//        int seqNumber = Integer.parseInt(sessionIdentifier);
//
//        // Find/Create a videoarchiveSet
//        IVideoArchiveSet vas = findVideoArchiveSetByPlatformAndDiveNumber(cameraIdentifier, seqNumber);
//
//        if (vas == null) {
//            // Create an empty VideoArchiveSet with the correct ids
//            vas = annotationFactory.newVideoArchiveSet();
//            vas.setPlatformName(cameraIdentifier);
//            ICameraDeployment cpd = annotationFactory.newCameraDeployment();
//            cpd.setSequenceNumber(seqNumber);
//            vas.addCameraDeployment(cpd);
//        }
//
//        /*
//         * Find Create a VideoArchive. For simplicity we're just tacking all
//         * SIMPA annotations onto the very first VideoArchive
//         */
//        List<IVideoArchive> videoArchives = new ArrayList<IVideoArchive>(vas.getVideoArchives());
//        IVideoArchive videoArchive = null;
//        if (videoArchives.size() > 0) {
//            Collections.sort(videoArchives, videoArchiveComparator);
//            videoArchive = videoArchives.get(0);
//        }
//        else {
//            videoArchive = annotationFactory.newVideoArchive();
//            String videoArchiveName = String.format("%c%04d-01", cameraIdentifier.charAt(0), seqNumber);
//            videoArchive.setName(videoArchiveName);
//            vas.addVideoArchive(videoArchive);
//        }
//
//        VARSMosaicAssembly mosiacAssembly = new VARSMosaicAssembly(videoArchive, annotationFactory);
//        List<VARSVideoTile> videoTiles = mosiacAssembly.getTiles();
//
//        List<IVideoFrame> videoFrames = new ArrayList<IVideoFrame>(vas.getVideoFrames());
//        Collections.sort(videoFrames, videoFrameComparator);
//
//
//        for (Tile tile : tiles) {
//
//            log.debug("Processing " + tile);
//
//            // Interpolate to time/timecode with the same value
//            VideoTime tapeTime = null;
//            try {
//                tapeTime = interpolateTimecodeToDate(cameraIdentifier, tile.getDate(), SAMPLERATE_MILLSEC,
//                    VideoStandard.NTSC.getFramesPerSecond());
//            }
//            catch (Exception e) {
//                log.warn("An error occured while attempting to interpolate timecode for " + tile, e);
//            }
//
//            if (tapeTime == null) {
//                log.warn("Unable to interpolate timecode for "  + tile);
//                continue;
//            }
//
//            // Search through the videoframes to find the one wth the correct timecode
//            IVideoFrame vf = annotationFactory.newVideoFrame();
//            vf.setRecordedDate(tapeTime.getDate());
//            vf.setTimecode(tapeTime.getTimecode());
//            int i = Collections.binarySearch(videoFrames, vf, videoFrameComparator);
//
//            VARSVideoTile videoTile = null;
//            if (i > 0) {
//                videoTile = new VARSVideoTile(videoFrames.get(i), annotationFactory);
//            }
//            else {
//                IVideoFrame videoFrame = annotationFactory.newVideoFrame();
//                videoArchive.addVideoFrame(videoFrame);
//                videoTile = new VARSVideoTile(videoFrame, annotationFactory);
//                videoTile.setVideoTime(tapeTime);
//                videoTile.setCameraPosition(tile.getCameraPosition());
//                videoTile.setDate(tile.getDate());
//                videoTile.setHeightInMeters(tile.getHeightInMeters());
//                videoTile.setTileIndex(tile.getTileIndex());
//                videoTile.setUrl(tile.getUrl());
//                videoTile.setWidthInMeters(tile.getWidthInMeters());
//
//            }
//
//            videoTiles.add(videoTile);
//
//        }
//
//        return mosiacAssembly;
//    }

    /**
     * Looks up existing MosaicAssemblies in VARS and returns them. If no match
     * is found then null is returned.
     *
     * @param cameraIdentifier The platform name (i.e. Ventana or Tiburon)
     * @param sessionIdentifier The dive number
     * @return The matching {@link MosaicAssembly} or <b>null</b> if no match is found
     */
    public MosaicAssembly findByCameraAndSessionIdentifiers(String cameraIdentifier, String sessionIdentifier) {

        MosaicAssembly<VARSVideoTile> mosaicAssembly = null;

        VideoArchiveSet videoArchiveSet = findVideoArchiveSetByPlatformAndDiveNumber(cameraIdentifier, Integer.parseInt(sessionIdentifier));
        List<VideoArchive> videoArchives = new ArrayList<VideoArchive>(videoArchiveSet.getVideoArchives());
        Collections.sort(videoArchives, new VideoArchiveNameComparator());
        if (videoArchives.size() > 0) {
            mosaicAssembly = new VARSMosaicAssembly(videoArchives.get(0), annotationFactory);
        }

        return mosaicAssembly;
    }

    /**
     * Fetch all the concepts that can be used for annotating
     *
     * @return A collection (here it's actually a sorted list) of all concepts
     *  that can be used to annotate with.
     */
    public Collection<String> findAllConcepts() {

        List<String> concepts = new ArrayList<String>();
        try {
            Collection<ConceptName> conceptNames = kbDaoFactory.newConceptNameDAO().findAll();
            for (ConceptName cn : conceptNames) {
                concepts.add(cn.getName());
            }
        }
        catch (Exception ex) {
            throw new AnnotationLookupException("Unable to lookup all the concepts", ex);
        }
        Collections.sort(concepts);
        return concepts;
    }

    public MosaicAssembly convertToMosaicAssembly(String cameraIdentifier, String sessionIdentifier, Collection<? extends Tile> tiles) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends VideoTime> findAllTimecodesByDate(String cameraIdentifier, Date date, int millisecTolerance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
