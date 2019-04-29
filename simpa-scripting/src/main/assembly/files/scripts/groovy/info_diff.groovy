/**
 * Generate a plot of time diffs from a mosaic data file
 *
 * Usage:
 *      info_diff [data_url] [camera_id] [session_id] [target_image_name]
 */

import com.google.inject.Guice
import com.google.inject.Stage
import org.slf4j.LoggerFactory
import simpa.annotation.ui.AppModule
import simpa.core.DataIngestorService
import simpa.annotation.VideoTileDateComparator
import org.mbari.movie.Timecode
import simpa.annotation.castor.VARSAnnotationGeneratorService
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.ui.RectangleInsets
import simpa.annotation.AnnotationLookupService
import org.jfree.chart.ChartRenderingInfo
import org.jfree.chart.entity.StandardEntityCollection
import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.PlotOrientation
import java.awt.Color
import org.jfree.chart.ChartUtilities
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.XYPlot

/* ****************************************************************************
 * Parse arguments
 */
def url = (new File(args[0])).toURI().toURL()
def cameraIdentifier = args[1]
def sessionIdentifier = args[2]
def file = new File(args[3])

/* ****************************************************************************
 * Specifiy constants
 */
def log = LoggerFactory.getLogger(this.class);
def framerate = VARSAnnotationGeneratorService.FRAMERATE_NTSC

/* ****************************************************************************
 * Activate dependency injection
 */
def injector = Guice.createInjector(Stage.PRODUCTION, new AppModule())
def ingestor = injector.getInstance(DataIngestorService.class)
def annotationLookupService = injector.getInstance(AnnotationLookupService.class)

/* ****************************************************************************
 * Read data from file
 */
def tiles = ingestor.loadTiles(url);
def mosaicAssembly = annotationLookupService.convertToMosaicAssembly(cameraIdentifier, sessionIdentifier, tiles);
def videoTiles = mosaicAssembly.getTiles();
Collections.sort(videoTiles, new VideoTileDateComparator())

/* ****************************************************************************
 * Generated time diffs
 */
simpa.AnalysisUtilities.createTimeDiffPlot(mosaicAssembly, framerate, file)
